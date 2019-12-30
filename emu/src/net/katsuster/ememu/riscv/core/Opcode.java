package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.core.Inst32;

public class Opcode {
    private Inst32 instRaw;
    private OpType type;
    private OpIndex index;

    /**
     * デコードされた命令を生成します。
     *
     * @param inst    命令データ
     * @param optype  命令の種類
     * @param opindex 命令の詳細な種類
     */
    public Opcode(Inst32 inst, OpType optype, OpIndex opindex) {
        instRaw = inst;
        type = optype;
        index = opindex;
    }

    /**
     * 指定された種類、指定されたインデックスを持つ、
     * デコードされた命令として、再利用します。
     *
     * @param inst    命令データ
     * @param optype  命令の種類
     * @param opindex 命令の詳細な種類
     */
    public void reuse(Inst32 inst, OpType optype, OpIndex opindex) {
        instRaw = inst;
        type = optype;
        index = opindex;
    }

    /**
     * デコードする前の命令データを取得します。
     *
     * @return 命令データ
     */
    public Inst32 getInstruction() {
        return instRaw;
    }

    /**
     * 命令の種類を取得します。
     *
     * @return 命令の種類
     */
    public OpType getType() {
        return type;
    }

    /**
     * 命令の詳細な種類を取得します。
     *
     * @return 命令の詳細な種類
     */
    public OpIndex getIndex() {
        return index;
    }
}
