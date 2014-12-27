package net.katsuster.ememu.arm;

/**
 * 割り込みを発生させるコア。
 *
 * @author katsuhiro
 */
public interface INTSource {
    /**
     * 割り込みコントローラが割り込みを要求しているかどうかを取得します。
     *
     * @return 割り込みを要求している場合は true、要求してない場合は false
     */
    public abstract boolean isAssert();

    /**
     * 割り込みの詳細説明を取得します。
     * デバッグ用です。
     *
     * @return 割り込みの詳細な説明
     */
    public abstract String getIRQMessage();
}
