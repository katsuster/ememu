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

    private String kimage;
    private String initram;
    private String cmdline;

    public Emulator() {
        cpu = new ARMv5();
        bus = new Bus64();
        ram = new RAM(64 * 1024 * 1024);
        board = new ARMVersatile();
    }

    /**
     * Linux カーネルイメージファイルの位置を取得します。
     *
     * @return Linux カーネルイメージファイルの位置
     */
    public String getKernelImage() {
        return kimage;
    }

    /**
     * Linux カーネルイメージファイルの位置を設定します。
     *
     * @param uri Linux カーネルイメージファイルの位置
     */
    public void setKernelImage(String uri) {
        kimage = uri;
    }

    /**
     * ブート時にカーネルに渡す initramfs イメージファイルの位置を取得します。
     *
     * @return initramfs イメージファイルの位置
     */
    public String getInitramfsImage() {
        return initram;
    }

    /**
     * ブート時にカーネルに渡す initramfs イメージファイルの位置を設定します。
     *
     * @param uri initramfs イメージファイルの位置
     */
    public void setInitramfsImage(String uri) {
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

    @Override
    public void run() {
        setName(getClass().getName());

        board.setup(cpu, bus, ram);

        InnerBootloader.bootFromURI(cpu, ram, kimage, initram, cmdline);

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
