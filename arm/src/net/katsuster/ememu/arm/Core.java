package net.katsuster.ememu.arm;

/**
 * コア。
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
