package net.katsuster.semu;

/**
 * 割り込みコントローラ
 *
 * @author katsuhiro
 */
public interface INTC {
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
    public String getIRQMessage();
}
