package net.katsuster.ememu.ui;

import java.net.*;
import java.util.*;

/**
 * エミュレータのプロパティのマップ。
 *
 * プロパティは固有のキーとインデックスにより参照されます。
 */
public class EmuPropertyMap {
    private Map<String, EmuProperty> props;

    public EmuPropertyMap() {
        props = new HashMap<>();
    }

    /**
     * マップを取得します。
     *
     * @return キー＋インデックス、エミュレータのプロパティのマップ
     */
    public Map<String, EmuProperty> getMap() {
        return props;
    }

    /**
     * キーとインデックスから、プロパティを識別する文字列を作成します。
     *
     * プロパティを識別する文字列は、キーの後ろにドット "." と文字列化した数値を加えたものです。
     * 例えば、キーが "abcd.efg" でインデックスが 1 の場合は "abcd.efg.1" となります。
     *
     * @param key   キー
     * @param index インデックス
     * @return プロパティ識別用の文字列
     */
    protected String keyWithIndex(String key, int index) {
        return key + "." + index;
    }

    /**
     * 指定されたキーに対応するプロパティを取得します。インデックスは 0 とみなします。
     * キーに対応するプロパティが存在しない場合は新たに作成します。
     *
     * @param key キー
     * @return キーに対応するプロパティ
     */
    public EmuProperty getProperty(String key) {
        return getProperty(key, 0, "");
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティを取得します。
     * キーとインデックスに対応するプロパティが存在しない場合は新たに作成します。
     *
     * @param key   キー
     * @param index インデックス
     * @return キーとインデックスに対応するプロパティ
     */
    public EmuProperty getProperty(String key, int index) {
        return getProperty(key, index, "");
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティを取得します。
     * キーとインデックスに対応するプロパティが存在しない場合は新たに作成します。
     *
     * @param key   キー
     * @param index インデックス
     * @param def   デフォルト値
     * @return キーとインデックスに対応するプロパティ
     */
    public EmuProperty getProperty(String key, int index, String def) {
        String k = keyWithIndex(key, index);

        if (!props.containsKey(k)) {
            EmuProperty p = new EmuProperty();
            p.setType("String");
            p.setValue(def);
            props.put(k, p);
        }

        return props.get(k);
    }

    /**
     * 指定されたキーに対応するプロパティを設定します。インデックスは 0 とみなします。
     * キーに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key キー
     * @param val キーに対応するプロパティ
     */
    public void setProperty(String key, EmuProperty val) {
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
    public void setProperty(String key, int index, EmuProperty val) {
        String k = keyWithIndex(key, index);

        props.put(k, val);
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティを設定します。
     * キーとインデックスに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key   キー
     * @param index インデックス
     * @param label プロパティのラベル
     * @param type  プロパティの型名
     * @param val   プロパティの値
     */
    public void setProperty(String key, int index, String label, String type, String val) {
        setProperty(key, index, new EmuProperty(type, val));
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの型名を取得します。
     * キーとインデックスに対応するプロパティが存在しない場合は新たに作成した後に取得します。
     *
     * @param key   キー
     * @param index インデックス
     * @return キーとインデックスに対するプロパティの型名
     */
    public String getType(String key, int index) {
        EmuProperty p = getProperty(key, index);
        return p.getType();
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの型名を設定します。
     * キーとインデックスに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key   キー
     * @param index インデックス
     * @param type  キーとインデックスに対応するプロパティの型名
     */
    public void setType(String key, int index, String type) {
        EmuProperty p = getProperty(key, index);
        p.setType(type);
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの付加情報を取得します。
     * キーとインデックスに対応する付加情報が存在しない場合は新たに作成します。
     *
     * @param key   キー
     * @param index インデックス
     * @param name  付加情報の名前
     * @return キーとインデックスに対応するプロパティの付加情報
     */
    public String[] getAttribute(String key, int index, String name) {
        return getProperty(key, index).getAttribute(name);
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの付加情報を設定します。
     *
     * @param key   キー
     * @param index インデックス
     * @param name  付加情報の名前
     * @param val   キーとインデックスに対応するプロパティの付加情報
     */
    public void setAttribute(String key, int index, String name, String... val) {
        getProperty(key, index).setAttribute(name, val);
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの値を取得します。
     * キーとインデックスに対応するプロパティが存在しない場合は新たに作成した後に取得します。
     *
     * @param key   キー
     * @param index インデックス
     * @return キーとインデックスに対するプロパティの値
     */
    public String getValue(String key, int index) {
        EmuProperty p = getProperty(key, index);
        return p.getValue();
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの値を設定します。
     * キーとインデックスに対応するプロパティが存在しない場合は新たに作成した後に設定します。
     *
     * @param key   キー
     * @param index インデックス
     * @param val   キーとインデックスに対応するプロパティの値
     */
    public void setValue(String key, int index, String val) {
        EmuProperty p = getProperty(key, index);
        p.setValue(val);
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの値を boolean として取得します。
     * "true"（大文字と小文字は区別しない）以外の値の場合 false とみなします。
     *
     * @param key   キー
     * @param index インデックス
     * @return キーとインデックスに対応するプロパティの boolean 値
     */
    public boolean getAsBoolean(String key, int index) {
        return getProperty(key, index).getAsBoolean();
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの値として、boolean を設定します。
     *
     * @param key   キー
     * @param index インデックス
     * @param val   キーとインデックスに対応するプロパティの boolean 値
     */
    public void setAsBoolean(String key, int index, boolean val) {
        getProperty(key, index).setValue(Boolean.toString(val));
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの値を int として取得します。
     * int への変換に失敗した場合は 0 を返します。
     *
     * @param key   キー
     * @param index インデックス
     * @return キーとインデックスに対応するプロパティの int 値
     */
    public int getAsInteger(String key, int index) {
        return getProperty(key, index).getAsInteger();
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの値として、int を設定します。
     *
     * @param key   キー
     * @param index インデックス
     * @param val   キーとインデックスに対応するプロパティの int 値
     */
    public void setAsInteger(String key, int index, int val) {
        getProperty(key, index).setValue(Integer.toString(val));
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの値を URI として取得します。
     *
     * URI への変換に失敗した場合は空の URI を返し、
     * 空の URI の生成にも失敗した場合は null を返します。
     *
     * @param key   キー
     * @param index インデックス
     * @return キーとインデックスに対応するプロパティの URI、もしくは null
     */
    public URI getAsURI(String key, int index) {
        return getProperty(key, index).getAsURI();
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの値として、URI を設定します。
     *
     * @param key   キー
     * @param index インデックス
     * @param uri   キーとインデックスに対応するプロパティの URI
     */
    public void setAsURI(String key, int index, URI uri) {
        getProperty(key, index).setValue(uri.toString());
    }

    /**
     * 指定されたキーとインデックスに対応するプロパティの値として、URI を設定します。
     * URI として解釈できない文字列を渡したときは空文字列と見なします。
     *
     * @param key   キー
     * @param index インデックス
     * @param uri   キーとインデックスに対応するプロパティの URI の文字列表現
     */
    public void setAsURI(String key, int index, String uri) {
        getProperty(key, index).setAsURI(uri);
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
