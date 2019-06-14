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
     * AUIPC (add upper immediate to pc) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAuipc(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        long imm = BitOp.signExt64(inst.getImm20U(), 20);

        if (!exec) {
            printDisasm(inst, "auipc",
                    String.format("%s, 0x%x", getRegName(rd), imm));
            return;
        }

        setReg(rd, getPC() + imm);
    }

    /**
     * LW (load word) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int imm12 = inst.getImm12I();
        long off = BitOp.signExt64(imm12, 12);
        int val;
        long vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "lw",
                    String.format("%s, %d(%s) # 0x%x",
                            getRegName(rd), off, getRegName(rs1), imm12));
            return;
        }

        vaddr = getReg(rs1) + off;

        //paddr = getMMU().translate(vaddr, 4, false, getPriv(), true);
        paddr = vaddr;
        //if (getMMU().isFault()) {
        //    getMMU().clearFault();
        //    return;
        //}

        if (!tryRead(paddr, 4)) {
            //raiseException(ARMv5.EXCEPT_ABT_DATA,
            //        String.format("ldrd [%08x]", paddr));
            return;
        }
        val = read32(paddr);

        setReg(rd, BitOp.signExt64(val, 32));
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
        case INS_RV32I_AUIPC:
            executeAuipc(inst, exec);
            break;
        case INS_RV32I_LW:
            executeLw(inst, exec);
            break;
        default:
            throw new IllegalArgumentException("Unknown RV32I instruction " +
                    decinst.getIndex());
        }
    }
}
