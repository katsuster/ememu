package net.katsuster.ememu.ui;

import static net.katsuster.ememu.ui.PropertyPanel.*;

/**
 * Linux 起動時に渡すオプション。
 */
public class LinuxOption extends PropertyPanels {
    /** エミュレートするアーキテクチャ */
    public static final String EMU_ARCH = "emu.arch";
    public static final String LINUX_DTB_ENABLE = "emu.linux.dtb.enable";
    /** Linux Device Tree Blob イメージファイルのパス */
    public static final String LINUX_DTB = "linux.dtb";
    /** Linux カーネルイメージファイルのパス */
    public static final String LINUX_KIMAGE = "linux.kimage";
    public static final String LINUX_INITRD_ENABLE = "linux.initrd.enable";
    /** ブート時にカーネルに渡す Initrd/InitramFS イメージファイルのパス */
    public static final String LINUX_INITRD = "linux.initrd";
    /** ブート時にカーネルに渡すコマンドライン */
    public static final String LINUX_CMDLINE = "linux.cmdline";

    public LinuxOption() {
        addLinuxOptions(this);
    }

    public static void addLinuxOptions(PropertyPanels p) {
        p.setProperty(EMU_ARCH, "Architecture", TYPE_STRING, "");

        p.setProperty(LINUX_DTB_ENABLE, "Use Device Tree", TYPE_BOOLEAN, "false");
        p.setProperty(LINUX_DTB, "Device Tree Image", TYPE_URI, "");
        p.setAttribute(LINUX_DTB, URI_FILTER_TITLE, "Device Tree Blob image (*.dtb)");
        p.setAttribute(LINUX_DTB, URI_FILTER, "dtb");

        p.setProperty(LINUX_KIMAGE, "Kernel Image", TYPE_URI, "");

        p.setProperty(LINUX_INITRD_ENABLE, "Use Initrd Image", TYPE_BOOLEAN, "true");
        p.setProperty(LINUX_INITRD, "Initrd Image", TYPE_URI, "");
        p.setAttribute(LINUX_INITRD, URI_FILTER_TITLE, "Initrd image (*.cpio, *.gz)");
        p.setAttribute(LINUX_INITRD, URI_FILTER, "cpio", "gz");

        p.setProperty(LINUX_CMDLINE, "Command line", TYPE_STRING, "");
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
                getValue(LINUX_INITRD),
                getValue(LINUX_KIMAGE),
                getValue(LINUX_INITRD),
                getValue(LINUX_CMDLINE));
    }
}
