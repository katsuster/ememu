package net.katsuster.semu;

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

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public int getValue() {
        return val;
    }

    public void setValue(int v) {
        val = v;
    }

    @Override
    public String toString() {
        return String.format("%s: %08x", getName(), getValue());
    }
}
