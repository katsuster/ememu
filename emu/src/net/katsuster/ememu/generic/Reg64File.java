package net.katsuster.ememu.generic;

/**
 * 64ビット長のレジスタファイルを表すインタフェースです。
 */
public interface Reg64File {
    /**
     * 指定された番号のレジスタを取得します。
     *
     * @param n レジスタ番号
     * @return 指定された番号のレジスタ
     */
    public abstract Reg64 getReg(int n);
}
