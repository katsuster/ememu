package net.katsuster.ememu.riscv.core.reg;

import net.katsuster.ememu.generic.core.Reg64;
import net.katsuster.ememu.riscv.core.*;

public class RegHartid64 extends Reg64 {
    private RV64 core;

    public RegHartid64(String name, long val, RV64 c) {
        super(name, 0);

        core = c;
    }

    @Override
    public long getValue() {
        return core.getThreadID();
    }

    @Override
    public void setValue(long v) {
        // Ignore
    }
}
