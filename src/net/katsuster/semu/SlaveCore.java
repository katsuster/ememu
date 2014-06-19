package net.katsuster.semu;

/**
 * バスのスレーブコア。
 *
 * バスからの読み取り、書き込み要求に応答する形で動作します。
 *
 * @author katsuhiro
 */
public interface SlaveCore<T extends ByteSeq> {
    /**
     * このスレーブコアが接続されているバスを取得します。
     *
     * @return コアが接続されているバス
     */
    public Bus<T> getMasterBus();

    /**
     * このスレーブコアを接続するバスを設定します。
     *
     * @param bus コアを接続するバス
     */
    public void setMasterBus(Bus<T> bus);

    /**
     * 指定されたアドレスからの読み取りが可能かどうかを判定します。
     *
     * @param addr アドレス
     * @return 読み取りが可能な場合は true、不可能な場合は false
     */
    public boolean tryRead(long addr);

    /**
     * 指定されたアドレスからデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public T read(long addr);

    /**
     * 指定されたアドレスへの書き込みが可能かどうかを判定します。
     *
     * @param addr アドレス
     * @return 書き込みが可能な場合は true、不可能な場合は false
     */
    public boolean tryWrite(long addr);

    /**
     * 指定したアドレスへデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public void write(long addr, T data);
}
