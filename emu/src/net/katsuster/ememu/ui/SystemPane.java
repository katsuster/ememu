package net.katsuster.ememu.ui;

import java.lang.reflect.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

/**
 * 指定した PrintStream への出力をフォークし、
 * テキスト領域に表示するクラスです。
 *
 * System.out の出力をフォークするために使います。
 *
 * @author katsuhiro
 */
public class SystemPane extends JPanel {
    private PipedInputStream outPin;
    private InputStreamReader outRead;
    private JTextArea outText;
    private JScrollPane outScr;

    private PipedOutputStream outPout;
    private PrintStream outInner;

    private PrintStream out;

    private Thread outDrainer;

    /**
     * systemOut への出力をフォークして、systemOut とテキスト領域に
     * 同時に出力するオブジェクトを生成します。
     *
     * @param systemOut テキスト領域と同時に出力するストリーム
     */
    public SystemPane(PrintStream systemOut) {
        super(new BorderLayout(), true);

        //out -> outInner -> outPout -> outPin -> outRead -> outText
        //    `-> systemOut
        outPin = new PipedInputStream(16384);
        outRead = new InputStreamReader(outPin);
        outText = new JTextArea();
        outScr = new JScrollPane(outText);
        outScr.setPreferredSize(new Dimension(320, 240));
        add(outScr);

        try {
            outPout = new PipedOutputStream();
            outInner = new PrintStream(outPout);
            outPout.connect(outPin);
        } catch (IOException e) {
            //ignore
        }

        out = new ForkedPrintStream(outInner, systemOut);

        outDrainer = new Thread(new TextDrainer());
        outDrainer.setName(TextDrainer.class.getName());
        outDrainer.start();
    }

    /**
     * 指定した PrintStream とテキスト領域に同時に出力するための、
     * PrintStream を返します。
     *
     * @return 指定した PrintStream とテキスト領域に出力するためのストリーム
     */
    public PrintStream getOutputStream() {
        return out;
    }

    /**
     * テキスト領域に表示済みの文字列を消去します。
     */
    public void clear() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                outText.setText("");
            }
        });
    }

    /**
     * ストリームを閉じ、リソースを解放します。
     */
    public void close() {
        try {
            outPin.close();
            outPout.close();
        } catch (IOException e) {
            //ignore
        }
    }

    /**
     * out に出力された文字を、テキスト領域に表示するためのクラスです。
     *
     * Swing は複数スレッドからのアクセスを想定していないため、
     * テキスト領域に Swing スレッド以外から文字列を追加することはできません。
     * このクラスを文字列を受け取る専用のスレッドとして生成し、
     * Swing スレッドへの文字列の受け渡しを仲介しています。
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
                        int ch = outRead.read();
                        if (ch == -1) {
                            //EOF
                            break output;
                        }

                        b.append((char) ch);
                    } while (outRead.ready());

                    try {
                        SwingUtilities.invokeAndWait(new StringAppender(b.toString()));
                    } catch (InterruptedException | InvocationTargetException e) {
                        e.printStackTrace(System.err);
                        //ignored
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
                throw new IllegalStateException(e);
            }
        }

        /**
         * テキストエリアに指定された文字列を追加するためのタスクです。
         */
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
