package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * マルチポートメモリコントローラ
 *
 * 参考: ARM PrimeCell MultiPort Memory Controller (GX175)
 * ARM DDI0277F
 *
 * @author katsuhiro
 */
public class MPMC implements BusSlave {
    private MPMCSlave slave;

    public static final int REG_MPMCControl            = 0x000;
    public static final int REG_MPMCStatus             = 0x004;
    public static final int REG_MPMCConfig             = 0x008;
    public static final int REG_MPMCDynamicControl     = 0x020;
    public static final int REG_MPMCDynamicRefresh     = 0x024;
    public static final int REG_MPMCDynamicReadConfig  = 0x028;
    public static final int REG_MPMCDynamictRP         = 0x030;
    public static final int REG_MPMCDynamictRAS        = 0x034;
    public static final int REG_MPMCDynamictSREX       = 0x038;
    public static final int REG_MPMCDynamictWR         = 0x044;
    public static final int REG_MPMCDynamictRC         = 0x048;
    public static final int REG_MPMCDynamictRFC        = 0x04c;
    public static final int REG_MPMCDynamictXSR        = 0x050;
    public static final int REG_MPMCDynamictRRD        = 0x054;
    public static final int REG_MPMCDynamictMRD        = 0x058;
    public static final int REG_MPMCDynamictCDLR       = 0x05c;
    public static final int REG_MPMCStaticExtendedWait = 0x080;
    public static final int REG_MPMCDynamicConfig0     = 0x100;
    public static final int REG_MPMCDynamicRasCas0     = 0x104;
    public static final int REG_MPMCDynamicConfig1     = 0x120;
    public static final int REG_MPMCDynamicRasCas1     = 0x124;
    public static final int REG_MPMCDynamicConfig2     = 0x140;
    public static final int REG_MPMCDynamicRasCas2     = 0x144;
    public static final int REG_MPMCDynamicConfig3     = 0x160;
    public static final int REG_MPMCDynamicRasCas3     = 0x164;
    public static final int REG_MPMCStaticConfig0      = 0x200;
    public static final int REG_MPMCStaticWaitWen0     = 0x204;
    public static final int REG_MPMCStaticWaitOen0     = 0x208;
    public static final int REG_MPMCStaticWaitRd0      = 0x20c;
    public static final int REG_MPMCStaticWaitPage0    = 0x210;
    public static final int REG_MPMCStaticWaitWr0      = 0x214;
    public static final int REG_MPMCStaticWaitTurn0    = 0x218;
    public static final int REG_MPMCStaticConfig1      = 0x220;
    public static final int REG_MPMCStaticWaitWen1     = 0x224;
    public static final int REG_MPMCStaticWaitOen1     = 0x228;
    public static final int REG_MPMCStaticWaitRd1      = 0x22c;
    public static final int REG_MPMCStaticWaitPage1    = 0x230;
    public static final int REG_MPMCStaticWaitWr1      = 0x234;
    public static final int REG_MPMCStaticWaitTurn1    = 0x238;
    public static final int REG_MPMCStaticConfig2      = 0x240;
    public static final int REG_MPMCStaticWaitWen2     = 0x244;
    public static final int REG_MPMCStaticWaitOen2     = 0x248;
    public static final int REG_MPMCStaticWaitRd2      = 0x24c;
    public static final int REG_MPMCStaticWaitPage2    = 0x250;
    public static final int REG_MPMCStaticWaitWr2      = 0x254;
    public static final int REG_MPMCStaticWaitTurn2    = 0x258;
    public static final int REG_MPMCStaticConfig3      = 0x260;
    public static final int REG_MPMCStaticWaitWen3     = 0x264;
    public static final int REG_MPMCStaticWaitOen3     = 0x268;
    public static final int REG_MPMCStaticWaitRd3      = 0x26c;
    public static final int REG_MPMCStaticWaitPage3    = 0x270;
    public static final int REG_MPMCStaticWaitWr3      = 0x274;
    public static final int REG_MPMCStaticWaitTurn3    = 0x278;
    public static final int REG_MPMCAHBControl0        = 0x400;
    public static final int REG_MPMCAHBStatus0         = 0x404;
    public static final int REG_MPMCAHBTimeOut0        = 0x408;
    public static final int REG_MPMCAHBControl1        = 0x420;
    public static final int REG_MPMCAHBStatus1         = 0x424;
    public static final int REG_MPMCAHBTimeOut1        = 0x428;
    public static final int REG_MPMCAHBControl2        = 0x440;
    public static final int REG_MPMCAHBStatus2         = 0x444;
    public static final int REG_MPMCAHBTimeOut2        = 0x448;
    public static final int REG_MPMCAHBControl3        = 0x460;
    public static final int REG_MPMCAHBStatus3         = 0x464;
    public static final int REG_MPMCAHBTimeOut3        = 0x468;
    public static final int REG_MPMCAHBControl4        = 0x480;
    public static final int REG_MPMCAHBStatus4         = 0x484;
    public static final int REG_MPMCAHBTimeOut4        = 0x488;
    public static final int REG_MPMCITCR               = 0xf00;
    public static final int REG_MPMCITIP0              = 0xf20;
    public static final int REG_MPMCITIP1              = 0xf24;
    public static final int REG_MPMCITOP               = 0xf40;
    public static final int REG_MPMCPeriphID4          = 0xfd0;
    public static final int REG_MPMCPeriphID5          = 0xfd4;
    public static final int REG_MPMCPeriphID6          = 0xfd8;
    public static final int REG_MPMCPeriphID7          = 0xfdc;
    public static final int REG_MPMCPeriphID0          = 0xfe0;
    public static final int REG_MPMCPeriphID1          = 0xfe4;
    public static final int REG_MPMCPeriphID2          = 0xfe8;
    public static final int REG_MPMCPeriphID3          = 0xfec;
    public static final int REG_MPMCPCellID0           = 0xff0;
    public static final int REG_MPMCPCellID1           = 0xff4;
    public static final int REG_MPMCPCellID2           = 0xff8;
    public static final int REG_MPMCPCellID3           = 0xffc;

    public MPMC() {
        slave = new MPMCSlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class MPMCSlave extends Controller32 {
        public MPMCSlave() {
            //addReg(REG_MPMCControl, "MPMCControl", 0x1);
            //addReg(REG_MPMCStatus, "MPMCStatus", 0x5);
            //addReg(REG_MPMCConfig, "MPMCConfig", 0x-a);
            //addReg(REG_MPMCDynamicControl, "MPMCDynamicControl", 0xe);
            //addReg(REG_MPMCDynamicRefresh, "MPMCDynamicRefresh", 0x0);
            //addReg(REG_MPMCDynamicReadConfig, "MPMCDynamicReadConfig", 0x----a);
            //addReg(REG_MPMCDynamictRP, "MPMCDynamictRP", 0xf);
            //addReg(REG_MPMCDynamictRAS, "MPMCDynamictRAS", 0xf);
            //addReg(REG_MPMCDynamictSREX, "MPMCDynamictSREX", 0x7f);
            //addReg(REG_MPMCDynamictWR, "MPMCDynamictWR", 0xf);
            //addReg(REG_MPMCDynamictRC, "MPMCDynamictRC", 0x1f);
            //addReg(REG_MPMCDynamictRFC, "MPMCDynamictRFC", 0x1f);
            //addReg(REG_MPMCDynamictXSR, "MPMCDynamictXSR", 0xff);
            //addReg(REG_MPMCDynamictRRD, "MPMCDynamictRRD", 0xf);
            //addReg(REG_MPMCDynamictMRD, "MPMCDynamictMRD", 0xf);
            //addReg(REG_MPMCDynamictCDLR, "MPMCDynamictCDLR", 0xf);
            //addReg(REG_MPMCStaticExtendedWait, "MPMCStaticExtendedWait", 0x0);
            //addReg(REG_MPMCDynamicConfig0, "MPMCDynamicConfig0", 0x0);
            //addReg(REG_MPMCDynamicRasCas0, "MPMCDynamicRasCas0", 0x783);
            //addReg(REG_MPMCDynamicConfig1, "MPMCDynamicConfig1", 0x0--a);
            //addReg(REG_MPMCDynamicRasCas1, "MPMCDynamicRasCas1", 0x--3a);
            //addReg(REG_MPMCDynamicConfig2, "MPMCDynamicConfig2", 0x0);
            //addReg(REG_MPMCDynamicRasCas2, "MPMCDynamicRasCas2", 0x783);
            //addReg(REG_MPMCDynamicConfig3, "MPMCDynamicConfig3", 0x0);
            //addReg(REG_MPMCDynamicRasCas3, "MPMCDynamicRasCas3", 0x783);
            //addReg(REG_MPMCStaticConfig0, "MPMCStaticConfig0", 0x-0a);
            //addReg(REG_MPMCStaticWaitWen0, "MPMCStaticWaitWen0", 0x0);
            //addReg(REG_MPMCStaticWaitOen0, "MPMCStaticWaitOen0", 0x0);
            //addReg(REG_MPMCStaticWaitRd0, "MPMCStaticWaitRd0", 0x1f);
            //addReg(REG_MPMCStaticWaitPage0, "MPMCStaticWaitPage0", 0x1f);
            //addReg(REG_MPMCStaticWaitWr0, "MPMCStaticWaitWr0", 0x1f);
            //addReg(REG_MPMCStaticWaitTurn0, "MPMCStaticWaitTurn0", 0xf);
            //addReg(REG_MPMCStaticConfig1, "MPMCStaticConfig1", 0x--a);
            //addReg(REG_MPMCStaticWaitWen1, "MPMCStaticWaitWen1", 0x0);
            //addReg(REG_MPMCStaticWaitOen1, "MPMCStaticWaitOen1", 0x0);
            //addReg(REG_MPMCStaticWaitRd1, "MPMCStaticWaitRd1", 0x1f);
            //addReg(REG_MPMCStaticWaitPage1, "MPMCStaticWaitPage1", 0x1f);
            //addReg(REG_MPMCStaticWaitWr1, "MPMCStaticWaitWr1", 0x1f);
            //addReg(REG_MPMCStaticWaitTurn1, "MPMCStaticWaitTurn1", 0xf);
            //addReg(REG_MPMCStaticConfig2, "MPMCStaticConfig2", 0x-0a);
            //addReg(REG_MPMCStaticWaitWen2, "MPMCStaticWaitWen2", 0x0);
            //addReg(REG_MPMCStaticWaitOen2, "MPMCStaticWaitOen2", 0x0);
            //addReg(REG_MPMCStaticWaitRd2, "MPMCStaticWaitRd2", 0x1f);
            //addReg(REG_MPMCStaticWaitPage2, "MPMCStaticWaitPage2", 0x1f);
            //addReg(REG_MPMCStaticWaitWr2, "MPMCStaticWaitWr2", 0x1f);
            //addReg(REG_MPMCStaticWaitTurn2, "MPMCStaticWaitTurn2", 0xf);
            //addReg(REG_MPMCStaticConfig3, "MPMCStaticConfig3", 0x-0a);
            //addReg(REG_MPMCStaticWaitWen3, "MPMCStaticWaitWen3", 0x0);
            //addReg(REG_MPMCStaticWaitOen3, "MPMCStaticWaitOen3", 0x0);
            //addReg(REG_MPMCStaticWaitRd3, "MPMCStaticWaitRd3", 0x1f);
            //addReg(REG_MPMCStaticWaitPage3, "MPMCStaticWaitPage3", 0x1f);
            //addReg(REG_MPMCStaticWaitWr3, "MPMCStaticWaitWr3", 0x1f);
            //addReg(REG_MPMCStaticWaitTurn3, "MPMCStaticWaitTurn3", 0xf);
            //addReg(REG_MPMCAHBControl0, "MPMCAHBControl0", 0x0);
            //addReg(REG_MPMCAHBStatus0, "MPMCAHBStatus0", 0x0);
            //addReg(REG_MPMCAHBTimeOut0, "MPMCAHBTimeOut0", 0x0);
            //addReg(REG_MPMCAHBControl1, "MPMCAHBControl1", 0x0);
            //addReg(REG_MPMCAHBStatus1, "MPMCAHBStatus1", 0x0);
            //addReg(REG_MPMCAHBTimeOut1, "MPMCAHBTimeOut1", 0x0);
            //addReg(REG_MPMCAHBControl2, "MPMCAHBControl2", 0x0);
            //addReg(REG_MPMCAHBStatus2, "MPMCAHBStatus2", 0x0);
            //addReg(REG_MPMCAHBTimeOut2, "MPMCAHBTimeOut2", 0x0);
            //addReg(REG_MPMCAHBControl3, "MPMCAHBControl3", 0x0);
            //addReg(REG_MPMCAHBStatus3, "MPMCAHBStatus3", 0x0);
            //addReg(REG_MPMCAHBTimeOut3, "MPMCAHBTimeOut3", 0x0);
            //addReg(REG_MPMCAHBControl4, "MPMCAHBControl4", 0x0);
            //addReg(REG_MPMCAHBStatus4, "MPMCAHBStatus4", 0x0);
            //addReg(REG_MPMCAHBTimeOut4, "MPMCAHBTimeOut4", 0x0);
            //addReg(REG_MPMCITCR, "MPMCITCR", 0x-a);
            //addReg(REG_MPMCITIP0, "MPMCITIP0", 0x----a);
            //addReg(REG_MPMCITIP1, "MPMCITIP1", 0x--a);
            //addReg(REG_MPMCITOP, "MPMCITOP", 0x1b);

            addReg(REG_MPMCPeriphID4, "MPMCPeriphID4", 0x05);
            addReg(REG_MPMCPeriphID5, "MPMCPeriphID5", 0x00);
            addReg(REG_MPMCPeriphID6, "MPMCPeriphID6", 0x00);
            addReg(REG_MPMCPeriphID7, "MPMCPeriphID7", 0x00);
            addReg(REG_MPMCPeriphID0, "MPMCPeriphID0", 0x75);
            addReg(REG_MPMCPeriphID1, "MPMCPeriphID1", 0x11);
            addReg(REG_MPMCPeriphID2, "MPMCPeriphID2", 0x04);
            addReg(REG_MPMCPeriphID3, "MPMCPeriphID3", 0x47);
            addReg(REG_MPMCPCellID0, "MPMCPCellID0", 0x0d);
            addReg(REG_MPMCPCellID1, "MPMCPCellID1", 0xf0);
            addReg(REG_MPMCPCellID2, "MPMCPCellID2", 0x05);
            addReg(REG_MPMCPCellID3, "MPMCPCellID3", 0xb1);
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
            case REG_MPMCPeriphID4:
            case REG_MPMCPeriphID5:
            case REG_MPMCPeriphID6:
            case REG_MPMCPeriphID7:
            case REG_MPMCPeriphID0:
            case REG_MPMCPeriphID1:
            case REG_MPMCPeriphID2:
            case REG_MPMCPeriphID3:
            case REG_MPMCPCellID0:
            case REG_MPMCPCellID1:
            case REG_MPMCPCellID2:
            case REG_MPMCPCellID3:
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
