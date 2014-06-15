package net.katsuster.semu;

/**
 * ARM MMU
 *
 * @author katsuhiro
 */
public class MMU {
    private CPU cpu;
    private boolean enable;

    public MMU(CPU cpu) {
        this.cpu = cpu;
        this.enable = false;
    }

    /**
     * MMU が有効か否かを取得します。
     *
     * @return MMU が有効ならば true、無効ならば false
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * MMU が有効か否かを設定します。
     *
     * @param e MMU が有効ならば true、無効ならば false
     */
    public void setEnable(boolean e) {
        enable = e;
    }

    /**
     * アドレス変換を行います。
     *
     * @param va 仮想アドレス（VA）
     * @return 物理アドレス（PA）
     */
    public int translate(int va) {
        if (!isEnable()) {
            return va;
        }

        //TODO: Not implemented
        throw new IllegalArgumentException("Sorry, not implemented.");
    }
}
