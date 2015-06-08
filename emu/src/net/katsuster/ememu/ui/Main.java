package net.katsuster.ememu.ui;

import java.io.*;
import java.awt.*;

/**
 * エミュレータのテキスト画面、ログ表示用のクラス。
 *
 * @author katsuhiro
 */
public class Main {
    public static void main(String[] args) {
        EmulatorOption opts = new EmulatorOption();

        opts.setKernelImage(new File("Image"));
        opts.setInitramfsImage(new File("initramfs.gz"));
        opts.setCommandLine("console=ttyAMA0 mem=64M lpj=0 root=/dev/ram init=/bin/init debug printk.time=1");

        if (args.length <= 0) {
            SystemPane.out.println("usage:\n" +
                    "  ememu image initramfs [cmdline]\n");
            return;
        }
        if (args.length >= 1) {
            opts.setKernelImage(new File(args[0]));
            opts.setInitramfsImage(new File(""));
        }
        if (args.length >= 2) {
            opts.setInitramfsImage(new File(args[1]));
        }
        if (args.length >= 3) {
            opts.setCommandLine(args[2]);
        }

        try {
            MainWindow w;

            w = new MainWindow(opts);
        } catch (HeadlessException ex) {
            //GUI を表示できない環境のため、コマンドラインで継続する
            mainConsole(opts);
        }
    }

    public static void mainConsole(EmulatorOption opts) {
        Emulator emu = new Emulator();

        emu.setOption(opts);
        emu.getBoard().setUARTInputStream(0, System.in);
        emu.getBoard().setUARTOutputStream(0, SystemPane.out);
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
