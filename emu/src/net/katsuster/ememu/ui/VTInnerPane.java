package net.katsuster.ememu.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
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

    //端末画面の描画領域
    ContentBox boxScreen;
    //1文字の描画領域（ただし x, y は無視されます）
    ContentBox boxChar;

    //入力された文字列を戻すためのバッファ
    private StringBuilder strPushback;

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
    //自動改行が必要かどうか
    private boolean needWrap;

    public VTInnerPane(VirtualTerminal p) {
        super();

        parent = p;

        boxScreen = new ContentBox();
        boxScreen.setPadding(10, 10, 10, 10);
        boxChar = new ContentBox();
        boxChar.setMargin(0, 2, 0, 2);

        strPushback = new StringBuilder();
        columns = 80;
        lines = 0;
        maxLines = 1000;
        currentLine = 0;
        cursorX = 0;
        cursorY = 0;
        layoutBox = new char[getColumns()][getMaxLines()];
        needWrap = false;

        setFocusable(false);
        addComponentListener(this);
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
        if (currentLine < l) {
            currentLine = l;
        }

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

    /**
     * 入力された文字を取得します。
     *
     * 戻した文字列があればそちらから 1文字を返し、
     * 戻した文字列がなければ入力ストリームから 1文字を返します。
     *
     * @param ins 文字列が入力されるストリーム
     * @return 次の文字
     * @throws IOException
     */
    protected char getNextChar(InputStream ins) throws IOException {
        if (strPushback.length() != 0) {
            char c = strPushback.charAt(0);
            strPushback.deleteCharAt(0);

            return c;
        }

        int i = ins.read();
        if (i == -1) {
            //EOF
            throw new IOException("Reached EOF");
        }

        //System.out.printf("%02x\n", i);

        return (char)i;
    }

    /**
     * 文字を戻します。
     *
     * 戻された文字は記憶され、次の getNextChar() で返されます。
     * 複数の文字を戻した場合は、最後に戻した文字が先に返されます（LIFO）。
     *
     * @param c 戻す文字
     */
    protected void pushbackChar(char c) {
        strPushback.insert(0, c);
    }

    /**
     * エスケープシーケンスの数値パラメータを取得します。
     *
     * 数値（'0' から '9' までの文字）以外が出現した場合、解析を終了します。
     * 1文字も数字が出現しなかった場合、デフォルト値を返します。
     *
     * @param ins 文字列を入力するストリーム
     * @param def デフォルト値
     * @return 数値パラメータ、一文字もなければデフォルト値を返します。
     * @throws IOException
     */
    protected int getNumberChar(InputStream ins, int def) throws IOException {
        char c;
        int result = 0;
        boolean isDefault = true;

        output:
        while (true) {
            c = getNextChar(ins);
            switch (c) {
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                result *= 10;
                result += c - '0';
                isDefault = false;
                break;
            default:
                pushbackChar(c);
                break output;
            }
        }

        if (isDefault) {
            return def;
        } else {
            return result;
        }
    }

    /**
     * CSI（ESC [）を処理します。
     *
     * @param ins 文字列を入力するストリーム
     */
    protected boolean layoutEscapeCSI(InputStream ins) throws IOException {
        int defNum = -1;
        int numN = getNumberChar(ins, defNum);
        int numM = 0;
        char csrChar;
        boolean processed = false;

        while (!processed) {
            csrChar = getNextChar(ins);
            switch (csrChar) {
            case 'C':
                //CUF - Cursor Forward
                if (numN == defNum) {
                    numN = 1;
                }
                setCursorX(getCursorX() + numN);

                processed = true;
                break;
            case 'D':
                //CUB - Cursor Back
                if (numN == defNum) {
                    numN = 1;
                }
                setCursorX(getCursorX() - numN);

                processed = true;
                break;
            case 'H':
                //CUP - Cursor Position
                if (numN == defNum) {
                    numN = 1;
                }
                if (numM == defNum) {
                    numN = 1;
                }

                //NOTE: ASCII Escape sequence cursor position is 1-origin
                setCursorX(numM - 1);
                setCursorY(getCurrentLine() + 1 - getLines() + numN - 1);

                processed = true;
                break;
            case 'J':
                //ED - Erase Display
                int yEnd = Math.min(getCurrentLine() + 1, getMaxLines());

                if (numN == defNum) {
                    numN = 0;
                }

                switch (numN) {
                case 0:
                    //0: Clear from cursor to end of screen
                    for (int x = getCursorX(); x < getColumns(); x++) {
                        layoutBox[x][getCursorY()] = 0;
                    }
                    for (int y = getCursorY() + 1; y < yEnd; y++) {
                        for (int x = 0; x < getColumns(); x++) {
                            layoutBox[x][y] = 0;
                        }
                    }
                    break;
                case 1:
                    //1: Clear from begging of screen to cursor
                    //FIXME: not implemented yet
                    System.out.println("CSI 1 J is not implemented, sorry.");
                    break;
                case 2:
                    //2: Clear entire screen
                    //FIXME: not implemented yet
                    System.out.println("CSI 2 J is not implemented, sorry.");
                    break;
                default:
                    //Unknown, do nothing
                    break;
                }

                processed = true;
                break;
            case ';':
                //Next number
                numM = getNumberChar(ins, defNum);
                break;
            default:
                //Unknown: Ignore it
                //System.out.printf("Unknown CSR 0x%02x\n", (int)csrChar);
                processed = true;
                break;
            }
        }

        return true;
    }

    /**
     * ESC を処理します。
     *
     * @param ins 文字列を入力するストリーム
     */
    protected boolean layoutEscape(InputStream ins) throws IOException {
        char c = getNextChar(ins);

        switch (c) {
        case 0x5b:
            //CSI
            layoutEscapeCSI(ins);
            break;
        default:
            //ignore it
            return false;
        }

        return true;
    }

    /**
     * 1文字を配置します。
     *
     * @param ins 文字列を入力するストリーム
     */
    protected void layoutNormalChar(InputStream ins) throws IOException {
        char c = getNextChar(ins);

        if (getCursorX() == getColumns() - 1 && needWrap) {
            nextLine();
        }

        layoutBox[getCursorX()][getCursorY()] = c;
        //NOTE: Need wrap the line at next char if we are in end of line
        needWrap = (getCursorX() == getColumns() - 1);
        setCursorX(getCursorX() + 1);
    }

    /**
     * 文字を配置します。
     *
     * 入力ストリームに十分な文字列がない場合、
     * 文字列が入力されるまでスレッドがブロックされます。
     *
     * @param ins 文字列を入力するストリーム
     */
    public void layoutChars(InputStream ins) throws IOException {
        do {
            char c = getNextChar(ins);

            switch (c) {
            case 0x07:
                //BELL
                //FIXME: ignored it
                break;
            case 0x08:
                //BS
                setCursorX(getCursorX() - 1);
                break;
            case 0x0a:
                //LF
                nextLine();
                break;
            case 0x0d:
                //CR
                setCursorX(0);
                break;
            case 0x1b:
                //ESC
                boolean processed = layoutEscape(ins);
                if (!processed) {
                    pushbackChar(c);
                    layoutNormalChar(ins);
                }
                break;
            default:
                //Other characters
                pushbackChar(c);
                layoutNormalChar(ins);
            }

            //Show last line
            if (getCurrentLine() < getCursorY()) {
                setCurrentLine(getCursorY());
            }
        } while (ins.available() != 0);
    }

    protected void drawAll(Graphics2D g, int start) {
        int yEnd = Math.min(start + getLines(), getMaxLines());
        Color before = g.getColor();
        Rectangle rscr, r;

        rscr = boxScreen.getContents();

        //Draw whole screen
        g.setColor(getBackground());
        g.fill3DRect(0, 0, getWidth(), getHeight(), false);
        g.setColor(before);
        for (int y = start; y < yEnd; y++) {
            for (int x = 0; x < getColumns(); x++) {
                if (layoutBox[x][y] == 0) {
                    continue;
                }

                boxChar.setX(rscr.x + x * boxChar.getWidth());
                boxChar.setY(rscr.y + (y - start) * boxChar.getHeight());
                r = boxChar.getContents();
                g.drawString(String.valueOf(layoutBox[x][y]),
                        r.x, r.y + r.height);
            }
        }

        //Draw cursor
        int x = getCursorX();
        int y = getCursorY();

        boxChar.setX(rscr.x + x * boxChar.getWidth());
        boxChar.setY(rscr.y + (y - start) * boxChar.getHeight());
        r = boxChar.getBounds();
        g.setColor(getForeground());
        g.fillRect(r.x, r.y, r.width, r.height);
        if (layoutBox[x][y] != 0) {
            r = boxChar.getContents();
            g.setColor(getBackground());
            g.drawString(String.valueOf(layoutBox[x][y]),
                    r.x, r.y + r.height);
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
