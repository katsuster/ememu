package net.katsuster.ememu.riscv.core;

import net.katsuster.ememu.generic.*;

public class InstructionRV16 extends Inst32 {
    /**
     * 指定されたバイナリ値の 16bit RISC-V 命令を作成します。
     *
     * @param inst 16bit RISC-V 命令のバイナリ値
     */
    public InstructionRV16(int inst) {
        super(inst & 0x0000ffff, 2);
    }

    /**
     * 命令の 16進数表記を取得します。
     *
     * @return 命令の 16進数表記
     */
    @Override
    public String toHex() {
        return String.format("%04x", getInst());
    }
}
