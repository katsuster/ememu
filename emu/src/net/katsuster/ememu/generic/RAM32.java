package net.katsuster.ememu.generic;

/**
 * 64 ビットアドレス、32 ビットデータ RAM
 *
 * @author katsuhiro
 */
public class RAM32 extends RAM {
    //データ幅（バイト単位）
    public static final int LEN_WORD = 4;
    //データ幅（ビット単位）
    public static final int LEN_WORD_BITS = LEN_WORD * 8;

    private int[] words;

    /**
     * RAM を作成します。
     *
     * @param size RAM サイズ（バイト単位）
     */
    public RAM32(int size) {
        super(size);

        this.words = new int[size / LEN_WORD];
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
        int v = readWord(addr);

        return (byte)readMasked(addr, v, LEN_WORD_BITS, 8);
    }

    @Override
    public short read16(long addr) {
        int v = readWord(addr);

        return (short)readMasked(addr, v, LEN_WORD_BITS, 16);
    }

    @Override
    public int read32(long addr) {
        return readWord(addr);
    }

    @Override
    public long read64(long addr) {
        long data;

        data = (((long)readWord(addr + 0) & 0xffffffffL) << 0) |
                (((long)readWord(addr + 4) & 0xffffffffL) << 32);

        return data;
    }

    @Override
    public void write8(long addr, byte data) {
        int v = readWord(addr);
        int w = (int)writeMasked(addr, v, data, LEN_WORD_BITS, 8);

        writeWord(addr, w);
    }

    @Override
    public void write16(long addr, short data) {
        int v = readWord(addr);
        int w = (int)writeMasked(addr, v, data, LEN_WORD_BITS, 16);

        writeWord(addr, w);
    }

    @Override
    public void write32(long addr, int data) {
        writeWord(addr, data);
    }

    @Override
    public void write64(long addr, long data) {
        writeWord(addr + 0, (int)(data >>> 0));
        writeWord(addr + 4, (int)(data >>> 32));
    }

    public int readWord(long addr) {
        int wordAddr;

        addr &= getAddressMask(LEN_WORD_BITS);
        checkAddress(addr, LEN_WORD);

        wordAddr = getWordAddress(addr);

        return words[wordAddr];
    }

    public void writeWord(long addr, int data) {
        int wordAddr;

        addr &= getAddressMask(LEN_WORD_BITS);
        checkAddress(addr, LEN_WORD);

        wordAddr = getWordAddress(addr);

        words[wordAddr] = data;
    }
}
