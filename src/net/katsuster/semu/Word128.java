package net.katsuster.semu;

/**
 * 128bit ワード
 *
 * @author katsuhiro
 */
public class Word128 implements ByteSeq {
    private long high;
    private long low;
    private int len;

    public Word128() {
        this(0, 0);
    }

    public Word128(long v) {
        this(0, v);
    }

    public Word128(long high, long low) {
        this.high = high;
        this.low = low;
        this.len = 16;
    }

    @Override
    public int length() {
        return len;
    }

    @Override
    public byte[] toBytes() {
        byte[] d = new byte[len];

        d[ 0] = (byte)(high >> 56);
        d[ 1] = (byte)(high >> 48);
        d[ 2] = (byte)(high >> 40);
        d[ 3] = (byte)(high >> 32);
        d[ 4] = (byte)(high >> 24);
        d[ 5] = (byte)(high >> 16);
        d[ 6] = (byte)(high >>  8);
        d[ 7] = (byte)(high >>  0);

        d[ 8] = (byte)(low >> 56);
        d[ 9] = (byte)(low >> 48);
        d[10] = (byte)(low >> 40);
        d[11] = (byte)(low >> 32);
        d[12] = (byte)(low >> 24);
        d[13] = (byte)(low >> 16);
        d[14] = (byte)(low >>  8);
        d[15] = (byte)(low >>  0);

        return d;
    }

    public long getDataHigh() {
        return high;
    }

    public long getDataLow() {
        return low;
    }
}
