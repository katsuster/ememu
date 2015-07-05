package net.katsuster.ememu.ui;

import net.katsuster.ememu.arm.core.*;
import net.katsuster.ememu.board.*;
import net.katsuster.ememu.generic.*;

/**
 * エミュレータです。
 *
 * @author katsuhiro
 */
class Emulator extends Thread {
    private ARMv5 cpu;
    private Bus64 bus;
    private RAM ram;
    private ARMVersatile board;

    private LinuxOption opts;

    public Emulator() {
        cpu = new ARMv5();
        bus = new Bus64();
        ram = new RAM(64 * 1024 * 1024);
        board = new ARMVersatile();
        opts = new LinuxOption();
    }

    /**
     * メイン CPU を取得します。
     *
     * @return メイン CPU
     */
    public CPU getMainCPU() {
        return cpu;
    }

    /**
     * メインバスを取得します。
     *
     * @return メインバス
     */
    public Bus64 getMainBus() {
        return bus;
    }

    /**
     * メイン RAM を取得します。
     *
     * @return メイン RAM
     */
    public RAM getMainRAM() {
        return ram;
    }

    /**
     * エミュレーション対象となるボードを取得します。
     *
     * TODO: 現状、使用可能なボードは 1種類に限定されています
     *
     * @return エミュレーション対象のボード
     */
    public ARMVersatile getBoard() {
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
        String kimage, initram, cmdline;

        setName(getClass().getName());

        board.setup(cpu, bus, ram);

        kimage = opts.getKernelImage().toString();
        initram = opts.getInitramfsImage().toString();
        cmdline = opts.getCommandLine();
        ARMLinuxLoader.bootFromURI(cpu, ram, kimage, initram, cmdline);

        //start cores
        bus.startAllSlaveCores();
        bus.startAllMasterCores();

        //wait CPU halted
        try {
            cpu.join();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
            //ignored
        }
    }

    public void halt() {
        bus.haltAllMasterCores();
        bus.haltAllSlaveCores();
    }
}
