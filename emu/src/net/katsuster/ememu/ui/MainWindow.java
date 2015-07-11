package net.katsuster.ememu.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

/**
 * エミュレータのグラフィカル画面、ログ表示用のウインドウ。
 *
 * @author katsuhiro
 */
public class MainWindow {
    private static final PrintStream systemOut = System.out;


    private JSplitPane panel;
    private JPanel panelLeft, panelRight;
    private SystemPane spane;
    private JPanel panelStdout, panelNavigator;

    private JTabbedPane tabPane;
    private StdoutPanel stdoutPanel;
    private LinuxOptionPanel linuxOptPanel;
    private ProxyOptionPanel proxyOptPanel;
    private Emulator emu;
    private VirtualTerminal[] vttyAMA;

    public MainWindow(LinuxOption linuxOpts) {
        vttyAMA = new VirtualTerminal[3];

        //window
        JFrame win = new JFrame("ememu");
        win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //menu
        ButtonListener listenButton = new ButtonListener();
        win.setJMenuBar(new MainMenuBar(listenButton));

        //tabs
        tabPane = new JTabbedPane();
        tabPane.setTabPlacement(JTabbedPane.BOTTOM);
        tabPane.setFocusable(false);
        tabPane.transferFocus();

        //stdout Tab
        panel = new JSplitPane();
        panel.setDividerSize(4);

        //stdout Tab - Left - stdout
        spane = new SystemPane(systemOut);
        System.setOut(spane.getOutputStream());

        panelStdout = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(listenButton);
        btnClear.setActionCommand("clear");
        panelStdout.add(btnClear);

        panelLeft = new JPanel(new BorderLayout(), true);
        panelLeft.add(spane, BorderLayout.CENTER);
        panelLeft.add(panelStdout, BorderLayout.SOUTH);
        panel.setLeftComponent(panelLeft);

        //stdout Tab - Right - Settings, Navigator
        linuxOptPanel = new LinuxOptionPanel(linuxOpts);
        proxyOptPanel = new ProxyOptionPanel();

        panelNavigator = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(listenButton);
        btnReset.setActionCommand("reset");
        panelNavigator.add(btnReset);

        panelRight = new JPanel(new GridLayout(3, 1, 5, 5), true);
        panelRight.add(linuxOptPanel);
        panelRight.add(proxyOptPanel);
        panelRight.add(panelNavigator);
        panelRight.setPreferredSize(new Dimension(100, 100));
        panelRight.setMinimumSize(panelRight.getPreferredSize());
        panel.setRightComponent(panelRight);

        tabPane.addTab("stdout", panel);

        win.setLayout(new BorderLayout());
        win.add(tabPane);

        //show
        win.setSize(800, 600);
        win.setVisible(true);
    }

    public void start() {
        System.out.println("start");

        //proxy
        ProxyOption optProxy = proxyOptPanel.getOption();
        System.setProperty("proxyHost", optProxy.getProxyHost().toString());
        System.setProperty("proxyPort", Integer.toString(optProxy.getProxyPort()));

        //stdout
        spane = new SystemPane(systemOut);
        System.setOut(spane.getOutputStream());

        panelLeft = new JPanel(new BorderLayout(), true);
        panelLeft.add(spane, BorderLayout.CENTER);
        panelLeft.add(panelStdout, BorderLayout.SOUTH);
        panel.setLeftComponent(panelLeft);

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

    public void stop() {
        System.out.println("stop");

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
