package net.katsuster.ememu.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * 端末への出力を表示するパネルです。
 *
 * @author katsuhiro
 */
public class VTInnerPane extends JComponent
        implements ChangeListener, ComponentListener {
    private static final long serialVersionUID = 1L;

    //親コンポーネント
    private VirtualTerminal parent;

    //端末画面の描画領域
    private ContentBox boxScreen;
    //1文字の描画領域（ただし x, y は無視されます）
    private ContentBox boxChar;

    //入力された文字列を戻すためのバッファ
    private StringBuilder strWriteBack;

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
    //現在の文字の色
    private DecoratedChar currentDecoration = new DecoratedChar();
    //画面上の文字の位置
    private DecoratedChar[][] layoutBox;
    //自動改行が必要かどうか
    private boolean needWrap;
    //自動改行（後退時）が必要かどうか
    private boolean needWrapBack;

    //色パレット
    // 0: black, 1: red, 2: green, 3: yellow,
    // 4: blue, 5: magenta, 6:cyan, 7:white, 8: default
    private Color[] currentPalette;
    private Color[] paletteNormal = {
            new Color(0, 0, 0), new Color(205, 0, 0),
            new Color(0, 205, 0), new Color(205, 205, 0),
            new Color(0, 0, 238), new Color(205, 0, 205),
            new Color(0, 205, 205), new Color(229, 229, 229),
            new Color(127, 127, 127)
    };
    private Color[] paletteBold = {
            new Color(0, 0, 0), new Color(255, 0, 0),
            new Color(0, 255, 0), new Color(255, 255, 0),
            new Color(92, 92, 255), new Color(255, 0, 255),
            new Color(0, 255, 255), new Color(255, 255, 255),
            new Color(127, 127, 127)
    };

    public VTInnerPane(VirtualTerminal p) {
        super();

        parent = p;

        setForeground(DecoratedChar.DEFAULT_FOREGROUND);
        setBackground(DecoratedChar.DEFAULT_BACKGROUND);

        boxScreen = new ContentBox();
        boxScreen.setPadding(10, 10, 10, 10);
        boxChar = new ContentBox();
        boxChar.setMargin(0, 2, 0, 2);

        strWriteBack = new StringBuilder();
        columns = 80;
        lines = 0;
        maxLines = 1000;
        currentLine = 0;
        cursorX = 0;
        cursorY = 0;
        currentDecoration = new DecoratedChar();
        layoutBox = new DecoratedChar[getColumns()][getMaxLines()];
        for (int x = 0; x < getColumns(); x++) {
            for (int y = 0; y < getMaxLines(); y++) {
                layoutBox[x][y] = new DecoratedChar();
            }
        }
        needWrap = false;
        needWrapBack = false;
        currentPalette = paletteNormal;

        setFocusable(false);
        addComponentListener(this);
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);

        getCurrentDecoration().setForeground(fg);
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);

        getCurrentDecoration().setBackground(bg);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);

        getCurrentDecoration().setFont(font);
    }

    /**
     * スクリーンのレイアウト情報を取得します。
     *
     * @return スクリーンのレイアウト情報
     */
    public ContentBox getBoxScreen() {
        return boxScreen;
    }

    /**
     * 1文字のレイアウト情報を取得します。
     *
     * @return 1文字のレイアウト情報
     */
    public ContentBox getBoxChar() {
        return boxChar;
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
    protected void setMaxLines(int m) {
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
     * スクリーンの一番上の行を取得します。
     *
     * @return スクリーンの一番上の行
     */
    public int getScreenTopLine() {
        return Math.max(0, getCurrentLine() + 1 - getLines());
    }

    /**
     * スクリーンの一番下の行を取得します。
     *
     * @return スクリーンの一番下の行
     */
    public int getScreenBottomLine() {
        return Math.min(getCurrentLine(), getMaxLines() - 1);
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
     * カーソルを前の行へ移動させます。
     */
    public void previousLine() {
        setCursorX(getColumns() - 1);
        setCursorY(getCursorY() - 1);
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
                layoutBox[x][y - 1].copy(layoutBox[x][y]);
            }
        }
    }

    /**
     * 現在の文字の装飾を取得します。
     *
     * 端末に新たな文字が入力されたときに設定される装飾です。
     *
     * @return 文字の装飾
     */
    public DecoratedChar getCurrentDecoration() {
        return currentDecoration;
    }

    /**
     * 指定した座標の装飾付き文字を取得します。
     *
     * @param x X座標
     * @param y Y座標
     * @return 指定した座標の装飾付き文字
     */
    public DecoratedChar getDecoratedChar(int x, int y) {
        return layoutBox[x][y];
    }

    /**
     * 指定した座標の文字を取得します。
     *
     * @param x X座標
     * @param y Y座標
     * @return 指定した座標の文字
     */
    public char getChar(int x, int y) {
        return layoutBox[x][y].getChar();
    }

    /**
     * 指定した座標の文字を設定します。
     *
     * @param x X座標
     * @param y Y座標
     * @param c 指定した座標の文字
     */
    public void setChar(int x, int y, char c) {
        layoutBox[x][y].setChar(c);
    }

    /**
     * 指定した座標の文字を消去します。
     *
     * @param x X座標
     * @param y Y座標
     */
    public void eraseChar(int x, int y) {
        layoutBox[x][y].setChar((char)0);
    }

    /**
     * 現在のパレットを取得します。
     *
     * @return 現在のパレット
     */
    public Color[] getCurrentPalette() {
        return currentPalette;
    }

    /**
     * 現在のパレットを設定します。
     *
     * @param p パレット
     */
    public void setCurrentPalette(Color[] p) {
        if (p.length < 8) {
            throw new IllegalArgumentException("Palette size is too small.");
        }

        currentPalette = p;
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
    protected char readNext(InputStream ins) throws IOException {
        if (strWriteBack.length() != 0) {
            char c = strWriteBack.charAt(0);
            strWriteBack.deleteCharAt(0);

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
     * 戻された文字は記憶され、次の readNext() で返されます。
     * 複数の文字を戻した場合は、最後に戻した文字が先に返されます（LIFO）。
     *
     * @param c 戻す文字
     */
    protected void writeBack(char c) {
        strWriteBack.insert(0, c);
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
    protected int readParam(InputStream ins, int def) throws IOException {
        char c;
        int result = 0;
        boolean isDefault = true;

        output:
        while (true) {
            c = readNext(ins);
            switch (c) {
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                result *= 10;
                result += c - '0';
                isDefault = false;
                break;
            default:
                writeBack(c);
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
     * パラメータリストから指定したパラメータを取得します。
     *
     * 指定したインデックスのパラメータが存在しない場合、
     * デフォルト値が返されます。
     *
     * @param l     パラメータリスト
     * @param index 取得するパラメータのインデックス
     * @param def   デフォルト値
     * @return パラメータの値
     */
    protected int getParameter(java.util.List<Integer> l, int index, int def) {
        if (l.size() > index) {
            return l.get(index);
        } else {
            return def;
        }
    }

    /**
     * ESC を処理します。
     *
     * @param ins 文字列を入力するストリーム
     */
    protected boolean layoutEscape(InputStream ins) throws IOException {
        char c = readNext(ins);

        switch (c) {
        case '[':
            //CSI
            layoutEscapeCSI(ins);
            break;
        default:
            //ignore it
            writeBack(c);
            layoutNormalChar(ins);

            return false;
        }

        return true;
    }

    /**
     * CSI（ESC [）を処理します。
     *
     * @param ins 文字列を入力するストリーム
     */
    protected boolean layoutEscapeCSI(InputStream ins) throws IOException {
        final int DEFAULT_NUMBER = -1;

        java.util.List<Integer> params = new ArrayList<>();
        char csrChar;
        boolean sequenceEnd = false;
        int tmp, param0, param1;
        Font f;

        tmp = readParam(ins, DEFAULT_NUMBER);
        if (tmp != DEFAULT_NUMBER) {
            params.add(tmp);
        }

        while (!sequenceEnd) {
            csrChar = readNext(ins);
            switch (csrChar) {
            case 'A':
                //CUU - Cursor Up
                param0 = getParameter(params, 0, 1);
                setCursorY(Math.max(getScreenTopLine(), getCursorY() - param0));

                sequenceEnd = true;
                break;
            case 'B':
                //CUD - Cursor Down
                param0 = getParameter(params, 0, 1);
                setCursorY(getCursorY() + param0);

                sequenceEnd = true;
                break;
            case 'C':
                //CUF - Cursor Forward
                param0 = getParameter(params, 0, 1);
                setCursorX(getCursorX() + param0);

                sequenceEnd = true;
                break;
            case 'D':
                //CUB - Cursor Back
                param0 = getParameter(params, 0, 1);
                setCursorX(getCursorX() - param0);

                sequenceEnd = true;
                break;
            case 'H':
                //CUP - Cursor Position
                param0 = getParameter(params, 0, 1);
                param1 = getParameter(params, 1, 1);
                //NOTE: ASCII Escape sequence cursor position is 1-origin
                setCursorLocation(param1 - 1, getScreenTopLine() + param0 - 1);

                sequenceEnd = true;
                break;
            case 'J':
                //ED - Erase Display
                param0 = getParameter(params, 0, 0);
                switch (param0) {
                case 0:
                    //0: Clear from cursor to end of screen
                    for (int x = getCursorX(); x < getColumns(); x++) {
                        eraseChar(x, getCursorY());
                    }
                    for (int y = getCursorY() + 1; y < getScreenBottomLine() + 1; y++) {
                        for (int x = 0; x < getColumns(); x++) {
                            eraseChar(x, y);
                        }
                    }
                    break;
                case 1:
                    //1: Clear from begging of screen to cursor
                    //FIXME: not implemented yet
                    //break;
                case 2:
                    //2: Clear entire screen
                    //FIXME: not implemented yet
                    //break;
                default:
                    //Unknown, do nothing
                    System.out.printf("CSI %d J (Erase Display) is not implemented, sorry.\n", param0);
                    break;
                }

                sequenceEnd = true;
                break;
            case 'K':
                //EL - Erase Line
                param0 = getParameter(params, 0, 0);
                switch (param0) {
                case 0:
                    //0: Clear from cursor to the end of the line
                    for (int x = getCursorX(); x < getColumns(); x++) {
                        eraseChar(x, getCursorY());
                    }
                    break;
                case 1:
                    //1: Clear from cursor to beginning of the line
                    //FIXME: not implemented yet
                    //break;
                case 2:
                    //2: Clear entire line
                    //FIXME: not implemented yet
                    //break;
                default:
                    //Unknown, do nothing
                    System.out.printf("CSI %d K (Erase Line) is not implemented, sorry.\n", param0);
                    break;
                }

                sequenceEnd = true;
                break;
            case 'm':
                //SGR - Select Graphic Rendition
                for (int i = 0; i < params.size(); i++) {
                    param0 = getParameter(params, i, 0);
                    switch (param0) {
                    case 0:
                        //0: default rendition
                        getCurrentDecoration().setForeground(getForeground());
                        getCurrentDecoration().setBackground(getBackground());
                        getCurrentDecoration().setNegaMode(false);
                        f = getCurrentDecoration().getFont();
                        getCurrentDecoration().setFont(f.deriveFont(Font.PLAIN));
                        setCurrentPalette(paletteNormal);
                        break;
                    case 1:
                        //1: bold or increased intensity
                        f = getCurrentDecoration().getFont();
                        getCurrentDecoration().setFont(f.deriveFont(Font.BOLD));
                        setCurrentPalette(paletteBold);
                        break;
                    case 2:
                        //2: faint, decreased intensity or second colour
                        f = getCurrentDecoration().getFont();
                        getCurrentDecoration().setFont(f.deriveFont(Font.PLAIN));
                        setCurrentPalette(paletteNormal);
                        break;
                    case 3:
                        //3: italicized
                        f = getCurrentDecoration().getFont();
                        getCurrentDecoration().setFont(f.deriveFont(Font.ITALIC));
                        break;
                    case 4:
                        //4: singly underlined
                        break;
                    case 5:
                        //5: slowly blinking (less than 150 per minute)
                        break;
                    case 6:
                        //6: rapidly blinking (150 per minute or more)
                        break;
                    case 7:
                        //7: negative image
                        getCurrentDecoration().setNegaMode(true);
                        break;
                    case 8:
                        //8: concealed characters
                        break;
                    case 9:
                        //9: crossed-out (characters still legible but marked as to be deleted)
                        break;
                    case 10:
                        //10: primary (default) font
                        break;
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                        //11 - 19: first - ninth alternative font
                        break;
                    case 20:
                        //20: Fraktur (Gothic)
                        break;
                    case 21:
                        //21: doubly underlined
                        break;
                    case 22:
                        //22: normal colour or nomal intensity (neither bold nor faint)
                        break;
                    case 23:
                        //23: not italicized, not fraktur
                        break;
                    case 24:
                        //24: not underlined (neither singly nor doubly)
                        break;
                    case 25:
                        //25: steady (not blinking)
                        break;
                    //case 26:
                    //26: reserved
                    //break;
                    case 27:
                        //27: positive image
                        getCurrentDecoration().setNegaMode(false);
                        break;
                    case 28:
                        //28: revealed characters
                        break;
                    case 29:
                        //29: not crossed out
                        break;
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                        //30 - 37: colour display
                        getCurrentDecoration().setForeground(getCurrentPalette()[param0 - 30]);
                        break;
                    //case 38:
                    //38: reserved
                    //break;
                    case 39:
                        //39: default display colour
                        getCurrentDecoration().setForeground(getCurrentPalette()[8]);
                        break;
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 45:
                    case 46:
                    case 47:
                        //40 - 47: colour background
                        getCurrentDecoration().setBackground(getCurrentPalette()[param0 - 30]);
                        break;
                    //case 48:
                    //48: reserved
                    //break;
                    case 49:
                        //49: default background colour
                        getCurrentDecoration().setBackground(getCurrentPalette()[8]);
                        break;
                    //case 50:
                    //50: reserved
                    //break;
                    case 51:
                        //51: framed
                        break;
                    case 52:
                        //52: encircled
                        break;
                    case 53:
                        //53: overlined
                        break;
                    case 54:
                        //54: not framed, not encircled
                        break;
                    case 55:
                        //55: not overlined
                        break;
                    //case 56:
                    //case 57:
                    //case 58:
                    //case 59:
                    //56 - 59: reserved
                    //break;
                    case 60:
                        //60: ideogram underline or right side line
                        break;
                    case 61:
                        //61: ideogram double underline or double line on the right side
                        break;
                    case 62:
                        //62: ideogram overline or left side line
                        break;
                    case 63:
                        //63: ideogram double overline or double line on the left side
                        break;
                    case 64:
                        //64: ideogram stress marking
                        break;
                    case 65:
                        //65: cancels the effect of the rendition aspects established by parameter values 60 to 64
                        break;
                    default:
                        //Unknown: ignore it
                        System.out.printf("Unknown SGR %d;\n", param0);
                        break;
                    }

                    //For debug
                    //System.out.printf("Unknown SGR %d;\n", param0);
                }

                sequenceEnd = true;
                break;
            case ';':
                //Get next parameter
                tmp = readParam(ins, DEFAULT_NUMBER);
                if (tmp != DEFAULT_NUMBER) {
                    params.add(tmp);
                }

                sequenceEnd = false;
                break;
            case '?':
                //DEC format
                sequenceEnd = layoutEscapeCSIDEC(ins);
                break;
            default:
                //Unknown: Ignore it
                StringBuilder sb = new StringBuilder();
                sb.append("Unknown CSI ");
                for (Integer i : params) {
                    sb.append(String.format("%d; ", i));
                }
                System.out.printf("%s%c(0x%02x)\n", sb, csrChar, (int)csrChar);

                sequenceEnd = true;
                break;
            }
        }

        return true;
    }

    /**
     * CSI DEC format（ESC [ ?）を処理します。
     *
     * @param ins 文字列を入力するストリーム
     */
    protected boolean layoutEscapeCSIDEC(InputStream ins) throws IOException {
        int defNum = -1;
        int numN = readParam(ins, defNum);
        int numM = defNum;
        char csrChar;
        boolean sequenceEnd = false;

        while (!sequenceEnd) {
            csrChar = readNext(ins);
            switch (csrChar) {
            case ';':
                //Get next parameter
                numM = readParam(ins, defNum);

                sequenceEnd = false;
                break;
            default:
                //Unknown: Ignore it
                System.out.printf("Unknown CSR ? %d; %d; %c(0x%02x)\n", numN, numM, csrChar,
                        (int)csrChar);

                sequenceEnd = true;
                break;
            }
        }

        return true;
    }

    /**
     * 1文字を配置します。
     *
     * @param ins 文字列を入力するストリーム
     */
    protected void layoutNormalChar(InputStream ins) throws IOException {
        DecoratedChar dch;
        char c = readNext(ins);

        if (getCursorX() == getColumns() - 1 && needWrap) {
            nextLine();
        }

        dch = getDecoratedChar(getCursorX(), getCursorY());
        dch.copyAttributes(getCurrentDecoration());
        dch.setChar(c);
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
            char c = readNext(ins);

            switch (c) {
            case 0x07:
                //BELL
                //FIXME: ignored it
                break;
            case 0x08:
                //BS
                if (getCursorX() == 0 && needWrapBack) {
                    previousLine();
                }

                //NOTE: Need wrap the line at next char if we are in end of line
                needWrapBack = (getCursorX() == 0);
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
                layoutEscape(ins);
                break;
            default:
                //Other characters
                writeBack(c);
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
        DecoratedChar dch;
        Rectangle rscr, r;

        rscr = boxScreen.getContents();

        //Draw whole screen
        g.setColor(getBackground());
        g.fill3DRect(0, 0, getWidth(), getHeight(), false);
        for (int y = start; y < yEnd; y++) {
            for (int x = 0; x < getColumns(); x++) {
                dch = getDecoratedChar(x, y);

                if (dch.getChar() == 0) {
                    continue;
                }

                boxChar.setX(rscr.x + x * boxChar.getWidth());
                boxChar.setY(rscr.y + (y - start) * boxChar.getHeight());
                r = boxChar.getBounds();
                g.setColor(dch.getBackground());
                g.fillRect(r.x, r.y, r.width, r.height);

                r = boxChar.getContents();
                g.setColor(dch.getForeground());
                g.setFont(dch.getFont());
                g.drawString(String.valueOf(dch.getChar()),
                        r.x, r.y + r.height - 1);
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
        if (getChar(x, y) != 0) {
            r = boxChar.getContents();
            g.setColor(getBackground());
            g.drawString(String.valueOf(getChar(x, y)),
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
            advance = ascent / 2 + 1;
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
