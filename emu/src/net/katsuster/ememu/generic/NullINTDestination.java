package net.katsuster.ememu.generic;

/**
 * 割り込みを受け付けて何もしないコア。
 *
 * @author katsuhiro
 */
public class NullINTDestination implements INTDestination {
    @Override
    public boolean isRaisedInterrupt() {
        return false;
    }

    @Override
    public void setRaisedInterrupt(boolean m) {
        //do nothing
    }
}
