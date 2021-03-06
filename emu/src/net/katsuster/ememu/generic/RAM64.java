package net.katsuster.ememu.generic;

import net.katsuster.ememu.generic.bus.BusMaster64;

/**
 * 64 ビットアドレス、64 ビットデータ RAM
 */
public class RAM64 extends RAM {
    //データ幅（バイト単位）
    public static final int LEN_WORD = 8;
    //データ幅（ビット単位）
    public static final int LEN_WORD_BITS = LEN_WORD * 8;

    private long[] words;

    /**
     * RAM を作成します。
     *
     * @param size RAM サイズ（バイト単位）
     */
    public RAM64(int size) {
        super(size);

        this.words = new long[size / LEN_WORD];
    }

    /**
     * バイトアドレスを RAM のワードアドレスに変換します。
     *
     * @param addr バイトアドレス
     * @return RAM のワードアドレス
     */
    protected int getWordAddress(long addr) {
        return (int)(addr / LEN_WORD);
    }

    /**
     * RAM のワード数を取得します。
     *
     * @return RAM のワード数
     */
    protected int getWords() {
        return words.length;
    }

    @Override
    public byte read8(BusMaster64 m, long addr) {
        long v = readWord(addr);

        return (byte)BitOp.readMasked(addr, v, LEN_WORD_BITS, 8);
    }

    @Override
    public short read16(BusMaster64 m, long addr) {
        long v = readWord(addr);

        return (short)BitOp.readMasked(addr, v, LEN_WORD_BITS, 16);
    }

    @Override
    public int read32(BusMaster64 m, long addr) {
        long v = readWord(addr);

        return (int)BitOp.readMasked(addr, v, LEN_WORD_BITS, 32);
    }

    @Override
    public long read64(BusMaster64 m, long addr) {
        return readWord(addr);
    }

    @Override
    public void write8(BusMaster64 m, long addr, byte data) {
        long v = readWord(addr);
        long w = BitOp.writeMasked(addr, v, data, LEN_WORD_BITS, 8);

        writeWord(addr, w);
    }

    @Override
    public void write16(BusMaster64 m, long addr, short data) {
        long v = readWord(addr);
        long w = BitOp.writeMasked(addr, v, data, LEN_WORD_BITS, 16);

        writeWord(addr, w);
    }

    @Override
    public void write32(BusMaster64 m, long addr, int data) {
        long v = readWord(addr);
        long w = BitOp.writeMasked(addr, v, data, LEN_WORD_BITS, 32);

        writeWord(addr, w);
    }

    @Override
    public void write64(BusMaster64 m, long addr, long data) {
        writeWord(addr, data);
    }

    public long readWord(long addr) {
        int wordAddr;

        addr &= BitOp.getAddressMask(LEN_WORD_BITS);
        checkAddress(addr, LEN_WORD);

        wordAddr = getWordAddress(addr);

        return words[wordAddr];
    }

    public void writeWord(long addr, long data) {
        int wordAddr;

        addr &= BitOp.getAddressMask(LEN_WORD_BITS);
        checkAddress(addr, LEN_WORD);

        wordAddr = getWordAddress(addr);

        words[wordAddr] = data;
    }
}
