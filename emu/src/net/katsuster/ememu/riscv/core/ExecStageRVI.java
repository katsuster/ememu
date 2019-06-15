package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class ExecStageRVI extends Stage64 {
    /**
     * RISC-V 64 コア c の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる RISC-V 64 コア
     */
    public ExecStageRVI(RV64 c) {
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
     * JALR () 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeJalr(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        long off = BitOp.signExt64(inst.getImm12I(), 12);

        if (!exec) {
            printDisasm(inst, "jalr",
                    String.format("%s, %d(%s)", getRegName(rd),
                            off, getRegName(rs1)));
            return;
        }

        setReg(rd, getPC() + 4);
        setPC((getReg(rs1) + off) & ~0x1L);
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
     * ADDI (add immediate) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAddi(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int imm12 = inst.getImm12I();
        long imm = BitOp.signExt64(imm12, 12);

        if (!exec) {
            printDisasm(inst, "add",
                    String.format("%s, %s, %d # 0x%x", getRegName(rd),
                            getRegName(rs1), imm, imm12));
            return;
        }

        setReg(rd, getReg(rs1) + imm);
    }

    /**
     * SLLI (logical left shift) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSlli(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int shamt = inst.getField(20, 5);
        int imm6 = inst.getImm6I();

        if (!exec) {
            printDisasm(inst, "slli",
                    String.format("%s, %s, %d", getRegName(rd),
                            getRegName(rs1), shamt));
            return;
        }

        setReg(rd, getReg(rs1) << shamt);
    }

    /**
     * ADD 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();

        if (!exec) {
            printDisasm(inst, "add",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rs1), getRegName(rs2)));
            return;
        }

        setReg(rd, getReg(rs1) + getReg(rs2));
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
        case INS_RV32I_JALR:
            executeJalr(inst, exec);
            break;
        case INS_RV32I_AUIPC:
            executeAuipc(inst, exec);
            break;
        case INS_RV32I_LW:
            executeLw(inst, exec);
            break;
        case INS_RV32I_ADDI:
            executeAddi(inst, exec);
            break;
        case INS_RV32I_SLLI:
        case INS_RV64I_SLLI:
            executeSlli(inst, exec);
            break;
        case INS_RV32I_ADD:
            executeAdd(inst, exec);
            break;
        default:
            throw new IllegalArgumentException("Unknown RV32I instruction " +
                    decinst.getIndex());
        }
    }
}
