package net.katsuster.ememu.arm;

/**
 * CPU の基本クラス
 *
 * @author katsuhiro
 */
public abstract class CPU extends MasterCore64 {
    private boolean fDisasmMode;
    private boolean fPrintDisasm;
    private boolean fPrintRegs;

    public CPU() {
        fDisasmMode = false;
        fPrintDisasm = false;
        fPrintRegs = false;
    }

    public boolean isDisasmMode() {
        return fDisasmMode;
    }

    public void setDisasmMode(boolean b) {
        fDisasmMode = b;
    }

    public boolean isPrintDisasm() {
        return fPrintDisasm;
    }

    public void setPrintDisasm(boolean b) {
        fPrintDisasm = b;
    }

    public boolean isPrintRegs() {
        return fPrintRegs;
    }

    public void setPrintRegs(boolean b) {
        fPrintRegs = b;
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
     * 命令を逆アセンブルした結果を表示します。
     *
     * @param inst      命令
     * @param operation 命令を表す文字列
     * @param operand   オペランドを表す文字列
     */
    public abstract void printDisasm(Instruction inst, String operation, String operand);

    /**
     * 現在のプログラムカウンタ（PC）を表示します。
     */
    public abstract void printPC();

    /**
     * 現在のレジスタを表示します。
     */
    public abstract void printRegs();

    /**
     * 現在位置から 1命令だけ実行します。
     */
    public abstract void step();

    @Override
    public void run() {
        try {
            while (!shouldHalt()) {
                step();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace(System.err);
            printPC();
            printRegs();

            throw new RuntimeException(e);
        }
    }
}
