package net.katsuster.semu;

/**
 * 64 ビットアドレス RAM
 *
 * @author katsuhiro
 */
public class RAM extends SlaveCore64 {
    //データ幅（バイト単位）
    public static final int LEN_WORD = 8;
    //データ幅（ビット単位）
    public static final int LEN_WORD_BITS = LEN_WORD * 8;

    private long[] words;

    public RAM(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size is negative.");
        }

        this.words = new long[size];
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

    /**
     * 指定されたアドレスからの読み書きが可能かどうかを判定します。
     *
     * @param addr アドレス
     * @return 読み書きが可能な場合は true、不可能な場合は false
     */
    public boolean tryAccess(long addr) {
        int wordAddr;

        wordAddr = (int)(addr / LEN_WORD);

        return words.length > wordAddr;
    }

    @Override
    public boolean tryRead(long addr) {
        return tryAccess(addr);
    }

    @Override
    public byte read8(long addr) {
        long v = readWord(addr);

        return (byte)readMasked(addr, v, LEN_WORD_BITS, 8);
    }

    @Override
    public short read16(long addr) {
        long v = readWord(addr);

        return (short)readMasked(addr, v, LEN_WORD_BITS, 16);
    }

    @Override
    public int read32(long addr) {
        long v = readWord(addr);

        return (int)readMasked(addr, v, LEN_WORD_BITS, 32);
    }

    @Override
    public long read64(long addr) {
        return readWord(addr);
    }

    @Override
    public boolean tryWrite(long addr) {
        return tryAccess(addr);
    }

    @Override
    public void write8(long addr, byte data) {
        long v = readWord(addr);
        long w = writeMasked(addr, v, data, LEN_WORD_BITS, 8);

        writeWord(addr, w);
    }

    @Override
    public void write16(long addr, short data) {
        long v = readWord(addr);
        long w = writeMasked(addr, v, data, LEN_WORD_BITS, 16);

        writeWord(addr, w);
    }

    @Override
    public void write32(long addr, int data) {
        long v = readWord(addr);
        long w = writeMasked(addr, v, data, LEN_WORD_BITS, 32);

        writeWord(addr, w);
    }

    @Override
    public void write64(long addr, long data) {
        writeWord(addr, data);
    }

    public long readWord(long addr) {
        int wordAddr;

        addr &= getAddressMask(LEN_WORD_BITS);
        checkAddress(addr);

        wordAddr = (int)(addr / LEN_WORD);

        return words[wordAddr];
    }

    public void writeWord(long addr, long data) {
        int wordAddr;

        addr &= getAddressMask(LEN_WORD_BITS);
        checkAddress(addr);

        wordAddr = (int)(addr / LEN_WORD);

        words[wordAddr] = data;
    }
}
