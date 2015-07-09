package net.katsuster.ememu.ui;

import java.io.*;
import java.net.*;

/**
 * Linux 起動時に渡すオプション。
 *
 * @author katsuhiro
 */
public class LinuxOption {
    private URI kimage;
    private URI initrd;
    private String cmdline;

    public LinuxOption() {
        try {
            kimage = new URI("");
            initrd = new URI("");
        } catch (URISyntaxException ex) {
            //ignore
        }
        cmdline = "";
    }

    /**
     * Linux カーネルイメージファイルの位置を取得します。
     *
     * @return Linux カーネルイメージファイルの URI
     */
    public URI getKernelImage() {
        return kimage;
    }

    /**
     * Linux カーネルイメージファイルの位置を設定します。
     *
     * @param file Linux カーネルイメージファイルパス
     */
    public void setKernelImage(File file) {
        kimage = file.toURI();
    }

    /**
     * Linux カーネルイメージファイルの位置を設定します。
     *
     * @param uri Linux カーネルイメージファイルの URI
     */
    public void setKernelImage(URI uri) {
        kimage = uri;
    }

    /**
     * ブート時にカーネルに渡す Initrd/InitramFS イメージファイルの位置を取得します。
     *
     * @return Initrd/InitramFS イメージファイルの URI
     */
    public URI getInitrdImage() {
        return initrd;
    }

    /**
     * ブート時にカーネルに渡す Initrd/InitramFS イメージファイルの位置を設定します。
     *
     * @param file Initrd/InitramFS イメージファイルパス
     */
    public void setInitrdImage(File file) {
        initrd = file.toURI();
    }

    /**
     * ブート時にカーネルに渡す Initrd/InitramFS イメージファイルの位置を設定します。
     *
     * @param uri Initrd/InitramFS イメージファイルの URI
     */
    public void setInitrdImage(URI uri) {
        initrd = uri;
    }

    /**
     * ブート時にカーネルに渡すコマンドラインを取得します。
     *
     * @return コマンドライン文字列
     */
    public String getCommandLine() {
        return cmdline;
    }

    /**
     * ブート時にカーネルに渡すコマンドラインを設定します。
     *
     * @param str コマンドライン文字列
     */
    public void setCommandLine(String str) {
        cmdline = str;
    }

    /**
     * オプションの概要を文字列で取得します。
     *
     * @return オプションの概要
     */
    @Override
    public String toString() {
        return String.format("%s: \n" +
                        "  Kernel      : '%s'\n" +
                        "  Initrd      : '%s'\n" +
                        "  Command Line: '%s'",
                getClass().getSimpleName(),
                getKernelImage().toString(),
                getInitrdImage().toString(),
                getCommandLine());
    }
}
