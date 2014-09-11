package net.katsuster.semu.arm;

import java.io.*;
import javax.swing.*;

/**
 * 標準出力の代わりに用いるテキスト表示領域です。
 *
 * System.out を置き換えて使います。
 *
 * @author katsuhiro
 */
public class SystemPane {
    //out -> outPout -> outPin -> outRead -> outText
    static private PipedInputStream outPin = new PipedInputStream(16384);
    static private PipedOutputStream outPout = new PipedOutputStream();
    static private InputStreamReader outRead = new InputStreamReader(outPin);

    static private Thread outThread = new Thread(new OutRunner());
    static private JTextArea outText = new JTextArea();

    //標準出力の代わりに用いる出力用ストリームです
    static PrintStream out = new PrintStream(outPout);

    public SystemPane() {
        try {
            outPout.connect(outPin);
        } catch (IOException e) {
            //ignore
        }
    }

    static class OutRunner implements Runnable {
        public OutRunner() {
            //do nothing
        }

        @Override
        public void run() {
            int ch;
            char[] chr = new char[1];

            while (true) {
                try {
                    ch = SystemPane.outRead.read();
                    if (ch == -1) {
                        //EOF
                    }

                    chr[0] = (char)ch;
                    SystemPane.outText.append(new String(chr));
                } catch (IOException e) {
                    //ignore
                }
            }
        }
    }
}
