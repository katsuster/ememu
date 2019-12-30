package net.katsuster.ememu.riscv;

import net.katsuster.ememu.generic.BitOp;
import net.katsuster.ememu.generic.Controller32;
import net.katsuster.ememu.generic.core.AbstractParentCore;
import net.katsuster.ememu.generic.bus.BusMaster64;

/**
 * General Purpose Input/Output Controller (GPIO)
 *
 * 参考: SiFive FU540-C000 Manual: v1p0
 */
public class GPIO extends AbstractParentCore {
    public static final int REG_INPUT_VAL  = 0x00;
    public static final int REG_INPUT_EN   = 0x04;
    public static final int REG_OUTPUT_EN  = 0x08;
    public static final int REG_OUTPUT_VAL = 0x0c;
    public static final int REG_PUE        = 0x10;
    public static final int REG_DS         = 0x14;
    public static final int REG_RISE_IE    = 0x18;
    public static final int REG_RISE_IP    = 0x1c;
    public static final int REG_FALL_IE    = 0x20;
    public static final int REG_FALL_IP    = 0x24;
    public static final int REG_HIGH_IE    = 0x28;
    public static final int REG_HIGH_IP    = 0x2c;
    public static final int REG_LOW_IE     = 0x30;
    public static final int REG_LOW_IP     = 0x34;
    public static final int REG_OUT_XOR    = 0x40;

    public GPIO(String n) {
        super(n);

        setSlaveCore(new GPIOSlave());
    }

    class GPIOSlave extends Controller32 {
        public GPIOSlave() {
            addReg(REG_INPUT_VAL,  "INPUT_VAL",  0x00000000);
            addReg(REG_INPUT_EN,   "INPUT_EN",   0x00000000);
            addReg(REG_OUTPUT_EN,  "OUTPUT_EN",  0x00000000);
            addReg(REG_OUTPUT_VAL, "OUTPUT_VAL", 0x00000000);
            addReg(REG_PUE,        "PUE",        0x00000000);
            addReg(REG_DS,         "DS",         0x00000000);
            addReg(REG_RISE_IE,    "RISE_IE",    0x00000000);
            addReg(REG_RISE_IP,    "RISE_IP",    0x00000000);
            addReg(REG_FALL_IE,    "FALL_IE",    0x00000000);
            addReg(REG_FALL_IP,    "FALL_IP",    0x00000000);
            addReg(REG_HIGH_IE,    "HIGH_IE",    0x00000000);
            addReg(REG_HIGH_IP,    "HIGH_IP",    0x00000000);
            addReg(REG_LOW_IE,     "LOW_IE",     0x00000000);
            addReg(REG_LOW_IP,     "LOW_IP",     0x00000000);
            addReg(REG_OUT_XOR,    "OUT_XOR",    0x00000000);
        }

        @Override
        public int readWord(BusMaster64 m, long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            default:
                result = super.readWord(m, regaddr);
                break;
            }

            return result;
        }

        @Override
        public void writeWord(BusMaster64 m, long addr, int data) {
            int regaddr;

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            default:
                super.writeWord(m, regaddr, data);
                break;
            }
        }
    }
}
