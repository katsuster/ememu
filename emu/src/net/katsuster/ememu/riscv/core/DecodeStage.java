package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class DecodeStage extends Stage64 {
    /**
     * RISC-V 64 コア c の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる RISC-V 64 コア
     */
    public DecodeStage(RV64 c) {
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
     * 32bit 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decode(InstructionRV32 inst) {
        int code = inst.getOpcode();

        switch (code) {
        default:
            throw new IllegalArgumentException("Unknown opcode " +
                    String.format("%d.", code));
        }
    }
}
