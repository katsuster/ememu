package net.katsuster.ememu.generic;

/**
 * 割り込みコントローラ。
 *
 * <p>
 * 複数の割り込み発生元となるコア（下位コア）から割り込みを受け付け、
 * 他のコア（上位コア）に割り込みを掛けます。
 * </p>
 *
 * <p>
 * 上位コアと下位コアの関係は下記の通りです。
 * 矢印は割り込みを掛ける方向を表します。
 * </p>
 *
 * <pre>
 * 上位                                   下位
 * -------------------------------------------
 * INTDestination &lt;----- INTC &lt;--+-- INTSource
 *                               +-- INTSource
 *                               +-- ...
 *                               `-- INTSource
 * </pre>
 *
 * @author katsuhiro
 */
public interface INTC extends INTDestination {
    /**
     * 割り込みコントローラに繋げられるコアの最大数を取得します。
     *
     * @return コントローラに繋げられるコアの最大数
     */
    public abstract int getMaxINTSources();

    /**
     * 割り込みコントローラに繋げられるコアの最大数を設定します。
     *
     * @param n コントローラに繋げられるコアの最大数
     */
    public abstract void setMaxINTSources(int n);

    /**
     * 割り込みコントローラにコアを接続します。
     *
     * <p>
     * 接続後、コアからの割り込みを受け付けます。
     * </p>
     *
     * @param n 下位の割り込み線の番号
     * @param c 割り込みを発生させるコア
     */
    public abstract void connectINTSource(int n, INTSource c);

    /**
     * 割り込みコントローラからコアを切断します。
     *
     * <p>
     * 切断後はコアからの割り込みを受け付けません。
     * </p>
     *
     * @param n 下位の割り込み線の番号
     */
    public abstract void disconnectINTSource(int n);

    /**
     * 指定された割り込み線に接続されているコアを返します。
     *
     * @param n 下位の割り込み線の番号
     * @return コア
     */
    public abstract INTSource getINTSource(int n);

    /**
     * 下位の割り込み線の状態を一度に取得します。
     *
     * <p>
     * 先頭の 32コアまでを同時に扱えます。
     * </p>
     *
     * <p>
     * 状態の 0 ビット目は割り込み線 0 に、
     * 1 ビット目は割り込み線 1 に、
     * n ビット目は割り込み線 n に、それぞれ対応します。
     * </p>
     *
     * <p>
     * 状態の各ビットには、コアが割り込みを要求していれば 1、
     * そうでなければ 0 が設定されます。
     * </p>
     *
     * @return 下位の割り込み線の状態
     */
    public abstract int getSourcesStatus();
}
