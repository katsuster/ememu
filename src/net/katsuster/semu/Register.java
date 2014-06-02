package net.katsuster.semu;

/**
 * レジスタ。
 *
 * @author katsuhiro
 */
public class Register {
    private String name;
    private int val;

    public Register() {
        this("", 0);
    }

    public Register(String name, int val) {
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
