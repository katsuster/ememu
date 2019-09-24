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
     * LW (Load word) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLw(InstructionRV16 inst, boolean exec) {
        int rs1 = inst.getRs1dash() + 8;
        int rd = inst.getRddash() + 8;
        int uimm = inst.getImm7LWSW();
        int val;
        long vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "c.lw",
                    String.format("%s, %d(%s)", getRegName(rd),
                            uimm, getRegName(rs1)));
            return;
        }

        vaddr = getReg(rs1) + uimm;

        //paddr = getMMU().translate(vaddr, 8, false, getPriv(), true);
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
     * SW (Store word) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSw(InstructionRV16 inst, boolean exec) {
        int rs1 = inst.getRs1dash() + 8;
        int rs2 = inst.getRs2dash() + 8;
        int uimm = inst.getImm7LWSW();
        long vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "c.sw",
                    String.format("%s, %d(%s)", getRegName(rs2),
                            uimm, getRegName(rs1)));
            return;
        }

        vaddr = getReg(rs1) + uimm;

        //paddr = getMMU().translate(vaddr, 4, false, getPriv(), true);
        paddr = vaddr;
        //if (getMMU().isFault()) {
        //    getMMU().clearFault();
        //    return;
        //}

        if (!tryWrite(paddr, 4)) {
            //raiseException(ARMv5.EXCEPT_ABT_DATA,
            //        String.format("ldrd [%08x]", paddr));
            return;
        }

        write32(paddr, (int)getReg(rs2));
    }

    /**
     * ADDI (Add immediate) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAddi(InstructionRV16 inst, boolean exec) {
        int rd = inst.getRd();
        int imm6 = inst.getImm6CI();
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
     * ADDIW (Add word immediate) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAddiw(InstructionRV16 inst, boolean exec) {
        int rd = inst.getRd();
        int imm6 = inst.getImm6CI();
        long imm = BitOp.signExt64(imm6, 6);
        long v;

        if (!exec) {
            printDisasm(inst, "c.addiw",
                    String.format("%s, %d # 0x%x", getRegName(rd),
                            imm, imm6));
            return;
        }

        v = (getReg(rd) + imm) & 0xffffffffL;

        setReg(rd, BitOp.signExt64(v, 32));
    }

    /**
     * LI (Load immediate) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLi(InstructionRV16 inst, boolean exec) {
        int rd = inst.getRd();
        int imm6 = inst.getImm6CI();
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
     * ADDI16SP (Add immediate, Scaled by 16, to Stack Pointer) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAddi16sp(InstructionRV16 inst, boolean exec) {
        int rd = inst.getRd();
        int imm10 = inst.getImm10ADDI16SP();
        long imm = BitOp.signExt64(imm10, 10);

        if (!exec) {
            printDisasm(inst, "c.addi16sp",
                    String.format("%s, %d # 0x%x", getRegName(rd),
                            imm, imm10));
            return;
        }

        setReg(rd, getReg(rd) + imm);
    }

    /**
     * LUI (Load upper immediate) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLui(InstructionRV16 inst, boolean exec) {
        int rd = inst.getRd();
        int imm6 = inst.getImm6CI();
        long imm = BitOp.signExt64(imm6 << 12, 18);

        if (!exec) {
            printDisasm(inst, "c.lui",
                    String.format("%s, 0x%x # 0x%x", getRegName(rd),
                            imm6, imm));
            return;
        }

        setReg(rd, imm);
    }

    /**
     * ANDI (And immediate) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAndi(InstructionRV16 inst, boolean exec) {
        int rd = inst.getRddash() + 8;
        int imm6 = inst.getImm6CI();
        long imm = BitOp.signExt64(imm6, 6);

        if (!exec) {
            printDisasm(inst, "c.andi",
                    String.format("%s, %d # 0x%x", getRegName(rd),
                            imm, imm6));
            return;
        }

        setReg(rd, rd & imm);
    }

    /**
     * BNEZ (Branch if not equal to zero) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBnez(InstructionRV16 inst, boolean exec) {
        int rs1 = inst.getRs1dash() + 8;
        int off = BitOp.signExt32(inst.getOffset9B(), 9);

        if (!exec) {
            printDisasm(inst, "c.bnez",
                    String.format("%s, 0x%x", getRegName(rs1),
                            getPC() + off));
            return;
        }

        if (getReg(rs1) != 0) {
            jumpRel(off);
        }
    }

    /**
     * SLLI (Shift left logical immediate) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSlli(InstructionRV16 inst, boolean exec) {
        int rd = inst.getRd();
        int imm6 = inst.getImm6CI();

        if (!exec) {
            printDisasm(inst, "c.slli",
                    String.format("%s, %s, %d", getRegName(rd),
                            getRegName(rd), imm6));
            return;
        }

        setReg(rd, getReg(rd) << imm6);
    }

    /**
     * ADD (Add) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAdd(InstructionRV16 inst, boolean exec) {
        int rd = inst.getRd();
        int rs2 = inst.getRs2();

        if (!exec) {
            printDisasm(inst, "c.add",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rd), getRegName(rs2)));
            return;
        }

        setReg(rd, getReg(rd) + getReg(rs2));
    }

    /**
     * SDSP (Store doubleword, stack-pointer relative) 命令。
     *
     * @param inst 16bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSdsp(InstructionRV16 inst, boolean exec) {
        int rs2 = inst.getRs2();
        int uimm = inst.getImm9SDSP();
        long vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "c.sdsp",
                    String.format("%s, %d(sp)", getRegName(rs2),
                            uimm));
            return;
        }

        vaddr = getReg(2) + uimm;

        //paddr = getMMU().translate(vaddr, 8, false, getPriv(), true);
        paddr = vaddr;
        //if (getMMU().isFault()) {
        //    getMMU().clearFault();
        //    return;
        //}

        if (!tryWrite(paddr, 8)) {
            //raiseException(ARMv5.EXCEPT_ABT_DATA,
            //        String.format("ldrd [%08x]", paddr));
            return;
        }

        write64(paddr, getReg(rs2));
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
        case INS_RVC_LW:
            executeLw(inst, exec);
            break;
        case INS_RVC_SW:
            executeSw(inst, exec);
            break;
        case INS_RVC_ADDI:
            executeAddi(inst, exec);
            break;
        case INS_RVC_ADDIW:
            executeAddiw(inst, exec);
            break;
        case INS_RVC_LI:
            executeLi(inst, exec);
            break;
        case INS_RVC_ADDI16SP:
            executeAddi16sp(inst, exec);
            break;
        case INS_RVC_LUI:
            executeLui(inst, exec);
            break;
        case INS_RVC_ANDI:
            executeAndi(inst, exec);
            break;
        case INS_RVC_BNEZ:
            executeBnez(inst, exec);
            break;
        case INS_RVC_SLLI:
            executeSlli(inst, exec);
            break;
        case INS_RVC_ADD:
            executeAdd(inst, exec);
            break;
        case INS_RVC_SDSP:
            executeSdsp(inst, exec);
            break;
        default:
            throw new IllegalArgumentException("Unknown RVC instruction " +
                    decinst.getIndex());
        }
    }
}
