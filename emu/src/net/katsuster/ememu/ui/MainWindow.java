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
public class MainWindow extends JFrame {
    private ButtonListener listenButton;
    private JTabbedPane tabPane;
    private JSplitPane panel;
    private StdoutPanel stdoutPanel;
    private LinuxOptionPanel linuxOptPanel;
    private ProxyOptionPanel proxyOptPanel;
    private Emulator emu;
    private VirtualTerminal[] vttyAMA;

    public MainWindow(LinuxOption linuxOpts) {
        ProxyOption proxyOpts = new ProxyOption();

        vttyAMA = new VirtualTerminal[3];

        //window
        setTitle("ememu");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //menu
        listenButton = new ButtonListener();
        setJMenuBar(new MainMenuBar(listenButton));

        //tabs
        tabPane = new JTabbedPane();
        tabPane.setFocusable(false);
        tabPane.setTabPlacement(JTabbedPane.BOTTOM);

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

        //show
        setSize(800, 600);
    }

    public void start() {
        System.out.println("start");

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

    public void stop() {
        System.out.println("stop");

        try {
            stdoutPanel.close();

            if (emu != null) {
                for (VirtualTerminal vta : vttyAMA) {
                    vta.close();
                }

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
                stdoutPanel.clear();
            }
            if (e.getActionCommand().equals("gc")) {
                System.gc();
            }
        }
    }
}
