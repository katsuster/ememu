package net.katsuster.ememu.generic;

/**
 * 32bit レジスタを持つ CPU の基本クラス
 */
public abstract class CPU32 extends CPU {
    public CPU32() {

    }

    /**
     * PC（プログラムカウンタ）の値を取得します。
     *
     * @return PC の値
     */
    public abstract int getPC();

    /**
     * PC（プログラムカウンタ）の値を設定します。
     *
     * @param val 新しい PC の値
     */
    public abstract void setPC(int val);

    /**
     * PC（プログラムカウンタ）そのものの値を取得します。
     *
     * アーキテクチャによっては、
     * レジスタそのものの値と、命令から見えるレジスタの値は異なる場合があります。
     *
     * @return PC の値
     */
    public abstract int getPCRaw();

    /**
     * PC（プログラムカウンタ）そのものの値を設定します。
     *
     * アーキテクチャによっては、
     * レジスタそのものの値と、命令から見えるレジスタの値は異なる場合があります。
     *
     * @param val 新しい PC の値
     */
    public abstract void setPCRaw(int val);

    /**
     * 指定したアドレス分だけ相対ジャンプします。
     *
     * PC + 相対アドレス を、
     * 新たな PC（プログラムカウンタ）として設定します。
     *
     * @param val 次に実行する命令の相対アドレス
     */
    public abstract void jumpRel(int val);

    /**
     * 命令から見えるレジスタ Rn の値を取得します。
     *
     * アーキテクチャによっては、
     * レジスタそのものの値と、命令から見えるレジスタの値は異なる場合があります。
     *
     * @param n レジスタ番号
     * @return レジスタの値
     */
    public abstract int getReg(int n);

    /**
     * 命令から見えるレジスタ Rn の値を設定します。
     *
     * アーキテクチャによっては、
     * レジスタそのものの値と、命令から見えるレジスタの値は異なる場合があります。
     *
     * @param n   レジスタ番号
     * @param val 新しいレジスタの値
     */
    public abstract void setReg(int n, int val);

    /**
     * レジスタ Rn そのものの値を取得します。
     *
     * アーキテクチャによっては、
     * レジスタそのものの値と、命令から見えるレジスタの値は異なる場合があります。
     *
     * @param n レジスタ番号
     * @return レジスタの値
     */
    public abstract int getRegRaw(int n);

    /**
     * レジスタ Rn そのもの値を設定します。
     *
     * アーキテクチャによっては、
     * レジスタそのものの値と、命令から見えるレジスタの値は異なる場合があります。
     *
     * @param n   レジスタ番号
     * @param val 新しいレジスタの値
     */
    public abstract void setRegRaw(int n, int val);
}
