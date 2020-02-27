package net.katsuster.ememu.ui;

import net.katsuster.ememu.generic.*;

import static net.katsuster.ememu.ui.EmuPropertyPanel.*;

/**
 * エミュレータです。
 */
public class Emulator extends Thread
        implements Configurable {
    private EmuPropertyMap props;
    private Board board;

    public Emulator() {

    }

    @Override
    public void initProperties(EmuPropertyMap p) {
        int index = 0;

        p.setProperty("test.test", index, "Test", TYPE_STRING, "test default");
    }

    @Override
    public EmuPropertyMap getProperties() {
        return props;
    }

    @Override
    public void setProperties(EmuPropertyMap p) {
        props = p;
    }

    @Override
    public void run() {
        boot();
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
    protected void setBoard(Board b) {
        board = b;
    }

    /**
     * エミュレータを設定します。
     */
    public void setup() {

    }

    /**
     * エミュレータを起動します。
     */
    public void boot() {
        getBoard().boot();
    }

    /**
     * エミュレータを停止します。
     */
    public void halt() {
        getBoard().halt();
    }
}
