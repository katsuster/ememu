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
    private PropertyPanels opts;
    private Emulator emu;
    private VirtualTerminal[] vttyAMA;

    public MainWindow(PropertyPanels o) {
        opts = o;

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
        JPanel emuOptPanel = opts.createPanel(keys, "Emulator Options");

        keys = new ArrayList<>();
        keys.add(LinuxOption.LINUX_DTB_ENABLE);
        keys.add(LinuxOption.LINUX_DTB);
        keys.add(LinuxOption.LINUX_KIMAGE);
        keys.add(LinuxOption.LINUX_INITRD);
        keys.add(LinuxOption.LINUX_CMDLINE);
        JPanel linuxOptPanel = opts.createPanel(keys, "Linux Boot Options");

        keys = new ArrayList<>();
        keys.add(ProxyOption.PROXY_ENABLE);
        keys.add(ProxyOption.PROXY_HOST);
        keys.add(ProxyOption.PROXY_PORT);
        JPanel proxyOptPanel = opts.createPanel(keys, "Proxies");

        JPanel panelOptions = new JPanel(true);
        panelOptions.setLayout(new BoxLayout(panelOptions, BoxLayout.Y_AXIS));
        panelOptions.add(emuOptPanel);
        panelOptions.add(linuxOptPanel);
        panelOptions.add(proxyOptPanel);

        JPanel panelNavigator = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(listenButton);
        btnReset.setActionCommand("reset");
        panelNavigator.add(btnReset);

        JPanel panelRight = new JPanel(new BorderLayout(), true);
        panelRight.add(panelOptions, BorderLayout.CENTER);
        panelRight.add(panelNavigator, BorderLayout.SOUTH);
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
        if (opts.getAsBoolean(ProxyOption.PROXY_ENABLE)) {
            System.setProperty("proxyHost",
                    opts.getAsURI(ProxyOption.PROXY_HOST).toString());
            System.setProperty("proxyPort",
                    Integer.toString(opts.getAsInteger(ProxyOption.PROXY_PORT)));
        }

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
        String arch = opts.getValue(LinuxOption.EMU_ARCH);

        if (arch.compareToIgnoreCase("arm") == 0) {
            emu = new EmulatorARM();
        } else if (arch.compareToIgnoreCase("riscv") == 0) {
            emu = new EmulatorRISCV();
        } else {
            throw new IllegalArgumentException("Not support '" +
                    arch + "' architecture.");
        }
        emu.setOption(opts);
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
