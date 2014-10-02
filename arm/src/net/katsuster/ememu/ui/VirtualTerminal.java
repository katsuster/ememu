package net.katsuster.ememu.ui;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.io.*;
import javax.swing.*;

/**
 * 仮想端末。
 *
 * @author katsuhiro
 */
public class VirtualTerminal extends JPanel {
    //Keyboard -> KeyListener -> inPout -> inPin -> (other class)
    private PipedInputStream inPin;
    private PipedOutputStream inPout;

    //(other class) -> outPout -> outPin -> (virtual terminal)
    private PipedInputStream outPin;
    private PipedOutputStream outPout;
    private JTextArea outText;
    private Thread outDrainer;

    private boolean halted = false;

    public VirtualTerminal() {
        super(new BorderLayout(), true);

        try {
            inPin = new PipedInputStream();
            inPout = new PipedOutputStream(inPin);

            outPin = new PipedInputStream();
            outPout = new PipedOutputStream(outPin);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }

        outText = new JTextArea();
        JScrollPane outScr = new JScrollPane(outText);
        outScr.setPreferredSize(new Dimension(160, 240));

        add(outScr);

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
     * キーボードからの入力を inPout に出力するクラスです。
     */
    private class KeyInListener implements KeyListener {
        public KeyInListener() {

        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            int c = e.getKeyCode();

            try {
                inPout.write(c);
            } catch (IOException ex) {
                //ignored
            }
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
                outText.append(s);
            }
        }
    }
}
