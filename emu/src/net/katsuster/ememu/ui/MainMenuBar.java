package net.katsuster.ememu.ui;

import javax.swing.*;
import java.awt.event.*;

/**
 * メインウインドウ、アプレットのメニュー。
 *
 * @author katsuhiro
 */
public class MainMenuBar extends JMenuBar {
    /**
     * メインメニューを作成します。
     *
     * @param listener メニュー項目を選択したイベントのリスナー。
     */
    public MainMenuBar(ActionListener listener) {
        //JMenuBar menuBar = new JMenuBar();
        JMenu menuSystem = new JMenu("System");
        JMenuItem itemReset = new JMenuItem("Reset");
        JMenuItem itemClear = new JMenuItem("Clear Log");
        JMenuItem itemGC = new JMenuItem("GC");

        menuSystem.add(itemReset);
        menuSystem.addSeparator();
        menuSystem.add(itemClear);
        menuSystem.addSeparator();
        menuSystem.add(itemGC);
        menuSystem.setMnemonic(KeyEvent.VK_S);

        itemReset.setActionCommand("reset");
        itemReset.addActionListener(listener);
        itemReset.setMnemonic(KeyEvent.VK_R);
        itemClear.setActionCommand("clear");
        itemClear.addActionListener(listener);
        itemClear.setMnemonic(KeyEvent.VK_C);
        itemGC.setActionCommand("gc");
        itemGC.addActionListener(listener);

        add(menuSystem);
    }
}
