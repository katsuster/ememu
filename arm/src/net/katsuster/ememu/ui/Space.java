package net.katsuster.ememu.ui;

/**
 * 上下、左右の空白の大きさを表すクラスです。
 *
 * @author katsuhiro
 */
public class Space {
    //左側の空白の大きさ
    public int left;
    //上側の空白の大きさ
    public int top;
    //右側の空白の大きさ
    public int right;
    //下側の空白の大きさ
    public int bottom;

    /**
     * 空白の大きさをすべて 0 として新たなオブジェクトを生成します。
     */
    public Space() {
        this(0);
    }

    /**
     * 空白の大きさにすべて同じ値を指定して新たなオブジェクトを生成します。
     *
     * @param n 空白の大きさ
     */
    public Space(int n) {
        this(n, n, n, n);
    }

    /**
     * 指定された空白と同じ大きさの空白を指定して、
     * 新たなオブジェクトを生成します。
     *
     * @param s 元となる空白
     */
    public Space(Space s) {
        this(s.left, s.top, s.right, s.bottom);
    }

    /**
     * 左、上、右、下の空白の大きさを指定して新たなオブジェクトを生成します。
     *
     * @param l 左側の空白の大きさ
     * @param t 上側の空白の大きさ
     * @param r 右側の空白の大きさ
     * @param b 下側の空白の大きさ
     */
    public Space(int l, int t, int r, int b) {
        left = l;
        top = t;
        right = r;
        bottom = b;
    }
}
