package net.katsuster.ememu.generic;

/**
 * 64 ビットアドレス、16 ビットデータ RAM
 *
 * @author katsuhiro
 */
public class RAM16 extends RAM {
    //データ幅（バイト単位）
    public static final int LEN_WORD = 2;
    //データ幅（ビット単位）
    public static final int LEN_WORD_BITS = LEN_WORD * 8;

    private short[] words;

    /**
     * RAM を作成します。
     *
     * @param size RAM サイズ（バイト単位）
     */
    public RAM16(int size) {
        super(size);

        this.words = new short[size / LEN_WORD];
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
    public byte read8(long addr) {
        short v = readWord(addr);

        return (byte)readMasked(addr, v, LEN_WORD_BITS, 8);
    }

    @Override
    public short read16(long addr) {
        return readWord(addr);
    }

    @Override
    public int read32(long addr) {
        int data;

        data = (((int)readWord(addr + 0) & 0xffff) << 0) |
                (((int)readWord(addr + 2) & 0xffff) << 16);

        return data;
    }

    @Override
    public long read64(long addr) {
        long data;

        data = (((long)readWord(addr + 0) & 0xffff) << 0) |
                (((long)readWord(addr + 2) & 0xffff) << 16) |
                (((long)readWord(addr + 4) & 0xffff) << 32) |
                (((long)readWord(addr + 6) & 0xffff) << 48);

        return data;
    }

    @Override
    public void write8(long addr, byte data) {
        short v = readWord(addr);
        short w = (short)writeMasked(addr, v, data, LEN_WORD_BITS, 8);

        writeWord(addr, w);
    }

    @Override
    public void write16(long addr, short data) {
        writeWord(addr, data);
    }

    @Override
    public void write32(long addr, int data) {
        writeWord(addr + 0, (short)(data >>> 0));
        writeWord(addr + 2, (short)(data >>> 16));
    }

    @Override
    public void write64(long addr, long data) {
        writeWord(addr + 0, (short)(data >>> 0));
        writeWord(addr + 2, (short)(data >>> 16));
        writeWord(addr + 4, (short)(data >>> 32));
        writeWord(addr + 6, (short)(data >>> 48));
    }

    public short readWord(long addr) {
        int wordAddr;

        addr &= getAddressMask(LEN_WORD_BITS);
        checkAddress(addr, LEN_WORD);

        wordAddr = getWordAddress(addr);

        return words[wordAddr];
    }

    public void writeWord(long addr, short data) {
        int wordAddr;

        addr &= getAddressMask(LEN_WORD_BITS);
        checkAddress(addr, LEN_WORD);

        wordAddr = getWordAddress(addr);

        words[wordAddr] = data;
    }
}
