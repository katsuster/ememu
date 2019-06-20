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
        throw new IllegalArgumentException("Cannot read 8bit.");
    }

    @Override
    public short read16(long addr) {
        throw new IllegalArgumentException("Cannot read 16bit.");
    }

    @Override
    public int read32(long addr) {
        throw new IllegalArgumentException("Cannot read 32bit.");
    }

    @Override
    public long read64(long addr) {
        throw new IllegalArgumentException("Cannot read 64bit.");
    }

    @Override
    public short read_ua16(long addr) {
        throw new IllegalArgumentException("Cannot unaligned read 16bit.");
    }

    @Override
    public int read_ua32(long addr) {
        throw new IllegalArgumentException("Cannot unaligned read 32bit.");
    }

    @Override
    public long read_ua64(long addr) {
        throw new IllegalArgumentException("Cannot unaligned read 64bit.");
    }

    @Override
    public void write8(long addr, byte data) {
        throw new IllegalArgumentException("Cannot write 8bit.");
    }

    @Override
    public void write16(long addr, short data) {
        throw new IllegalArgumentException("Cannot write 16bit.");
    }

    @Override
    public void write32(long addr, int data) {
        throw new IllegalArgumentException("Cannot write 32bit.");
    }

    @Override
    public void write64(long addr, long data) {
        throw new IllegalArgumentException("Cannot write 64bit.");
    }

    @Override
    public void write_ua16(long addr, short data) {
        throw new IllegalArgumentException("Cannot unaligned write 16bit.");
    }

    @Override
    public void write_ua32(long addr, int data) {
        throw new IllegalArgumentException("Cannot unaligned write 32bit.");
    }

    @Override
    public void write_ua64(long addr, long data) {
        throw new IllegalArgumentException("Cannot unaligned write 64bit.");
    }
}
