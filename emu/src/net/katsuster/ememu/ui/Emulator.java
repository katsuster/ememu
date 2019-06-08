package net.katsuster.ememu.ui;

import net.katsuster.ememu.arm.core.*;
import net.katsuster.ememu.board.*;
import net.katsuster.ememu.generic.*;

/**
 * ARM エミュレータです。
 *
 * @author katsuhiro
 */
public class Emulator extends Thread {
    private Board board;
    private LinuxOption opts;

    public Emulator() {
        board = new ARMVersatile();
        opts = new LinuxOption();
    }

    /**
     * エミュレーション対象となるボードを取得します。
     *
     * @return エミュレーション対象のボード
     */
    public Board getBoard() {
        return board;
    }

    /**
     * エミュレータ起動のオプションを取得します。
     *
     * @return エミュレータに渡すオプション
     */
    public LinuxOption getOption() {
        return opts;
    }

    /**
     * エミュレータ起動のオプションを設定します。
     *
     * @param op エミュレータに渡すオプション
     */
    public void setOption(LinuxOption op) {
        opts = op;
    }

    @Override
    public void run() {
        String dtree, kimage, initrd, cmdline;
        ARMv5 cpu;
        RAM ram;

        setName(getClass().getName());

        board.setup();
        cpu = (ARMv5)board.getMainCPU();
        ram = board.getMainRAM();

        dtree = opts.getDeviceTreeImage().toString();
        kimage = opts.getKernelImage().toString();
        initrd = opts.getInitrdImage().toString();
        cmdline = opts.getCommandLine();
        if (dtree.equals("")) {
            ARMLinuxLoader.bootFromURI(cpu, ram, kimage, initrd, cmdline);
        } else {
            ARMLinuxLoader.bootFromURIWithDT(cpu, ram, dtree, kimage, initrd, cmdline);
        }

        board.start();
    }

    public void halt() {
        board.stop();
    }
}
