package net.katsuster.semu.ui;

import java.lang.reflect.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

/**
 * 標準出力の代わりに用いるテキスト表示領域です。
 *
 * System.out を置き換えて使います。
 *
 * @author katsuhiro
 */
public class SystemPane extends JPanel {
    //out -> outPout -> outPin -> outRead -> outText
    private static PipedOutputStream outPout = new PipedOutputStream();
    private static PipedInputStream outPin = new PipedInputStream(16384);
    private static InputStreamReader outRead = new InputStreamReader(outPin);

    private static JTextArea outText = new JTextArea();
    private static JScrollPane outScr = new JScrollPane(outText);
    private static Thread outThread = new Thread(new OutRunner());

    //標準出力の代わりに用いる出力用ストリームです
    public static PrintStream out = new PrintStream(outPout);

    public SystemPane() {
        super(true);

        try {
            outPout.connect(outPin);
        } catch (IOException e) {
            //ignore
        }

        setLayout(new BorderLayout());
        add(outScr);
        outScr.setPreferredSize(new Dimension(500, 500));
        outThread.start();
    }

    static class OutRunner implements Runnable {
        public OutRunner() {
            //do nothing
        }

        @Override
        public void run() {
            StringBuffer b = new StringBuffer();

            while (true) {
                try {
                    b.setLength(0);
                    do {
                        int ch = SystemPane.outRead.read();
                        if (ch == -1) {
                            //EOF
                            break;
                        }

                        b.append((char)ch);
                    } while (SystemPane.outRead.ready());
                } catch (IOException e) {
                    //ignore
                }

                try {
                    String s = b.toString();

                    System.out.print(s);
                    SwingUtilities.invokeAndWait(new StringAppender(s));
                } catch (InterruptedException e) {
                    //ignore
                } catch (InvocationTargetException e) {
                    //ignore
                }
            }
        }

        private class StringAppender implements Runnable {
            private String s;

            public StringAppender(String str) {
                s = str;
            }

            public void run() {
                SystemPane.outText.append(s);
            }
        }
    }
}
