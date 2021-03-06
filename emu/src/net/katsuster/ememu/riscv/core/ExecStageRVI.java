package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.BitOp;
import net.katsuster.ememu.generic.IntegerExt;
import net.katsuster.ememu.generic.core.Stage64;

import java.math.*;
import java.util.concurrent.locks.*;

public class ExecStageRVI extends Stage64 {
    /**
     * RVI 命令の実行ステージを生成します。
     *
     * @param c 実行ステージの持ち主となる CPU コア
     */
    public ExecStageRVI(RV64 c) {
        super(c);
    }

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
     * Fence 命令の pred, succ の名前を取得します。
     *
     * @param n pred もしくは succ の値
     * @return pred もしくは succ の名前
     */
    public String getPredName(int n) {
        StringBuffer result = new StringBuffer();

        if ((n & 8) != 0) {
            result.append("i");
        }
        if ((n & 4) != 0) {
            result.append("o");
        }
        if ((n & 2) != 0) {
            result.append("r");
        }
        if ((n & 1) != 0) {
            result.append("w");
        }

        return result.toString();
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
     * AUIPC (Add upper immediate to pc) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAuipc(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int imm20 = inst.getImm20U();
        long imm = BitOp.signExt64((imm20 << 12) & 0xffffffffL, 32);

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
        int imm20 = inst.getImm20U();
        long imm = BitOp.signExt64((imm20 << 12) & 0xffffffffL, 31);

        if (!exec) {
            printDisasm(inst, "lui",
                    String.format("%s, 0x%x", getRegName(rd), imm));
            return;
        }

        setReg(rd, imm);
    }

    /**
     * JAL (Jump and link) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeJal(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        long off = BitOp.signExt64(inst.getImm20J(), 20);
        long t;

        if (!exec) {
            printDisasm(inst, "jal",
                    String.format("%s, 0x%x", getRegName(rd),
                            off + getPC()));
            return;
        }

        t = getPC() + 4;
        jumpRel((int)off);
        setReg(rd, t);
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
                    String.format("%s, %d(%s) # 0x%x", getRegName(rd),
                            off, getRegName(rs1), getReg(rs1) + off));
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
        int off = BitOp.signExt32(inst.getImm13B(), 13);

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
        int off = BitOp.signExt32(inst.getImm13B(), 13);

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
     * BLT (Branch if less than) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBlt(InstructionRV32 inst, boolean exec) {
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int off = BitOp.signExt32(inst.getImm13B(), 13);

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
     * BGE (Branch if greater than or equal) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeBge(InstructionRV32 inst, boolean exec) {
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int off = BitOp.signExt32(inst.getImm13B(), 13);

        if (!exec) {
            printDisasm(inst, "bge",
                    String.format("%s, %s, 0x%x", getRegName(rs1),
                            getRegName(rs2), getPC() + off));
            return;
        }

        if (getReg(rs1) >= getReg(rs2)) {
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
        int off = BitOp.signExt32(inst.getImm13B(), 13);

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
        int off = BitOp.signExt32(inst.getImm13B(), 13);

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
     * LBU (Load byte, unsigned) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLbu(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int imm12 = inst.getImm12I();
        long off = BitOp.signExt64(imm12, 12);
        long vaddr, paddr;
        byte val;

        if (!exec) {
            printDisasm(inst, "lbu",
                    String.format("%s, %d(%s) # 0x%x",
                            getRegName(rd), off, getRegName(rs1), imm12));
            return;
        }

        vaddr = getReg(rs1) + off;

        //paddr = getMMU().translate(vaddr, 1, false, getPriv(), true);
        paddr = vaddr;
        //if (getMMU().isFault()) {
        //    getMMU().clearFault();
        //    return;
        //}

        if (!tryRead(paddr, 1)) {
            //raiseException(ARMv5.EXCEPT_ABT_DATA,
            //        String.format("ldrd [%08x]", paddr));
            return;
        }
        val = read8(paddr);

        setReg(rd, val & 0xffL);
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
        long vaddr, paddr;
        int val;

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

        setReg(rd, BitOp.signExt64(val & 0xffffffffL, 32));
    }

    /**
     * SB (Store byte) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSb(InstructionRV32 inst, boolean exec) {
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int offraw = inst.getImm12S();
        long off = BitOp.signExt64(offraw, 12);
        long vaddr, paddr;

        if (!exec) {
            printDisasm(inst, "sb",
                    String.format("%s, %d(%s) # 0x%x",
                            getRegName(rs2), off, getRegName(rs1), offraw));
            return;
        }

        vaddr = getReg(rs1) + off;

        //paddr = getMMU().translate(vaddr, 1, false, getPriv(), true);
        paddr = vaddr;
        //if (getMMU().isFault()) {
        //    getMMU().clearFault();
        //    return;
        //}

        if (!tryWrite(paddr, 1)) {
            //raiseException(ARMv5.EXCEPT_ABT_DATA,
            //        String.format("ldrd [%08x]", paddr));
            return;
        }

        write8(paddr, (byte)getReg(rs2));
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
        int offraw = inst.getImm12S();
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
        int offraw = inst.getImm12S();
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
     * XORI (Exclusive-or immediate) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeXori(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int imm12 = inst.getImm12I();
        long imm = BitOp.signExt64(imm12, 12);

        if (!exec) {
            printDisasm(inst, "xori",
                    String.format("%s, %s, %d # 0x%x", getRegName(rd),
                            getRegName(rs1), imm, imm12));
            return;
        }

        setReg(rd, getReg(rs1) ^ imm);
    }

    /**
     * ORI (Or immediate) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeOri(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int imm12 = inst.getImm12I();
        long imm = BitOp.signExt64(imm12, 12);

        if (!exec) {
            printDisasm(inst, "ori",
                    String.format("%s, %s, %d # 0x%x", getRegName(rd),
                            getRegName(rs1), imm, imm12));
            return;
        }

        setReg(rd, getReg(rs1) | imm);
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
        int shamt = inst.getField(20, 6);

        if (!exec) {
            printDisasm(inst, "slli",
                    String.format("%s, %s, 0x%x # %d", getRegName(rd),
                            getRegName(rs1), shamt, shamt));
            return;
        }

        setReg(rd, getReg(rs1) << shamt);
    }

    /**
     * SRLI (Shift right logical immediate) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSrli(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int shamt = inst.getField(20, 6);

        if (!exec) {
            printDisasm(inst, "srli",
                    String.format("%s, %s, 0x%x # %d", getRegName(rd),
                            getRegName(rs1), shamt, shamt));
            return;
        }

        setReg(rd, getReg(rs1) >>> shamt);
    }

    /**
     * SRAI (Shift right arithmetic immediate) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSrai(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int shamt = inst.getField(20, 6);

        if (!exec) {
            printDisasm(inst, "srai",
                    String.format("%s, %s, 0x%x # %d", getRegName(rd),
                            getRegName(rs1), shamt, shamt));
            return;
        }

        setReg(rd, getReg(rs1) >> shamt);
    }

    /**
     * ADD (Add) 命令。
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
     * SUB (Subtract) 命令。
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
     * SLL (Shift left logical) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSll(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();

        if (!exec) {
            printDisasm(inst, "sll",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rs1), getRegName(rs2)));
            return;
        }

        setReg(rd, getReg(rs1) << getReg(rs2));
    }

    /**
     * SLTU (Set if less than) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSltu(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();

        if (!exec) {
            printDisasm(inst, "sltu",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rs1), getRegName(rs2)));
            return;
        }

        if (IntegerExt.compareUint64(getReg(rs1), getReg(rs2)) < 0) {
            setReg(rd, 1);
        } else {
            setReg(rd, 0);
        }
    }

    /**
     * XOR (Exclusive-or) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeXor(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();

        if (!exec) {
            printDisasm(inst, "xor",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rs1), getRegName(rs2)));
            return;
        }

        setReg(rd, getReg(rs1) ^ getReg(rs2));
    }

    /**
     * OR (Or) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeOr(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();

        if (!exec) {
            printDisasm(inst, "or",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rs1), getRegName(rs2)));
            return;
        }

        setReg(rd, getReg(rs1) | getReg(rs2));
    }

    /**
     * AND (And) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAnd(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();

        if (!exec) {
            printDisasm(inst, "and",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rs1), getRegName(rs2)));
            return;
        }

        setReg(rd, getReg(rs1) & getReg(rs2));
    }

    /**
     * Fence (Fence memory and I/O) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeFence(InstructionRV32 inst, boolean exec) {
        int imm = inst.getImm12I();
        int succ = BitOp.getField32(imm, 0, 4);
        int pred = BitOp.getField32(imm, 4, 4);

        if (!exec) {
            printDisasm(inst, "fence",
                    String.format("%s, %s", getPredName(pred),
                            getPredName(succ)));
            return;
        }
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
     * LD (Load doubleword) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeLd(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int imm12 = inst.getImm12I();
        long off = BitOp.signExt64(imm12, 12);
        long vaddr, paddr;
        long val;

        if (!exec) {
            printDisasm(inst, "ld",
                    String.format("%s, %d(%s) # 0x%x",
                            getRegName(rd), off, getRegName(rs1), imm12));
            return;
        }

        vaddr = getReg(rs1) + off;

        //paddr = getMMU().translate(vaddr, 8, false, getPriv(), true);
        paddr = vaddr;
        //if (getMMU().isFault()) {
        //    getMMU().clearFault();
        //    return;
        //}

        if (!tryRead(paddr, 8)) {
            //raiseException(ARMv5.EXCEPT_ABT_DATA,
            //        String.format("ldrd [%08x]", paddr));
            return;
        }
        val = read64(paddr);

        setReg(rd, val);
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
     * SLLIW (Shift left logical word immediate) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSlliw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int shamt = inst.getField(20, 6);
        long v;

        if (!exec) {
            printDisasm(inst, "slliw",
                    String.format("%s, %s, 0x%x # %d", getRegName(rd),
                            getRegName(rs1), shamt, shamt));
            return;
        }

        v = getReg(rs1) << shamt;
        setReg(rd, BitOp.signExt64(v & 0xffffffffL, 32));
    }

    /**
     * SRLIW (Shift right logical word immediate) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSrliw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int shamt = inst.getField(20, 6);
        long v;

        if (!exec) {
            printDisasm(inst, "srliw",
                    String.format("%s, %s, 0x%x # %d", getRegName(rd),
                            getRegName(rs1), shamt, shamt));
            return;
        }

        v = (getReg(rs1) & 0xffffffffL) >>> shamt;
        setReg(rd, BitOp.signExt64(v & 0xffffffffL, 32));
    }

    /**
     * SRAIW (Shift right arithmetic word immediate) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSraiw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int shamt = inst.getField(20, 6);
        long v;

        if (!exec) {
            printDisasm(inst, "sraiw",
                    String.format("%s, %s, 0x%x # %d", getRegName(rd),
                            getRegName(rs1), shamt, shamt));
            return;
        }

        v = (int)getReg(rs1) >> shamt;
        setReg(rd, BitOp.signExt64(v & 0xffffffffL, 32));
    }

    /**
     * ADDW (Add word) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAddw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        long v;

        if (!exec) {
            printDisasm(inst, "addw",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rs1), getRegName(rs2)));
            return;
        }

        v = getReg(rs1) + getReg(rs2);
        setReg(rd, BitOp.signExt64(v & 0xffffffffL, 32));
    }

    /**
     * SUBW (subtract word) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSubw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        long v;

        if (!exec) {
            printDisasm(inst, "subw",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rs1), getRegName(rs2)));
            return;
        }

        v = getReg(rs1) - getReg(rs2);
        setReg(rd, BitOp.signExt64(v & 0xffffffffL, 32));
    }

    /**
     * SRLW (shift right logical word) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeSrlw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        long v;

        if (!exec) {
            printDisasm(inst, "srlw",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rs1), getRegName(rs2)));
            return;
        }

        v = (getReg(rs1) & 0xffffffffL) >>> getReg(rs2);
        setReg(rd, BitOp.signExt64(v & 0xffffffffL, 32));
    }

    /**
     * MUL (Multiply) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeMul(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();

        if (!exec) {
            printDisasm(inst, "mul",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rs1), getRegName(rs2)));
            return;
        }

        setReg(rd, getReg(rs1) * getReg(rs2));
    }

    /**
     * DIVU (Divide, Unsigned) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeDivu(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        BigInteger dividend, divisor;

        if (!exec) {
            printDisasm(inst, "divu",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rs1), getRegName(rs2)));
            return;
        }

        dividend = BitOp.toUnsignedBigInt(getReg(rs1));
        divisor = BitOp.toUnsignedBigInt(getReg(rs2));

        setReg(rd, dividend.divide(divisor).longValue());
    }

    /**
     * DIVUW (Divide word, Unsigned) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeDivuw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        long dividend, divisor;

        if (!exec) {
            printDisasm(inst, "divuw",
                    String.format("%s, %s, %s", getRegName(rd),
                            getRegName(rs1), getRegName(rs2)));
            return;
        }

        dividend = getReg(rs1) & 0xffffffffL;
        divisor = getReg(rs2) & 0xffffffffL;

        setReg(rd, BitOp.signExt64(dividend / divisor, 32));
    }

    /**
     * AMOSWAP.W (Atomic memory operation: Swap word) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAmoswapw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int aq = (inst.getFunct7R() >> 1) & 1;
        int rl = inst.getFunct7R() & 1;
        long vaddr, paddr;
        int t, valRs2;
        Lock l;

        if (!exec) {
            String name = "amoswap.w";

            if (aq != 0) {
                name += ".aq";
            }
            if (rl != 0) {
                name += ".rl";
            }

            printDisasm(inst, name,
                    String.format("%s, %s, (%s)", getRegName(rd),
                            getRegName(rs2), getRegName(rs1)));
            return;
        }

        vaddr = getReg(rs1);

        //paddr = getMMU().translate(vaddr, 4, false, getPriv(), true);
        paddr = vaddr;
        //if (getMMU().isFault()) {
        //    getMMU().clearFault();
        //    return;
        //}

        valRs2 = (int)getReg(rs2);

        l = getWriteLock();
        l.lock();
        try {
            if (!tryRead(paddr, 4)) {
                //raiseException(ARMv5.EXCEPT_ABT_DATA,
                //        String.format("ldrd [%08x]", paddr));
                return;
            }

            t = read32(paddr);
            write32(paddr, valRs2);
        } finally {
            l.unlock();
        }

        setReg(rd, BitOp.signExt64(t & 0xffffffffL, 32));
    }

    /**
     * AMOADD.W (Atomic memory operation: Add word) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAmoaddw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int aq = (inst.getFunct7R() >> 1) & 1;
        int rl = inst.getFunct7R() & 1;
        long vaddr, paddr;
        int t, valRs2;
        Lock l;

        if (!exec) {
            String name = "amoadd.w";

            if (aq != 0) {
                name += ".aq";
            }
            if (rl != 0) {
                name += ".rl";
            }

            printDisasm(inst, name,
                    String.format("%s, %s, (%s)", getRegName(rd),
                            getRegName(rs2), getRegName(rs1)));
            return;
        }

        vaddr = getReg(rs1);

        //paddr = getMMU().translate(vaddr, 4, false, getPriv(), true);
        paddr = vaddr;
        //if (getMMU().isFault()) {
        //    getMMU().clearFault();
        //    return;
        //}

        valRs2 = (int)getReg(rs2);

        l = getWriteLock();
        l.lock();
        try {
            if (!tryRead(paddr, 4)) {
                //raiseException(ARMv5.EXCEPT_ABT_DATA,
                //        String.format("ldrd [%08x]", paddr));
                return;
            }

            t = read32(paddr);
            write32(paddr, t + valRs2);
        } finally {
            l.unlock();
        }

        setReg(rd, BitOp.signExt64(t & 0xffffffffL, 32));
    }

    /**
     * AMOAND.W (Atomic memory operation: and word) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAmoandw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int aq = (inst.getFunct7R() >> 1) & 1;
        int rl = inst.getFunct7R() & 1;
        long vaddr, paddr;
        int t, valRs2;
        Lock l;

        if (!exec) {
            String name = "amoand.w";

            if (aq != 0) {
                name += ".aq";
            }
            if (rl != 0) {
                name += ".rl";
            }

            printDisasm(inst, name,
                    String.format("%s, %s, (%s)", getRegName(rd),
                            getRegName(rs2), getRegName(rs1)));
            return;
        }

        vaddr = getReg(rs1);

        //paddr = getMMU().translate(vaddr, 4, false, getPriv(), true);
        paddr = vaddr;
        //if (getMMU().isFault()) {
        //    getMMU().clearFault();
        //    return;
        //}

        valRs2 = (int)getReg(rs2);

        l = getWriteLock();
        l.lock();
        try {
            if (!tryRead(paddr, 4)) {
                //raiseException(ARMv5.EXCEPT_ABT_DATA,
                //        String.format("ldrd [%08x]", paddr));
                return;
            }

            t = read32(paddr);
            write32(paddr, t & valRs2);
        } finally {
            l.unlock();
        }

        setReg(rd, BitOp.signExt64(t & 0xffffffffL, 32));
    }

    /**
     * AMOOR.W (Atomic memory operation: OR word) 命令。
     *
     * @param inst 32bit 命令
     * @param exec デコードと実行なら true、デコードのみなら false
     */
    public void executeAmoorw(InstructionRV32 inst, boolean exec) {
        int rd = inst.getRd();
        int rs1 = inst.getRs1();
        int rs2 = inst.getRs2();
        int aq = (inst.getFunct7R() >> 1) & 1;
        int rl = inst.getFunct7R() & 1;
        long vaddr, paddr;
        int t, valRs2;
        Lock l;

        if (!exec) {
            String name = "amoor.w";

            if (aq != 0) {
                name += ".aq";
            }
            if (rl != 0) {
                name += ".rl";
            }

            printDisasm(inst, name,
                    String.format("%s, %s, (%s)", getRegName(rd),
                            getRegName(rs2), getRegName(rs1)));
            return;
        }

        vaddr = getReg(rs1);

        //paddr = getMMU().translate(vaddr, 4, false, getPriv(), true);
        paddr = vaddr;
        //if (getMMU().isFault()) {
        //    getMMU().clearFault();
        //    return;
        //}

        valRs2 = (int)getReg(rs2);

        l = getWriteLock();
        l.lock();
        try {
            if (!tryRead(paddr, 4)) {
                //raiseException(ARMv5.EXCEPT_ABT_DATA,
                //        String.format("ldrd [%08x]", paddr));
                return;
            }

            t = read32(paddr);
            write32(paddr, t | valRs2);
        } finally {
            l.unlock();
        }

        setReg(rd, BitOp.signExt64(t & 0xffffffffL, 32));
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
        case INS_RV32I_JAL:
            executeJal(inst, exec);
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
        case INS_RV32I_BGE:
            executeBge(inst, exec);
            break;
        case INS_RV32I_BLTU:
            executeBltu(inst, exec);
            break;
        case INS_RV32I_BGEU:
            executeBgeu(inst, exec);
            break;
        case INS_RV32I_LBU:
            executeLbu(inst, exec);
            break;
        case INS_RV32I_LW:
            executeLw(inst, exec);
            break;
        case INS_RV32I_SB:
            executeSb(inst, exec);
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
        case INS_RV32I_XORI:
            executeXori(inst, exec);
            break;
        case INS_RV32I_ORI:
            executeOri(inst, exec);
            break;
        case INS_RV32I_ANDI:
            executeAndi(inst, exec);
            break;
        case INS_RV32I_SLLI:
        case INS_RV64I_SLLI:
            executeSlli(inst, exec);
            break;
        case INS_RV32I_SRLI:
        case INS_RV64I_SRLI:
            executeSrli(inst, exec);
            break;
        case INS_RV32I_SRAI:
        case INS_RV64I_SRAI:
            executeSrai(inst, exec);
            break;
        case INS_RV32I_ADD:
            executeAdd(inst, exec);
            break;
        case INS_RV32I_SUB:
            executeSub(inst, exec);
            break;
        case INS_RV32I_SLL:
            executeSll(inst, exec);
            break;
        case INS_RV32I_SLTU:
            executeSltu(inst, exec);
            break;
        case INS_RV32I_XOR:
            executeXor(inst, exec);
            break;
        case INS_RV32I_OR:
            executeOr(inst, exec);
            break;
        case INS_RV32I_AND:
            executeAnd(inst, exec);
            break;
        case INS_RV32I_FENCE:
            executeFence(inst, exec);
            break;
        case INS_RV32I_CSRRW:
            executeCsrrw(inst, exec);
            break;
        case INS_RV32I_CSRRS:
            executeCsrrs(inst, exec);
            break;
        case INS_RV32I_WFI:
            executeWfi(inst, exec);
            break;
        case INS_RV64I_LD:
            executeLd(inst, exec);
            break;
        case INS_RV64I_ADDIW:
            executeAddiw(inst, exec);
            break;
        case INS_RV64I_SLLIW:
            executeSlliw(inst, exec);
            break;
        case INS_RV64I_SRLIW:
            executeSrliw(inst, exec);
            break;
        case INS_RV64I_SRAIW:
            executeSraiw(inst, exec);
            break;
        case INS_RV64I_ADDW:
            executeAddw(inst, exec);
            break;
        case INS_RV64I_SUBW:
            executeSubw(inst, exec);
            break;
        case INS_RV64I_SRLW:
            executeSrlw(inst, exec);
            break;
        case INS_RV32M_MUL:
            executeMul(inst, exec);
            break;
        case INS_RV32M_DIVU:
            executeDivu(inst, exec);
            break;
        case INS_RV64M_DIVUW:
            executeDivuw(inst, exec);
            break;
        case INS_RV32A_AMOSWAP_W:
            executeAmoswapw(inst, exec);
            break;
        case INS_RV32A_AMOADD_W:
            executeAmoaddw(inst, exec);
            break;
        case INS_RV32A_AMOAND_W:
            executeAmoandw(inst, exec);
            break;
        case INS_RV32A_AMOOR_W:
            executeAmoorw(inst, exec);
            break;
        default:
            throw new IllegalArgumentException("Unknown RV32I instruction " +
                    decinst.getIndex());
        }
    }
}
