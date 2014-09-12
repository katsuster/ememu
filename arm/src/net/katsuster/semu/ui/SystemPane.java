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
    //out -> outInner -> outPout -> outPin -> outRead -> outText
    //    `-> System.out
    private static final PipedOutputStream outPout = new PipedOutputStream();
    private static final PrintStream outInner = new PrintStream(outPout);

    private static final PipedInputStream outPin = new PipedInputStream(16384);
    private static final InputStreamReader outRead = new InputStreamReader(outPin);
    private static final JTextArea outText = new JTextArea();
    private static final Thread outDrainer = new Thread(new TextDrainer());

    private static final JScrollPane outScr = new JScrollPane(outText);

    //標準出力の代わりに用いる出力用ストリームです
    public static final PrintStream out = new ForkedPrintStream(outInner, System.out);

    public SystemPane() {
        super(new BorderLayout(), true);

        try {
            outPout.connect(outPin);
        } catch (IOException e) {
            //ignore
        }

        add(outScr);
        outScr.setPreferredSize(new Dimension(320, 240));
        outDrainer.start();
    }

    public void clear() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                outText.setText("");
            }
        });
    }

    static private class TextDrainer implements Runnable {
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
                        int ch = SystemPane.outRead.read();
                        if (ch == -1) {
                            //EOF
                            break output;
                        }

                        b.append((char) ch);
                    } while (SystemPane.outRead.ready());

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

            public void run() {
                SystemPane.outText.append(s);
            }
        }
    }
}
