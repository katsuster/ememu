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
public abstract class SlaveCore extends AbstractCore
        implements RWCore {
    private Bus masterBus;

    /**
     * このスレーブコアが接続されているバスを取得します。
     *
     * @return コアが接続されているバス
     */
    public Bus getMasterBus() {
        return masterBus;
    }

    /**
     * このスレーブコアを接続するバスを設定します。
     *
     * @param bus コアを接続するバス
     */
    public void setMasterBus(Bus bus) {
        masterBus = bus;
    }

    @Override
    public byte read8(long addr) {
        throw new IllegalArgumentException(String.format(
                "Cannot read 8bit from 0x%08x.", addr));
    }

    @Override
    public short read16(long addr) {
        throw new IllegalArgumentException(String.format(
                "Cannot read 16bit from 0x%08x.", addr));
    }

    @Override
    public int read32(long addr) {
        throw new IllegalArgumentException(String.format(
                "Cannot read 32bit from 0x%08x.", addr));
    }

    @Override
    public long read64(long addr) {
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
    public long read_ua(long addr, int len) {
        long v = 0;

        for (int i = 0; i < len; i += 8) {
            v |= (long)(read8(addr + (i >>> 3)) & 0xff) << i;
        }

        return v;
    }

    @Override
    public short read_ua16(long addr) {
        return (short)read_ua(addr, 16);
    }

    @Override
    public int read_ua32(long addr) {
        return (int)read_ua(addr, 32);
    }

    @Override
    public long read_ua64(long addr) {
        return read_ua(addr, 64);
    }

    @Override
    public void write8(long addr, byte data) {
        throw new IllegalArgumentException(String.format(
                "Cannot write 8bit to 0x%08x.", addr));
    }

    @Override
    public void write16(long addr, short data) {
        throw new IllegalArgumentException(String.format(
                "Cannot write 16bit to 0x%08x.", addr));
    }

    @Override
    public void write32(long addr, int data) {
        throw new IllegalArgumentException(String.format(
                "Cannot write 32bit to 0x%08x.", addr));
    }

    @Override
    public void write64(long addr, long data) {
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
    public void write_ua(long addr, long data, int len) {
        for (int i = 0; i < len; i += 8) {
            write8(addr + (i >>> 3),  (byte)(data >>> i));
        }
    }

    @Override
    public void write_ua16(long addr, short data) {
        write_ua(addr, data, 16);
    }

    @Override
    public void write_ua32(long addr, int data) {
        write_ua(addr, data, 32);
    }

    @Override
    public void write_ua64(long addr, long data) {
        write_ua(addr, data, 64);
    }
}
