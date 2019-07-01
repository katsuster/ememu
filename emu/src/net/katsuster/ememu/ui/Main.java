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
        LinuxOption opts = new LinuxOption();

        try {
            opts.setArch("arm");
            opts.setKernelImage(new URI("http://www.katsuster.net/contents/java/ememu/Image-4.4.57"));
            opts.setInitrdImage(new URI("http://www.katsuster.net/contents/java/ememu/initramfs.gz"));
            opts.setCommandLine("console=ttyAMA0 mem=64M root=/dev/ram init=/bin/init debug printk.time=1");
        } catch (URISyntaxException e) {
            //ignore
        }

        if (args.length >= 1) {
            if (args[0].equals("-h") || args[0].equals("--help") ||
                    args[0].equals("/?")) {
                usage(args);
                return;
            }
            opts.setArch(args[0]);
        }
        if (args.length >= 2) {
            opts.setKernelImage(new File(args[1]));
            opts.setInitrdImage(new File(""));
        }
        if (args.length >= 3) {
            opts.setInitrdImage(new File(args[2]));
        }
        if (args.length >= 4) {
            opts.setCommandLine(args[3]);
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

    public static void mainConsole(LinuxOption opts) {
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
