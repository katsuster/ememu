package net.katsuster.ememu.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainApplet extends JApplet {
    private static final SystemPane spane = new SystemPane();

    private JTabbedPane tabPane;
    private Emulator emu;
    private VirtualTerminal[] vttyAMA;

    private String kimage = "http://www2.katsuster.net/~katsuhiro/contents/java/Image-3.18.11";
    private String initram = "http://www2.katsuster.net/~katsuhiro/contents/java/initramfs.gz";
    private String cmdline = "console=ttyAMA0 mem=64M lpj=0 root=/dev/ram init=/bin/init debug printk.time=1";

    public MainApplet() {
        vttyAMA = new VirtualTerminal[3];
    }

    @Override
    public void init() {
        SystemPane.out.println("init");

        super.init();

        if (getParameter("proxyHost") != null &&
                getParameter("proxyPort") != null) {
            System.setProperty("proxySet", "true");
            System.setProperty("proxyHost", getParameter("proxyHost"));
            System.setProperty("proxyPort", getParameter("proxyPort"));
        } else {
            SystemPane.out.println("Parameter 'proxyHost', 'proxyPort' are not found. " +
                    "We will use no proxy.");
        }
        if (getParameter("kernelImage") != null) {
            kimage = getParameter("kernelImage");
        } else {
            SystemPane.out.println("Parameter 'kernelImage' is not found. " +
                    "We will use default kernel image.");
        }
        if (getParameter("initramfsImage") != null) {
            initram = getParameter("initramfsImage");
        } else {
            SystemPane.out.println("Parameter 'initramfsImage' is not found. " +
                    "We will use default initramfs image.");
        }
        if (getParameter("commandLine") != null) {
            cmdline = getParameter("commandLine");
        } else {
            SystemPane.out.println("Parameter 'commandLine' is not found. " +
                    "We will use default command line.");
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
        emu.setKernelImage(kimage);
        emu.setInitramfsImage(initram);
        emu.setCommandLine(cmdline);
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
