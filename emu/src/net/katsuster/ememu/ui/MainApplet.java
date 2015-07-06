package net.katsuster.ememu.ui;

import java.io.*;
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

    private static final PrintStream systemOut = System.out;

    private JSplitPane panel;
    private SystemPane spane;
    private JTabbedPane tabPane;
    private LinuxOptionPanel optsPanel;
    private Emulator emu;
    private VirtualTerminal[] vttyAMA;

    public MainApplet() {
        vttyAMA = new VirtualTerminal[3];
    }

    @Override
    public void init() {
        LinuxOption opts = new LinuxOption();
        String kimage = "http://www2.katsuster.net/~katsuhiro/contents/java/ememu/Image-3.18.14";
        String initram = "http://www2.katsuster.net/~katsuhiro/contents/java/ememu/initramfs.gz";
        String cmdline = "console=ttyAMA0 mem=64M lpj=0 root=/dev/ram init=/bin/init debug printk.time=1";

        System.out.println("init");

        super.init();

        //options of applet
        if (getParameter(PARAM_PROXY_HOST) != null &&
                getParameter(PARAM_PROXY_PORT) != null) {
            System.setProperty("proxySet", "true");
            System.setProperty("proxyHost", getParameter(PARAM_PROXY_HOST));
            System.setProperty("proxyPort", getParameter(PARAM_PROXY_PORT));
        } else {
            System.out.printf("Parameter '%s', '%s' not found, " +
                            "use no proxy.\n",
                    PARAM_PROXY_HOST, PARAM_PROXY_PORT);
        }
        if (getParameter(PARAM_KERNEL_IMAGE) != null) {
            kimage = getParameter(PARAM_KERNEL_IMAGE);
        } else {
            System.out.printf("Parameter '%s' not found, " +
                            "use default kernel image.\n",
                    PARAM_KERNEL_IMAGE);
        }
        if (getParameter(PARAM_INITRAMFS_IMAGE) != null) {
            initram = getParameter(PARAM_INITRAMFS_IMAGE);
        } else {
            System.out.printf("Parameter '%s' not found, " +
                            "use default initramfs image.\n",
                    PARAM_INITRAMFS_IMAGE);
        }
        if (getParameter(PARAM_COMMAND_LINE) != null) {
            cmdline = getParameter(PARAM_COMMAND_LINE);
        } else {
            System.out.printf("Parameter '%s' not found, " +
                            "use default command line.\n",
                    PARAM_COMMAND_LINE);
        }

        //options
        try {
            opts.setKernelImage(new URI(kimage));
            opts.setInitramfsImage(new URI(initram));
            opts.setCommandLine(cmdline);
        } catch (URISyntaxException ex) {
            //ignore
        }

        //menu
        ButtonListener listenButton = new ButtonListener();
        setJMenuBar(new MainMenuBar(listenButton));

        //tabs
        tabPane = new JTabbedPane();
        tabPane.setTabPlacement(JTabbedPane.BOTTOM);
        tabPane.setFocusable(false);
        tabPane.transferFocus();

        //stdout
        panel = new JSplitPane();
        panel.setDividerSize(4);

        spane = new SystemPane(systemOut);
        System.setOut(spane.getOutputStream());
        panel.setLeftComponent(spane);

        optsPanel = new LinuxOptionPanel(opts);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(listenButton);
        btnReset.setActionCommand("reset");
        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(listenButton);
        btnClear.setActionCommand("clear");

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnReset);
        panelButtons.add(btnClear);

        JPanel panelRight = new JPanel(new BorderLayout(), true);
        panelRight.add(optsPanel, BorderLayout.CENTER);
        panelRight.add(panelButtons, BorderLayout.SOUTH);
        panelRight.setPreferredSize(new Dimension(180, 180));
        panelRight.setMinimumSize(panelRight.getPreferredSize());
        panel.setRightComponent(panelRight);

        tabPane.addTab("stdout", panel);

        setLayout(new BorderLayout());
        add(tabPane);
    }

    @Override
    public void start() {
        System.out.println("start");

        super.start();

        //stdout
        spane = new SystemPane(systemOut);
        System.setOut(spane.getOutputStream());
        panel.setLeftComponent(spane);

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
        emu.setOption(optsPanel.getOption());
        for (int i = 0; i < vttyAMA.length; i++) {
            emu.getBoard().setUARTInputStream(i, vttyAMA[i].getInputStream());
            emu.getBoard().setUARTOutputStream(i, vttyAMA[i].getOutputStream());
        }
        emu.start();
    }

    @Override
    public void stop() {
        System.out.println("stop");

        super.stop();

        try {
            if (emu != null) {
                emu.halt();
                emu.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
            //ignored
        }
    }

    @Override
    public void destroy() {
        System.out.println("destroy");

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
