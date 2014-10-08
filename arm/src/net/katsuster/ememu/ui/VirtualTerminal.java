package net.katsuster.ememu.ui;

import java.awt.*;
import java.lang.reflect.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * 仮想端末。
 *
 * @author katsuhiro
 */
public class VirtualTerminal extends JPanel {
    //端末への出力の表示領域パネル
    private VTInner vt;
    //表示領域の右端スクロールバー
    private JScrollBar scr;

    //イベント処理
    private VTMouseIn mouseIn;
    private VTKeyIn keyIn;

    //端末への入力をクラスの外へ渡すためのストリーム
    //Keyboard -> KeyListener -> inPout -> inPin -> (other class)
    private PipedInputStream inPin;
    private PipedOutputStream inPout;

    //端末への出力を表示領域へ渡すためのストリーム
    //(other class) -> outPout -> outPin -> (virtual terminal)
    private PipedInputStream outPin;
    private PipedOutputStream outPout;

    //端末への表示を行うスレッド
    private Thread outDrainer;
    //スレッドを停止させるためのフラグ
    private boolean halted = false;

    //表示を開始する行数
    private int lines;

    public VirtualTerminal() {
        super(new BorderLayout(), true);

        //中央にバイナリデータ表示パネルを配置する
        vt = new VTInner();
        vt.setPreferredSize(new Dimension(160, 240));
        add(vt, BorderLayout.CENTER);
        vt.setFont(new Font(Font.MONOSPACED, 0, 12));

        //右端にスクロールバーを配置する
        scr = new JScrollBar();
        scr.getModel().addChangeListener(vt);
        add(scr, BorderLayout.EAST);

        //マウス入力リスナ
        mouseIn = new VTMouseIn();
        vt.addMouseListener(mouseIn);

        //キー入力リスナ
        keyIn = new VTKeyIn();
        vt.addKeyListener(keyIn);

        //入出力用のストリームを作成する
        try {
            inPin = new PipedInputStream();
            inPout = new PipedOutputStream(inPin);

            outPin = new PipedInputStream();
            outPout = new PipedOutputStream(outPin);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }

        //端末への表示を行うスレッドを作成する
        outDrainer = new Thread(new TextDrainer());
        outDrainer.start();
    }

    /**
     * 仮想端末への入力を受け取るためのストリームを取得します。
     *
     * @return 仮想端末への入力を受け取るためのストリーム
     */
    public InputStream getInputStream() {
        return inPin;
    }

    /**
     * 仮想端末に出力するためのストリームを取得します。
     *
     * @return 仮想端末に出力するためのストリーム
     */
    public OutputStream getOutputStream() {
        return outPout;
    }

    /**
     * 今すぐスレッドを停止すべきかどうかを取得します。
     */
    public boolean shouldHalt() {
        return halted;
    }

    /**
     * 今すぐスレッドを停止すべきであることを通知します。
     */
    public void halt() {
        halted = true;
    }

    /**
     * 表示を開始する行数を取得します。
     *
     * @return 表示を開始する行数
     */
    public int getLines() {
        return lines;
    }

    /**
     * 表示を開始する行数を設定します。
     * @param l 表示を開始する行数
     */
    public void setLines(int l) {
        lines = l;
    }

    /**
     * マウスからの入力を受け付けるクラスです。
     */
    private class VTMouseIn implements MouseListener {
        public VTMouseIn() {

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            vt.setFocusable(true);
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    /**
     * キーボードからの入力を inPout に出力するクラスです。
     */
    private class VTKeyIn implements KeyListener {
        public VTKeyIn() {

        }

        @Override
        public void keyTyped(KeyEvent e) {

            try {
                inPout.write(e.getKeyChar());
            } catch (IOException ex) {
                //ignored
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            int dat = 0;
            int onmask, offmask;
            boolean shift = false, ctrl = false, alt = false;

            switch (code) {
            case KeyEvent.VK_ALT:
            case KeyEvent.VK_CONTROL:
            case KeyEvent.VK_SHIFT:
                //ignore
                return;
            }

            onmask = KeyEvent.SHIFT_DOWN_MASK;
            offmask = KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK;
            if ((e.getModifiersEx() & (onmask | offmask)) == onmask) {
                //Shift のみ
                shift = true;
            }

            if (KeyEvent.VK_A <= code && code <= KeyEvent.VK_Z) {
                if (shift) {
                    dat = 'A';
                } else {
                    dat = 'a';
                }
                dat += code - KeyEvent.VK_A;
            }

            /*try {
                inPout.write(dat);
            } catch (IOException ex) {
                //ignored
            }*/
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    /**
     * outPout に出力された文字を画面に印字するクラスです。
     */
    private class TextDrainer implements Runnable {
        public TextDrainer() {
            //do nothing
        }

        @Override
        public void run() {
            try {
                StringBuffer b = new StringBuffer();

                output:
                while (!shouldHalt()) {
                    b.setLength(0);
                    do {
                        int ch = outPin.read();
                        if (ch == -1) {
                            //EOF
                            break output;
                        }

                        b.append((char) ch);
                    } while (outPin.available() != 0);

                    try {
                        SwingUtilities.invokeAndWait(new StringAppender(b.toString()));
                    } catch (InterruptedException e) {
                        e.printStackTrace(System.err);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace(System.err);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }

        private class StringAppender implements Runnable {
            private String s;

            public StringAppender(String str) {
                s = str;
            }

            @Override
            public void run() {
                vt.append(s);
                //outText.append(s);
            }
        }
    }


















    /**
     * 端末への出力を表示するパネルです。
     *
     * @author katsuhiro
     */
    protected class VTInner extends JComponent
            implements ChangeListener {
        private static final long serialVersionUID = 1L;

        //端末への出力データ
        private StringBuffer buf;

        //1行の桁数
        private int columns;
        //スクロールにより巻き戻せる最大の行数
        private int maxLines;
        //現在の行数
        private int currentLine;
        //カーソルの位置
        private int cursorX;
        private int cursorY;
        //レイアウト枠
        private char[][] layoutBox;

        public VTInner() {
            super();

            buf = new StringBuffer();
            columns = 80;
            maxLines = 300;
            currentLine = 0;
            cursorX = 0;
            cursorY = 0;
            layoutBox = new char[getColumns()][getMaxLines()];
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
                    scr.setMaximum(currentLine);
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

        protected void drawAll(Graphics g, int start) {
            int yLines = getHeight() / 16;
            int yEnd = Math.min(start + yLines, getMaxLines());

            for (int y = start; y < yEnd; y++) {
                for (int x = 0; x < getColumns(); x++) {
                    if (layoutBox[x][y] == 0) {
                        continue;
                    }

                    g.drawString(String.valueOf(layoutBox[x][y]),
                            x * 8, (y - start + 1) * 16);
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            drawAll(g, getLines());
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            setLines(scr.getValue());

            repaint();
        }
    }
}
