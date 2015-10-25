package net.katsuster.ememu.ui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * 標準出力を表示するパネル。
 *
 * @author katsuhiro
 */
public class StdoutPanel extends JPanel {
    private static final PrintStream systemOut = System.out;

    private SystemPane spane;

    public StdoutPanel(ActionListener listener) {
        super(new BorderLayout(), true);

        spane = new SystemPane(systemOut);
        System.setOut(spane.getOutputStream());

        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(listener);
        btnClear.setActionCommand("clear");
        editPanel.add(btnClear);

        add(spane, BorderLayout.CENTER);
        add(editPanel, BorderLayout.SOUTH);
    }

    /**
     * 表示済みの文字列を消去します。
     */
    public void clear() {
        spane.clear();
    }

    /**
     * ストリームを閉じ、リソースを解放します。
     */
    public void close() {
        spane.close();
    }
}
