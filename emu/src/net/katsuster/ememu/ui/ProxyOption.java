package net.katsuster.ememu.ui;

import static net.katsuster.ememu.ui.EmuPropertyPanel.*;

/**
 * プロキシの設定用のプロパティ。
 */
public class ProxyOption extends EmuPropertyPanelMap
        implements Configurable {
    public static final String PROXY_ENABLE = "proxy.enable";
    /** プロキシの URI */
    public static final String PROXY_HOST = "proxy.host";
    /** プロキシのポート番号 */
    public static final String PROXY_PORT = "proxy.port";

    private int index;

    public ProxyOption()  {
        index = 0;

        initProperties(this);
    }

    @Override
    public void initProperties(EmuPropertyMap p) {
        int index = 0;

        p.setProperty(PROXY_ENABLE, index, "Enable proxy configuration", TYPE_BOOLEAN, "false");
        p.setProperty(PROXY_HOST, index, "Host", TYPE_STRING, "");
        p.setProperty(PROXY_PORT, index, "Port", TYPE_INT, "0");
    }

    @Override
    public EmuPropertyMap getProperties() {
        return null;
    }

    @Override
    public void setProperties(EmuPropertyMap m) {

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
                getValue(PROXY_ENABLE, index),
                getValue(PROXY_HOST, index),
                getValue(PROXY_PORT, index));
    }
}
