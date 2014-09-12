package net.katsuster.semu.arm;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;

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

        setLayout(new BorderLayout());

        panel = new JPanel();
        btn = new JButton("button!!");
        spane = new SystemPane();

        add(panel);
        panel.add(btn);
        panel.add(spane);

        btn.addActionListener(new ButtonListener());
    }

    @Override
    public void start() {
        SystemPane.out.println("start");

        super.start();

        Thread t = new Thread(new Booter());
        t.start();
    }

    class Booter implements Runnable {
        public Booter() {

        }

        public void run() {
            Main.boot("Y:\\arm_cross\\linux-3.14.17\\arch\\arm\\boot\\Image",
                    "Y:\\arm_cross\\initramfs.gz",
                    "console=ttyAMA0 mem=64M lpj=0 root=/dev/ram init=/bin/sh debug printk.time=1\0");
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
