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
    public static final String PARAM_INITRD_IMAGE= "initramfsImage";
    public static final String PARAM_COMMAND_LINE = "commandLine";

    private static final PrintStream systemOut = System.out;

    private ButtonListener listenButton;
    private JTabbedPane tabPane;
    private JSplitPane panel;
    private StdoutPanel stdoutPanel;
    private LinuxOptionPanel linuxOptPanel;
    private ProxyOptionPanel proxyOptPanel;
    private Emulator emu;
    private VirtualTerminal[] vttyAMA;

    public MainApplet() {
        vttyAMA = new VirtualTerminal[3];
    }

    @Override
    public void init() {
        LinuxOption linuxOpts = new LinuxOption();
        ProxyOption proxyOpts = new ProxyOption();
        String kimage = "http://www.katsuster.net/contents/java/ememu/Image-4.1.10";
        String initrd = "http://www.katsuster.net/contents/java/ememu/initramfs.gz";
        String cmdline = "console=ttyAMA0 mem=64M root=/dev/ram init=/bin/init debug printk.time=1";
        String proxyhost = "";
        String proxyport = "0";

        System.out.println("init");

        super.init();

        //options of applet
        if (getParameter(PARAM_KERNEL_IMAGE) != null) {
            kimage = getParameter(PARAM_KERNEL_IMAGE);
        } else {
            System.out.printf("Parameter '%s' not found, " +
                            "use default kernel image.\n",
                    PARAM_KERNEL_IMAGE);
        }
        if (getParameter(PARAM_INITRD_IMAGE) != null) {
            initrd = getParameter(PARAM_INITRD_IMAGE);
        } else {
            System.out.printf("Parameter '%s' not found, " +
                            "use default Initrd/InitramFS image.\n",
                    PARAM_INITRD_IMAGE);
        }
        if (getParameter(PARAM_COMMAND_LINE) != null) {
            cmdline = getParameter(PARAM_COMMAND_LINE);
        } else {
            System.out.printf("Parameter '%s' not found, " +
                            "use default command line.\n",
                    PARAM_COMMAND_LINE);
        }
        if (getParameter(PARAM_PROXY_HOST) != null &&
                getParameter(PARAM_PROXY_PORT) != null) {
            proxyhost = getParameter(PARAM_PROXY_HOST);
            proxyport = getParameter(PARAM_PROXY_PORT);
        } else {
            System.out.printf("Parameter '%s', '%s' not found, " +
                            "use no proxy.\n",
                    PARAM_PROXY_HOST, PARAM_PROXY_PORT);
        }

        //options
        try {
            linuxOpts.setKernelImage(new URI(kimage));
            linuxOpts.setInitrdImage(new URI(initrd));
            linuxOpts.setCommandLine(cmdline);
            //FIXME: for debug
            System.out.println(linuxOpts);
        } catch (URISyntaxException ex) {
            //ignore
        }
        proxyOpts.setProxyHost(proxyhost);
        proxyOpts.setProxyPort(proxyport);
        //FIXME: for debug
        System.out.println(proxyOpts);

        //menu
        listenButton = new ButtonListener();
        setJMenuBar(new MainMenuBar(listenButton));

        //tabs
        tabPane = new JTabbedPane();
        tabPane.setTabPlacement(JTabbedPane.BOTTOM);
        tabPane.setFocusable(false);
        tabPane.transferFocus();

        //stdout Tab
        panel = new JSplitPane();
        panel.setDividerSize(4);

        //stdout Tab - Left - stdout
        stdoutPanel = new StdoutPanel(listenButton);
        panel.setLeftComponent(stdoutPanel);

        //stdout Tab - Right - Settings, Navigator
        linuxOptPanel = new LinuxOptionPanel(linuxOpts);
        proxyOptPanel = new ProxyOptionPanel(proxyOpts);

        JPanel panelNavigator = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(listenButton);
        btnReset.setActionCommand("reset");
        panelNavigator.add(btnReset);

        JPanel panelRight = new JPanel(new GridLayout(3, 1, 5, 5), true);
        panelRight.add(linuxOptPanel);
        panelRight.add(proxyOptPanel);
        panelRight.add(panelNavigator);
        panelRight.setPreferredSize(new Dimension(100, 100));
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

        //proxy
        ProxyOption optProxy = proxyOptPanel.getOption();
        System.setProperty("proxyHost", optProxy.getProxyHost().toString());
        System.setProperty("proxyPort", Integer.toString(optProxy.getProxyPort()));

        //stdout Tab - Left - stdout
        stdoutPanel = new StdoutPanel(listenButton);
        panel.setLeftComponent(stdoutPanel);

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

        //Run the emulator
        emu = new Emulator();
        emu.setOption(linuxOptPanel.getOption());
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
                stdoutPanel.clear();
            }
            if (e.getActionCommand().equals("gc")) {
                System.gc();
            }
        }
    }
}
