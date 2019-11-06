package net.katsuster.ememu.riscv;

import net.katsuster.ememu.generic.*;

/**
 * Inter-Integrated Circuit (I2C)
 *
 * 参考: OpenCores I2C controller core
 *       I2C-Master Core Specifications Rev.0.9
 */
public class I2C extends AbstractParentCore {
    public static final int REG_PRERLO  = 0x0000;
    public static final int REG_PRERHI  = 0x0004;
    public static final int REG_CTR     = 0x0008;
    public static final int REG_TXRRXR  = 0x000c;
    public static final int REG_CRSR    = 0x0010;

    public I2C(String n) {
        super(n);

        setSlaveCore(new I2CSlave());
    }

    class I2CSlave extends Controller32 {
        public I2CSlave() {
            addReg(REG_PRERLO,  "PRERLO", 0x00000000);
            addReg(REG_PRERHI,  "PRERHI", 0x00000000);
            addReg(REG_CTR,  "CTR", 0x00000000);
            addReg(REG_TXRRXR,  "TXRRXR", 0x00000000);
            addReg(REG_CRSR,  "CRSR", 0x00000000);
        }

        @Override
        public int readWord(BusMaster64 m, long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_TXRRXR:
                result = 0;
                System.out.printf("I2C TXRX: read 0x%x\n", result);
                break;
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
            case REG_PRERLO:
                System.out.printf("I2C PRERLO: write 0x%x\n", data);
                super.writeWord(m, regaddr, data);
                break;
            case REG_PRERHI:
                System.out.printf("I2C PRERHI: write 0x%x\n", data);
                super.writeWord(m, regaddr, data);
                break;
            case REG_TXRRXR:
                System.out.printf("I2C TXRX: write 0x%x\n", data);
                super.writeWord(m, regaddr, data);
                break;
            default:
                super.writeWord(m, regaddr, data);
                break;
            }
        }
    }
}
