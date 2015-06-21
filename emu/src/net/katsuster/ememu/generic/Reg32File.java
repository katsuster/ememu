package net.katsuster.ememu.generic;

/**
 * 32ビット長のレジスタファイルを表すインタフェースです。
 *
 * @author katsuhiro
 */
public interface Reg32File {
    /**
     * 指定された番号のレジスタを取得します。
     *
     * @param n レジスタ番号
     * @return 指定された番号のレジスタ
     */
    public abstract Reg32 getReg(int n);
}
