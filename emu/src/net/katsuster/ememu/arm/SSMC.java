package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * メモリコントローラ
 *
 * 参考: ARM PrimeCell Synchronous Static Memory Controller (PL093)
 * ARM DDI0236H
 *
 * @author katsuhiro
 */
public class SSMC implements BusSlave {
    private SSMCSlave slave;

    public static final int REG_SMBIDCYR0     = 0x000;
    public static final int REG_SMBWSTRDR0    = 0x004;
    public static final int REG_SMBWSTWRR0    = 0x008;
    public static final int REG_SMBWSTOENR0   = 0x00c;
    public static final int REG_SMBWSTWENR0   = 0x010;
    public static final int REG_SMBCR0        = 0x014;
    public static final int REG_SMBSR0        = 0x018;
    public static final int REG_SMBWSTBRDR0   = 0x01c;
    public static final int REG_SMBIDCYR1     = 0x020;
    public static final int REG_SMBWSTRDR1    = 0x024;
    public static final int REG_SMBWSTWRR1    = 0x028;
    public static final int REG_SMBWSTOENR1   = 0x02c;
    public static final int REG_SMBWSTWENR1   = 0x030;
    public static final int REG_SMBCR1        = 0x034;
    public static final int REG_SMBSR1        = 0x038;
    public static final int REG_SMBWSTBRDR1   = 0x03c;
    public static final int REG_SMBIDCYR2     = 0x040;
    public static final int REG_SMBWSTRDR2    = 0x044;
    public static final int REG_SMBWSTWRR2    = 0x048;
    public static final int REG_SMBWSTOENR2   = 0x04c;
    public static final int REG_SMBWSTWENR2   = 0x050;
    public static final int REG_SMBCR2        = 0x054;
    public static final int REG_SMBSR2        = 0x058;
    public static final int REG_SMBWSTBRDR2   = 0x05c;
    public static final int REG_SMBIDCYR3     = 0x060;
    public static final int REG_SMBWSTRDR3    = 0x064;
    public static final int REG_SMBWSTWRR3    = 0x068;
    public static final int REG_SMBWSTOENR3   = 0x06c;
    public static final int REG_SMBWSTWENR3   = 0x070;
    public static final int REG_SMBCR3        = 0x074;
    public static final int REG_SMBSR3        = 0x078;
    public static final int REG_SMBWSTBRDR3   = 0x07c;
    public static final int REG_SMBIDCYR4     = 0x080;
    public static final int REG_SMBWSTRDR4    = 0x084;
    public static final int REG_SMBWSTWRR4    = 0x088;
    public static final int REG_SMBWSTOENR4   = 0x08c;
    public static final int REG_SMBWSTWENR4   = 0x090;
    public static final int REG_SMBCR4        = 0x094;
    public static final int REG_SMBSR4        = 0x098;
    public static final int REG_SMBWSTBRDR4   = 0x09c;
    public static final int REG_SMBIDCYR5     = 0x0a0;
    public static final int REG_SMBWSTRDR5    = 0x0a4;
    public static final int REG_SMBWSTWRR5    = 0x0a8;
    public static final int REG_SMBWSTOENR5   = 0x0ac;
    public static final int REG_SMBWSTWENR5   = 0x0b0;
    public static final int REG_SMBCR5        = 0x0b4;
    public static final int REG_SMBSR5        = 0x0b8;
    public static final int REG_SMBWSTBRDR5   = 0x0bc;
    public static final int REG_SMBIDCYR6     = 0x0c0;
    public static final int REG_SMBWSTRDR6    = 0x0c4;
    public static final int REG_SMBWSTWRR6    = 0x0c8;
    public static final int REG_SMBWSTOENR6   = 0x0cc;
    public static final int REG_SMBWSTWENR6   = 0x0d0;
    public static final int REG_SMBCR6        = 0x0d4;
    public static final int REG_SMBSR6        = 0x0d8;
    public static final int REG_SMBWSTBRDR6   = 0x0dc;
    public static final int REG_SMBIDCYR7     = 0x0e0;
    public static final int REG_SMBWSTRDR7    = 0x0e4;
    public static final int REG_SMBWSTWRR7    = 0x0e8;
    public static final int REG_SMBWSTOENR7   = 0x0ec;
    public static final int REG_SMBWSTWENR7   = 0x0f0;
    public static final int REG_SMBCR7        = 0x0f4;
    public static final int REG_SMBSR7        = 0x0f8;
    public static final int REG_SMBWSTBRDR7   = 0x0fc;

    public static final int REG_SSMCSR        = 0x200;
    public static final int REG_SSMCCR        = 0x204;
    public static final int REG_SSMCITCR      = 0x208;
    public static final int REG_SSMCITIP      = 0x20c;
    public static final int REG_SSMCITOP      = 0x210;

    public static final int REG_SSMCPeriphID0 = 0xfe0;
    public static final int REG_SSMCPeriphID1 = 0xfe4;
    public static final int REG_SSMCPeriphID2 = 0xfe8;
    public static final int REG_SSMCPeriphID3 = 0xfec;
    public static final int REG_SSMCPCellID0  = 0xff0;
    public static final int REG_SSMCPCellID1  = 0xff4;
    public static final int REG_SSMCPCellID2  = 0xff8;
    public static final int REG_SSMCPCellID3  = 0xffc;

    public SSMC() {
        slave = new SSMCSlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class SSMCSlave extends Controller32 {
        public SSMCSlave() {
            addReg(REG_SMBIDCYR0, "SMBIDCYR0", 0xf);
            //addReg(REG_SMBWSTRDR0, "SMBWSTRDR0", 0x1f);
            //addReg(REG_SMBWSTWRR0, "SMBWSTWRR0", 0x1f);
            addReg(REG_SMBWSTOENR0, "SMBWSTOENR0", 0x0);
            //addReg(REG_SMBWSTWENR0, "SMBWSTWENR0", 0x1);
            //addReg(REG_SMBCR0, "SMBCR0", 0x303020);
            //addReg(REG_SMBSR0, "SMBSR0", 0x0);
            //addReg(REG_SMBWSTBRDR0, "SMBWSTBRDR0", 0x1f);
            //addReg(REG_SMBIDCYR1, "SMBIDCYR1", 0xf);
            //addReg(REG_SMBWSTRDR1, "SMBWSTRDR1", 0x1f);
            //addReg(REG_SMBWSTWRR1, "SMBWSTWRR1", 0x1f);
            //addReg(REG_SMBWSTOENR1, "SMBWSTOENR1", 0x0);
            //addReg(REG_SMBWSTWENR1, "SMBWSTWENR1", 0x1);
            //addReg(REG_SMBCR1, "SMBCR1", 0x303000);
            //addReg(REG_SMBSR1, "SMBSR1", 0x0);
            //addReg(REG_SMBWSTBRDR1, "SMBWSTBRDR1", 0x1f);
            //addReg(REG_SMBIDCYR2, "SMBIDCYR2", 0xf);
            //addReg(REG_SMBWSTRDR2, "SMBWSTRDR2", 0x1f);
            //addReg(REG_SMBWSTWRR2, "SMBWSTWRR2", 0x1f);
            //addReg(REG_SMBWSTOENR2, "SMBWSTOENR2", 0x0);
            //addReg(REG_SMBWSTWENR2, "SMBWSTWENR2", 0x1);
            //addReg(REG_SMBCR2, "SMBCR2", 0x303010);
            //addReg(REG_SMBSR2, "SMBSR2", 0x0);
            //addReg(REG_SMBWSTBRDR2, "SMBWSTBRDR2", 0x1f);
            //addReg(REG_SMBIDCYR3, "SMBIDCYR3", 0xf);
            //addReg(REG_SMBWSTRDR3, "SMBWSTRDR3", 0x1f);
            //addReg(REG_SMBWSTWRR3, "SMBWSTWRR3", 0x1f);
            //addReg(REG_SMBWSTOENR3, "SMBWSTOENR3", 0x0);
            //addReg(REG_SMBWSTWENR3, "SMBWSTWENR3", 0x1);
            //addReg(REG_SMBCR3, "SMBCR3", 0x303000);
            //addReg(REG_SMBSR3, "SMBSR3", 0x0);
            //addReg(REG_SMBWSTBRDR3, "SMBWSTBRDR3", 0x1f);
            //addReg(REG_SMBIDCYR4, "SMBIDCYR4", 0xf);
            //addReg(REG_SMBWSTRDR4, "SMBWSTRDR4", 0x1f);
            //addReg(REG_SMBWSTWRR4, "SMBWSTWRR4", 0x1f);
            //addReg(REG_SMBWSTOENR4, "SMBWSTOENR4", 0x0);
            //addReg(REG_SMBWSTWENR4, "SMBWSTWENR4", 0x1);
            //addReg(REG_SMBCR4, "SMBCR4", 0x303020);
            //addReg(REG_SMBSR4, "SMBSR4", 0x0);
            //addReg(REG_SMBWSTBRDR4, "SMBWSTBRDR4", 0x1f);
            //addReg(REG_SMBIDCYR5, "SMBIDCYR5", 0xf);
            //addReg(REG_SMBWSTRDR5, "SMBWSTRDR5", 0x1f);
            //addReg(REG_SMBWSTWRR5, "SMBWSTWRR5", 0x1f);
            //addReg(REG_SMBWSTOENR5, "SMBWSTOENR5", 0x0);
            //addReg(REG_SMBWSTWENR5, "SMBWSTWENR5", 0x1);
            //addReg(REG_SMBCR5, "SMBCR5", 0x303020);
            //addReg(REG_SMBSR5, "SMBSR5", 0x0);
            //addReg(REG_SMBWSTBRDR5, "SMBWSTBRDR5", 0x1f);
            //addReg(REG_SMBIDCYR6, "SMBIDCYR6", 0xf);
            //addReg(REG_SMBWSTRDR6, "SMBWSTRDR6", 0x1f);
            //addReg(REG_SMBWSTWRR6, "SMBWSTWRR6", 0x1f);
            //addReg(REG_SMBWSTOENR6, "SMBWSTOENR6", 0x0);
            //addReg(REG_SMBWSTWENR6, "SMBWSTWENR6", 0x1);
            //addReg(REG_SMBCR6, "SMBCR6", 0x303010);
            //addReg(REG_SMBSR6, "SMBSR6", 0x0);
            //addReg(REG_SMBWSTBRDR6, "SMBWSTBRDR6", 0x1f);
            //addReg(REG_SMBIDCYR7, "SMBIDCYR7", 0xf);
            //addReg(REG_SMBWSTRDR7, "SMBWSTRDR7", 0x1f);
            //addReg(REG_SMBWSTWRR7, "SMBWSTWRR7", 0x1f);
            //addReg(REG_SMBWSTOENR7, "SMBWSTOENR7", 0x0);
            //addReg(REG_SMBWSTWENR7, "SMBWSTWENR7", 0x1);
            //addReg(REG_SMBCR7, "SMBCR7", 0x303000);
            //addReg(REG_SMBSR7, "SMBSR7", 0x0);
            //addReg(REG_SMBWSTBRDR7, "SMBWSTBRDR7", 0x1f);

            //addReg(REG_SSMCSR, "SSMCSR", 0x0);
            //addReg(REG_SSMCCR, "SSMCCR", 0x1);
            //addReg(REG_SSMCITCR, "SSMCITCR", 0x0);
            //addReg(REG_SSMCITIP, "SSMCITIP", -);
            //addReg(REG_SSMCITOP, "SSMCITOP", -);

            addReg(REG_SSMCPeriphID0, "SSMCPeriphID0", 0x93);
            addReg(REG_SSMCPeriphID1, "SSMCPeriphID1", 0x10);
            addReg(REG_SSMCPeriphID2, "SSMCPeriphID2", 0x14);
            addReg(REG_SSMCPeriphID3, "SSMCPeriphID3", 0x00);
            addReg(REG_SSMCPCellID0, "SSMCPCellID0", 0x0d);
            addReg(REG_SSMCPCellID1, "SSMCPCellID1", 0xf0);
            addReg(REG_SSMCPCellID2, "SSMCPCellID2", 0x05);
            addReg(REG_SSMCPCellID3, "SSMCPCellID3", 0xb1);
        }

        @Override
        public int readWord(long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_SMBIDCYR0:
                //TODO: Not implemented
                result = super.readWord(regaddr);
                System.out.printf("SMBIDCYR0: read 0x%08x\n", result);
                break;
            case REG_SMBWSTOENR0:
                //TODO: Not implemented
                result = super.readWord(regaddr);
                System.out.printf("SMBWSTOENR0: read 0x%08x\n", result);
                break;
            default:
                result = super.readWord(regaddr);
                break;
            }

            return result;
        }

        @Override
        public void writeWord(long addr, int data) {
            int regaddr;

            regaddr = (int) (addr & getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_SMBIDCYR0:
                //TODO: Not implemented
                System.out.printf("SMBIDCYR0: 0x%08x\n", data);
                break;
            case REG_SMBWSTOENR0:
                //TODO: Not implemented
                System.out.printf("SMBWSTOENR0: 0x%08x\n", data);
                break;
            case REG_SSMCPeriphID0:
            case REG_SSMCPeriphID1:
            case REG_SSMCPeriphID2:
            case REG_SSMCPeriphID3:
            case REG_SSMCPCellID0:
            case REG_SSMCPCellID1:
            case REG_SSMCPCellID2:
            case REG_SSMCPCellID3:
                //read only, ignored
                break;
            default:
                super.writeWord(regaddr, data);
                break;
            }
        }

        @Override
        public void run() {
            //do nothing
        }
    }

}
