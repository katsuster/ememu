package net.katsuster.ememu.ui;

import java.awt.*;
import java.net.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class PropertyPanels {
    private Map<String, PropertyPanel> props;

    public PropertyPanels() {
        props = new HashMap<>();
    }

    public Map<String, PropertyPanel> getProperties() {
        return props;
    }

    /**
     * 指定されたキーに対応するプロパティを取得します。
     * キーに対応するプロパティが存在しない場合は新たに作成します。
     *
     * @param key キー
     * @return キーに対応するプロパティ
     */
    public PropertyPanel getProperty(String key) {
        return getProperty(key, "");
    }

    /**
     * 指定されたキーに対応するプロパティを取得します。
     * キーに対応するプロパティが存在しない場合は新たに作成します。
     *
     * @param key キー
     * @param def デフォルト値
     * @return キーに対応するプロパティ
     */
    public PropertyPanel getProperty(String key, String def) {
        if (!props.containsKey(key)) {
            PropertyPanel p = new PropertyPanel();
            p.setLabel(key);
            p.setType("String");
            p.setValue(def);
            props.put(key, p);
        }

        return props.get(key);
    }

    /**
     * 指定されたキーに対応するプロパティを設定し、以前に設定されていた値を返します。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key キー
     * @param val キーに対応するプロパティ
     * @return 以前のプロパティ
     */
    public PropertyPanel setProperty(String key, PropertyPanel val) {
        PropertyPanel before = getProperty(key);
        props.put(key, val);
        return before;
    }

    /**
     * 指定されたキーに対応するプロパティを設定し、以前に設定されていた値を返します。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key   キー
     * @param label キーに対応するプロパティのラベル
     * @param type  キーに対応するプロパティの型名
     * @param val   キーに対応するプロパティの値
     * @return 以前のプロパティ
     */
    public PropertyPanel setProperty(String key, String label, String type, String val) {
        PropertyPanel before = getProperty(key);
        props.put(key, new PropertyPanel(label, type, val));
        return before;
    }

    /**
     * 指定されたキーに対応するプロパティのラベルを取得します。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に取得します。
     *
     * @param key キー
     * @return キーに対するプロパティのラベル
     */
    public String getLabel(String key) {
        PropertyPanel p = getProperty(key);
        return p.getLabel();
    }

    /**
     * 指定されたキーに対応するプロパティのラベルを設定し、以前に設定されていた値を返します。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key   キー
     * @param label キーに対応するプロパティのラベル
     * @return 以前のプロパティのラベル
     */
    public String setLabel(String key, String label) {
        PropertyPanel p = getProperty(key);
        String before = p.getLabel();
        p.setLabel(label);
        return before;
    }

    /**
     * 指定されたキーに対応するプロパティの型名を取得します。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に取得します。
     *
     * @param key キー
     * @return キーに対するプロパティの型名
     */
    public String getType(String key) {
        PropertyPanel p = getProperty(key);
        return p.getType();
    }

    /**
     * 指定されたキーに対応するプロパティの型名を設定し、以前に設定されていた値を返します。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key  キー
     * @param type キーに対応するプロパティの型名
     * @return 以前のプロパティの型名
     */
    public String setType(String key, String type) {
        PropertyPanel p = getProperty(key);
        String before = p.getType();
        p.setType(type);
        return before;
    }

    /**
     * 指定されたキーに対応するプロパティの値を取得します。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に取得します。
     *
     * @param key キー
     * @return キーに対するプロパティの値
     */
    public String getValue(String key) {
        PropertyPanel p = getProperty(key);
        return p.getValue();
    }

    /**
     * 指定されたキーに対応するプロパティの値を設定し、以前に設定されていた値を返します。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key キー
     * @param val キーに対応するプロパティの値
     * @return 以前のプロパティの値
     */
    public String setValue(String key, String val) {
        PropertyPanel p = getProperty(key);
        String before = p.getValue();
        p.setValue(val);
        return before;
    }

    public JPanel createPanel(List<String> keys, String title) {
        JPanel panel = new JPanel(true);
        GridBagLayout layout = new GridBagLayout();

        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setLayout(layout);

        Iterator<String> it = keys.iterator();
        for (int i = 0; it.hasNext(); i++) {
            PropertyPanel p = getProperty(it.next());

            GridBagLayoutHelper.add(panel, layout, p.getComponent(),
                    1, i, 1, 1);
        }

        return panel;
    }

    /**
     * 文字列を URI に変換します。変換に失敗した場合は空の URI を返します。
     * 空の URI の生成に失敗した場合は null を返します。
     *
     * @param str 文字列
     * @return 文字列を URI に変換した結果、もしくは null
     */
    protected URI toURI(String str) {
        try {
            URI emptyURI = new URI("");

            try {
                return new URI(str);
            } catch (URISyntaxException ex) {
                return emptyURI;
            }
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
