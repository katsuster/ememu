package net.katsuster.ememu.generic.bus;

/**
 * バススレーブのインタフェース（64bit アドレス）
 *
 * バスからの Read/Write 要求を受け取れるコアを表します。
 */
public interface BusSlave64 {
    /**
     * 指定されたアドレスからの読み取りが可能かどうかを判定します。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @param len  読み取るデータのサイズ
     * @return 読み取りが可能な場合は true、不可能な場合は false
     */
    public abstract boolean tryRead(BusMaster64 m, long addr, int len);

    /**
     * 指定されたアドレスから 8 ビットのデータを読み取ります。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @return データ
     */
    public abstract byte read8(BusMaster64 m, long addr);

    /**
     * 指定されたアドレスから 16 ビットのデータを読み取ります。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @return データ
     */
    public abstract short read16(BusMaster64 m, long addr);

    /**
     * 指定されたアドレスから 32 ビットのデータを読み取ります。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @return データ
     */
    public abstract int read32(BusMaster64 m, long addr);

    /**
     * 指定されたアドレスから 64 ビットのデータを読み取ります。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @return データ
     */
    public abstract long read64(BusMaster64 m, long addr);

    /**
     * 指定されたアドレスから 16 ビットのデータを読み取ります。
     * アドレスは 16ビット境界でなくても構いません。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @return データ
     */
    public abstract short read_ua16(BusMaster64 m, long addr);

    /**
     * 指定されたアドレスから 32 ビットのデータを読み取ります。
     * アドレスは 32ビット境界でなくても構いません。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @return データ
     */
    public abstract int read_ua32(BusMaster64 m, long addr);

    /**
     * 指定されたアドレスから 64 ビットのデータを読み取ります。
     * アドレスは 64ビット境界でなくても構いません。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @return データ
     */
    public abstract long read_ua64(BusMaster64 m, long addr);

    /**
     * 指定されたアドレスへの書き込みが可能かどうかを判定します。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @param len  書き込むデータのサイズ
     * @return 書き込みが可能な場合は true、不可能な場合は false
     */
    public abstract boolean tryWrite(BusMaster64 m, long addr, int len);

    /**
     * 指定したアドレスへ 8 ビットのデータを書き込みます。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write8(BusMaster64 m, long addr, byte data);

    /**
     * 指定したアドレスへ 16 ビットのデータを書き込みます。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write16(BusMaster64 m, long addr, short data);

    /**
     * 指定したアドレスへ 32 ビットのデータを書き込みます。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write32(BusMaster64 m, long addr, int data);

    /**
     * 指定したアドレスへ 64 ビットのデータを書き込みます。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write64(BusMaster64 m, long addr, long data);

    /**
     * 指定したアドレスへ 16 ビットのデータを書き込みます。
     * アドレスは 16ビット境界でなくても構いません。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write_ua16(BusMaster64 m, long addr, short data);

    /**
     * 指定したアドレスへ 32 ビットのデータを書き込みます。
     * アドレスは 32ビット境界でなくても構いません。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write_ua32(BusMaster64 m, long addr, int data);

    /**
     * 指定したアドレスへ 64 ビットのデータを書き込みます。
     * アドレスは 64ビット境界でなくても構いません。
     *
     * @param m    要求を出したマスター
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write_ua64(BusMaster64 m, long addr, long data);
}
