package net.katsuster.ememu.arm;

/**
 * 割り込みを受け付けるコア。
 *
 * @author katsuhiro
 */
public abstract class INTC extends Controller64Reg32 {
    private INTSource[] intsrcs = new INTSource[0];
    private int maxintsrcs = 0;

    /**
     * 割り込みコントローラに繋げられるコアの最大数を取得します。
     *
     * @return コントローラに繋げられるコアの最大数
     */
    public int getMaxINTSources() {
        return maxintsrcs;
    }

    /**
     * 割り込みコントローラに繋げられるコアの最大数を設定します。
     *
     * @param n コントローラに繋げられるコアの最大数
     */
    public void setMaxINTSources(int n) {
        //割り込み元の初期化をします
        intsrcs = new INTSource[n];
        maxintsrcs = n;
        for (int i = 0; i < n; i++) {
            connectINTSource(i, new NullINTSource());
        }
    }

    /**
     * 割り込みコントローラにコアを接続します。
     *
     * 接続後、コアからの割り込みを受け付け、
     * 条件に応じて割り込みコントローラの接続先（大抵は CPU です）に、
     * 割り込みを要求します。
     *
     * @param n 割り込み線の番号
     * @param c コア
     */
    public void connectINTSource(int n, INTSource c) {
        if (n < 0 || getMaxINTSources() <= n) {
            throw new IllegalArgumentException(String.format(
                    "Illegal IRQ source number %d.", n));
        }

        intsrcs[n] = c;
    }

    /**
     * 割り込みコントローラからコアを切断します。
     *
     * 切断後はコアからの割り込みを受け付けません。
     *
     * @param n 割り込み線の番号
     */
    public void disconnectINTSource(int n) {
        if (n < 0 || getMaxINTSources() <= n) {
            throw new IllegalArgumentException(String.format(
                    "Illegal IRQ source number %d.", n));
        }

        intsrcs[n] = new NullINTSource();
    }

    /**
     * 指定された割り込み線に接続されているコアを返します。
     *
     * @param n 割り込み線の番号
     * @return コア
     */
    public INTSource getINTSource(int n) {
        if (n < 0 || getMaxINTSources() <= n) {
            throw new IllegalArgumentException(String.format(
                    "Illegal IRQ source number %d.", n));
        }

        return intsrcs[n];
    }

    /**
     * 現在の割り込み線の状態を一度に取得します。
     * 先頭の 32コアまでを同時に扱えます。
     *
     * 状態の 0 ビット目は割り込み線 0 に、
     * 1 ビット目は割り込み線 1 に、
     * n ビット目は割り込み線 n に、それぞれ対応します。
     *
     * 状態の各ビットには、コアが割り込みを要求していれば 1、
     * そうでなければ 0 が設定されます。
     *
     * @return 割り込み線の状態
     */
    public int getINTStatus() {
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
}
