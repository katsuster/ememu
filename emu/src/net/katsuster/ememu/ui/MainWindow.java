package net.katsuster.ememu.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 * エミュレータのグラフィカル画面、ログ表示用のウインドウ。
 *
 * @author katsuhiro
 */
public class MainWindow {
    private static final PrintStream systemOut = System.out;

    private JSplitPane panel;
    private SystemPane spane;
    private JTabbedPane tabPane;
    private LinuxOptionPanel optsPanel;
    private Emulator emu;
    private VirtualTerminal[] vttyAMA;

    public MainWindow(LinuxOption opts) {
        vttyAMA = new VirtualTerminal[3];

        //window
        JFrame win = new JFrame("ememu");
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //menu
        ButtonListener listenButton = new ButtonListener();
        win.setJMenuBar(new MainMenuBar(listenButton));

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

        JPanel panelRight = new JPanel(new GridLayout(3, 1, 5, 5), true);
        optsPanel = new LinuxOptionPanel(opts);
        JButton btnReset = new JButton("Reset");
        JButton btnClear = new JButton("Clear");

        panelRight.add(optsPanel);
        btnReset.addActionListener(listenButton);
        btnReset.setActionCommand("reset");
        panelRight.add(btnReset);
        btnClear.addActionListener(listenButton);
        btnClear.setActionCommand("clear");
        panelRight.add(btnClear);
        panelRight.setPreferredSize(new Dimension(200, 400));
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
