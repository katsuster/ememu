package net.katsuster.ememu.arm.core;

/**
 * ARM アーキテクチャの APSR(アプリケーションプログラムステートレジスタ)です。
 *
 * 読み出し時は、
 * N, Z, C, V, Q, GE ビットのみ取得され、他の値は 0 でマスクされます。
 * また、
 * N, Z, C, V, Q, GE ビットのみ変更可能です。
 *
 * @author katsuhiro
 */
public class APSR extends PSR {
    private PSR br;

    /**
     * APSR を作成します。
     * 作成した APSR に変更を加えるとバックレジスタ back の値が変更されます。
     *
     * @param back APSR の値を保持するバックレジスタ
     */
    public APSR(PSR back) {
        this(back.getName(), back);
    }

    /**
     * 別名を指定して、APSR を作成します。
     * 作成した APSR に変更を加えるとバックレジスタ back の値が変更されます。
     *
     * @param name レジスタ名
     * @param back APSR の値を保持するバックレジスタ
     */
    public APSR(String name, PSR back) {
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
        return br.getValue() & 0xf80f0000;
    }

    /**
     * バックレジスタの値を取得します。
     *
     * @param v レジスタの値
     */
    @Override
    public void setValue(int v) {
        int r;

        //N, Z, C, V, Q, GE のみ変更可能
        r = br.getValue();
        r &= ~0xf80f0000;
        r |= v & 0xf80f0000;
        br.setValue(r);
    }
}
