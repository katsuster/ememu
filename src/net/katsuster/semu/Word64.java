package net.katsuster.semu;

/**
 * 64bit ワード
 *
 * @author katsuhiro
 */
public class Word64 implements ByteSeq {
    private long data;
    private int len;

    public Word64() {
        this(0);
    }

    public Word64(long data) {
        this.data = data;
        this.len = 8;
    }

    @Override
    public int length() {
        return len;
    }

    @Override
    public byte[] toBytes() {
        byte[] d = new byte[len];

        d[ 0] = (byte)(data >> 56);
        d[ 1] = (byte)(data >> 48);
        d[ 2] = (byte)(data >> 40);
        d[ 3] = (byte)(data >> 32);
        d[ 4] = (byte)(data >> 24);
        d[ 5] = (byte)(data >> 16);
        d[ 6] = (byte)(data >>  8);
        d[ 7] = (byte)(data >>  0);

        return d;
    }

    public long getData() {
        return data;
    }
}
