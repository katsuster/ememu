package net.katsuster.ememu.ui;

import java.awt.*;
import javax.swing.*;

/**
 * GridBagLayout での典型的なコンポーネント配置を補助するクラスです。
 *
 * @author katsuhiro
 */
public class GridBagLayoutHelper {
    private GridBagLayoutHelper() {
        //Cannot call
    }

    /**
     * 指定されたレイアウトにコンポーネントの配置制約を設定します。
     *
     * @param panel      コンポーネントを配置する親コンテナ
     * @param layout     配置制約を指定する GridBagLayout
     * @param comp       配置制約の対象となるコンポーネント
     * @param gridx      X 位置
     * @param gridy      Y 位置
     * @param gridwidth  行方向の幅
     * @param gridheight 列方向の高さ
     */
    public static void add(JPanel panel, GridBagLayout layout, JComponent comp, int gridx, int gridy, int gridwidth, int gridheight) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.ipadx = 3;
        gbc.ipady = 3;
        gbc.insets = new Insets(2, 3, 2, 3);
        gbc.fill = GridBagConstraints.BOTH;
        layout.setConstraints(comp, gbc);
        panel.add(comp);
    }
}
