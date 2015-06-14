package net.katsuster.ememu.generic;

/**
 * コア。
 *
 * <p>
 * 自身のタイミングで動作します。
 * 外部からの停止要求を受け付け、停止する努力をします。
 * </p>
 *
 * @author katsuhiro
 */
public interface Core extends Runnable {
    /**
     * 今すぐコアを停止すべきかどうかを取得します。
     *
     * @return すぐに停止すべきならば true、そうでなければ false
     */
    public abstract boolean shouldHalt();

    /**
     * 今すぐコアを停止すべきであることを通知します。
     */
    public abstract void halt();
}
