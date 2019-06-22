package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class ExecStageRVC extends Stage64 {
    /**
     * RVC 命令の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる CPU コア
     */
    public ExecStageRVC(RV64 c) {
        super(c);
    }

    /**
     * RVI 命令の実行ステージの持ち主となる CPU コアを取得します。
     *
     * @return 実行ステージの持ち主となる CPU コア
     */
    @Override
    public RV64 getCore() {
        return (RV64)super.getCore();
    }

    /**
     * CSR の値を取得します。
     *
     * @param n レジスタ番号
     * @return レジスタの値
     */
    public long getCSR(int n) {
        return getCore().getCSR(n);
    }

    /**
     * CSR の値を設定します。
     *
     * @param n   レジスタ番号
     * @param val 新しいレジスタの値
     */
    public void setCSR(int n, long val) {
        getCore().setCSR(n, val);
    }

    /**
     * CSR の名前を取得します。
     *
     * @param n レジスタ番号
     * @return レジスタの名前
     */
    public String getCSRName(int n) {
        return getCore().getCSRName(n);
    }

    /**
     * ADDI (Add immediate) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAddi(InstructionRV16 inst, boolean exec) {
        int rd = inst.getRd();
        int imm6 = inst.getImm6();
        long imm = BitOp.signExt64(imm6, 6);

        if (!exec) {
            printDisasm(inst, "c.addi",
                    String.format("%s, %d # 0x%x", getRegName(rd),
                            imm, imm6));
            return;
        }

        setReg(rd, getReg(rd) + imm);
    }

    /**
     * LI (Load immediate) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLi(InstructionRV16 inst, boolean exec) {
        int rd = inst.getRd();
        int imm6 = inst.getImm6();
        long imm = BitOp.signExt64(imm6, 6);

        if (!exec) {
            printDisasm(inst, "c.li",
                    String.format("%s, %d # 0x%x", getRegName(rd),
                            imm, imm6));
            return;
        }

        setReg(rd, imm);
    }

    /**
     * 16bit 命令を実行します。
     *
     * @param decinst デコードされた命令
     * @param exec    実行するなら true、実行しないなら false
     */
    public void execute(Opcode decinst, boolean exec) {
        InstructionRV16 inst = (InstructionRV16) decinst.getInstruction();

        switch (decinst.getIndex()) {
        case INS_RVC_ADDI:
            executeAddi(inst, exec);
            break;
        case INS_RVC_LI:
            executeLi(inst, exec);
            break;
        default:
            throw new IllegalArgumentException("Unknown RVC instruction " +
                    decinst.getIndex());
        }
    }
}
