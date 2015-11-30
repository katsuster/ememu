package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * システムコントローラ
 *
 * 参考: PrimeXsys System Controller (SP810)
 * ARM DDI0254B
 *
 * @author katsuhiro
 */
public class SysController implements BusSlave {
    private SysControllerSlave slave;

    public static final int REG_SCCTRL      = 0x000;
    public static final int REG_SCSYSSTAT   = 0x004;
    public static final int REG_SCIMCTRL    = 0x008;
    public static final int REG_SCIMSTAT    = 0x00c;
    public static final int REG_SCXTALCTRL  = 0x010;
    public static final int REG_SCPLLCTRL   = 0x014;
    public static final int REG_SCPLLFCTRL  = 0x018;
    public static final int REG_SCPERCTRL0  = 0x01c;
    public static final int REG_SCPERCTRL1  = 0x020;
    public static final int REG_SCPEREN     = 0x024;
    public static final int REG_SCPERDIS    = 0x028;
    public static final int REG_SCPERCLKEN  = 0x02c;
    public static final int REG_SCPERSTAT   = 0x030;
    public static final int REG_SCSysID0    = 0xee0;
    public static final int REG_SCSysID1    = 0xee4;
    public static final int REG_SCSysID2    = 0xee8;
    public static final int REG_SCSysID3    = 0xeec;
    public static final int REG_SCITCR      = 0xf00;
    public static final int REG_SCITIR0     = 0xf04;
    public static final int REG_SCITIR1     = 0xf08;
    public static final int REG_SCITOR      = 0xf0c;
    public static final int REG_SCCNTCTRL   = 0xf10;
    public static final int REG_SCCNTDATA   = 0xf14;
    public static final int REG_SCCNTSTEP   = 0xf18;
    public static final int REG_SCPeriphID0 = 0xfe0;
    public static final int REG_SCPeriphID1 = 0xfe4;
    public static final int REG_SCPeriphID2 = 0xfe8;
    public static final int REG_SCPeriphID3 = 0xfec;
    public static final int REG_SCPCellID0  = 0xff0;
    public static final int REG_SCPCellID1  = 0xff4;
    public static final int REG_SCPCellID2  = 0xff8;
    public static final int REG_SCPCellID3  = 0xffc;

    public SysController() {
        slave = new SysControllerSlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class SysControllerSlave extends Controller32 {
        public SysControllerSlave() {
            addReg(REG_SCCTRL, "SCCTRL", 0x00000009);
            //addReg(REG_SCSYSSTAT, "SCSYSSTAT", -);
            //addReg(REG_SCIMCTRL, "SCIMCTRL", 0x00);
            //addReg(REG_SCIMSTAT, "SCIMSTAT", 0x0);
            //addReg(REG_SCXTALCTRL, "SCXTALCTRL", 0x00000);
            //addReg(REG_SCPLLCTRL, "SCPLLCTRL", 0x0000000);
            //addReg(REG_SCPLLFCTRL, "SCPLLFCTRL", 0x00000000);
            //addReg(REG_SCPERCTRL0, "SCPERCTRL0", 0x00000000);
            //addReg(REG_SCPERCTRL1, "SCPERCTRL1", 0x00000000);
            //addReg(REG_SCPEREN, "SCPEREN", -);
            //addReg(REG_SCPERDIS, "SCPERDIS", -);
            //addReg(REG_SCPERCLKEN, "SCPERCLKEN", 0xffffffff);
            //addReg(REG_SCPERSTAT, "SCPERSTAT", -);

            //addReg(REG_SCSysID0, "SCSysID0", -);
            //addReg(REG_SCSysID1, "SCSysID1", -);
            //addReg(REG_SCSysID2, "SCSysID2", -);
            //addReg(REG_SCSysID3, "SCSysID3", -);

            //addReg(REG_SCITCR, "SCITCR", 0x0);
            //addReg(REG_SCITIR0, "SCITIR0", 0x0000);
            //addReg(REG_SCITIR1, "SCITIR1", 0x00000000);
            //addReg(REG_SCITOR, "SCITOR", 0x0000);
            //addReg(REG_SCCNTCTRL, "SCCNTCTRL", 0x0);
            //addReg(REG_SCCNTDATA, "SCCNTDATA", 0x00000000);
            //addReg(REG_SCCNTSTEP, "SCCNTSTEP", -);

            addReg(REG_SCPeriphID0, "SCPeriphID0", 0x10);
            addReg(REG_SCPeriphID1, "SCPeriphID1", 0x18);
            addReg(REG_SCPeriphID2, "SCPeriphID2", 0x04);
            addReg(REG_SCPeriphID3, "SCPeriphID3", 0x00);
            addReg(REG_SCPCellID0, "SCPCellID0", 0x0d);
            addReg(REG_SCPCellID1, "SCPCellID1", 0xf0);
            addReg(REG_SCPCellID2, "SCPCellID2", 0x05);
            addReg(REG_SCPCellID3, "SCPCellID3", 0xb1);
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
            case REG_SCCTRL:
                //do nothing
                break;
            case REG_SCPeriphID0:
            case REG_SCPeriphID1:
            case REG_SCPeriphID2:
            case REG_SCPeriphID3:
            case REG_SCPCellID0:
            case REG_SCPCellID1:
            case REG_SCPCellID2:
            case REG_SCPCellID3:
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
