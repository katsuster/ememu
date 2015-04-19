package net.katsuster.ememu.generic;

/**
 * 32ビットレジスタ。
 *
 * @author katsuhiro
 */
public class Reg32 {
    private String name;
    private int val;

    public Reg32() {
        this("", 0);
    }

    public Reg32(String name, int val) {
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
    public int getValue() {
        return val;
    }

    /**
     * レジスタの値を設定します。
     *
     * @param v レジスタの値
     */
    public void setValue(int v) {
        val = v;
    }

    @Override
    public String toString() {
        return String.format("%s: %08x", getName(), getValue());
    }
}
