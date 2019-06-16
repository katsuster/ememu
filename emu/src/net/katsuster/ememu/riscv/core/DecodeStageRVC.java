package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class DecodeStageRVC extends Stage64 {
    /**
     * RVC 命令のデコードステージを生成します。
     *
     * @param c デコードステージの持ち主となる CPU コア
     */
    public DecodeStageRVC(RV64 c) {
        super(c);
    }

    /**
     * RVC 命令のデコードステージの持ち主となる CPU コアを取得します。
     *
     * @return デコードステージの持ち主となる CPU コア
     */
    @Override
    public RV64 getCore() {
        return (RV64)super.getCore();
    }

    /**
     * RISC-V アーキテクチャのビット数を返します。
     *
     * @return RV32 なら 32、RV64 なら 64
     */
    public int getRVBits() {
        return getCore().getRVBits();
    }

    /**
     * 16bit LI 命令をデコードします。
     *
     * @param inst 16bit 命令
     * @return 命令の種類
     */
    public OpIndex decodeLi(InstructionRV16 inst) {
        int rd = inst.getRd();

        if (rd != 0) {
            //C.LI
            return OpIndex.INS_RVC_LI;
        }

        throw new IllegalArgumentException("Unknown LI " +
                String.format("rd %d.", rd));
    }

    /**
     * 16bit 命令をデコードします。
     *
     * @param inst 32bit 命令
     * @return 命令の種類
     */
    public OpIndex decode(InstructionRV16 inst) {
        int code = inst.getOpcode();

        switch (code) {
        case InstructionRV16.OPCODE_LI:
            return decodeLi(inst);
        default:
            throw new IllegalArgumentException("Unknown opcode " +
                    String.format("%d.", code));
        }
    }
}
