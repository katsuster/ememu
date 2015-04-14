package net.katsuster.ememu.generic;

/**
 * 割り込みを受け付けるコア。
 *
 * @author katsuhiro
 */
public interface INTDestination {
    /**
     * 外部コアから割り込みを要求されているかどうかを取得します。
     *
     * @return 割り込みを要求されている場合 true、
     * 要求されていない場合 false
     */
    public abstract boolean isRaisedInterrupt();

    /**
     * 外部コアから割り込みを受け付けます。
     *
     * @param m 割り込みを要求されている場合 true、
     * 要求されていない場合 false
     */
    public abstract void setRaisedInterrupt(boolean m);
}
