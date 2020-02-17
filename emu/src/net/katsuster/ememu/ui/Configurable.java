package net.katsuster.ememu.ui;

public interface Configurable {
    /**
     * プロパティを初期化します。
     *
     * @param p プロパティのマップ
     */
    public void initProperties(EmuPropertyMap p);

    /**
     * プロパティを取得します。
     *
     * @return プロパティのマップ
     */
    public EmuPropertyMap getProperties();

    /**
     * プロパティを設定します。
     *
     * @param p プロパティのマップ
     */
    public void setProperties(EmuPropertyMap p);
}
