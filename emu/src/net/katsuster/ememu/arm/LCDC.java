package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * カラー LCD コントローラ
 *
 * 参考: ARM PrimeCell Color LCD Controller (PL110)
 * ARM DDI0161DJ
 *
 * ARM DDI0161E はレジスタアドレスの仕様が変わっている。
 * Linux のドライバは DDI0161E に対応していない。
 *
 * @author katsuhiro
 */
public class LCDC implements BusSlave {
    private LCDCSlave slave;

    public static final int REG_LCDTiming0       = 0x000;
    public static final int REG_LCDTiming1       = 0x004;
    public static final int REG_LCDTiming2       = 0x008;
    public static final int REG_LCDTiming3       = 0x00c;
    public static final int REG_LCDUPBASE        = 0x010;
    public static final int REG_LCDLPBASE        = 0x014;
    public static final int REG_LCDINTRENABLE    = 0x018;
    public static final int REG_LCDControl       = 0x01c;
    public static final int REG_LCDStatus        = 0x020;
    public static final int REG_LCDInterrupt     = 0x024;
    public static final int REG_LCDUPCURR        = 0x028;
    public static final int REG_LCDLPCURR        = 0x02c;

    //0x200-0x3FC: LCDPalette

    public static final int REG_CLCDPERIPHID0    = 0xfe0;
    public static final int REG_CLCDPERIPHID1    = 0xfe4;
    public static final int REG_CLCDPERIPHID2    = 0xfe8;
    public static final int REG_CLCDPERIPHID3    = 0xfec;
    public static final int REG_CLCDPCELLID0     = 0xff0;
    public static final int REG_CLCDPCELLID1     = 0xff4;
    public static final int REG_CLCDPCELLID2     = 0xff8;
    public static final int REG_CLCDPCELLID3     = 0xffc;

    public LCDC() {
        slave = new LCDCSlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class LCDCSlave extends Controller32 {
        public LCDCSlave() {
            addReg(REG_LCDTiming0, "LCDTiming0", 0x00000000);
            addReg(REG_LCDTiming1, "LCDTiming1", 0x00000000);
            addReg(REG_LCDTiming2, "LCDTiming2", 0x00000000);
            addReg(REG_LCDTiming3, "LCDTiming3", 0x00000);
            addReg(REG_LCDUPBASE, "LCDUPBASE", 0x0000000);
            addReg(REG_LCDLPBASE, "LCDLPBASE", 0x00000000);
            addReg(REG_LCDINTRENABLE, "LCDINTRENABLE", 0x00);
            addReg(REG_LCDControl, "LCDControl", 0x0000);
            addReg(REG_LCDStatus, "LCDStatus", 0x00);
            addReg(REG_LCDInterrupt, "LCDInterrupt", 0x00);
            addReg(REG_LCDUPCURR, "LCDUPCURR", 0x00000000);
            addReg(REG_LCDLPCURR, "LCDLPCURR", 0x00000000);

            //0x200-0x3FC: LCDPalette

            addReg(REG_CLCDPERIPHID0, "CLCDPERIPHID0", 0x10);
            addReg(REG_CLCDPERIPHID1, "CLCDPERIPHID1", 0x11);
            addReg(REG_CLCDPERIPHID2, "CLCDPERIPHID2", 0x04);
            addReg(REG_CLCDPERIPHID3, "CLCDPERIPHID3", 0x00);
            addReg(REG_CLCDPCELLID0, "CLCDPCELLID0", 0x0d);
            addReg(REG_CLCDPCELLID1, "CLCDPCELLID1", 0xf0);
            addReg(REG_CLCDPCELLID2, "CLCDPCELLID2", 0x05);
            addReg(REG_CLCDPCELLID3, "CLCDPCELLID3", 0xb1);
        }

        @Override
        public int readWord(long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_LCDTiming0:
                //TODO: not implemented
                System.out.printf("LCDTiming0: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_LCDTiming1:
                //TODO: not implemented
                System.out.printf("LCDTiming1: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_LCDTiming2:
                //TODO: not implemented
                System.out.printf("LCDTiming2: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_LCDTiming3:
                //TODO: not implemented
                System.out.printf("LCDTiming3: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_LCDUPBASE:
                //TODO: not implemented
                System.out.printf("LCDUPBASE: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_LCDLPBASE:
                //TODO: not implemented
                System.out.printf("LCDLPBASE: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_LCDINTRENABLE:
                //TODO: not implemented
                System.out.printf("LCDIMSC: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_LCDControl:
                //TODO: not implemented
                System.out.printf("LCDControl: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_LCDStatus:
                //TODO: not implemented
                System.out.printf("LCDStatus: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_LCDInterrupt:
                //TODO: not implemented
                System.out.printf("LCDInterrupt: read 0x%08x\n", 0);
                result = 0;
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
            case REG_LCDTiming0:
                //TODO: not implemented
                System.out.printf("LCDTiming0: 0x%08x\n", data);
                break;
            case REG_LCDTiming1:
                //TODO: not implemented
                System.out.printf("LCDTiming1: 0x%08x\n", data);
                break;
            case REG_LCDTiming2:
                //TODO: not implemented
                System.out.printf("LCDTiming2: 0x%08x\n", data);
                break;
            case REG_LCDTiming3:
                //TODO: not implemented
                System.out.printf("LCDTiming3: 0x%08x\n", data);
                break;
            case REG_LCDUPBASE:
                //TODO: not implemented
                System.out.printf("LCDUPBASE: 0x%08x\n", data);
                break;
            case REG_LCDLPBASE:
                //TODO: not implemented
                System.out.printf("LCDLPBASE: 0x%08x\n", data);
                break;
            case REG_LCDINTRENABLE:
                //TODO: not implemented
                System.out.printf("LCDIMSC: 0x%08x\n", data);
                break;
            case REG_LCDControl:
                //TODO: not implemented
                System.out.printf("LCDControl: 0x%08x\n", data);
                break;
            case REG_LCDStatus:
                //TODO: not implemented
                System.out.printf("LCDStatus: 0x%08x\n", data);
                break;
            case REG_LCDInterrupt:
                //TODO: not implemented
                System.out.printf("LCDInterrupt: 0x%08x\n", data);
                break;
            case REG_CLCDPERIPHID0:
            case REG_CLCDPERIPHID1:
            case REG_CLCDPERIPHID2:
            case REG_CLCDPERIPHID3:
            case REG_CLCDPCELLID0:
            case REG_CLCDPCELLID1:
            case REG_CLCDPCELLID2:
            case REG_CLCDPCELLID3:
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
