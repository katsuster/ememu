package net.katsuster.ememu.ui;

import net.katsuster.ememu.generic.*;

import static net.katsuster.ememu.ui.EmuPropertyPanel.*;

/**
 * エミュレータです。
 */
public class Emulator extends Thread
        implements Configurable {
    private Board board;
    private EmuPropertyMap opts;

    public Emulator() {

    }

    public Emulator(Board b) {
        board = b;
    }

    @Override
    public void initProperties(EmuPropertyMap m) {
        int index = 0;

        m.setProperty("test.test", index, "Test", TYPE_STRING, "test default");
    }

    @Override
    public EmuPropertyMap getProperties() {
        return opts;
    }

    @Override
    public void setProperties(EmuPropertyMap m) {
        opts = m;
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
     * エミュレータを停止します。
     */
    public void halt() {

    }
}
