package net.katsuster.ememu.ui;

import net.katsuster.ememu.generic.*;

/**
 * エミュレータです。
 */
public class Emulator extends Thread {
    private Board board;
    private LinuxOption opts;

    public Emulator() {

    }

    public Emulator(Board b, LinuxOption o) {
        board = b;
        opts = o;
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
     * エミュレーション対象となるボードを設定します。
     *
     * @param b エミュレーション対象のボード
     */
    public void setBoard(Board b) {
        board = b;
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

    /**
     * エミュレータを停止します。
     */
    public void halt() {

    }
}
