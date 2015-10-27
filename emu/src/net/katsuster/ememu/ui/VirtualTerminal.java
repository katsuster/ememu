package net.katsuster.ememu.ui;

import java.awt.*;
import java.lang.reflect.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * 仮想端末。
 *
 * @author katsuhiro
 */
public class VirtualTerminal extends JPanel
        implements MouseWheelListener, KeyListener {
    //端末への出力の表示領域パネル
    private VTInnerPane vt;
    //表示領域の右端スクロールバー
    private JScrollBar scr;

    //端末への入力をクラスの外へ渡すためのストリーム
    //Keyboard -> KeyListener -> inPout -> inPin -> (other class)
    private PipedInputStream inPin;
    private PipedOutputStream inPout;

    //端末への出力を表示領域へ渡すためのストリーム
    //(other class) -> outPout -> outPinRaw -> outPin -> (virtual terminal)
    private PipedInputStream outPinRaw;
    private BufferedInputStream outPin;
    private PipedOutputStream outPout;

    //端末への表示を行うスレッド
    private Thread outDrainer;
    //スレッドを停止させるためのフラグ
    private boolean halted = false;

    //表示を開始する行数
    private int startLine;

    public VirtualTerminal() {
        super(new BorderLayout(), true);

        setFocusable(true);
        addMouseWheelListener(this);
        addKeyListener(this);

        //中央にバイナリデータ表示パネルを配置する
        vt = new VTInnerPane(this);
        vt.setForeground(Color.GRAY);
        vt.setBackground(Color.BLACK);
        vt.setOpaque(false);
        vt.setFont(new Font(Font.MONOSPACED, 0, 12));
        add(vt, BorderLayout.CENTER);

        //右端にスクロールバーを配置する
        scr = new JScrollBar();
        scr.getModel().addChangeListener(vt);
        add(scr, BorderLayout.EAST);

        //入出力用のストリームを作成する
        try {
            inPin = new PipedInputStream();
            inPout = new PipedOutputStream(inPin);

            outPinRaw = new PipedInputStream();
            outPin = new BufferedInputStream(outPinRaw);
            outPout = new PipedOutputStream(outPinRaw);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new IllegalStateException(e);
        }

        //端末への表示を行うスレッドを作成する
        outDrainer = new Thread(new TextDrainer());
        outDrainer.start();
    }

    /**
     * スクロールバーを取得します。
     *
     * @return スクロールバー
     */
    public JScrollBar getScrollBar() {
        return scr;
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
     *
     * @return 停止すべきならば true、そうでなければ false
     */
    public boolean shouldHalt() {
        return halted;
    }

    /**
     * 今すぐスレッドを停止すべきかどうかを設定します。
     *
     * @param b 今すぐスレッドを停止すべきなら true、
     *          そうでなければ false
     */
    public void setHalt(boolean b) {
        halted = b;
    }

    /**
     * ストリームを閉じ、リソースを解放します。
     */
    public void close() {
        try {
            setHalt(true);
            inPin.close();
            inPout.close();
            outPin.close();
            outPout.close();
        } catch (IOException e) {
            //ignore
        }
    }

    /**
     * 表示を開始する行数を取得します。
     *
     * @return 表示を開始する行数
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * 表示を開始する行数を設定します。
     *
     * @param l 表示を開始する行数
     */
    public void setStartLine(int l) {
        startLine = l;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scr.setValue(scr.getValue() + e.getWheelRotation() * 3);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //do nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keycode = e.getKeyCode();
        char keychar = e.getKeyChar();
        int dat = 0;
        boolean shift = false, ctrl = false, alt = false;
        boolean valid = true;

        switch (keycode) {
        case KeyEvent.VK_ALT:
        case KeyEvent.VK_CONTROL:
        case KeyEvent.VK_SHIFT:
            //ignore
            return;
        }
        //System.out.printf("press:code:0x%02x, char:0x%02x\n", e.getKeyCode(), (int)e.getKeyChar());

        if ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK) {
            shift = true;
        }
        if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
            ctrl = true;
        }
        if ((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK) {
            alt = true;
        }

        if (!ctrl && !alt) {
            dat = keychar;
            if (keychar == 0xffff) {
                valid = false;
            }
        } else if (!shift && ctrl && !alt) {
            //Ctrl + @, A...Z, [, \, ], ^, _
            if (KeyEvent.VK_A <= keycode && keycode <= KeyEvent.VK_Z) {
                dat = 0x01 + keycode - KeyEvent.VK_A;
            } else {
                switch (keycode) {
                case KeyEvent.VK_AT:
                    dat = 0x00;
                    break;
                case KeyEvent.VK_OPEN_BRACKET:
                case KeyEvent.VK_BACK_SLASH:
                case KeyEvent.VK_CLOSE_BRACKET:
                    dat = 0x1b + keycode - KeyEvent.VK_OPEN_BRACKET;
                    break;
                case KeyEvent.VK_CIRCUMFLEX:
                    dat = 0x1e;
                    break;
                default:
                    valid = false;
                    break;
                }
            }
        } else {
            dat = keychar;
            if (keychar == 0xffff) {
                valid = false;
            }
        }

        if (valid) {
            try {
                inPout.write(dat);
            } catch (IOException ex) {
                //ignored
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //do nothing
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
                int i = 0;
                while (!shouldHalt()) {
                    vt.layoutChars(outPin);

                    try {
                        SwingUtilities.invokeAndWait(new Painter(vt));
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

        private class Painter implements Runnable {
            VTInnerPane pane;

            public Painter(VTInnerPane p) {
                pane = p;
            }

            @Override
            public void run() {
                pane.repaint();
            }
        }
    }
}
