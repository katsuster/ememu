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
    private JTextField txtImage, txtInitram, txtCmdline;
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

        //stdout
        JSplitPane panel = new JSplitPane();

        JPanel panelRight = new JPanel(new GridLayout(5, 2, 5, 5), true);
        JButton btnReset = new JButton("Reset");
        JButton btnClear = new JButton("Clear");
        txtImage = new JTextField(opts.getKernelImage().toString());
        txtInitram = new JTextField(opts.getInitramfsImage().toString());
        txtCmdline = new JTextField(opts.getCommandLine());

        btnReset.addActionListener(listenButton);
        btnReset.setActionCommand("reset");
        btnClear.addActionListener(listenButton);
        btnClear.setActionCommand("clear");
        panelRight.add(btnReset);
        panelRight.add(btnClear);
        panelRight.add(new JLabel("Kernel Image", SwingConstants.RIGHT));
        panelRight.add(txtImage);
        panelRight.add(new JLabel("InitramFS Image", SwingConstants.RIGHT));
        panelRight.add(txtInitram);
        panelRight.add(new JLabel("Command line", SwingConstants.RIGHT));
        panelRight.add(txtCmdline);
        panelRight.setPreferredSize(new Dimension(200, 400));

        panel.setLeftComponent(spane);
        panel.setRightComponent(panelRight);

        tabPane.addTab("stdout", panel);

        win.setLayout(new BorderLayout());
        win.add(tabPane);

        //show
        win.setSize(800, 600);
        win.setVisible(true);
    }

    public void start() {
        SystemPane.out.println("start");

        //options
        try {
            opts.setKernelImage(new URI(txtImage.getText()));
            opts.setInitramfsImage(new URI(txtInitram.getText()));
            opts.setCommandLine(txtCmdline.getText());
        } catch (URISyntaxException e) {
            //ignored
        }

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
