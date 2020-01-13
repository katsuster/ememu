package net.katsuster.ememu.ui;

import java.awt.*;
import java.net.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * プロパティのマップ。
 *
 * プロパティは固有のキーにより参照されます。
 */
public class PropertyPanelMap {
    private Map<String, PropertyPanel> props;

    public PropertyPanelMap() {
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
     * 指定されたキーに対応するプロパティを設定します。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key キー
     * @param val キーに対応するプロパティ
     */
    public void setProperty(String key, PropertyPanel val) {
        props.put(key, val);
    }

    /**
     * 指定されたキーに対応するプロパティを設定します。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key   キー
     * @param label キーに対応するプロパティのラベル
     * @param type  キーに対応するプロパティの型名
     * @param val   キーに対応するプロパティの値
     */
    public void setProperty(String key, String label, String type, String val) {
        props.put(key, new PropertyPanel(label, type, val));
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
     * 指定されたキーに対応するプロパティのラベルを設定します。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key   キー
     * @param label キーに対応するプロパティのラベル
     */
    public void setLabel(String key, String label) {
        PropertyPanel p = getProperty(key);
        p.setLabel(label);
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
     * 指定されたキーに対応するプロパティの型名を設定します。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key  キー
     * @param type キーに対応するプロパティの型名
     */
    public void setType(String key, String type) {
        PropertyPanel p = getProperty(key);
        p.setType(type);
    }

    /**
     * 指定されたキーに対応するプロパティの付加情報を取得します。
     * キーに対応する付加情報が存在しない場合は新たに作成します。
     *
     * @param key  キー
     * @param name 付加情報の名前
     * @return キーに対応するプロパティの付加情報
     */
    public String[] getAttribute(String key, String name) {
        return getProperty(key).getAttribute(name);
    }

    /**
     * 指定されたキーに対応するプロパティの付加情報を設定します。
     *
     * @param key  キー
     * @param name 付加情報の名前
     * @param val  キーに対応するプロパティの付加情報
     */
    public void setAttribute(String key, String name, String... val) {
        getProperty(key).setAttribute(name, val);
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
     * 指定されたキーに対応するプロパティの値を設定します。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key キー
     * @param val キーに対応するプロパティの値
     */
    public void setValue(String key, String val) {
        PropertyPanel p = getProperty(key);
        p.setValue(val);
    }

    /**
     * 指定されたキーに対応するプロパティの値を boolean として取得します。
     * "true"（大文字と小文字は区別しない）以外の値の場合 false とみなします。
     *
     * @param key キー
     * @return キーに対応するプロパティの boolean 値
     */
    public boolean getAsBoolean(String key) {
        return getProperty(key).getAsBoolean();
    }

    /**
     * 指定されたキーに対応するプロパティの値として、boolean を設定します。
     *
     * @param key キー
     * @param val キーに対応するプロパティの boolean 値
     */
    public void setAsBoolean(String key, boolean val) {
        getProperty(key).setValue(Boolean.toString(val));
    }

    /**
     * 指定されたキーに対応するプロパティの値を int として取得します。
     * int への変換に失敗した場合は 0 を返します。
     *
     * @param key キー
     * @return キーに対応するプロパティの int 値
     */
    public int getAsInteger(String key) {
        return getProperty(key).getAsInteger();
    }

    /**
     * 指定されたキーに対応するプロパティの値として、int を設定します。
     *
     * @param key キー
     * @param val キーに対応するプロパティの int 値
     */
    public void setAsInteger(String key, int val) {
        getProperty(key).setValue(Integer.toString(val));
    }

    /**
     * 指定されたキーに対応するプロパティの値を URI として取得します。
     *
     * URI への変換に失敗した場合は空の URI を返し、
     * 空の URI の生成にも失敗した場合は null を返します。
     *
     * @param key キー
     * @return キーに対応するプロパティの URI、もしくは null
     */
    public URI getAsURI(String key) {
        return getProperty(key).getAsURI();
    }

    /**
     * 指定されたキーに対応するプロパティの値として、URI を設定します。
     *
     * @param key キー
     * @param uri キーに対応するプロパティの URI
     */
    public void setAsURI(String key, URI uri) {
        getProperty(key).setValue(uri.toString());
    }

    /**
     * 指定されたキーに対応するプロパティの値として、URI を設定します。
     * URI として解釈できない文字列を渡したときは空文字列と見なします。
     *
     * @param key キー
     * @param uri キーに対応するプロパティの URI の文字列表現
     */
    public void setAsURI(String key, String uri) {
        getProperty(key).setAsURI(uri);
    }

    /**
     * 指定されたキー（複数も可能）に対応するプロパティの設定用 GUI を作成します。
     *
     * @param keys キーのリスト
     * @param title 設定用 GUI パネルのタイトル
     * @return 設定用 GUI パネル
     */
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
