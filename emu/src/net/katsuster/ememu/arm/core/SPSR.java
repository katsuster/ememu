package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * ARM アーキテクチャの SPSR(保存プログラムステートレジスタ)です。
 *
 * @author katsuhiro
 */
public class SPSR extends PSR {
    private Reg32 br;

    /**
     * SPSR を作成します。
     * 作成した SPSR に変更を加えるとバックレジスタ back の値が変更されます。
     *
     * @param back SPSR の値を保持するバックレジスタ
     */
    public SPSR(Reg32 back) {
        this(back.getName(), back);
    }

    /**
     * 別名を指定して、SPSR を作成します。
     * 作成した SPSR に変更を加えるとバックレジスタ back の値が変更されます。
     *
     * @param name レジスタ名
     * @param back SPSR の値を保持するバックレジスタ
     */
    public SPSR(String name, Reg32 back) {
        super(name, 0, null);
        br = back;
    }

    /**
     * バックレジスタの値を取得します。
     *
     * @return レジスタの値
     */
    @Override
    public int getValue() {
        return br.getValue();
    }

    /**
     * バックレジスタの値を取得します。
     *
     * @param v レジスタの値
     */
    @Override
    public void setValue(int v) {
        br.setValue(v);
    }
}
