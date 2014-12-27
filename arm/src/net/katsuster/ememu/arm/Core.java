package net.katsuster.ememu.arm;

/**
 * コア。
 *
 * 自身のタイミングで動作します。
 * 外部からの停止要求を受け付け、停止する努力をします。
 *
 * @author katsuhiro
 */
public abstract class Core extends Thread {
    private boolean halted = false;

    /**
     * 今すぐコアを停止すべきかどうかを取得します。
     */
    public boolean shouldHalt() {
        return halted;
    }

    /**
     * 今すぐコアを停止すべきであることを通知します。
     */
    public void halt() {
        halted = true;
    }

}
