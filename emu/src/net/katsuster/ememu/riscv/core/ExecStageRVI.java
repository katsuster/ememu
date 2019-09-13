package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class ExecStageRVI extends Stage64 {
    /**
     * RVI 命令の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる CPU コア
     */
    public ExecStageRVI(RV64 c) {
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
     * AUIPC (Add upper immediate to pc) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAuipc(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        long imm = BitOp.signExt64(inst.getImm20U() << 12, 32);

        if (!exec) {
            printDisasm(inst, "auipc",
                    String.format("%s, 0x%x", getRegName(rd), imm));
            return;
        }

        setReg(rd, getPC() + imm);
    }

    /**
     * LUI (Load upper immediate) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLui(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        long imm = BitOp.signExt64(inst.getImm20U() << 12, 31);

        if (!exec) {
            printDisasm(inst, "lui",
                    String.format("%s, 0x%x", getRegName(rd), imm));
            return;
        }

        setReg(rd, imm);
    }

    /**
     * JALR (Jump and link register) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeJalr(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        long off = BitOp.signExt64(inst.getImm12I(), 12);
        long t;

        if (!exec) {
            printDisasm(inst, "jalr",
                    String.format("%s, %d(%s)", getRegName(rd),
                            off, getRegName(rs1)));
            return;
        }

        t = getPC() + 4;
        setPC((getReg(rs1) + off) & ~0x1L);
        setReg(rd, t);
    }

    /**
     * BEQ (Branch if equal) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBeq(InstructionRV32 inst, boolean exec) {
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int off = BitOp.signExt32(inst.getOffsetB(), 13);

        if (!exec) {
            printDisasm(inst, "beq",
                    String.format("%s, %s, 0x%x", getRegName(rs1),
                            getRegName(rs2), getPC() + off));
            return;
        }

        if (getReg(rs1) == getReg(rs2)) {
            jumpRel(off);
        }
    }

    /**
     * BNE (Branch if not equal) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBne(InstructionRV32 inst, boolean exec) {
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int off = BitOp.signExt32(inst.getOffsetB(), 13);

        if (!exec) {
            printDisasm(inst, "bne",
                    String.format("%s, %s, 0x%x", getRegName(rs1),
                            getRegName(rs2), getPC() + off));
            return;
        }

        if (getReg(rs1) != getReg(rs2)) {
            jumpRel(off);
        }
    }

    /**
     * BNE (Branch if less than) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBlt(InstructionRV32 inst, boolean exec) {
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int off = BitOp.signExt32(inst.getOffsetB(), 13);

        if (!exec) {
            printDisasm(inst, "blt",
                    String.format("%s, %s, 0x%x", getRegName(rs1),
                            getRegName(rs2), getPC() + off));
            return;
        }

        if (getReg(rs1) < getReg(rs2)) {
            jumpRel(off);
        }
    }

    /**
     * BLTU (Branch if less than, unsigned) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBltu(InstructionRV32 inst, boolean exec) {
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int off = BitOp.signExt32(inst.getOffsetB(), 13);

        if (!exec) {
            printDisasm(inst, "bltu",
                    String.format("%s, %s, 0x%x", getRegName(rs1),
                            getRegName(rs2), getPC() + off));
            return;
        }

        if (IntegerExt.compareUint64(getReg(rs1),  getReg(rs2)) < 0) {
            jumpRel(off);
        }
    }

    /**
     * BGEU (Branch if greater than or equal, unsigned) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBgeu(InstructionRV32 inst, boolean exec) {
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int off = BitOp.signExt32(inst.getOffsetB(), 13);

        if (!exec) {
            printDisasm(inst, "bgeu",
                    String.format("%s, %s, 0x%x", getRegName(rs1),
                            getRegName(rs2), getPC() + off));
            return;
        }

        if (IntegerExt.compareUint64(getReg(rs1),  getReg(rs2)) >= 0) {
            jumpRel(off);
        }
    }

    /**
     * LW (Load word) 命令。
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
     * SW (Store word) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSw(InstructionRV32 inst, boolean exec) {
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int offraw = inst.getOffsetS();
        long off = BitOp.signExt64(offraw, 12);
        long vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "sw",
                    String.format("%s, %d(%s) # 0x%x",
                            getRegName(rs2), off, getRegName(rs1), offraw));
            return;
        }

        vaddr = getReg(rs1) + off;

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
     * SD (Store double word) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSd(InstructionRV32 inst, boolean exec) {
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int offraw = inst.getOffsetS();
        long off = BitOp.signExt64(offraw, 12);
        long vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "sd",
                    String.format("%s, %d(%s) # 0x%x",
                            getRegName(rs2), off, getRegName(rs1), offraw));
            return;
        }

        vaddr = getReg(rs1) + off;

        //paddr = getMMU().translate(vaddr, 4, false, getPriv(), true);
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
     * ADDI (Add immediate) 命令。
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
            printDisasm(inst, "addi",
                    String.format("%s, %s, %d # 0x%x", getRegName(rd),
                            getRegName(rs1), imm, imm12));
            return;
        }

        setReg(rd, getReg(rs1) + imm);
    }

    /**
     * ANDI (And immediate) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAndi(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int imm12 = inst.getImm12I();
        long imm = BitOp.signExt64(imm12, 12);

        if (!exec) {
            printDisasm(inst, "andi",
                    String.format("%s, %s, %d # 0x%x", getRegName(rd),
                            getRegName(rs1), imm, imm12));
            return;
        }

        setReg(rd, getReg(rs1) & imm);
    }

    /**
     * SLLI (Shift left logical immediate) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSlli(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int shamt = inst.getField(20, 5);

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
     * SUB 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSub(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();

        if (!exec) {
            printDisasm(inst, "sub",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rs1), getRegName(rs2)));
            return;
        }

        setReg(rd, getReg(rs1) - getReg(rs2));
    }

    /**
     * CSRRW (Control and status register read and write) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeCsrrw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int csr = inst.getImm12I();
        long t;

        if (!exec) {
            printDisasm(inst, "csrrw",
                    String.format("%s, %s, %s", getRegName(rd),
                            getCSRName(csr), getRegName(rs1)));
            return;
        }

        t = getCSR(csr);
        setCSR(csr, getReg(rs1));
        setReg(rd, t);
    }

    /**
     * CSRRS (Control and status register read and set) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeCsrrs(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int csr = inst.getImm12I();
        long t;

        if (!exec) {
            printDisasm(inst, "csrrs",
                    String.format("%s, %s, %s", getRegName(rd),
                            getCSRName(csr), getRegName(rs1)));
            return;
        }

        t = getCSR(csr);
        setCSR(csr, t | getReg(rs1));
        setReg(rd, t);
    }

    /**
     * ADDIW (Add word immediate) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAddiw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int imm12 = inst.getImm12I();
        long imm = BitOp.signExt64(imm12, 12);
        long v;

        if (!exec) {
            printDisasm(inst, "addiw",
                    String.format("%s, %s, %d # 0x%x", getRegName(rd),
                            getRegName(rs1), imm, imm12));
            return;
        }

        v = getReg(rs1) + imm;
        setReg(rd, BitOp.signExt64(v & 0xffffffffL, 32));
    }

    /**
     * WFI () 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeWfi(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int imm12 = inst.getImm12I();
        long imm = BitOp.signExt64(imm12, 12);
        long v;

        if (!exec) {
            printDisasm(inst, "wfi", "");
            return;
        }

        waitInt();
    }

    /**
     * 割り込み待ち。
     */
    public void waitInt() {
        RV64 c = getCore();

        synchronized (c) {
            while (!c.isRaisedInterrupt() &&
                    !c.isRaisedInternalInterrupt() &&
                    !c.shouldHalt()) {
                try {
                    c.wait(1000);
                } catch (InterruptedException ex) {
                    //do nothing
                }
            }
        }
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
        case INS_RV32I_LUI:
            executeLui(inst, exec);
            break;
        case INS_RV32I_JALR:
            executeJalr(inst, exec);
            break;
        case INS_RV32I_BEQ:
            executeBeq(inst, exec);
            break;
        case INS_RV32I_BNE:
            executeBne(inst, exec);
            break;
        case INS_RV32I_BLT:
            executeBlt(inst, exec);
            break;
        case INS_RV32I_BLTU:
            executeBltu(inst, exec);
            break;
        case INS_RV32I_BGEU:
            executeBgeu(inst, exec);
            break;
        case INS_RV32I_LW:
            executeLw(inst, exec);
            break;
        case INS_RV32I_SW:
            executeSw(inst, exec);
            break;
        case INS_RV64I_SD:
            executeSd(inst, exec);
            break;
        case INS_RV32I_ADDI:
            executeAddi(inst, exec);
            break;
        case INS_RV32I_ANDI:
            executeAndi(inst, exec);
            break;
        case INS_RV32I_SLLI:
        case INS_RV64I_SLLI:
            executeSlli(inst, exec);
            break;
        case INS_RV32I_ADD:
            executeAdd(inst, exec);
            break;
        case INS_RV32I_SUB:
            executeSub(inst, exec);
            break;
        case INS_RV32I_CSRRW:
            executeCsrrw(inst, exec);
            break;
        case INS_RV32I_CSRRS:
            executeCsrrs(inst, exec);
            break;
        case INS_RV64I_ADDIW:
            executeAddiw(inst, exec);
            break;
        case INS_RV32_WFI:
            executeWfi(inst, exec);
            break;
        default:
            throw new IllegalArgumentException("Unknown RV32I instruction " +
                    decinst.getIndex());
        }
    }
}
