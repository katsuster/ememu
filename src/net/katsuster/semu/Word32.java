package net.katsuster.semu;

/**
 * 32bit ワード
 *
 * @author katsuhiro
 */
public class Word32 implements ByteSeq {
    private int data;
    private int len;

    public Word32() {
        this(0);
    }

    public Word32(int data) {
        this.data = data;
        this.len = 4;
    }

    @Override
    public int length() {
        return len;
    }

    @Override
    public byte[] toBytes() {
        byte[] d = new byte[len];

        d[ 0] = (byte)(data >> 24);
        d[ 1] = (byte)(data >> 16);
        d[ 2] = (byte)(data >>  8);
        d[ 3] = (byte)(data >>  0);

        return d;
    }

    public int getData() {
        return data;
    }
}
