package net.katsuster.ememu.generic;

import java.util.concurrent.locks.*;

/**
 * バスマスターのインタフェース（64bit アドレス）
 *
 * アドレス空間への Read/Write 要求を出せるコアを表します。
 */
public interface BusMaster64 {
    /**
     * コアが接続されているスレーブバスを取得します。
     *
     * @return スレーブバス
     */
    public abstract Bus64 getSlaveBus();

    /**
     * スレーブバスにコアを接続します。
     *
     * @param bus スレーブバス
     */
    public abstract void setSlaveBus(Bus64 bus);

    /**
     * スレーブバスの読み込みロックを取得します。
     *
     * @return 読み込みロック
     */
    public abstract Lock getReadLock();

    /**
     * スレーブバスの書き込み、読み込みロックを取得します。
     *
     * @return 書き込みロック
     */
    public abstract Lock getWriteLock();

    /**
     * 指定されたアドレスからの読み取りが可能かどうかを判定します。
     *
     * @param addr アドレス
     * @param len  読み取るデータのサイズ
     * @return 読み取りが可能な場合は true、不可能な場合は false
     */
    public abstract boolean tryRead(long addr, int len);

    /**
     * 指定されたアドレスから 8 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public abstract byte read8(long addr);

    /**
     * 指定されたアドレスから 16 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public abstract short read16(long addr);

    /**
     * 指定されたアドレスから 32 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public abstract int read32(long addr);

    /**
     * 指定されたアドレスから 64 ビットのデータを読み取ります。
     *
     * @param addr アドレス
     * @return データ
     */
    public abstract long read64(long addr);

    /**
     * 指定されたアドレスから 16 ビットのデータを読み取ります。
     * アドレスは 16ビット境界でなくても構いません。
     *
     * @param addr アドレス
     * @return データ
     */
    public abstract short read_ua16(long addr);

    /**
     * 指定されたアドレスから 32 ビットのデータを読み取ります。
     * アドレスは 32ビット境界でなくても構いません。
     *
     * @param addr アドレス
     * @return データ
     */
    public abstract int read_ua32(long addr);

    /**
     * 指定されたアドレスから 64 ビットのデータを読み取ります。
     * アドレスは 64ビット境界でなくても構いません。
     *
     * @param addr アドレス
     * @return データ
     */
    public abstract long read_ua64(long addr);

    /**
     * 指定されたアドレスへの書き込みが可能かどうかを判定します。
     *
     * @param addr アドレス
     * @param len  書き込むデータのサイズ
     * @return 書き込みが可能な場合は true、不可能な場合は false
     */
    public abstract boolean tryWrite(long addr, int len);

    /**
     * 指定したアドレスへ 8 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write8(long addr, byte data);

    /**
     * 指定したアドレスへ 16 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write16(long addr, short data);

    /**
     * 指定したアドレスへ 32 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write32(long addr, int data);

    /**
     * 指定したアドレスへ 64 ビットのデータを書き込みます。
     *
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write64(long addr, long data);

    /**
     * 指定したアドレスへ 16 ビットのデータを書き込みます。
     * アドレスは 16ビット境界でなくても構いません。
     *
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write_ua16(long addr, short data);

    /**
     * 指定したアドレスへ 32 ビットのデータを書き込みます。
     * アドレスは 32ビット境界でなくても構いません。
     *
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write_ua32(long addr, int data);

    /**
     * 指定したアドレスへ 64 ビットのデータを書き込みます。
     * アドレスは 64ビット境界でなくても構いません。
     *
     * @param addr アドレス
     * @param data データ
     */
    public abstract void write_ua64(long addr, long data);
}
