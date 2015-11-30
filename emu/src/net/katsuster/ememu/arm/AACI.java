package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * オーディオコーデックインタフェース
 *
 * 参考: ARM PrimeCell Advanced Audio CODEC Interface (PL041)
 * ARM DDI0173B
 *
 * @author katsuhiro
 */
public class AACI implements BusSlave {
    private AACISlave slave;

    public static final int REG_AACIRXCR1     = 0x000;
    public static final int REG_AACITXCR1     = 0x004;
    public static final int REG_AACISR1       = 0x008;
    public static final int REG_AACIISR1      = 0x00c;
    public static final int REG_AACIIE1       = 0x010;
    public static final int REG_AACIRXCR2     = 0x014;
    public static final int REG_AACITXCR2     = 0x018;
    public static final int REG_AACISR2       = 0x01c;
    public static final int REG_AACIISR2      = 0x020;
    public static final int REG_AACIIE2       = 0x024;
    public static final int REG_AACIRXCR3     = 0x028;
    public static final int REG_AACITXCR3     = 0x02c;
    public static final int REG_AACISR3       = 0x030;
    public static final int REG_AACIISR3      = 0x034;
    public static final int REG_AACIIE3       = 0x038;
    public static final int REG_AACIRXCR4     = 0x03c;
    public static final int REG_AACITXCR4     = 0x040;
    public static final int REG_AACISR4       = 0x044;
    public static final int REG_AACIISR4      = 0x048;
    public static final int REG_AACIIE4       = 0x04c;
    public static final int REG_AACISL1RX     = 0x050;
    public static final int REG_AACISL1TX     = 0x054;
    public static final int REG_AACISL2RX     = 0x058;
    public static final int REG_AACISL2TX     = 0x05c;
    public static final int REG_AACISL12RX    = 0x060;
    public static final int REG_AACISL12TX    = 0x064;
    public static final int REG_AACISLFR      = 0x068;
    public static final int REG_AACISLISTAT   = 0x06c;
    public static final int REG_AACISLIEN     = 0x070;
    public static final int REG_AACIINTCLR    = 0x074;
    public static final int REG_AACIMAINCR    = 0x078;
    public static final int REG_AACIRESET     = 0x07c;
    public static final int REG_AACISYNC      = 0x080;
    public static final int REG_AACIALLINTS   = 0x084;
    public static final int REG_AACIMAINFR    = 0x088;

    //0x090-0x0ac AACIDR1
    //0x0b0-0x0cc AACIDR2
    //0x0d0-0x0ec AACIDR3
    //0x0f0-0xa0c AACIDR4

    public static final int REG_AACIPERIPHID0 = 0xfe0;
    public static final int REG_AACIPERIPHID1 = 0xfe4;
    public static final int REG_AACIPERIPHID2 = 0xfe8;
    public static final int REG_AACIPERIPHID3 = 0xfec;
    public static final int REG_AACIPCELLID0  = 0xff0;
    public static final int REG_AACIPCELLID1  = 0xff4;
    public static final int REG_AACIPCELLID2  = 0xff8;
    public static final int REG_AACIPCELLID3  = 0xffc;

    public AACI() {
        slave = new AACISlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class AACISlave extends Controller32 {
        public AACISlave() {
            //addReg(REG_AACIRXCR1, "AACIRXCR1", 0x00000000);
            //addReg(REG_AACITXCR1, "AACITXCR1", 0x00000);
            //addReg(REG_AACISR1, "AACISR1", 0x00b);
            //addReg(REG_AACIISR1, "AACIISR1", 0x00);
            //addReg(REG_AACIIE1, "AACIIE1", 0x00);
            //addReg(REG_AACIRXCR2, "AACIRXCR2", 0x00);
            //addReg(REG_AACITXCR2, "AACITXCR2", 0x00000000);
            //addReg(REG_AACISR2, "AACISR2", 0x00000);
            //addReg(REG_AACIISR2, "AACIISR2", 0x00b);
            //addReg(REG_AACIIE2, "AACIIE2", 0x00);
            //addReg(REG_AACIRXCR3, "AACIRXCR3", 0x00);
            //addReg(REG_AACITXCR3, "AACITXCR3", 0x00000000);
            //addReg(REG_AACISR3, "AACISR3", 0x00000);
            //addReg(REG_AACIISR3, "AACIISR3", 0x00b);
            //addReg(REG_AACIIE3, "AACIIE3", 0x00);
            //addReg(REG_AACIRXCR4, "AACIRXCR4", 0x00000000);
            //addReg(REG_AACITXCR4, "AACITXCR4", 0x00000);
            //addReg(REG_AACISR4, "AACISR4", 0x00b);
            //addReg(REG_AACIISR4, "AACIISR4", 0x00);
            //addReg(REG_AACIIE4, "AACIIE4", 0x00);
            //addReg(REG_AACISL1RX, "AACISL1RX", 0x00000);
            //addReg(REG_AACISL1TX, "AACISL1TX", 0x00000);
            //addReg(REG_AACISL2RX, "AACISL2RX", 0x00000);
            //addReg(REG_AACISL2TX, "AACISL2TX", 0x00000);
            //addReg(REG_AACISL12RX, "AACISL12RX", 0x00000);
            //addReg(REG_AACISL12TX, "AACISL12TX", 0x00000);
            //addReg(REG_AACISLFR, "AACISLFR", 0x0a80);
            //addReg(REG_AACISLISTAT, "AACISLISTAT", 0x00);
            //addReg(REG_AACISLIEN, "AACISLIEN", 0x00);
            //addReg(REG_AACIINTCLR, "AACIINTCLR", 0x00);
            //addReg(REG_AACIMAINCR, "AACIMAINCR", 0x00);
            //addReg(REG_AACIRESET, "AACIRESET", 0x00);
            //addReg(REG_AACISYNC, "AACISYNC", 0x00);
            //addReg(REG_AACIALLINTS, "AACIALLINTS", 0x00);
            //addReg(REG_AACIMAINFR, "AACIMAINFR", 0x00);

            //0x090-0x0ac AACIDR1
            //0x0b0-0x0cc AACIDR2
            //0x0d0-0x0ec AACIDR3
            //0x0f0-0xa0c AACIDR4

            addReg(REG_AACIPERIPHID0, "AACIPERIPHID0", 0x41);
            addReg(REG_AACIPERIPHID1, "AACIPERIPHID1", 0x10);
            addReg(REG_AACIPERIPHID2, "AACIPERIPHID2", 0x04);
            addReg(REG_AACIPERIPHID3, "AACIPERIPHID3", 0x00);
            addReg(REG_AACIPCELLID0, "AACIPCELLID0", 0x0d);
            addReg(REG_AACIPCELLID1, "AACIPCELLID1", 0xf0);
            addReg(REG_AACIPCELLID2, "AACIPCELLID2", 0x05);
            addReg(REG_AACIPCELLID3, "AACIPCELLID3", 0xb1);
        }

        @Override
        public int readWord(long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
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
            case REG_AACIPERIPHID0:
            case REG_AACIPERIPHID1:
            case REG_AACIPERIPHID2:
            case REG_AACIPERIPHID3:
            case REG_AACIPCELLID0:
            case REG_AACIPCELLID1:
            case REG_AACIPCELLID2:
            case REG_AACIPCELLID3:
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