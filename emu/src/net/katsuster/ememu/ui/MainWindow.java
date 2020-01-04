package net.katsuster.ememu.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * エミュレータのグラフィカル画面、ログ表示用のウインドウ。
 */
public class MainWindow extends JFrame {
    private ButtonListener listenButton;
    private JTabbedPane tabPane;
    private JSplitPane panel;
    private StdoutPanel stdoutPanel;
    private LinuxOption linuxOpts;
    private ProxyOption proxyOpts;
    private Emulator emu;
    private VirtualTerminal[] vttyAMA;

    public MainWindow(LinuxOption lopts) {
        linuxOpts = lopts;
        proxyOpts = new ProxyOption();

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
        List<String> keys;

        keys = new ArrayList<>();
        keys.add(LinuxOption.EMU_ARCH);
        keys.add(LinuxOption.LINUX_DTB_ENABLE);
        keys.add(LinuxOption.LINUX_DTB);
        keys.add(LinuxOption.LINUX_KIMAGE);
        keys.add(LinuxOption.LINUX_INITRD);
        keys.add(LinuxOption.LINUX_CMDLINE);
        JPanel linuxOptPanel = linuxOpts.createPanel(keys, "Linux Boot Options");

        keys = new ArrayList<>();
        keys.add(ProxyOption.PROXY_ENABLE);
        keys.add(ProxyOption.PROXY_HOST);
        keys.add(ProxyOption.PROXY_PORT);
        JPanel proxyOptPanel = proxyOpts.createPanel(keys, "Proxies");

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
        System.setProperty("proxyHost", proxyOpts.getValue(ProxyOption.PROXY_HOST));
        System.setProperty("proxyPort", proxyOpts.getValue(ProxyOption.PROXY_PORT));

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
        String arch = linuxOpts.getArch();

        if (arch.compareToIgnoreCase("arm") == 0) {
            emu = new EmulatorARM();
        } else if (arch.compareToIgnoreCase("riscv") == 0) {
            emu = new EmulatorRISCV();
        } else {
            throw new IllegalArgumentException("Not support '" +
                    arch + "' architecture.");
        }
        emu.setOption(linuxOpts);
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
