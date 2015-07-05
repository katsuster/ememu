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
    private URI initram;
    private String cmdline;

    public LinuxOption() {
        try {
            kimage = new URI("");
            initram = new URI("");
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
     * ブート時にカーネルに渡す initramfs イメージファイルの位置を取得します。
     *
     * @return initramfs イメージファイルの URI
     */
    public URI getInitramfsImage() {
        return initram;
    }

    /**
     * ブート時にカーネルに渡す initramfs イメージファイルの位置を設定します。
     *
     * @param file initramfs イメージファイルパス
     */
    public void setInitramfsImage(File file) {
        initram = file.toURI();
    }

    /**
     * ブート時にカーネルに渡す initramfs イメージファイルの位置を設定します。
     *
     * @param uri initramfs イメージファイルの URI
     */
    public void setInitramfsImage(URI uri) {
        initram = uri;
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
}
