package net.katsuster.ememu.generic;

/**
 * コアの典型的な実装を持った抽象クラス。
 *
 * <p>
 * 自身のタイミングで動作します。
 * 外部からの停止要求を受け付け、停止する努力をします。
 * </p>
 *
 * @author katsuhiro
 */
public abstract class AbstractCore extends Thread
        implements Core {
    private boolean halted = false;

    @Override
    public boolean shouldHalt() {
        return halted;
    }

    @Override
    public void halt() {
        synchronized(this) {
            halted = true;
            notifyAll();
        }
    }
}
