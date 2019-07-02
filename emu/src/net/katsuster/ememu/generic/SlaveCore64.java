package net.katsuster.ememu.generic;

/**
 * 64 ビットアドレスバスのスレーブコア。
 *
 * <p>
 * 自身のタイミングで動作します。
 * マスターバスを持ち、バスからの読み取り、
 * 書き込み要求に応答する形でも動作します。
 * </p>
 *
 * <p>
 * バスからのアクセス時に用いるアドレスは 64 ビット幅です。
 * </p>
 */
public abstract class SlaveCore64 extends AbstractCore
        implements BusSlave64 {
    private Bus64 masterBus;

    /**
     * このスレーブコアが接続されているバスを取得します。
     *
     * @return コアが接続されているバス
     */
    public Bus64 getMasterBus() {
        return masterBus;
    }

    /**
     * このスレーブコアを接続するバスを設定します。
     *
     * @param bus コアを接続するバス
     */
    public void setMasterBus(Bus64 bus) {
        masterBus = bus;
    }

    @Override
    public boolean tryRead(BusMaster64 m, long addr, int len) {
        return tryAccess(m, addr, len);
    }

    @Override
    public boolean tryWrite(BusMaster64 m, long addr, int len) {
        return tryAccess(m, addr, len);
    }

    /**
     * 指定されたアドレスからの読み書きが可能かどうかを判定します。
     *
     * @param addr アドレス
     * @param len  データのサイズ
     * @return 読み書きが可能な場合は true、不可能な場合は false
     */
    public abstract boolean tryAccess(BusMaster64 m, long addr, int len);

    @Override
    public byte read8(BusMaster64 m, long addr) {
        throw new IllegalArgumentException(String.format(
                "Cannot read 8bit from 0x%08x.", addr));
    }

    @Override
    public short read16(BusMaster64 m, long addr) {
        throw new IllegalArgumentException(String.format(
                "Cannot read 16bit from 0x%08x.", addr));
    }

    @Override
    public int read32(BusMaster64 m, long addr) {
        throw new IllegalArgumentException(String.format(
                "Cannot read 32bit from 0x%08x.", addr));
    }

    @Override
    public long read64(BusMaster64 m, long addr) {
        throw new IllegalArgumentException(String.format(
                "Cannot read 64bit from 0x%08x.", addr));
    }

    /**
     * アラインメントされていないアドレスから指定された長さの値を取得します。
     *
     * @param addr アドレス（バイト単位）
     * @param len  値の長さ（ビット単位）
     * @return 値
     */
    public long read_ua(BusMaster64 m, long addr, int len) {
        long v = 0;

        for (int i = 0; i < len; i += 8) {
            v |= (long)(read8(m, addr + (i >>> 3)) & 0xff) << i;
        }

        return v;
    }

    @Override
    public short read_ua16(BusMaster64 m, long addr) {
        return (short)read_ua(m, addr, 16);
    }

    @Override
    public int read_ua32(BusMaster64 m, long addr) {
        return (int)read_ua(m, addr, 32);
    }

    @Override
    public long read_ua64(BusMaster64 m, long addr) {
        return read_ua(m, addr, 64);
    }

    @Override
    public void write8(BusMaster64 m, long addr, byte data) {
        throw new IllegalArgumentException(String.format(
                "Cannot write 8bit to 0x%08x.", addr));
    }

    @Override
    public void write16(BusMaster64 m, long addr, short data) {
        throw new IllegalArgumentException(String.format(
                "Cannot write 16bit to 0x%08x.", addr));
    }

    @Override
    public void write32(BusMaster64 m, long addr, int data) {
        throw new IllegalArgumentException(String.format(
                "Cannot write 32bit to 0x%08x.", addr));
    }

    @Override
    public void write64(BusMaster64 m, long addr, long data) {
        throw new IllegalArgumentException(String.format(
                "Cannot write 64bit to 0x%08x.", addr));
    }

    /**
     * アラインメントされていないアドレスに指定された長さの値を設定します。
     *
     * @param addr アドレス（バイト単位）
     * @param data 書き込むデータ
     * @param len  値の長さ（ビット単位）
     */
    public void write_ua(BusMaster64 m, long addr, long data, int len) {
        for (int i = 0; i < len; i += 8) {
            write8(m, addr + (i >>> 3),  (byte)(data >>> i));
        }
    }

    @Override
    public void write_ua16(BusMaster64 m, long addr, short data) {
        write_ua(m, addr, data, 16);
    }

    @Override
    public void write_ua32(BusMaster64 m, long addr, int data) {
        write_ua(m, addr, data, 32);
    }

    @Override
    public void write_ua64(BusMaster64 m, long addr, long data) {
        write_ua(m, addr, data, 64);
    }
}
