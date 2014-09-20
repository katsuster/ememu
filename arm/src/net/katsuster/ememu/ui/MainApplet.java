package net.katsuster.ememu.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.katsuster.ememu.arm.*;

public class MainApplet extends JApplet {
    private static final SystemPane spane = new SystemPane();

    private Emulator emu;

    class Emulator extends Thread {
        private ARMv5 cpu;
        private Bus64 bus;
        private RAM ramMain;

        public Emulator() {
            //do nothing
        }

        @Override
        public void run() {
            setName(getClass().getName());

            String kimage = "http://www2.katsuster.net/~katsuhiro/contents/java/Image-3.14.16";
            String initram = "http://www2.katsuster.net/~katsuhiro/contents/java/initramfs.gz";
            String cmdline = "console=ttyAMA0 mem=64M lpj=0 root=/dev/ram init=/bin/sh debug printk.time=1\0";

            cpu = new ARMv5();
            bus = new Bus64();
            ramMain = new RAM(64 * 1024 * 1024); //64MB

            Main.addVersatileCores(cpu, bus, ramMain);
            Main.bootFromURL(cpu, ramMain, kimage, initram, cmdline);
        }

        public void halt() {
            cpu.halt();
            bus.haltAllSlaveCores();
        }
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
        }

        ButtonListener listenButton = new ButtonListener();

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

        JPanel panel = new JPanel(new BorderLayout(), true);
        JPanel panelEast = new JPanel(new FlowLayout(), true);
        JButton btnReset = new JButton("Reset");
        JButton btnClear = new JButton("Clear");

        btnReset.addActionListener(listenButton);
        btnReset.setActionCommand("reset");
        btnClear.addActionListener(listenButton);
        btnClear.setActionCommand("clear");

        setLayout(new BorderLayout());
        add(panel);
        panel.add("Center", spane);
        panel.add("East", panelEast);
        panelEast.add(btnReset);
        panelEast.add(btnClear);
    }

    @Override
    public void start() {
        SystemPane.out.println("start");

        super.start();

        spane.clear();

        emu = new Emulator();
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
