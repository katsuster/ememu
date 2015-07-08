package net.katsuster.ememu.arm.core;

import net.katsuster.ememu.generic.*;

/**
 * ARM のレジスタファイルです。
 *
 * @author katsuhiro
 */
public class ARMRegFile implements Reg32File {
    public static final int ARM_REGFILE_SIZE = 17;
    public static final int ARM_REG_SPSR = 16;

    private Reg32[] regs_usr;
    private Reg32[] regs_svc;
    private Reg32[] regs_abt;
    private Reg32[] regs_und;
    private Reg32[] regs_irq;
    private Reg32[] regs_fiq;
    private PSR cpsr;

    //現在選択されているレジスタセットです
    private Reg32[] regs;

    public ARMRegFile() {
        String[] name_usr = {
                "r0", "r1", "r2", "r3", "r4", "r5", "r6", "r7",
                "r8", "r9", "r10", "r11", "r12", "r13", "r14", "pc", "spsr_usr",
        };
        String[] name_svc = {
                "r0", "r1", "r2", "r3", "r4", "r5", "r6", "r7",
                "r8", "r9", "r10", "r11", "r12", "r13_svc", "r14_svc", "pc", "spsr_svc",
        };
        String[] name_abt = {
                "r0", "r1", "r2", "r3", "r4", "r5", "r6", "r7",
                "r8", "r9", "r10", "r11", "r12", "r13_abt", "r14_abt", "pc", "spsr_abt",
        };
        String[] name_und = {
                "r0", "r1", "r2", "r3", "r4", "r5", "r6", "r7",
                "r8", "r9", "r10", "r11", "r12", "r13_und", "r14_und", "pc", "spsr_und",
        };
        String[] name_irq = {
                "r0", "r1", "r2", "r3", "r4", "r5", "r6", "r7",
                "r8", "r9", "r10", "r11", "r12", "r13_irq", "r14_irq", "pc", "spsr_irq",
        };
        String[] name_fiq = {
                "r0", "r1", "r2", "r3", "r4", "r5", "r6", "r7",
                "r8_fiq", "r9_fiq", "r10_fiq", "r11_fiq", "r12_fiq", "r13_fiq", "r14_fiq", "pc", "spsr_fiq",
        };

        regs_usr = new Reg32[ARM_REGFILE_SIZE];
        regs_svc = new Reg32[ARM_REGFILE_SIZE];
        regs_abt = new Reg32[ARM_REGFILE_SIZE];
        regs_und = new Reg32[ARM_REGFILE_SIZE];
        regs_irq = new Reg32[ARM_REGFILE_SIZE];
        regs_fiq = new Reg32[ARM_REGFILE_SIZE];
        for (int i = 0; i < ARM_REGFILE_SIZE; i++) {
            regs_usr[i] = new Reg32(name_usr[i], 0);
            regs_svc[i] = regs_usr[i];
            regs_abt[i] = regs_usr[i];
            regs_und[i] = regs_usr[i];
            regs_irq[i] = regs_usr[i];
            regs_fiq[i] = regs_usr[i];
        }
        for (int i = 13; i < 15; i++) {
            regs_svc[i] = new Reg32(name_svc[i], 0);
            regs_abt[i] = new Reg32(name_abt[i], 0);
            regs_und[i] = new Reg32(name_und[i], 0);
            regs_irq[i] = new Reg32(name_irq[i], 0);
        }
        for (int i = 8; i < 15; i++) {
            regs_fiq[i] = new Reg32(name_fiq[i], 0);
        }
        for (int i = ARM_REG_SPSR; i < ARM_REGFILE_SIZE; i++) {
            regs_svc[i] = new Reg32(name_svc[i], 0);
            regs_abt[i] = new Reg32(name_abt[i], 0);
            regs_und[i] = new Reg32(name_und[i], 0);
            regs_irq[i] = new Reg32(name_irq[i], 0);
            regs_fiq[i] = new Reg32(name_fiq[i], 0);
        }
        cpsr = new PSR("cpsr", 0, this);
        regs_usr[ARM_REG_SPSR] = new SPSR(regs_usr[ARM_REG_SPSR]);
        regs_svc[ARM_REG_SPSR] = new SPSR(regs_svc[ARM_REG_SPSR]);
        regs_abt[ARM_REG_SPSR] = new SPSR(regs_abt[ARM_REG_SPSR]);
        regs_und[ARM_REG_SPSR] = new SPSR(regs_und[ARM_REG_SPSR]);
        regs_irq[ARM_REG_SPSR] = new SPSR(regs_irq[ARM_REG_SPSR]);
        regs_fiq[ARM_REG_SPSR] = new SPSR(regs_fiq[ARM_REG_SPSR]);

        regs = regs_usr;
    }

    @Override
    public Reg32 getReg(int n) {
        return regs[n];
    }

    /**
     * 指定された動作モードにおけるレジスタセットを取得します。
     *
     * @param mode 動作モード
     * @return レジスタセット
     */
    protected Reg32[] getRegSet(int mode) {
        switch (mode) {
        case PSR.MODE_USR:
        case PSR.MODE_SYS:
            return regs_usr;
        case PSR.MODE_SVC:
            return regs_svc;
        case PSR.MODE_ABT:
            return regs_abt;
        case PSR.MODE_UND:
            return regs_und;
        case PSR.MODE_IRQ:
            return regs_irq;
        case PSR.MODE_FIQ:
            return regs_fiq;
        default:
            //do nothing
            break;
        }

        throw new IllegalArgumentException("Illegal mode " +
                String.format("mode:0x%x.", mode));
    }

    /**
     * PSR の値が変化したことを通知します。
     * PSR オブジェクトからコールバックされることを想定しています。
     *
     * このメソッドを呼び出すと、
     * PSR のモードに合ったレジスタセットが選択されます。
     *
     * @see PSR#setValue(int)
     */
    public void notifyChangedPSR() {
        regs = getRegSet(getCPSR().getMode());
    }

    /**
     * CPSR(カレントプログラムステートレジスタ)を取得します。
     *
     * @return CPSR
     */
    public PSR getCPSR() {
        return cpsr;
    }

    /**
     * SPSR（保存されたプログラムステートレジスタ）を取得します。
     *
     * @return SPSR
     */
    public SPSR getSPSR() {
        return (SPSR)getReg(ARM_REG_SPSR);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        for (int i = 0; i < 16; i += 4) {
            b.append(String.format("  %3s: %08x, %3s: %08x, %3s: %08x, %3s: %08x, \n",
                    getReg(i).getName(), getReg(i).getValue(),
                    getReg(i + 1).getName(), getReg(i + 1).getValue(),
                    getReg(i + 2).getName(), getReg(i + 2).getValue(),
                    getReg(i + 3).getName(), getReg(i + 3).getValue()));
        }
        b.append(String.format("  %s, %s\n",
                getCPSR().toString(), getSPSR().toString()));

        return b.toString();
    }
}
