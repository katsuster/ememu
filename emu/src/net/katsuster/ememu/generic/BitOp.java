package net.katsuster.ememu.generic;

/**
 * 整数値へのビット演算ユーティリティクラス。
 *
 * @author katsuhiro
 */
public class BitOp {
    /**
     * 整数値の bit ビット目の値を取得します。
     *
     * ビット位置に 32 ビット以上を指定した場合、
     * 下位 5 ビットが有効となります。
     *
     * @param val 整数値
     * @param bit ビット位置
     * @return ビットがセットされていれば true、クリアされていれば false
     */
    public static boolean getBit32(int val, int bit) {
        return ((val >> bit) & 0x1) == 1;
    }

    /**
     * 整数値の bit ビット目の値を設定します。
     *
     * ビット位置に 32 ビット以上を指定した場合、
     * 下位 5 ビットが有効となります。
     *
     * @param val 整数値
     * @param bit ビット位置
     * @param nv  新しいビットの値、セットするなら true、クリアするなら false
     * @return val の指定されたビット位置を変更した後の値
     */
    public static int setBit32(int val, int bit, boolean nv) {
        int m = 1 << bit;

        if (nv) {
            return val | m;
        } else {
            return val & ~m;
        }
    }

    /**
     * 整数値の指定された位置にあるビットフィールドの値を取得します。
     *
     * ビット位置に 32 ビット以上を指定した場合、
     * 下位 5 ビットが有効となります。
     *
     * ビットフィールドの長さに 32 ビット以上を指定した場合、
     * 長さは 32 ビットとなります。
     *
     * ビット位置とビットフィールドの長さの合計が 32 ビットを超えた位置を参照する場合、
     * 全てのビットは 0 で埋められます。
     * すなわち 32 ビットより上位のビットに全て 0 が入っているかのように振舞います。
     *
     * @param val 整数値
     * @param pos ビット位置
     * @param len ビットフィールドの長さ
     * @return ビットフィールドの値
     */
    public static int getField32(int val, int pos, int len) {
        int mask;

        if (len >= 32) {
            mask = 0xffffffff;
        } else {
            mask = (1 << len) - 1;
        }

        return (val >>> pos) & mask;
    }

    /**
     * 整数値の指定された位置にあるビットフィールドの値を設定します。
     *
     * ビット位置に 32 ビット以上を指定した場合、
     * 下位 5 ビットが有効となります。
     *
     * ビットフィールドの長さに 32 ビット以上を指定した場合、
     * 長さは 32 ビットとなります。
     *
     * @param val 整数値
     * @param pos ビット位置
     * @param len ビットフィールドの長さ
     * @param nv  ビットフィールドに設定する値
     * @return val の指定されたビットフィールドを変更した後の値
     */
    public static int setField32(int val, int pos, int len, int nv) {
        int mask;

        if (len >= 32) {
            mask = 0xffffffff;
        } else {
            mask = (1 << len) - 1;
        }
        mask <<= pos;
        nv <<= pos;

        return (val & ~mask) | (nv & mask);
    }

    /**
     * ブール値を 1/0 に変換します。
     *
     * ビットフラグを 1 または 0 の数値として扱うときに使用します。
     *
     * @param b ブール値
     * @return true の場合は 1、false の場合は 0
     */
    public static int toInt(boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 符号拡張を行います。
     *
     * @param v 任意の値
     * @param n 値のビット数、64 を指定すると v を返します。
     *          65 以上を指定すると 64 で割った余りを使います。
     * @return v を符号拡張した値
     */
    public static long signExt64(long v, int n) {
        long sb, mb;

        if (n <= 0) {
            return 0;
        }

        sb = 1L << (n - 1);
        mb = (-1L << (n - 1)) << 1;
        v &= ~mb;
        if ((v & sb) != 0) {
            v = mb + v;
        }

        return v;
    }
}
