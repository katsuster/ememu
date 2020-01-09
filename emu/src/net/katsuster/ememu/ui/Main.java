package net.katsuster.ememu.ui;

import java.io.*;
import java.net.*;
import java.awt.*;

/**
 * エミュレータのテキスト画面、ログ表示用のクラス。
 */
public class Main {
    public static void usage(String[] args) {
        System.out.println("Usage:\n" +
                "    ememu [-h] arch image initramfs [cmdline]\n" +
                "  Arguments:\n" +
                "    -h       : Show this help messages.\n" +
                "    arch     : Architecture of CPU.\n"  +
                "    image    : Linux kernel image file.\n" +
                "    initramfs: initrd or initramfs image file.\n" +
                "    cmdline  : Command line parameters to Linux kernel.\n");
    }

    public static void main(String[] args) {
        PropertyPanels opts = new PropertyPanels();

        LinuxOption.addLinuxOptions(opts);
        ProxyOption.addProxyOptions(opts);

        opts.setValue(LinuxOption.EMU_ARCH, "arm");
        opts.setAsURI(LinuxOption.LINUX_KIMAGE, "http://www.katsuster.net/contents/java/ememu/Image-4.4.57");
        opts.setAsURI(LinuxOption.LINUX_INITRD, "http://www.katsuster.net/contents/java/ememu/initramfs.gz");
        opts.setValue(LinuxOption.LINUX_CMDLINE, "console=ttyAMA0 mem=64M root=/dev/ram init=/bin/init debug printk.time=1");

        if (args.length >= 1) {
            if (args[0].equals("-h") || args[0].equals("--help") ||
                    args[0].equals("/?")) {
                usage(args);
                return;
            }
            opts.setValue(LinuxOption.EMU_ARCH, args[0]);
        }
        if (args.length >= 2) {
            opts.setAsURI(LinuxOption.LINUX_KIMAGE, new File(args[1]).toURI());
            opts.setAsURI(LinuxOption.LINUX_INITRD, new File("").toURI());
        }
        if (args.length >= 3) {
            opts.setAsURI(LinuxOption.LINUX_INITRD, new File(args[2]).toURI());
        }
        if (args.length >= 4) {
            opts.setValue(LinuxOption.LINUX_CMDLINE, args[3]);
        }

        try {
            MainWindow w;

            w = new MainWindow(opts);
            w.setVisible(true);
        } catch (HeadlessException ex) {
            //GUI を表示できない環境のため、コマンドラインで継続する
            mainConsole(opts);
        }
    }

    public static void mainConsole(PropertyPanels opts) {
        EmulatorARM emu = new EmulatorARM();

        emu.setOption(opts);
        emu.getBoard().setUARTInputStream(0, System.in);
        emu.getBoard().setUARTOutputStream(0, System.out);
        emu.start();

        //wait CPU halted
        try {
            emu.join();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
            //ignored
        }
    }
}
