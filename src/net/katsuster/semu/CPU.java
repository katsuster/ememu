package net.katsuster.semu;

/**
 * ARM9 CPU
 *
 * 命令セット: ARMv5TE
 *
 * ARM アーキテクチャリファレンスマニュアル Second Edition による。
 * （文章番号 ARM DDI 0100DJ-00）
 *
 * T は Thumb 命令、
 * E はエンハンスド DSP 命令、
 * のことらしい。
 *
 * @author katsuhiro
 */
public abstract class CPU extends MasterCore64 implements Runnable {
    private boolean flagDisasm;
    private boolean flagPrintDisasm;
    private boolean flagPrintReg;

    public CPU() {
        flagDisasm = false;
        flagPrintDisasm = false;
        flagPrintReg = false;
    }

    public boolean isDisasmMode() {
        return flagDisasm;
    }

    public void setDisasmMode(boolean b) {
        flagDisasm = b;
    }

    public boolean isPrintingDisasm() {
        return flagPrintDisasm;
    }

    public void setPrintingDisasm(boolean b) {
        flagPrintDisasm = b;
    }

    public boolean isPrintingRegs() {
        return flagPrintReg;
    }

    public void setPrintingRegs(boolean b) {
        flagPrintReg = b;
    }

    /**
     * 符号拡張を行います。
     *
     * @param v 任意の値
     * @param n 値のビット数
     */
    public static long signExt64(long v, int n) {
        long sb, mb;

        if (n == 0) {
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

    /**
     * キャリーが発生する（符号無し演算の加算がオーバーフローする）か、
     * 否か、を取得します。
     *
     * @param left  被加算数
     * @param right 加算する数
     * @return キャリーが発生する場合は true、発生しない場合は false
     */
    public static boolean carryFrom32(int left, int right) {
        long ll = left & 0xffffffffL;
        long lr = right & 0xffffffffL;

        return ((ll + lr) & ~0xffffffffL) != 0;
    }

    /**
     * ボローが発生する（符号無し演算の減算がアンダーフローする）か、
     * 否か、を取得します。
     *
     * @param left  被減算数
     * @param right 減算する数
     * @return キャリーが発生する場合は true、発生しない場合は false
     */
    public static boolean borrowFrom32(int left, int right) {
        long ll = left & 0xffffffffL;
        long lr = right & 0xffffffffL;

        return lr > ll;
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
    public static boolean overflowFrom32(int left, int right, boolean add) {
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
     * 現在位置から 1命令だけ実行します。
     */
    public abstract void step();

    public void run() {
        while (true) {
            step();
        }
    }
}