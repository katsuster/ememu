package net.katsuster.ememu.generic;

/**
 * 整数演算のクラス
 *
 * @author katsuhiro
 */
public class IntegerExt {
    /**
     * キャリーが発生する（符号無し演算の加算がオーバーフローする）か、
     * 否か、を取得します。
     *
     * @param left  被加算数
     * @param right 加算する数
     * @return キャリーが発生する場合は true、発生しない場合は false
     */
    public static boolean carryFrom(int left, int right) {
        return compareUnsigned(left + right, left) < 0;
    }

    /**
     * ボローが発生する（符号無し演算の減算がアンダーフローする）か、
     * 否か、を取得します。
     *
     * @param left  被減算数
     * @param right 減算する数
     * @return キャリーが発生する場合は true、発生しない場合は false
     */
    public static boolean borrowFrom(int left, int right) {
        return compareUnsigned(left, right) < 0;
    }

    /**
     * オーバーフローが発生する（符号付き演算の結果が符号が変わる）か、
     * 否か、を取得します。
     *
     * @param left  被演算数
     * @param right 演算数
     * @param add   加算なら true、減算なら false
     * @return オーバーフローが発生したなら true、そうでなければ false
     */
    public static boolean overflowFrom(int left, int right, boolean add) {
        int dest;
        boolean cond1, cond2;

        if (add) {
            //加算の場合
            dest = left + right;

            //left と right が同じ符号
            cond1 = (left >= 0 && right >= 0) || (left < 0 && right < 0);
            //なおかつ left, right と dest の符号が異なる
            cond2 = (left < 0 && dest >= 0) || (left >= 0 && dest < 0);
        } else {
            //減算の場合
            dest = left - right;

            //left と right が異なる符号
            cond1 = (left < 0 && right >= 0) || (left >= 0 && right < 0);
            //なおかつ left と dest の符号が異なる
            cond2 = (left < 0 && dest >= 0) || (left >= 0 && dest < 0);
        }

        return cond1 && cond2;
    }

    /**
     * 2 つの int 値を符号無しと見なして数値的に比較します。
     *
     * @param x 比較する最初の int
     * @param y 比較する 2番目の int
     * @return x == y の場合は値 0、
     * x &lt; y の場合は 0 より小さい値、
     * x &gt; y の場合は 0 より大きい値
     */
    public static int compareUnsigned(int x, int y) {
        int r;

        //上位 63ビットを比べる
        r = (x >>> 1) - (y >>> 1);
        if (r != 0) {
            return r;
        }

        //下位 1ビットを比べる
        return (x & 1) - (y & 1);
    }
}
