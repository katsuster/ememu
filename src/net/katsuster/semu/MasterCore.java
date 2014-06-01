package net.katsuster.semu;

/**
 * バスのマスターコア。
 *
 * @author katsuhiro
 */
public interface MasterCore<T extends ByteSeq> {
    /**
     * コアが接続されているスレーブバスを取得します。
     *
     * @return スレーブバス
     */
    public Bus<T> getSlaveBus();

    /**
     * スレーブバスにコアを接続します。
     *
     * @param bus スレーブバス
     */
    public void setSlaveBus(Bus<T> bus);

    /**
     * バス幅を取得します。
     *
     * @return バス幅（ビット単位）
     */
    public int getBusBits();
}
