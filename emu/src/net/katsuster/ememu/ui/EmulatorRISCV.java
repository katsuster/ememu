package net.katsuster.ememu.ui;

import net.katsuster.ememu.riscv.core.*;
import net.katsuster.ememu.board.*;
import net.katsuster.ememu.generic.*;

/**
 * RISCV エミュレータです。
 */
public class EmulatorRISCV extends Thread {
    private Board board;
    private LinuxOption opts;

    public EmulatorRISCV() {
        board = new RISCVUnleashed();
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
        RV64 cpu;
        Bus bus;

        setName(getClass().getName());

        board.setup();
        cpu = (RV64)board.getMainCPU();
        bus = board.getMainBus();

        dtree = opts.getDeviceTreeImage().toString();
        kimage = "file:///home/katsuhiro/share/ememu/unleashed/rom0.bin";//opts.getKernelImage().toString();
        initrd = opts.getInitrdImage().toString();
        cmdline = opts.getCommandLine();

        BinaryLoader.loadFromURI(bus, kimage, 0x1000);

        board.start();
    }

    public void halt() {
        board.stop();
    }
}
