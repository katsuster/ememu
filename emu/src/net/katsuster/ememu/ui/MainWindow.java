package net.katsuster.ememu.ui;

import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;

/**
 * エミュレータのグラフィカル画面、ログ表示用のウインドウ。
 *
 * @author katsuhiro
 */
public class MainWindow {
    private static final SystemPane spane = new SystemPane();

    private JTabbedPane tabPane;
    private Emulator emu;
    private VirtualTerminal[] vttyAMA;

    private EmulatorOption opts;

    public MainWindow(EmulatorOption op) {
        vttyAMA = new VirtualTerminal[3];

        opts = op;

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

        win.setLayout(new BorderLayout());
        win.add(tabPane);

        //show
        win.setSize(800, 600);
        win.setVisible(true);
    }

    public void start() {
        SystemPane.out.println("start");

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

    public void stop() {
        SystemPane.out.println("stop");

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
