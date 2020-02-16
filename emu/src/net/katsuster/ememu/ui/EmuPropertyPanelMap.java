package net.katsuster.ememu.ui;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * エミュレータのプロパティ設定パネルのマップ。
 *
 * プロパティは固有のキーとインデックスにより参照されます。
 */
public class EmuPropertyPanelMap extends EmuPropertyMap {
    public EmuPropertyPanelMap() {
    }

    @Override
    public EmuPropertyPanel getProperty(String key) {
        return getProperty(key, 0, "");
    }

    @Override
    public EmuPropertyPanel getProperty(String key, int index) {
        return getProperty(key, index, "");
    }

    @Override
    public EmuPropertyPanel getProperty(String key, int index, String def) {
        String k = keyWithIndex(key, index);

        if (!getMap().containsKey(k)) {
            EmuPropertyPanel p = new EmuPropertyPanel();
            p.setLabel(k);
            p.setType("String");
            p.setValue(def);
            getMap().put(k, p);
        }

        return (EmuPropertyPanel) getMap().get(k);
    }

    /**
     * 指定されたキーに対応するプロパティを設定します。インデックスは 0 とみなします。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key キー
     * @param val キーに対応するプロパティ
     */
    public void setProperty(String key, EmuPropertyPanel val) {
        setProperty(key, 0, val);
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティを設定します。
     * キーとインデックスに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key   キー
     * @param index インデックス
     * @param val   キーとインデックスに対応するプロパティ
     */
    public void setProperty(String key, int index, EmuPropertyPanel val) {
        String k = keyWithIndex(key, index);

        getMap().put(k, val);
    }

    @Override
    public void setProperty(String key, int index, String label, String type, String val) {
        setProperty(key, index, new EmuPropertyPanel(label, type, val));
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティのラベルを取得します。
     * キーとインデックスに対応するプロパティが存在しない場合は新たに作成した後に取得します。
     *
     * @param key   キー
     * @param index インデックス
     * @return キーとインデックスに対するプロパティのラベル
     */
    public String getLabel(String key, int index) {
        EmuPropertyPanel p = getProperty(key, index);
        return p.getLabel();
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティのラベルを設定します。
     * キーとインデックスに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key   キー
     * @param index インデックス
     * @param label キーとインデックスに対応するプロパティのラベル
     */
    public void setLabel(String key, int index, String label) {
        EmuPropertyPanel p = getProperty(key, index);
        p.setLabel(label);
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの説明を取得します。
     * キーとインデックスに対応するプロパティが存在しない場合は新たに作成した後に取得します。
     *
     * @param key   キー
     * @param index インデックス
     * @return キーとインデックスに対するプロパティの説明
     */
    public String getDescription(String key, int index) {
        EmuPropertyPanel p = getProperty(key, index);
        return p.getLabel();
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの説明を設定します。
     * キーとインデックスに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key   キー
     * @param index インデックス
     * @param desc キーとインデックスに対応するプロパティの説明
     */
    public void setDescription(String key, int index, String desc) {
        EmuPropertyPanel p = getProperty(key, index);
        p.setDescription(desc);
    }

    /**
     * 指定されたキー（複数も可能）とインデックスに対応するプロパティの設定用 GUI を作成します。
     *
     * @param keys  キーのリスト
     * @param index インデックス
     * @param title 設定用 GUI パネルのタイトル
     * @return 設定用 GUI パネル
     */
    public JPanel createPanel(List<String> keys, int index, String title) {
        JPanel panel = new JPanel(true);
        GridBagLayout layout = new GridBagLayout();

        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setLayout(layout);

        Iterator<String> it = keys.iterator();
        for (int i = 0; it.hasNext(); i++) {
            EmuPropertyPanel p = getProperty(it.next(), index);

            GridBagLayoutHelper.add(panel, layout, p.getComponent(),
                    1, i, 1, 1);
        }

        return panel;
    }
}
