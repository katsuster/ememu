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
    private EmuPropertyPanelMap opts;
    private Emulator emu;
    private JPanel emuOptPanel;
    private VirtualTerminal[] vttyAMA;

    public MainWindow(EmuPropertyPanelMap o) {
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
        JPanel emuOptPanel = opts.createPanel(keys, 0, "Emulator Options");

        keys = new ArrayList<>();
        keys.add(LinuxOption.LINUX_DTB_ENABLE);
        keys.add(LinuxOption.LINUX_DTB);
        keys.add(LinuxOption.LINUX_KIMAGE);
        keys.add(LinuxOption.LINUX_INITRD);
        keys.add(LinuxOption.LINUX_CMDLINE);
        JPanel linuxOptPanel = opts.createPanel(keys, 0, "Linux Boot Options");

        keys = new ArrayList<>();
        keys.add(ProxyOption.PROXY_ENABLE);
        keys.add(ProxyOption.PROXY_HOST);
        keys.add(ProxyOption.PROXY_PORT);
        JPanel proxyOptPanel = opts.createPanel(keys, 0, "Proxies");

        JPanel panelOptions = new JPanel(true);
        panelOptions.setLayout(new BoxLayout(panelOptions, BoxLayout.Y_AXIS));
        panelOptions.add(emuOptPanel);
        panelOptions.add(linuxOptPanel);
        panelOptions.add(proxyOptPanel);

        JScrollPane scrOptions = new JScrollPane(panelOptions);
        scrOptions.setPreferredSize(new Dimension(320, 640));

        JPanel panelNavigator = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCreate = new JButton("Create");
        btnCreate.addActionListener(listenButton);
        btnCreate.setActionCommand("create");
        panelNavigator.add(btnCreate);

        JButton btnDestroy = new JButton("Destroy");
        btnDestroy.addActionListener(listenButton);
        btnDestroy.setActionCommand("destroy");
        panelNavigator.add(btnDestroy);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(listenButton);
        btnReset.setActionCommand("reset");
        panelNavigator.add(btnReset);

        JPanel panelRight = new JPanel(new BorderLayout(), true);
        panelRight.add(scrOptions, BorderLayout.CENTER);
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

    public void create() {
        System.out.println("create");

        //Create emulator properties
        String arch = opts.getValue(LinuxOption.EMU_ARCH, 0);
        Emulator tempEmu;
        List<String> keys = new ArrayList<>();
        int index = 0;

        if (arch.compareToIgnoreCase("arm") == 0) {
            tempEmu = new EmulatorARM();

            keys.add("test.test");
        } else if (arch.compareToIgnoreCase("riscv") == 0) {
            tempEmu = new EmulatorRISCV();

            keys.add("test.test");
        } else {
            throw new IllegalArgumentException("Not support '" +
                    arch + "' architecture.");
        }
        tempEmu.initProperties(opts);

        emuOptPanel = opts.createPanel(keys, index, "Emulator");
        tabPane.addTab("emulator", emuOptPanel);
    }

    public void destroy() {
        System.out.println("destroy");

        //Destroy emulator properties
        if (emuOptPanel != null) {
            tabPane.remove(emuOptPanel);
            emuOptPanel = null;
        }
    }

    public void start() {
        int index = 0;

        System.out.println("start");

        if (emu != null)
            return;

        //Create the emulator
        String arch = opts.getValue(LinuxOption.EMU_ARCH, 0);

        if (arch.compareToIgnoreCase("arm") == 0) {
            emu = new EmulatorARM();
        } else if (arch.compareToIgnoreCase("riscv") == 0) {
            emu = new EmulatorRISCV();
        } else {
            throw new IllegalArgumentException("Not support '" +
                    arch + "' architecture.");
        }

        //Set emulator properties
        if (opts.getAsBoolean(ProxyOption.PROXY_ENABLE, 0)) {
            System.setProperty("proxyHost",
                    opts.getAsURI(ProxyOption.PROXY_HOST, 0).toString());
            System.setProperty("proxyPort",
                    Integer.toString(opts.getAsInteger(ProxyOption.PROXY_PORT, 0)));
        }
        emu.setProperties(opts);
        emu.setup();

        //Create and connect stdout
        stdoutPanel = new StdoutPanel(listenButton);
        panel.setLeftComponent(stdoutPanel);

        //Create and connect terminals
        for (int i = 0; i < vttyAMA.length; i++) {
            vttyAMA[i] = new VirtualTerminal();
            tabPane.addTab("ttyAMA" + i, vttyAMA[i]);

            emu.getBoard().setUARTInputStream(i, vttyAMA[i].getInputStream());
            emu.getBoard().setUARTOutputStream(i, vttyAMA[i].getOutputStream());
        }
        tabPane.setSelectedIndex(2);

        //Start emulator
        emu.start();
    }

    public void stop() {
        System.out.println("stop");

        if (emu == null)
            return;

        //Stop and destroy emulator
        try {
            emu.halt();
            emu.join();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
            //ignored
        }
        emu = null;

        //Disconnect and destroy terminals
        for (int i = 0; i < vttyAMA.length; i++) {
            if (vttyAMA[i] == null)
                continue;

            vttyAMA[i].close();
            tabPane.remove(vttyAMA[i]);
            vttyAMA[i] = null;
        }

        //Disconnect and destroy stdout
        stdoutPanel.close();
        stdoutPanel = null;
    }

    class ButtonListener implements ActionListener {
        public ButtonListener() {
            //do nothing
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("create")) {
                create();
            }
            if (e.getActionCommand().equals("destroy")) {
                stop();
                destroy();
            }
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
