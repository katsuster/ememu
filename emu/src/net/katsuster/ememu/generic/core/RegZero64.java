package net.katsuster.ememu.generic.core;

/**
 * 常に 0 の 64ビットレジスタ。
 */
public class RegZero64 extends Reg64 {
    public RegZero64() {
        super("", 0);
    }

    public RegZero64(String name) {
        super(name, 0);
    }

    /**
     * レジスタの値を取得します。値は常に 0 です。
     *
     * @return レジスタの値（常に 0）
     */
    @Override
    public long getValue() {
        return 0;
    }

    /**
     * レジスタの値を設定します。値は無視されます。
     *
     * @param v レジスタの値（無視される）
     */
    @Override
    public void setValue(long v) {
        //ignore
    }
}
