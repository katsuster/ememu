package net.katsuster.semu;

/**
 * 整数値へのビット演算ユーティリティクラス。
 *
 * @author katsuhiro
 */
public class BitOp {
    /**
     * 整数値の bit ビット目の値を取得します。
     *
     * @param val 整数値
     * @param bit ビット位置
     * @return ビットがセットされていれば true、クリアされていれば false
     */
    public static boolean getBit(int val, int bit) {
        return ((val >> bit) & 0x1) == 1;
    }

    /**
     * 整数値の bit ビット目の値を設定します。
     *
     * @param val 整数値
     * @param bit ビット位置
     * @param nv  新しいビットの値、セットするなら true、クリアするなら false
     */
    public static int setBit(int val, int bit, boolean nv) {
        int m = 1 << bit;

        if (nv) {
            return val | m;
        } else {
            return val & ~m;
        }
    }

    /**
     * ブール値を 1/0 に変換します。
     *
     * ビットフラグを 1 または 0 の数値として扱うときに使用します。
     *
     * @param b ブール値
     * @return true の場合は 1、false の場合は 0
     */
    public static int toBit(boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }
}
