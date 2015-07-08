package net.katsuster.ememu.generic;

/**
 * 命令。
 *
 * @author katsuhiro
 */
public abstract class Instruction {
    private int rawInst;
    private int lengthInst;

    /**
     * 指定されたバイナリ値、指定された長さを持つ命令を作成します。
     *
     * @param inst 命令のバイナリ値
     * @param len  命令長（バイト単位）
     */
    public Instruction(int inst, int len) {
        this.rawInst = inst;
        this.lengthInst = len;
    }

    /**
     * 指定されたバイナリ値、指定された長さを持つ命令として、
     * 再利用します。
     *
     * @param inst 命令のバイナリ値
     * @param len  命令長（バイト単位）
     */
    public void reuse(int inst, int len) {
        rawInst = inst;
        lengthInst = len;
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

    /**
     * 命令長をバイト単位で取得します。
     *
     * @return 命令長（バイト単位）
     */
    public int getLength() {
        return lengthInst;
    }

    /**
     * 命令の 16進数表記を取得します。
     *
     * @return 命令の 16進数表記
     */
    public abstract String toHex();
}
