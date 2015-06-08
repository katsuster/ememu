package net.katsuster.ememu.ui;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * エミュレータのグラフィカル画面、ログ表示用のアプレット。
 *
 * @author katsuhiro
 */
public class MainApplet extends JApplet {
    public static final String PARAM_PROXY_HOST = "proxyHost";
    public static final String PARAM_PROXY_PORT = "proxyPort";
    public static final String PARAM_KERNEL_IMAGE = "kernelImage";
    public static final String PARAM_INITRAMFS_IMAGE = "initramfsImage";
    public static final String PARAM_COMMAND_LINE = "commandLine";

    private static final SystemPane spane = new SystemPane();

    private JTabbedPane tabPane;
    private Emulator emu;
    private VirtualTerminal[] vttyAMA;

    private EmulatorOption opts;

    public MainApplet() {
        vttyAMA = new VirtualTerminal[3];
        opts = new EmulatorOption();
    }

    @Override
    public void init() {
        String kimage, initram, cmdline;

        SystemPane.out.println("init");

        super.init();

        if (getParameter(PARAM_PROXY_HOST) != null &&
                getParameter(PARAM_PROXY_PORT) != null) {
            System.setProperty("proxySet", "true");
            System.setProperty("proxyHost", getParameter(PARAM_PROXY_HOST));
            System.setProperty("proxyPort", getParameter(PARAM_PROXY_PORT));
        } else {
            SystemPane.out.printf("Parameter '%s', '%s' not found, " +
                            "use no proxy.\n",
                    PARAM_PROXY_HOST, PARAM_PROXY_PORT);
        }
        if (getParameter(PARAM_KERNEL_IMAGE) != null) {
            kimage = getParameter(PARAM_KERNEL_IMAGE);
        } else {
            SystemPane.out.printf("Parameter '%s' not found, " +
                            "use default kernel image.\n",
                    PARAM_KERNEL_IMAGE);
        }
        if (getParameter(PARAM_INITRAMFS_IMAGE) != null) {
            initram = getParameter(PARAM_INITRAMFS_IMAGE);
        } else {
            SystemPane.out.printf("Parameter '%s' not found, " +
                            "use default initramfs image.\n",
                    PARAM_INITRAMFS_IMAGE);
        }
        if (getParameter(PARAM_COMMAND_LINE) != null) {
            cmdline = getParameter(PARAM_COMMAND_LINE);
        } else {
            SystemPane.out.printf("Parameter '%s' not found, " +
                            "use default command line.\n",
                    PARAM_COMMAND_LINE);
        }

        try {
            opts.setKernelImage(new URI("http://www2.katsuster.net/~katsuhiro/contents/java/Image-3.18.11"));
            opts.setInitramfsImage(new URI("http://www2.katsuster.net/~katsuhiro/contents/java/initramfs.gz"));
            opts.setCommandLine("console=ttyAMA0 mem=64M lpj=0 root=/dev/ram init=/bin/init debug printk.time=1");
        } catch (URISyntaxException ex) {
            //ignore
        }

        ButtonListener listenButton = new ButtonListener();

        //menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menuSystem = new JMenu("System");
        JMenuItem itemReset = new JMenuItem("Reset");
        JMenuItem itemClear = new JMenuItem("Clear Log");
        setJMenuBar(menuBar);
        menuBar.add(menuSystem);
        menuSystem.add(itemReset);
        menuSystem.addSeparator();
        menuSystem.add(itemClear);
        menuSystem.setMnemonic(KeyEvent.VK_S);

        itemReset.setActionCommand("reset");
        itemReset.addActionListener(listenButton);
        itemReset.setMnemonic(KeyEvent.VK_R);
        itemClear.setActionCommand("clear");
        itemClear.addActionListener(listenButton);
        itemClear.setMnemonic(KeyEvent.VK_C);

        tabPane = new JTabbedPane();
        tabPane.setTabPlacement(JTabbedPane.BOTTOM);
        tabPane.setFocusable(false);

        //stdout
        JPanel panel = new JPanel(new BorderLayout(), true);

        JPanel panelEast = new JPanel(new FlowLayout(), true);
        JButton btnReset = new JButton("Reset");
        JButton btnClear = new JButton("Clear");

        btnReset.addActionListener(listenButton);
        btnReset.setActionCommand("reset");
        btnClear.addActionListener(listenButton);
        btnClear.setActionCommand("clear");
        panelEast.add(btnReset);
        panelEast.add(btnClear);

        panel.add("Center", spane);
        panel.add("East", panelEast);

        tabPane.addTab("stdout", panel);

        setLayout(new BorderLayout());
        add(tabPane);
    }

    @Override
    public void start() {
        SystemPane.out.println("start");

        super.start();

        spane.clear();

        //terminal
        for (int i = 0; i < vttyAMA.length; i++) {
            if (vttyAMA[i] != null) {
                tabPane.remove(vttyAMA[i]);
                vttyAMA[i] = null;
            }

            vttyAMA[i] = new VirtualTerminal();
            tabPane.addTab("ttyAMA" + i, vttyAMA[i]);
        }
        tabPane.setSelectedIndex(1);

        emu = new Emulator();
        emu.setOption(opts);
        //board.setUARTInputStream(0, System.in);
        for (int i = 0; i < vttyAMA.length; i++) {
            emu.getBoard().setUARTInputStream(i, vttyAMA[i].getInputStream());
            emu.getBoard().setUARTOutputStream(i, vttyAMA[i].getOutputStream());
        }
        emu.start();
    }

    @Override
    public void stop() {
        SystemPane.out.println("stop");

        super.stop();

        try {
            emu.halt();
            emu.join();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
            //ignored
        }
    }

    @Override
    public void destroy() {
        SystemPane.out.println("destroy");

        super.destroy();
    }

    class ButtonListener implements ActionListener {
        public ButtonListener() {
            //do nothing
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("reset")) {
                stop();
                start();
            }
            if (e.getActionCommand().equals("clear")) {
                spane.clear();
            }
        }
    }
}
