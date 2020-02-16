package net.katsuster.ememu.ui;

public interface Configurable {
    /**
     * プロパティを初期化します。
     *
     * @param m プロパティのマップ
     */
    public void initProperties(EmuPropertyMap m);

    /**
     * プロパティを取得します。
     *
     * @return プロパティのマップ
     */
    public EmuPropertyMap getProperties();

    /**
     * プロパティを設定します。
     *
     * @param m プロパティのマップ
     */
    public void setProperties(EmuPropertyMap m);
}
