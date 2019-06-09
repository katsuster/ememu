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
                "r0",  "r1",  "r2",  "r3",  "r4",  "r5",  "r6",  "r7",
                "r8",  "r9",  "r10", "r11", "r12", "r13", "r14", "r15",
                "r16", "r17", "r18", "r19", "r20", "r21", "r22", "r23",
                "r24", "r25", "r26", "r27", "r28", "r29", "r30", "r31",
                "pc",
        };

        regs_usr = new Reg64[RISCV_REGFILE_SIZE];

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
