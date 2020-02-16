package net.katsuster.ememu.ui;

import java.net.*;
import java.util.*;

/**
 * エミュレータのプロパティ。
 *
 * プロパティはラベル、値を保持します。
 */
public class EmuProperty {
    public static final String TYPE_BOOLEAN = "Boolean";
    public static final String TYPE_INT = "Int";
    public static final String TYPE_URI = "URI";
    public static final String TYPE_STRING = "String";

    private String type;
    private String value;
    private Map<String, String[]> attrs;

    public EmuProperty() {
        this(TYPE_STRING, "");
    }

    public EmuProperty(String type, String value) {
        this.type = type;
        this.value = value;
        this.attrs = new HashMap<>();
    }

    /**
     * プロパティの値の型を取得します。
     *
     * @return プロパティの値の型
     */
    public String getType() {
        return type;
    }

    /**
     * プロパティの値の型を設定します。
     *
     * @param val プロパティの値の型
     */
    public void setType(String val) {
        type = val;
    }

    /**
     * プロパティの値を取得します。
     *
     * @return プロパティの値
     */
    public String getValue() {
        updateForRead();
        return getRawValue();
    }

    /**
     * プロパティの値を設定します。
     *
     * @param val プロパティの値
     */
    public void setValue(String val) {
        setRawValue(val);
        updateForWrite();
    }

    /**
     * プロパティの取得に備えて、更新処理を行います。
     */
    protected void updateForRead() {
        //do nothing
    }

    /**
     * プロパティの設定に応じて、更新処理を行います。
     */
    protected void updateForWrite() {
        //do nothing
    }

    /**
     * プロパティの値を変換せず取得します。
     *
     * @return プロパティの値
     */
    protected String getRawValue() {
        return value;
    }

    /**
     * プロパティの値を変換せず設定します。
     *
     * @param val プロパティの値
     */
    protected void setRawValue(String val) {
        value = val;
    }

    /**
     * プロパティの値を boolean として取得します。
     * "true"（大文字と小文字は区別しない）以外の値の場合 false とみなします。
     *
     * @return プロパティの boolean 値
     */
    public boolean getAsBoolean() {
        return Boolean.parseBoolean(getValue());
    }

    /**
     * プロパティの値として、boolean を設定します。
     * "true"（大文字と小文字は区別しない）以外の値の場合 false とみなします。
     *
     * @param val プロパティの boolean 値として解釈する文字列
     */
    public void setAsBoolean(String val) {
        value = Boolean.valueOf(getValue()).toString();
    }

    /**
     * プロパティの値を int として取得します。
     * int への変換に失敗した場合は 0 を返します。
     *
     * @return プロパティの int 値
     */
    public int getAsInteger() {
        try {
            return Integer.parseInt(getValue());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * プロパティの値として、int を設定します。
     * int への変換に失敗した場合は 0 とみなします。
     *
     * @param val プロパティの int 値として解釈する文字列
     */
    public void setAsInteger(String val) {
        try {
            int i = Integer.parseInt(val);
            setValue(Integer.toString(i));
        } catch (NumberFormatException ex) {
            setValue("0");
        }
    }

    /**
     * プロパティの値を URI として取得します。
     * URI への変換に失敗した場合は空の URI を返し、
     * 空の URI の生成にも失敗した場合は null を返します。
     *
     * @return プロパティの URI、もしくは null
     */
    public URI getAsURI() {
        try {
            URI emptyURI = new URI("");

            try {
                return new URI(getValue());
            } catch (URISyntaxException ex) {
                return emptyURI;
            }
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * プロパティの値として、URI を設定します。
     * URI として解釈できない文字列を渡したときは空文字列とみなします。
     *
     * @param uri プロパティの URI の文字列表現
     */
    public void setAsURI(String uri) {
        try {
            URI url = new URI(uri);
            setValue(url.toString());
        } catch (URISyntaxException ex) {
            setValue("");
        }
    }

    /**
     * プロパティの付加情報を取得します。
     * キーに対応する付加情報が存在しない場合は新たに作成します。
     *
     * @param name 付加情報の名前
     * @return プロパティの付加情報
     */
    public String[] getAttribute(String name) {
        if (!attrs.containsKey(name)) {
            String[] empty = new String[1];
            empty[0] = "";
            attrs.put(name, empty);
        }

        return attrs.get(name);
    }

    /**
     * プロパティの付加情報を設定します。
     *
     * @param name 付加情報の名前
     * @param val  プロパティの付加情報
     */
    public void setAttribute(String name, String... val) {
        attrs.put(name, val);
    }
}
