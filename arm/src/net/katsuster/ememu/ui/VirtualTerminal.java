package net.katsuster.semu.ui;

import java.lang.reflect.*;
import java.io.*;
import java.awt.event.*;
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

    public VirtualTerminal() throws IOException {
        inPin = new PipedInputStream();
        inPout = new PipedOutputStream(inPin);

        outPin = new PipedInputStream();
        outPout = new PipedOutputStream(outPin);

        JScrollPane outScr = new JScrollPane(outText);
        outText = new JTextArea();
        outScr.add(outText);
        add(outScr);
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
                while (true) {
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
