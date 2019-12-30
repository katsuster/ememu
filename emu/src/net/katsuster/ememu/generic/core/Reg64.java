package net.katsuster.ememu.generic.core;

/**
 * 64ビットレジスタ。
 */
public class Reg64 {
    private String name;
    private long val;

    public Reg64() {
        this("", 0);
    }

    public Reg64(String name, long val) {
        this.name = name;
        this.val = val;
    }

    /**
     * レジスタ名を取得します。
     *
     * @return レジスタ名
     */
    public String getName() {
        return name;
    }

    /**
     * レジスタ名を設定します。
     *
     * @param s レジスタ名
     */
    public void setName(String s) {
        name = s;
    }

    /**
     * レジスタの値を取得します。
     *
     * @return レジスタの値
     */
    public long getValue() {
        return val;
    }

    /**
     * レジスタの値を設定します。
     *
     * @param v レジスタの値
     */
    public void setValue(long v) {
        val = v;
    }

    @Override
    public String toString() {
        return String.format("%s: %08x", getName(), getValue());
    }
}
