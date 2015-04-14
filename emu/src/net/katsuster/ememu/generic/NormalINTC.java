package net.katsuster.ememu.generic;

/**
 * 割り込みコントローラ。
 *
 * <p>
 * 複数の割り込み発生元から割り込みを受け付け、
 * 他のコアに割り込みを入れます。
 * </p>
 *
 * @author katsuhiro
 */
public class NormalINTC implements INTSource, INTC {
    private INTDestination intDst = new NullINTDestination();
    private INTSource[] intSrcs;
    private int maxintSrcs;

    public NormalINTC() {
        this(0);
    }

    /**
     * 割り込みコントローラを作成します。
     *
     * @param n コントローラに繋げられる下位コアの最大数
     */
    public NormalINTC(int n) {
        setMaxINTSources(n);
    }

    @Override
    public INTDestination getINTDestination() {
        return intDst;
    }

    @Override
    public void connectINTDestination(INTDestination c) {
        intDst = c;
    }

    @Override
    public void disconnectINTDestination() {
        intDst = new NullINTDestination();
    }

    /**
     * 上位コアへの割り込み要求を取得します。
     *
     * @return 下位のコアがひとつでも割り込み要求をセットしていれば true、
     * 下位のコアが全て割り込み要求をクリアしていれば false
     */
    @Override
    public boolean isAssert() {
        return isRaisedInterrupt();
    }

    @Override
    public String getIRQMessage() {
        return "NormalINTC";
    }


    @Override
    public int getMaxINTSources() {
        return maxintSrcs;
    }

    @Override
    public void setMaxINTSources(int n) {
        //割り込み元の初期化をします
        intSrcs = new INTSource[n];
        maxintSrcs = n;
        for (int i = 0; i < n; i++) {
            connectINTSource(i, new NullINTSource());
        }
    }

    @Override
    public void connectINTSource(int n, INTSource c) {
        if (n < 0 || getMaxINTSources() <= n) {
            throw new IllegalArgumentException(String.format(
                    "Illegal IRQ source number %d.", n));
        }
        if (c == null) {
            throw new IllegalArgumentException(String.format(
                    "number %d, INTSource is null.", n));
        }

        intSrcs[n] = c;
        c.connectINTDestination(this);
    }

    @Override
    public void disconnectINTSource(int n) {
        if (n < 0 || getMaxINTSources() <= n) {
            throw new IllegalArgumentException(String.format(
                    "Illegal IRQ source number %d.", n));
        }

        intSrcs[n].disconnectINTDestination();
        intSrcs[n] = new NullINTSource();
    }

    @Override
    public INTSource getINTSource(int n) {
        if (n < 0 || getMaxINTSources() <= n) {
            throw new IllegalArgumentException(String.format(
                    "Illegal IRQ source number %d.", n));
        }

        return intSrcs[n];
    }

    @Override
    public int getSourcesStatus() {
        INTSource c;
        int rawInt = 0;

        for (int i = 0; i < getMaxINTSources(); i++) {
            c = getINTSource(i);

            if (c.isAssert()) {
                rawInt |= 1 << i;
            }
        }

        return rawInt;
    }

    /**
     * 下位コアからの割り込み要求を取得します。
     *
     * @return 下位のコアがひとつでも割り込み要求をセットしていれば true、
     * 下位のコアが全て割り込み要求をクリアしていれば false
     */
    @Override
    public boolean isRaisedInterrupt() {
        return getSourcesStatus() != 0;
    }

    /**
     * 下位のコアからの割り込み要求を設定します。
     *
     * <p>
     * 下位のコアが、一つでも割り込み要求をセットしていれば、
     * 上位のコアに割り込み要求をセットします。
     * </p>
     *
     * <p>
     * 下位のコアが、全て割り込み要求をクリアしていれば、
     * 上位のコアへの割り込み要求をクリアします。
     * </p>
     *
     * @param m 無視されます
     */
    @Override
    public void setRaisedInterrupt(boolean m) {
        intDst.setRaisedInterrupt(isRaisedInterrupt());
    }
}
