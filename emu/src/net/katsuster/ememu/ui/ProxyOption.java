package net.katsuster.ememu.ui;

import java.net.*;

/**
 * プロキシの設定オプション。
 *
 * @author katsuhiro
 */
public class ProxyOption {
    private URI proxyHost;
    private int proxyPort;

    public ProxyOption() {
        try {
            proxyHost = new URI("");
            proxyPort = 0;
        } catch (URISyntaxException ex) {
            //ignored
        }
    }

    /**
     * プロキシのホスト名を取得します。
     *
     * @return プロキシのホストの URI
     */
    public URI getProxyHost() {
        return proxyHost;
    }

    /**
     * プロキシのホスト名を設定します。
     *
     * @param uri プロキシのホストの URI
     */
    public void setProxyHost(URI uri) {
        proxyHost = uri;
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
            URI emptyURI = new URI("");

            try {
                setProxyHost(new URI(h));
            } catch (URISyntaxException ex) {
                setProxyHost(emptyURI);
            }
        } catch (URISyntaxException ex) {
            //ignore
        }
    }

    /**
     * プロキシとの通信に使用するポート番号を取得します。
     *
     * @return ポート番号
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * プロキシとの通信に使用するポート番号を設定します。
     *
     * @param p ポート番号
     */
    public void setProxyPort(int p) {
        proxyPort = p;
    }

    /**
     * プロキシとの通信に使用するポート番号を設定します。
     *
     * 数値として解釈できない文字列を渡したときは 0 と見なします。
     *
     * @param p ポート番号の文字列表記
     */
    public void setProxyPort(String p) {
        try {
            setProxyPort(Integer.valueOf(p));
        } catch (NumberFormatException ex) {
            setProxyPort(0);
        }
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
                Integer.toString(getProxyPort()));
    }
}
