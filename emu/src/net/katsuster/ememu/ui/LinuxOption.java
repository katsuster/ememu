package net.katsuster.ememu.ui;

import java.io.*;
import java.net.*;

/**
 * Linux 起動時に渡すオプション。
 */
public class LinuxOption extends PropertyPanels {
    public static final String EMU_ARCH = "emu.arch";
    public static final String LINUX_DTB_ENABLE = "emu.linux.dtb.enable";
    public static final String LINUX_DTB = "linux.dtb";
    public static final String LINUX_KIMAGE = "linux.kimage";
    public static final String LINUX_INITRD_ENABLE = "linux.initrd.enable";
    public static final String LINUX_INITRD = "linux.initrd";
    public static final String LINUX_CMDLINE = "linux.cmdline";

    public LinuxOption() {
        setProperty(EMU_ARCH, "Architecture", "String", "");
        setProperty(LINUX_DTB_ENABLE, "Use Device Tree", "Boolean", "false");
        setProperty(LINUX_DTB, "Device Tree Image", "File", "");
        setProperty(LINUX_KIMAGE, "Kernel Image", "File", "");
        setProperty(LINUX_INITRD_ENABLE, "Use Initrd Image", "Boolean", "true");
        setProperty(LINUX_INITRD, "Initrd Image", "File", "");
        setProperty(LINUX_CMDLINE, "Command line", "String", "");
    }

    /**
     * エミュレートするアーキテクチャを取得します。
     *
     * @return アーキテクチャ
     */
    public String getArch() {
        return getProperty(EMU_ARCH).getValue();
    }

    /**
     * エミュレートするアーキテクチャをを設定します。
     *
     * @param str アーキテクチャ
     */
    public void setArch(String str) {
        setValue(EMU_ARCH, str);
    }

    /**
     * Linux Device Tree Blob イメージファイルの位置を取得します。
     *
     * @return Linux Device Tree Blob イメージファイルの URI
     */
    public URI getDeviceTreeImage() {
        return toURI(getProperty(LINUX_DTB).getValue());
    }

    /**
     * Linux Device Tree Blob イメージファイルの位置を設定します。
     *
     * @param file Linux Device Tree Blob イメージファイルパス
     */
    public void setDeviceTreeImage(File file) {
        setValue(LINUX_DTB, file.toURI().toString());
    }

    /**
     * Linux Device Tree Blob イメージファイルの位置を設定します。
     *
     * @param uri Linux Device Tree Blob イメージファイルの URI
     */
    public void setDeviceTreeImage(URI uri) {
        setValue(LINUX_DTB, uri.toString());
    }

    /**
     * Linux カーネルイメージファイルの位置を取得します。
     *
     * @return Linux カーネルイメージファイルの URI
     */
    public URI getKernelImage() {
        return toURI(getProperty(LINUX_KIMAGE).getValue());
    }

    /**
     * Linux カーネルイメージファイルの位置を設定します。
     *
     * @param file Linux カーネルイメージファイルパス
     */
    public void setKernelImage(File file) {
        setValue(LINUX_KIMAGE, file.toURI().toString());
    }

    /**
     * Linux カーネルイメージファイルの位置を設定します。
     *
     * @param uri Linux カーネルイメージファイルの URI
     */
    public void setKernelImage(URI uri) {
        setValue(LINUX_KIMAGE, uri.toString());
    }

    /**
     * ブート時にカーネルに渡す Initrd/InitramFS イメージファイルの位置を取得します。
     *
     * @return Initrd/InitramFS イメージファイルの URI
     */
    public URI getInitrdImage() {
        return toURI(getProperty(LINUX_INITRD).getValue());
    }

    /**
     * ブート時にカーネルに渡す Initrd/InitramFS イメージファイルの位置を設定します。
     *
     * @param file Initrd/InitramFS イメージファイルパス
     */
    public void setInitrdImage(File file) {
        setValue(LINUX_INITRD, file.toURI().toString());
    }

    /**
     * ブート時にカーネルに渡す Initrd/InitramFS イメージファイルの位置を設定します。
     *
     * @param uri Initrd/InitramFS イメージファイルの URI
     */
    public void setInitrdImage(URI uri) {
        setValue(LINUX_INITRD, uri.toString());
    }

    /**
     * ブート時にカーネルに渡すコマンドラインを取得します。
     *
     * @return コマンドライン文字列
     */
    public String getCommandLine() {
        return getProperty(LINUX_CMDLINE).getValue();
    }

    /**
     * ブート時にカーネルに渡すコマンドラインを設定します。
     *
     * @param str コマンドライン文字列
     */
    public void setCommandLine(String str) {
        setValue(LINUX_CMDLINE, str);
    }

    /**
     * オプションの概要を文字列で取得します。
     *
     * @return オプションの概要
     */
    @Override
    public String toString() {
        return String.format("%s: \n" +
                        "  Arch        : '%s'\n" +
                        "  Kernel      : '%s'\n" +
                        "  Initrd      : '%s'\n" +
                        "  Command Line: '%s'",
                getClass().getSimpleName(),
                getArch(),
                getKernelImage().toString(),
                getInitrdImage().toString(),
                getCommandLine());
    }
}
