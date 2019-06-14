package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

/**
 * RISC-V 64 のレジスタファイルです。
 */
public class RV64RegFile implements Reg64File {
    public static final int RISCV_REGFILE_SIZE = 33;

    private Reg64[] regs_usr;

    //現在選択されているレジスタセットです
    private Reg64[] regs;

    public RV64RegFile() {
        String[] name_usr = {
                "zero", "ra",  "sp",  "gp",  "tp",  "t0",  "t1",  "t2",
                "fp",   "s1",  "a0",  "a1",  "a2",  "a3",  "a4",  "a5",
                "a6",   "a7",  "s2",  "s3",  "s4",  "s5",  "s6",  "s7",
                "s8",   "s9",  "s10", "s11", "t3",  "t4",  "t5",  "t6",
                "pc",
        };

        regs_usr = new Reg64[RISCV_REGFILE_SIZE];
        for (int i = 0; i < RISCV_REGFILE_SIZE; i++) {
            regs_usr[i] = new Reg64(name_usr[i], 0);
        }

        regs = regs_usr;
    }

    @Override
    public Reg64 getReg(int n) {
        return regs[n];
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        for (int i = 0; i < 32; i += 4) {
            b.append(String.format("  %3s: %08x, %3s: %08x, %3s: %08x, %3s: %08x, \n",
                    getReg(i).getName(), getReg(i).getValue(),
                    getReg(i + 1).getName(), getReg(i + 1).getValue(),
                    getReg(i + 2).getName(), getReg(i + 2).getValue(),
                    getReg(i + 3).getName(), getReg(i + 3).getValue()));
        }
        b.append(String.format("  %s, %s\n",
                getReg(32).toString(), getReg(32).toString()));

        return b.toString();
    }
}
