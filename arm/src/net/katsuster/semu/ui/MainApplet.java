package net.katsuster.semu.ui;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;

import net.katsuster.semu.arm.*;

public class MainApplet extends Applet {
    private JPanel panel;
    private JButton btn;
    private SystemPane spane;

    class ButtonListener implements ActionListener {
        private int clicked = 0;

        public ButtonListener() {

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SystemPane.out.println("clicked " + clicked);

            clicked += 1;
        }
    }

    @Override
    public void init() {
        SystemPane.out.println("init");

        super.init();

        panel = new JPanel();
        btn = new JButton("button!!");
        spane = new SystemPane();

        setLayout(new BorderLayout());
        add(panel);
        panel.add(btn);
        panel.add(spane);

        btn.addActionListener(new ButtonListener());

        Thread t = new Thread(new Booter());
        t.start();
    }

    @Override
    public void start() {
        SystemPane.out.println("start");

        super.start();
    }

    class Booter implements Runnable {
        public Booter() {

        }

        public void run() {
            String kimage = "http://www2.katsuster.net/~katsuhiro/contents/java/Image-3.14.16";
            String initram = "http://www2.katsuster.net/~katsuhiro/contents/java/initramfs.gz";
            String cmdline = "console=ttyAMA0 mem=64M lpj=0 root=/dev/ram init=/bin/sh debug printk.time=1\0";

            ARMv5 cpu = new ARMv5();
            Bus64 bus = new Bus64();
            RAM ramMain = new RAM(64 * 1024 * 1024); //64MB

            Main.addVersatileCores(cpu, bus, ramMain);
            Main.bootFromURL(cpu, ramMain, kimage, initram, cmdline);
        }
    }

    @Override
    public void stop() {
        SystemPane.out.println("stop");

        super.stop();
    }

    @Override
    public void destroy() {
        System.out.println("destroy");

        super.destroy();
    }
}
