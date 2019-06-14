package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class ExecStage extends Stage64 {
    /**
     * RISC-V 64 コア c の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる RISC-V 64 コア
     */
    public ExecStage(RV64 c) {
        super(c);
    }

    /**
     * 実行ステージの持ち主となる CPU コアを取得します。
     *
     * @return 実行ステージの持ち主となる CPU コア
     */
    @Override
    public RV64 getCore() {
        return (RV64)super.getCore();
    }

    /**
     * 32bit 命令を実行します。
     *
     * @param decinst デコードされた命令
     * @param exec    実行するなら true、実行しないなら false
     */
    public void execute(Opcode decinst, boolean exec) {
        InstructionRV32 inst = (InstructionRV32) decinst.getInstruction();

        switch (decinst.getIndex()) {
        default:
            throw new IllegalArgumentException("Unknown RV32I instruction " +
                    decinst.getIndex());
        }
    }
}
