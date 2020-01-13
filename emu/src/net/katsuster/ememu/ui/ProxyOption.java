package net.katsuster.ememu.ui;

import static net.katsuster.ememu.ui.PropertyPanel.*;

/**
 * プロキシの設定用のプロパティ。
 */
public class ProxyOption extends PropertyPanelMap {
    public static final String PROXY_ENABLE = "proxy.enable";
    /** プロキシの URI */
    public static final String PROXY_HOST = "proxy.host";
    /** プロキシのポート番号 */
    public static final String PROXY_PORT = "proxy.port";

    public ProxyOption()  {
        addPropertyPanels(this);
    }

    public static void addPropertyPanels(PropertyPanelMap p) {
        p.setProperty(PROXY_ENABLE, "Enable proxy configuration", TYPE_BOOLEAN, "false");
        p.setProperty(PROXY_HOST, "Host", TYPE_STRING, "");
        p.setProperty(PROXY_PORT, "Port", TYPE_INT, "0");
    }

    /**
     * オプションの概要を文字列で取得します。
     *
     * @return オプションの概要
     */
    @Override
    public String toString() {
        return String.format("%s: \n" +
                        "  enable: '%s'\n" +
                        "  proxyHost: '%s'\n" +
                        "  proxyPort: '%s'",
                getClass().getSimpleName(),
                getValue(PROXY_ENABLE),
                getValue(PROXY_HOST),
                getValue(PROXY_PORT));
    }
}
