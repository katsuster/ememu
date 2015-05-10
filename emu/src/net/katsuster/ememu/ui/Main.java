package net.katsuster.ememu.ui;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        String kimage = "Image";
        String initram = "initramfs.gz";
        String cmdline = "console=ttyAMA0 mem=64M lpj=0 root=/dev/ram init=/bin/init debug printk.time=1";

        if (args.length <= 0) {
            SystemPane.out.println("usage:\n" +
                    "  ememu image initramfs [cmdline]\n");

            return;
        }
        if (args.length >= 1) {
            kimage = args[0];
            initram = "";
        }
        if (args.length >= 2) {
            initram = args[1];
        }
        if (args.length >= 3) {
            cmdline = args[2];
        }

        String uriKimage = new File(kimage).toURI().toString();
        String uriInitram = new File(initram).toURI().toString();

        Emulator emu = new Emulator();
        emu.setKernelImage(uriKimage);
        emu.setInitramfsImage(uriInitram);
        emu.setCommandLine(cmdline);
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
