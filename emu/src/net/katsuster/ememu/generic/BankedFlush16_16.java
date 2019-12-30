package net.katsuster.ememu.generic;

import net.katsuster.ememu.generic.bus.BusMaster64;
import net.katsuster.ememu.generic.core.SlaveCore64;

/**
 * 64 ビットアドレス、32ビットデータ、バンク付き Flush メモリ
 *
 * 16ビットデータメモリ＋16ビットデータメモリの構成で使用します。
 */
public class BankedFlush16_16 extends SlaveCore64 {
    //データ幅（バイト単位）
    public static final int LEN_WORD = 4;
    //データ幅（ビット単位）
    public static final int LEN_WORD_BITS = LEN_WORD * 8;

    private Flush16 bank0;
    private Flush16 bank1;
    private int size;

    /**
     * バンク付き Flush メモリを作成します。
     *
     * バンク 0 のデータが LSB 側に、
     * バンク 1 のデータが MSB 側に詰められます。
     *
     * @param bank0 バンク 0 メモリ
     * @param bank1 バンク 1 メモリ
     */
    public BankedFlush16_16(Flush16 bank0, Flush16 bank1) {
        this.bank0 = bank0;
        this.bank1 = bank1;
        this.size = bank0.getSize() + bank1.getSize();
    }

    /**
     * メモリのサイズを取得します。
     *
     * @return メモリのサイズ（バイト単位）
     */
    public int getSize() {
        return size;
    }

    /**
     * 指定したアドレスが正当かどうか検査します。
     *
     * 下記の条件を満たすアドレスを正当と見なします。
     *
     * アドレスのアライメントを満たしていること、
     * Integer.MAX_VALUE を超えないこと。
     *
     * @param addr アドレス
     */
    protected void checkAddress(long addr) {
        if (addr % LEN_WORD != 0) {
            throw new IllegalArgumentException(String.format(
                    "addr(0x%08x) is not aligned %d.", addr, LEN_WORD));
        }
        if (addr / LEN_WORD > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(String.format(
                    "addr(0x%08x) is too large.", addr));
        }
    }

    @Override
    public boolean tryAccess(BusMaster64 m, long addr, int len) {
        int wordAddr;

        wordAddr = (int)(addr / LEN_WORD);
        len = size / LEN_WORD;

        return len > wordAddr;
    }

    @Override
    public byte read8(BusMaster64 m, long addr) {
        long v = readWord(m, addr) & 0xffffffffL;

        return (byte)BitOp.readMasked(addr, v, LEN_WORD_BITS, 8);
    }

    @Override
    public short read16(BusMaster64 m, long addr) {
        long v = readWord(m, addr) & 0xffffffffL;

        return (short)BitOp.readMasked(addr, v, LEN_WORD_BITS, 16);
    }

    @Override
    public int read32(BusMaster64 m, long addr) {
        return readWord(m, addr);
    }

    @Override
    public void write8(BusMaster64 m, long addr, byte data) {
        long v = readWord(m, addr) & 0xffffffffL;
        int w = (int)BitOp.writeMasked(addr, v, data, LEN_WORD_BITS, 8);

        writeWord(m, addr, w);
    }

    @Override
    public void write16(BusMaster64 m, long addr, short data) {
        long v = readWord(m, addr) & 0xffffffffL;
        int w = (int)BitOp.writeMasked(addr, v, data, LEN_WORD_BITS, 16);

        writeWord(m, addr, w);
    }

    @Override
    public void write32(BusMaster64 m, long addr, int data) {
        writeWord(m, addr, data);
    }

    public int readWord(BusMaster64 m, long addr) {
        long addrBank = addr >>> 1;
        int data0, data1;
        int data;

        data0 = bank0.read16(m, addrBank) & 0xffff;
        data1 = bank1.read16(m, addrBank) & 0xffff;
        data = (data0 << 0) | (data1 << 16);

        return data;
    }

    public void writeWord(BusMaster64 m, long addr, int data) {
        long addrBank = addr >>> 1;
        short data0, data1;

        data0 = (short)(data >>> 0);
        data1 = (short)(data >>> 16);

        bank0.write16(m, addrBank, data0);
        bank1.write16(m, addrBank, data1);
    }

    @Override
    public void run() {
        //do nothing
    }
}
