package net.katsuster.semu.arm;

import java.awt.*;
import java.applet.*;
import javax.swing.*;

public class MainApplet extends Applet {
    private JPanel panel;
    private JButton btn;

    @Override
    public void init() {
        System.out.println("init");

        super.init();

        setLayout(new BorderLayout());

        panel = new JPanel();
        btn = new JButton("button!!");
        add(panel);
        panel.add(btn);

        SystemPane.out.println("init");
    }

    @Override
    public void start() {
        System.out.println("start");

        super.start();
    }

    @Override
    public void stop() {
        System.out.println("stop");

        super.stop();
    }

    @Override
    public void destroy() {
        System.out.println("destroy");

        super.destroy();
    }
}
