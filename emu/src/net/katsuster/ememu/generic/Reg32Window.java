package net.katsuster.ememu.generic;

/**
 * @author katsuhiro
 */
public class Reg32Window {
    private Reg32 regs[];

    public Reg32Window(int size) {
        regs = new Reg32[size];
        for (int i = 0; i < size; i++) {
            regs[i] = new Reg32();
        }
    }

    public Reg32 getReg(int n) {
        return regs[n];
    }

    public void setReg(int n, Reg32 r) {
        regs[n] = r;
    }
}
