package net.katsuster.ememu.ui;

import java.util.*;
import java.io.*;

/**
 * 出力内容を 2つに分岐させて印刷するクラスです。
 *
 * 出力の順番は、支流が先、本流が後となります。
 * 支流への出力でブロックした場合は、本流への出力が遅れることがあります。
 *
 * 支流への出力で発生した例外は全て無視され、
 * 本流への出力で発生した例外のみ呼び出し元にスローされます。
 *
 * @author katsuhiro
 */
public class ForkedPrintStream extends PrintStream {
    private PrintStream main;
    private PrintStream sub;

    /**
     * 2つの宛先に対して出力するストリームを作成します。
     *
     * @param main この出力ストリームの第一の宛先として使用するストリーム
     * @param sub この出力ストリームの第二の宛先として使用するストリーム
     */
    public ForkedPrintStream(PrintStream main, PrintStream sub) {
        super(new ByteArrayOutputStream());
        this.main = main;
        this.sub = sub;
    }

    /**
     * この出力ストリームに指定された文字を追加します。
     *
     * @param c 出力する文字
     * @return この出力ストリーム
     */
    @Override
    public PrintStream append(char c) {
        try {
            sub.append(c);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return main.append(c);
    }

    /**
     * この出力ストリームに指定された文字シーケンスを追加します。
     *
     * @param csq 出力する文字シーケンス。null の場合「null」という 4 文字が追加される
     * @return この出力ストリーム
     */
    @Override
    public PrintStream append(CharSequence csq) {
        try {
            sub.append(csq);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return main.append(csq);
    }

    /**
     * 指定された文字シーケンスのサブシーケンスをこの出力ストリームに追加します。
     *
     * 次の呼び出しと同じ結果が得られます。
     * <code>
     * out.print(csq.subSequence(start, end).toString())
     * </code>
     *
     * @param csq 出力する文字シーケンス。null の場合「null」という 4 文字が追加される
     * @param start 最初の文字のインデックス
     * @param end 最後の文字の、さらに次の文字のインデックス
     * @return この出力ストリーム
     */
    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        try {
            sub.append(csq, start, end);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return main.append(csq, start, end);
    }

    /**
     * ストリームをフラッシュし、そのエラー状況を確認します。
     *
     * @return InterruptedIOException ではなく IOException を検出した場合、
     * または setError メソッドが呼び出された場合は true、そうでなければ false
     */
    @Override
    public boolean checkError() {
        try {
            sub.checkError();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return main.checkError();
    }

    /**
     * ストリームを閉じます。
     */
    @Override
    public void close() {
        try {
            sub.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.close();
    }

    /**
     * ストリームをフラッシュします。
     */
    @Override
    public void flush() {
        try {
            sub.flush();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.flush();
    }

    /**
     * 指定された書式文字列および引数を使用して、
     * 書式付き文字列をこの出力ストリームの宛先に書き込みます。
     *
     * @param l 書式設定時に適用する locale、null の場合は locale は適用されません
     * @param format 書式文字列（詳細は Formatter クラスを参照）
     * @param args 0 個以上の引数、null を指定した場合は変換により動作が異なります
     * @return この出力ストリーム
     */
    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        try {
            sub.format(l, format, args);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return main.format(l, format, args);
    }

    /**
     * 指定された書式文字列および引数を使用して、
     * 書式付き文字列をこの出力ストリームの宛先に書き込みます。
     *
     * @param format 書式文字列（詳細は Formatter クラスを参照）
     * @param args 0 個以上の引数、null を指定した場合は変換により動作が異なります
     * @return この出力ストリーム
     */
    @Override
    public PrintStream format(String format, Object... args) {
        try {
            sub.format(format, args);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return main.format(format, args);
    }

    /**
     * boolean 型の値を出力します。
     *
     * @param b 出力する boolean 値
     */
    @Override
    public void print(boolean b) {
        try {
            sub.print(b);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.print(b);
    }

    /**
     * 文字を出力します。
     *
     * @param c 出力する文字
     */
    @Override
    public void print(char c) {
        try {
            sub.print(c);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.print(c);
    }

    /**
     * 文字の配列を出力します。
     *
     * @param s 出力する文字配列
     */
    @Override
    public void print(char[] s) {
        try {
            sub.print(s);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.print(s);
    }

    /**
     * 倍精度の浮動小数点数を出力します。
     *
     * @param d 出力する double 値
     */
    @Override
    public void print(double d) {
        try {
            sub.print(d);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.print(d);
    }

    /**
     * 浮動小数点数を出力します。
     *
     * @param f 出力する float 値
     */
    @Override
    public void print(float f) {
        try {
            sub.print(f);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.print(f);
    }

    /**
     * 整数を出力します。
     *
     * @param i 出力する int 値
     */
    @Override
    public void print(int i) {
        try {
            sub.print(i);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.print(i);
    }

    /**
     * long 整数を出力します。
     *
     * @param l 出力する long 値
     */
    @Override
    public void print(long l) {
        try {
            sub.print(l);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.print(l);
    }

    /**
     * オブジェクトを出力します。
     *
     * @param obj 出力するオブジェクト
     */
    @Override
    public void print(Object obj) {
        try {
            sub.print(obj);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.print(obj);
    }

    /**
     * 文字列を出力します。
     *
     * @param s 出力する文字列
     */
    @Override
    public void print(String s) {
        try {
            sub.print(s);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.print(s);
    }

    /**
     * 書式付き文字列を、指定された書式文字列と引数を使用し、
     * この出力ストリームに書き込む便利な方法です。
     *
     * @param l 書式設定時に適用する locale、null の場合は locale は適用されません
     * @param format 書式文字列（詳細は Formatter クラスを参照）
     * @param args 0 個以上の引数、null を指定した場合は変換により動作が異なります
     * @return この出力ストリーム
     *
     * @see java.util.Formatter
     */
    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        try {
            sub.printf(l, format, args);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return main.printf(l, format, args);
    }

    /**
     * 書式付き文字列を、指定された書式文字列と引数を使用し、
     * この出力ストリームに書き込む便利な方法です。
     *
     * @param format 書式文字列（詳細は Formatter クラスを参照）
     * @param args 0 個以上の引数、null を指定した場合は変換により動作が異なります
     * @return この出力ストリーム
     *
     * @see java.util.Formatter
     */
    @Override
    public PrintStream printf(String format, Object... args) {
        try {
            sub.printf(format, args);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return main.printf(format, args);
    }

    /**
     * 行の区切り文字列を書き込むことで、現在の行を終了させます。
     */
    @Override
    public void println() {
        try {
            sub.println();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.println();
    }

    /**
     * boolean 値を出力して、行を終了します。
     *
     * @param b 出力する boolean 値
     */
    @Override
    public void println(boolean b) {
        try {
            sub.println(b);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.println(b);
    }

    /**
     * 文字を出力して、行を終了します。
     *
     * @param c 出力する文字
     */
    @Override
    public void println(char c) {
        try {
            sub.println(c);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.println(c);
    }

    /**
     * 文字の配列を出力して、行を終了します。
     *
     * @param s 出力する文字配列
     */
    @Override
    public void println(char[] s) {
        try {
            sub.println(s);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.println(s);
    }

    /**
     * double を出力して、行を終了します。
     *
     * @param d 出力する double 値
     */
    @Override
    public void println(double d) {
        try {
            sub.println(d);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.println(d);
    }

    /**
     * float を出力して、行を終了します。
     *
     * @param f 出力する float 値
     */
    @Override
    public void println(float f) {
        try {
            sub.println(f);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.println(f);
    }

    /**
     * 整数を出力して、行を終了します。
     *
     * @param i 出力する int 値
     */
    @Override
    public void println(int i) {
        try {
            sub.println(i);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.println(i);
    }

    /**
     * long を出力して、行を終了します。
     *
     * @param l 出力する long 値
     */
    @Override
    public void println(long l) {
        try {
            sub.println(l);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.println(l);
    }

    /**
     * Object を出力して、行を終了します。
     *
     * @param obj 出力するオブジェクト
     */
    @Override
    public void println(Object obj) {
        try {
            sub.println(obj);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.println(obj);
    }

    /**
     * String を出力して、行を終了します。
     *
     * @param s 出力する文字列
     */
    @Override
    public void println(String s) {
        try {
            sub.println(s);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.println(s);
    }

    /**
     * オフセット位置 off から始まる指定されたバイト配列から、
     * このストリームに len バイトを書き込みます。
     *
     * @param buf バイト配列
     * @param off buf のどこから出力するか指定するオフセット
     * @param len 出力するバイト数
     */
    @Override
    public void write(byte[] buf, int off, int len) {
        try {
            sub.write(buf, off, len);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.write(buf, off, len);
    }

    /**
     * 指定されたバイトを、このストリームに書き込みます。
     *
     * @param b 出力するバイト値
     */
    @Override
    public void write(int b) {
        try {
            sub.write(b);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        main.write(b);
    }
}
