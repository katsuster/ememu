package net.katsuster.ememu.ui;

import java.awt.*;

/**
 * 装飾付き文字を表すクラスです。
 *
 * @author katsuhiro
 */
public class DecoratedChar {
    public static final Color DEFAULT_FOREGROUND = Color.WHITE;
    public static final Color DEFAULT_BACKGROUND = Color.DARK_GRAY;
    public static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 10);

    private char ch;
    private Color foreground;
    private Color background;
    private boolean negaMode;
    private Font font;

    public DecoratedChar() {
        this((char)0, DEFAULT_FOREGROUND, DEFAULT_BACKGROUND,
                DEFAULT_FONT);
    }

    public DecoratedChar(char c, Color fg, Color bg, Font f) {
        ch = c;
        foreground = fg;
        background = bg;
        negaMode = false;
        font = f;
    }

    /**
     * 文字と装飾をコピーします。
     *
     * @param dc コピー元の装飾付き文字
     */
    public void copy(DecoratedChar dc) {
        ch = dc.ch;
        copyAttributes(dc);
    }

    /**
     * 文字以外の装飾をコピーします。
     *
     * @param dc コピー元の装飾付き文字
     */
    public void copyAttributes(DecoratedChar dc) {
        foreground = dc.foreground;
        background = dc.background;
        negaMode = dc.negaMode;
        font = dc.font;
    }

    /**
     * 文字を取得します。
     *
     * @return 文字
     */
    public char getChar() {
        return ch;
    }

    /**
     * 文字を設定します。
     *
     * @param c 文字
     */
    public void setChar(char c) {
        ch = c;
    }

    /**
     * 文字の色を取得します。
     *
     * @return 文字の色
     */
    public Color getForeground() {
        if (negaMode) {
            return background;
        } else {
            return foreground;
        }
    }

    /**
     * 文字の色を設定します。
     *
     * @param fg 文字の色
     */
    public void setForeground(Color fg) {
        foreground = fg;
    }

    /**
     * 文字の背景色を取得します。
     *
     * @return 文字の背景色
     */
    public Color getBackground() {
        if (negaMode) {
            return foreground;
        } else {
            return background;
        }
    }

    /**
     * 文字の背景色を設定します。
     *
     * @param bg 文字の背景色
     */
    public void setBackground(Color bg) {
        background = bg;
    }

    /**
     * ネガモード（前景色と背景色を入れ替える）を取得します。
     *
     * @return ネガモードならば true、そうでなければ false
     */
    public boolean getNegaMode() {
        return negaMode;
    }

    /**
     * ネガモード（前景色と背景色を入れ替える）を設定します。
     *
     * @param mode ネガモードならば true、そうでなければ false
     */
    public void setNegaMode(boolean mode) {
        negaMode = mode;
    }

    /**
     * 文字のフォントを取得します。
     *
     * @return 文字のフォント
     */
    public Font getFont() {
        return font;
    }

    /**
     * 文字のフォントを設定します。
     *
     * @param f 文字のフォント
     */
    public void setFont(Font f) {
        font = f;
    }
}

