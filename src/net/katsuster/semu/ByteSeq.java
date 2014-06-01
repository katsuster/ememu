package net.katsuster.semu;

/**
 * 1バイト以上のバイト列として表現可能なデータ。
 *
 * @author katsuhiro
 */
public interface ByteSeq {
    /**
     * 長さを取得します。
     *
     * @return 長さ（バイト単位）
     */
    public int length();

    /**
     * バイト列表現を取得します。
     *
     * @return データを表すバイト列
     */
    public byte[] toBytes();
}
