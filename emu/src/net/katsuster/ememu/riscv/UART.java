package net.katsuster.ememu.riscv;

import net.katsuster.ememu.generic.*;

public class UART implements ParentCore {
    private UART.UARTSlave slave;

    public static final int REG_TXDATA = 0x0000;
    public static final int REG_RXDATA = 0x0004;
    public static final int REG_TXCTRL = 0x0008;
    public static final int REG_RXCTRL = 0x000c;
    public static final int REG_IE     = 0x0010;
    public static final int REG_IP     = 0x0014;
    public static final int REG_DIV    = 0x0018;

    public UART() {
        slave = new UARTSlave();
    }

    @Override
    public SlaveCore64 getSlaveCore() {
        return slave;
    }

    class UARTSlave extends Controller32 {
        public UARTSlave() {
            addReg(REG_TXDATA, "TXDATA", 0x00000000);
            /*
            addReg(REG_RXDATA, "RXDATA", 0x00000000);
            */
            addReg(REG_TXCTRL, "TXCTRL", 0x00000000);
            /*
            addReg(REG_RXCTRL, "RXCTRL", 0x00000000);
            addReg(REG_IE,     "IE", 0x00000000);
            addReg(REG_IP,     "IP", 0x00000000);
            */
            addReg(REG_DIV,     "DIV", 0x00000000);
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
            case REG_TXDATA:
                System.out.printf("UART TXDATA: %c\n", data);
                break;
            case REG_TXCTRL:
                System.out.printf("UART TXCTRL: 0x%x\n", data);
                break;
            case REG_DIV:
                System.out.printf("UART Divisor: %d\n", data);
                break;
            default:
                super.writeWord(m, regaddr, data);
                break;
            }
        }

    }
}
