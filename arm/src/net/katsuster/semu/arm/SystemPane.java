package net.katsuster.semu.arm;

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
    static private PipedInputStream outPin = new PipedInputStream(16384);
    static private PipedOutputStream outPout = new PipedOutputStream();
    static private InputStreamReader outRead = new InputStreamReader(outPin);

    static private Thread outThread = new Thread(new OutRunner());
    static private JTextArea outText = new JTextArea();
    static private JScrollPane outScr = new JScrollPane(outText);

    //標準出力の代わりに用いる出力用ストリームです
    static PrintStream out = new PrintStream(outPout);

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
