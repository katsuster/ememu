package net.katsuster.ememu.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * 端末への出力を表示するパネルです。
 *
 * @author katsuhiro
 */
class VTInnerPane extends JComponent
        implements ChangeListener, ComponentListener {
    private static final long serialVersionUID = 1L;

    //親コンポーネント
    private VirtualTerminal parent;

    //端末への出力データ
    private StringBuffer buf;

    //端末画面の描画領域
    ContentBox boxScreen;
    //1文字の描画領域（ただし x, y は無視されます）
    ContentBox boxChar;

    //1行の桁数
    private int columns;
    //1画面に表示できる行数
    private int lines;
    //スクロールにより巻き戻せる最大の行数
    private int maxLines;
    //現在の行数
    private int currentLine;
    //カーソルの位置
    private int cursorX;
    private int cursorY;
    //画面上の文字の位置
    private char[][] layoutBox;

    public VTInnerPane(VirtualTerminal p) {
        super();

        parent = p;

        boxScreen = new ContentBox();
        boxScreen.setPadding(10, 10, 10, 10);
        boxChar = new ContentBox();
        boxChar.setMargin(0, 2, 0, 2);

        buf = new StringBuffer();
        columns = 80;
        lines = 0;
        maxLines = 1000;
        currentLine = 0;
        cursorX = 0;
        cursorY = 0;
        layoutBox = new char[getColumns()][getMaxLines()];

        setFocusable(false);
        addComponentListener(this);
    }

    /**
     * 端末への出力データを追加します。
     *
     * @param str 追加する端末への出力データ
     */
    public void append(String str) {
        buf.append(str);

        layoutChars();
        repaint();
    }

    /**
     * 端末への出力データを取得します。
     *
     * @return 端末への出力データ
     */
    public String getText() {
        return buf.toString();
    }

    /**
     * 端末への出力データを設定します。
     *
     * 古いデータは全て削除されます。
     *
     * @param t 端末への出力データ
     */
    public void setText(String t) {
        buf.delete(0, buf.length());
        buf.append(t);

        layoutChars();
        repaint();
    }

    /**
     * 1行に表示する文字数（桁数）を取得します。
     *
     * @return 1行の桁数
     */
    public int getColumns() {
        return columns;
    }

    /**
     * 1行に表示する文字数（桁数）を設定します。
     *
     * @param col 1行の桁数
     */
    public void setColumns(int col) {
        columns = col;
    }

    /**
     * 1画面に表示する行数を取得します。
     *
     * @return 1画面の行数
     */
    public int getLines() {
        return lines;
    }

    /**
     * 1画面に表示する行数を設定します。
     *
     * @param lin 1画面の行数
     */
    protected void setLines(int lin) {
        lines = lin;
    }

    /**
     * スクロールにより巻き戻せる最大の行数を取得します。
     *
     * @return 巻き戻せる最大の行数
     */
    public int getMaxLines() {
        return maxLines;
    }

    /**
     * スクロールにより巻き戻せる最大の行数を設定します。
     *
     * @param m 巻き戻せる最大の行数
     */
    public void setMaxLines(int m) {
        maxLines = m;
    }

    /**
     * 現在の行数を取得します。
     *
     * 現在の行数は、
     * 端末への出力された文字の最も大きな Y 座標と等しくなります。
     *
     * @return 現在の行数
     */
    public int getCurrentLine() {
        return currentLine;
    }

    /**
     * 現在の行数を設定します。
     *
     * 現在の行数は、
     * 端末への出力された文字の最も大きな Y 座標と等しくなります。
     *
     * スクロールバーの範囲を更新します。
     *
     * @param l 現在の行数
     */
    protected void setCurrentLine(int l) {
        currentLine = l;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JScrollBar pscr = parent.getScrollBar();
                int maximum;
                boolean chase = false;

                //一番最後の行を追うかどうか
                if (pscr.getValue() == pscr.getMaximum()) {
                    chase = true;
                }

                maximum = currentLine - getLines() + 1;
                maximum = Math.max(0, maximum);
                pscr.setMaximum(maximum);
                if (chase) {
                    pscr.setValue(pscr.getMaximum());
                }
            }
        });
    }

    /**
     * 現在のカーソル位置の X 座標を取得します。
     *
     * @return カーソル位置の現在の X 座標
     */
    public int getCursorX() {
        return cursorX;
    }

    /**
     * カーソル位置の X 座標を設定します。
     *
     * 設定可能な値は 0 から、1行の桁数 - 1 までです。
     * 範囲外の値を指定した場合、有効な値に丸められます。
     *
     * @param x カーソル位置の新しい X 座標
     */
    public void setCursorX(int x) {
        x = Math.max(0, x);
        x = Math.min(getColumns() - 1, x);

        cursorX = x;
    }

    /**
     * カーソル位置の Y 座標を取得します。
     *
     * @return カーソル位置の現在の Y 座標
     */
    public int getCursorY() {
        return cursorY;
    }

    /**
     * カーソル位置の Y 座標を設定します。
     *
     * 設定可能な値は 0 から、最大行数 - 1 までです。
     * 範囲外の値を指定した場合、有効な値に丸められます。
     *
     * @param y カーソル位置の新しい Y 座標
     */
    public void setCursorY(int y) {
        y = Math.max(0, y);
        y = Math.min(getMaxLines() - 1, y);

        cursorY = y;
    }

    /**
     * 現在のカーソル位置の X 座標と Y 座標を設定します。
     *
     * @param x カーソルの新しい X 座標
     * @param y カーソルの新しい Y 座標
     */
    public void setCursorLocation(int x, int y) {
        setCursorX(x);
        setCursorY(y);
    }

    /**
     * カーソルを次の行へ移動させます。
     */
    public void nextLine() {
        setCursorX(0);
        setCursorY(getCursorY() + 1);

        if (getCursorY() == getMaxLines() - 1) {
            scrollLine();
            setCursorY(getCursorY() - 1);
        }
    }

    /**
     * 1行古い履歴をスクロールし、捨てます。
     */
    public void scrollLine() {
        for (int y = 1; y < getMaxLines(); y++) {
            for (int x = 0; x < getColumns(); x++) {
                layoutBox[x][y - 1] = layoutBox[x][y];
            }
        }
    }

    protected void layoutChars() {
        char c;

        while (buf.length() > 0) {
            c = buf.charAt(0);
            buf.deleteCharAt(0);

            layoutBox[getCursorX()][getCursorY()] = c;
            if (getCurrentLine() < getCursorY()) {
                setCurrentLine(getCursorY());
            }

            if (c == 10) {
                nextLine();
            }

            if (getCursorX() == getColumns() - 1) {
                nextLine();
            } else {
                setCursorX(getCursorX() + 1);
            }
        }
    }

    protected void drawAll(Graphics2D g, int start) {
        int yEnd = Math.min(start + getLines(), getMaxLines());
        Color before = g.getColor();
        FontMetrics fm = g.getFontMetrics();
        int ascent = fm.getMaxAscent();
        Rectangle rscr, r;

        g.setColor(getBackground());
        g.fill3DRect(0, 0, getWidth(), getHeight(), false);
        g.setColor(before);
        for (int y = start; y < yEnd; y++) {
            for (int x = 0; x < getColumns(); x++) {
                if (layoutBox[x][y] == 0) {
                    continue;
                }

                rscr = boxScreen.getContents();
                boxChar.setX(rscr.x + x * boxChar.getWidth());
                boxChar.setY(rscr.y + (y - start) * boxChar.getHeight());
                r = boxChar.getContents();
                g.drawString(String.valueOf(layoutBox[x][y]),
                        r.x, r.y + ascent);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        drawAll((Graphics2D)g, parent.getStartLine());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        parent.setStartLine(parent.getScrollBar().getValue());

        repaint();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        Graphics g = getComponentGraphics(getGraphics());
        FontMetrics fm = g.getFontMetrics();
        int advance = fm.getMaxAdvance();
        int ascent = fm.getMaxAscent();

        //一行の高さの設定を更新する
        if (advance == -1) {
            advance = ascent / 2;
        }
        boxChar.setWidth(advance * 20 / 45);
        boxChar.setHeight(ascent);

        //一画面に表示できる行数の設定を更新する
        boxScreen.setWidth(getWidth());
        boxScreen.setHeight(getHeight());
        setLines(boxScreen.getContents().height / boxChar.getHeight());

        //スクロールできる範囲を更新する
        setCurrentLine(getCurrentLine());
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        //do nothing
    }

    @Override
    public void componentShown(ComponentEvent e) {
        //do nothing
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        //do nothing
    }
}
