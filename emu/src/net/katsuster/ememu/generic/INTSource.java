package net.katsuster.ememu.generic;

/**
 * 割り込みを発生させるコア。
 *
 * @author katsuhiro
 */
public interface INTSource {
    /**
     * 割り込み先となるコアを取得します。
     *
     * @return 割り込み先のコア
     */
    public abstract INTDestination getINTDestination();

    /**
     * 割り込み先となるコアを設定します。
     *
     * @param c 割り込み先のコア
     */
    public abstract void connectINTDestination(INTDestination c);

    /**
     * 割り込み先となるコアを解除します。
     */
    public abstract void disconnectINTDestination();

    /**
     * コアが割り込みを要求しているかどうかを取得します。
     *
     * @return 割り込みを要求している場合は true、要求してない場合は false
     */
    public abstract boolean isAssert();

    /**
     * コアが発生させた割り込みの詳細説明を取得します。
     * デバッグ用です。
     *
     * @return 割り込みの詳細な説明
     */
    public abstract String getIRQMessage();
}
