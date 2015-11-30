package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * スマートカードインタフェース
 *
 * 参考: ARM PrimeCell Smart Card Interface (PL131)
 * ARM DDI0228A
 *
 * @author katsuhiro
 */
public class SCard implements BusSlave {
    private SCardSlave slave;

    public static final int REG_SCIDATA        = 0x000;
    public static final int REG_SCICR0         = 0x004;
    public static final int REG_SCICR1         = 0x008;
    public static final int REG_SCICR2         = 0x00c;
    public static final int REG_SCICLKICC      = 0x010;
    public static final int REG_SCIVALUE       = 0x014;
    public static final int REG_SCIBAUDE       = 0x018;
    public static final int REG_SCITIDE        = 0x01c;
    public static final int REG_SCIDMACR       = 0x020;
    public static final int REG_SCISTABLE      = 0x024;
    public static final int REG_SCIATIME       = 0x028;
    public static final int REG_SCIDTIME       = 0x02c;
    public static final int REG_SCIATRSTIME    = 0x030;
    public static final int REG_SCIATRDTIME    = 0x034;
    public static final int REG_SCISTOPTIME    = 0x038;
    public static final int REG_SCISTARTTIME   = 0x03c;
    public static final int REG_SCIRETRY       = 0x040;
    public static final int REG_SCICHTIMELS    = 0x044;
    public static final int REG_SCICHTIMEMS    = 0x048;
    public static final int REG_SCIBLKTIMELS   = 0x04c;
    public static final int REG_SCIBLKTIMEMS   = 0x050;
    public static final int REG_SCICHGUARD     = 0x054;
    public static final int REG_SCIBLKGUARD    = 0x058;
    public static final int REG_SCIRXTIME      = 0x05c;
    public static final int REG_SCIFIFOSTATUS  = 0x060;
    public static final int REG_SCITXCOUNT     = 0x064;
    public static final int REG_SCIRXCOUNT     = 0x068;
    public static final int REG_SCIIMSC        = 0x06c;
    public static final int REG_SCIRIS         = 0x070;
    public static final int REG_SCIMIS         = 0x074;
    public static final int REG_SCIICR         = 0x078;
    public static final int REG_SCISYNCACT     = 0x07c;
    public static final int REG_SCISYNCTX      = 0x080;
    public static final int REG_SCISYNCRX      = 0x084;
    public static final int REG_SCIPeriphID0   = 0xfe0;
    public static final int REG_SCIPeriphID1   = 0xfe4;
    public static final int REG_SCIPeriphID2   = 0xfe8;
    public static final int REG_SCIPeriphID3   = 0xfec;
    public static final int REG_SCIPCellID0    = 0xff0;
    public static final int REG_SCIPCellID1    = 0xff4;
    public static final int REG_SCIPCellID2    = 0xff8;
    public static final int REG_SCIPCellID3    = 0xffc;

    public SCard() {
        slave = new SCardSlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class SCardSlave extends Controller32 {
        public SCardSlave() {
            //addReg(REG_SCIDATA, "SCIDATA", 0x0);
            //addReg(REG_SCICR0, "SCICR0", 0x0);
            //addReg(REG_SCICR1, "SCICR1", 0x0);
            //addReg(REG_SCICR2, "SCICR2", 0x0);
            //addReg(REG_SCICLKICC, "SCICLKICC", 0x0);
            //addReg(REG_SCIVALUE, "SCIVALUE", 0x0);
            //addReg(REG_SCIBAUDE, "SCIBAUDE", 0x0);
            //addReg(REG_SCITIDE, "SCITIDE", 0x0);
            //addReg(REG_SCIDMACR, "SCIDMACR", 0x0);
            //addReg(REG_SCISTABLE, "SCISTABLE", 0x0);
            //addReg(REG_SCIATIME, "SCIATIME", 0x0);
            //addReg(REG_SCIDTIME, "SCIDTIME", 0x0);
            //addReg(REG_SCIATRSTIME, "SCIATRSTIME", 0x0);
            //addReg(REG_SCIATRDTIME, "SCIATRDTIME", 0x0);
            //addReg(REG_SCISTOPTIME, "SCISTOPTIME", 0x0);
            //addReg(REG_SCISTARTTIME, "SCISTARTTIME", 0x0);
            //addReg(REG_SCIRETRY, "SCIRETRY", 0x0);
            //addReg(REG_SCICHTIMELS, "SCICHTIMELS", 0x0);
            //addReg(REG_SCICHTIMEMS, "SCICHTIMEMS", 0x0);
            //addReg(REG_SCIBLKTIMELS, "SCIBLKTIMELS", 0x0);
            //addReg(REG_SCIBLKTIMEMS, "SCIBLKTIMEMS", 0x0);
            //addReg(REG_SCICHGUARD, "SCICHGUARD", 0x0);
            //addReg(REG_SCIBLKGUARD, "SCIBLKGUARD", 0x0);
            //addReg(REG_SCIRXTIME, "SCIRXTIME", 0x0);
            //addReg(REG_SCIFIFOSTATUS, "SCIFIFOSTATUS", 0xa);
            //addReg(REG_SCITXCOUNT, "SCITXCOUNT", 0x0);
            //addReg(REG_SCIRXCOUNT, "SCIRXCOUNT", 0x0);
            //addReg(REG_SCIIMSC, "SCIIMSC", 0x0);
            //addReg(REG_SCIRIS, "SCIRIS", 0x400a);
            //addReg(REG_SCIMIS, "SCIMIS", 0x0);
            //addReg(REG_SCIICR, "SCIICR", 0x0);
            //addReg(REG_SCISYNCACT, "SCISYNCACT", 0x0);
            //addReg(REG_SCISYNCTX, "SCISYNCTX", 0x0);
            //addReg(REG_SCISYNCRX, "SCISYNCRX", 0x0);

            addReg(REG_SCIPeriphID0, "SCIPeriphID0", 0x31);
            addReg(REG_SCIPeriphID1, "SCIPeriphID1", 0x11);
            addReg(REG_SCIPeriphID2, "SCIPeriphID2", 0x04);
            addReg(REG_SCIPeriphID3, "SCIPeriphID3", 0x00);
            addReg(REG_SCIPCellID0, "SCIPCellID0", 0x0d);
            addReg(REG_SCIPCellID1, "SCIPCellID1", 0xf0);
            addReg(REG_SCIPCellID2, "SCIPCellID2", 0x05);
            addReg(REG_SCIPCellID3, "SCIPCellID3", 0xb1);
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
            case REG_SCIPeriphID0:
            case REG_SCIPeriphID1:
            case REG_SCIPeriphID2:
            case REG_SCIPeriphID3:
            case REG_SCIPCellID0:
            case REG_SCIPCellID1:
            case REG_SCIPCellID2:
            case REG_SCIPCellID3:
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
