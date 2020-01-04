package net.katsuster.ememu.ui;

import java.net.*;

/**
 * プロキシの設定用のプロパティ。
 *
 * 有効なキーとプロパティの意味は下記のとおりです。
 *
 * proxy.host: プロキシの URL
 * proxy.port: プロキシのポート番号
 */
public class ProxyOption extends PropertyPanels {
    public static final String PROXY_ENABLE = "proxy.enable";
    public static final String PROXY_HOST = "proxy.host";
    public static final String PROXY_PORT = "proxy.port";

    public ProxyOption()  {
        setProperty(PROXY_ENABLE, "Enable proxy configuration", "Boolean", "false");
        setProperty(PROXY_HOST, "Host", "String", "");
        setProperty(PROXY_PORT, "Port", "String", "0");
    }

    /**
     * プロキシ設定が有効かどうか取得します。
     *
     * @return プロキシ設定が有効なら true、無効なら false
     */
    public boolean getProxyEnabled() {
        return Boolean.parseBoolean(getProperty(PROXY_ENABLE).getValue());
    }

    /**
     * プロキシ設定が有効かどうか設定します。
     *
     * @param ena プロキシ設定が有効なら true、無効なら false
     */
    public void setProxyEnabled(boolean ena) {
        setValue(PROXY_ENABLE, Boolean.toString(ena));
    }

    /**
     * プロキシのホスト名を取得します。
     *
     * @return プロキシのホストの URI
     */
    public URI getProxyHost() {
        return toURI(getProperty(PROXY_HOST).getValue());
    }

    /**
     * プロキシのホスト名を設定します。
     *
     * @param uri プロキシのホストの URI
     */
    public void setProxyHost(URI uri) {
        setValue(PROXY_HOST, uri.toString());
    }

    /**
     * プロキシのホスト名を設定します。
     *
     * URI 解釈できない文字列を渡したときは空文字列と見なします。
     *
     * @param h ホスト名の文字列表記
     */
    public void setProxyHost(String h) {
        try {
            setProxyHost(new URI(h));
        } catch (URISyntaxException ex) {
            setProxyHost("");
        }
    }

    /**
     * プロキシとの通信に使用するポート番号を取得します。
     *
     * @return ポート番号
     */
    public int getProxyPort() {
        try {
            return Integer.parseInt(getProperty(PROXY_PORT).getValue());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * プロキシとの通信に使用するポート番号を設定します。
     *
     * @param p ポート番号
     */
    public void setProxyPort(int p) {
        try {
            setValue(PROXY_PORT, Integer.toString(p));
        } catch (NumberFormatException ex) {
            setValue(PROXY_PORT, "0");
        }
    }

    /**
     * プロキシとの通信に使用するポート番号を設定します。
     *
     * 数値として解釈できない文字列を渡したときは 0 と見なします。
     *
     * @param p ポート番号の文字列表記
     */
    public void setProxyPort(String p) {
        setValue(PROXY_PORT, p);
    }

    /**
     * オプションの概要を文字列で取得します。
     *
     * @return オプションの概要
     */
    @Override
    public String toString() {
        return String.format("%s: \n" +
                        "  proxyHost: '%s'\n" +
                        "  proxyPort: '%s'",
                getClass().getSimpleName(),
                getProxyHost().toString(),
                getProxyPort());
    }
}
