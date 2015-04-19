package net.katsuster.ememu.generic;

/**
 * 命令。
 *
 * @author katsuhiro
 */
public class Instruction {
    private int rawInst;

    public Instruction(int inst) {
        this.rawInst = inst;
    }

    /**
     * 命令のバイナリデータを取得します。
     *
     * @return 命令のバイナリデータ
     */
    public int getInst() {
        return rawInst;
    }

    /**
     * 命令のバイナリデータの指定された 1ビットを取得します。
     *
     * @param bit ビット位置
     * @return 指定されたビットがセットされていれば true、そうでなければ false
     */
    public boolean getBit(int bit) {
        return BitOp.getBit32(rawInst, bit);
    }

    /**
     * 命令のバイナリデータの指定されたビットフィールドを取得します。
     *
     * @param pos ビット位置
     * @param len ビットフィールドの長さ
     * @return ビットフィールドの値
     */
    public int getField(int pos, int len) {
        return BitOp.getField32(rawInst, pos, len);
    }
}
