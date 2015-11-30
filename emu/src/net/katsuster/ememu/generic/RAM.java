package net.katsuster.ememu.generic;

/**
 * 64 ビットアドレス RAM
 *
 * @author katsuhiro
 */
abstract public class RAM extends SlaveCore {
    private int size;

    /**
     * RAM を作成します。
     *
     * @param size RAM サイズ（バイト単位）
     */
    public RAM(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size is negative.");
        }

        this.size = size;
    }

    /**
     * RAM のサイズを取得します。
     *
     * @return RAM サイズ（バイト単位）
     */
    public int getSize() {
        return size;
    }

    /**
     * バイトアドレスを RAM のワードアドレスに変換します。
     *
     * @param addr バイトアドレス
     * @return RAM のワードアドレス
     */
    abstract protected int getWordAddress(long addr);

    /**
     * RAM のワード数を取得します。
     *
     * @return RAM のワード数
     */
    abstract protected int getWords();

    /**
     * 指定したアドレスが正当かどうか検査します。
     *
     * 下記の条件を満たすアドレスを正当と見なします。
     *
     * アドレスのアライメントを満たしていること、
     * Integer.MAX_VALUE を超えないこと。
     *
     * @param addr  アドレス
     * @param align アラインメントのサイズ
     */
    protected void checkAddress(long addr, int align) {
        if (addr % align != 0) {
            throw new IllegalArgumentException(String.format(
                    "addr(0x%08x) is not aligned %d.", addr, align));
        }
        if (addr / align > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(String.format(
                    "addr(0x%08x) is too large.", addr));
        }
    }

    @Override
    public boolean tryRead(long addr, int len) {
        return tryAccess(addr, len);
    }

    @Override
    public boolean tryWrite(long addr, int len) {
        return tryAccess(addr, len);
    }

    /**
     * 指定されたアドレスからの読み書きが可能かどうかを判定します。
     *
     * @param addr アドレス
     * @param len  データのサイズ
     * @return 読み書きが可能な場合は true、不可能な場合は false
     */
    public boolean tryAccess(long addr, int len) {
        int wordAddr;

        wordAddr = getWordAddress(addr);

        return getWords() > wordAddr;
    }

    @Override
    public void run() {
        //do nothing
    }
}
