package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * DMA コントローラ
 *
 * 参考: ARM PrimeCell DMA Controller (PL080)
 * ARM DDI0196G
 *
 * @author katsuhiro
 */
public class DMAC implements BusSlave {
    private DMACSlave slave;

    public static final int REG_DMACIntStatus           = 0x000;
    public static final int REG_DMACIntTCStatus         = 0x004;
    public static final int REG_DMACIntTCClear          = 0x008;
    public static final int REG_DMACIntErrorStatus      = 0x00c;
    public static final int REG_DMACIntErrClr           = 0x010;
    public static final int REG_DMACRawIntTCStatus      = 0x014;
    public static final int REG_DMACRawIntErrorStatus   = 0x018;
    public static final int REG_DMACEnbldChns           = 0x01c;
    public static final int REG_DMACSoftBReq            = 0x020;
    public static final int REG_DMACSoftSReq            = 0x024;
    public static final int REG_DMACSoftLBReq           = 0x028;
    public static final int REG_DMACSoftLSReq           = 0x02c;
    public static final int REG_DMACConfiguration       = 0x030;
    public static final int REG_DMACSync                = 0x034;
    public static final int REG_DMACC0SrcAddr           = 0x100;
    public static final int REG_DMACC0DestAddr          = 0x104;
    public static final int REG_DMACC0LLI               = 0x108;
    public static final int REG_DMACC0Control           = 0x10c;
    public static final int REG_DMACC0Configuration     = 0x110;
    public static final int REG_DMACC1SrcAddr           = 0x120;
    public static final int REG_DMACC1DestAddr          = 0x124;
    public static final int REG_DMACC1LLI               = 0x128;
    public static final int REG_DMACC1Control           = 0x12c;
    public static final int REG_DMACC1Configuration     = 0x130;
    public static final int REG_DMACC2SrcAddr           = 0x140;
    public static final int REG_DMACC2DestAddr          = 0x144;
    public static final int REG_DMACC2LLI               = 0x148;
    public static final int REG_DMACC2Control           = 0x14c;
    public static final int REG_DMACC2Configuration     = 0x150;
    public static final int REG_DMACC3SrcAddr           = 0x160;
    public static final int REG_DMACC3DestAddr          = 0x164;
    public static final int REG_DMACC3LLI               = 0x168;
    public static final int REG_DMACC3Control           = 0x16c;
    public static final int REG_DMACC3Configuration     = 0x170;
    public static final int REG_DMACC4SrcAddr           = 0x180;
    public static final int REG_DMACC4DestAddr          = 0x184;
    public static final int REG_DMACC4LLI               = 0x188;
    public static final int REG_DMACC4Control           = 0x18c;
    public static final int REG_DMACC4Configuration     = 0x190;
    public static final int REG_DMACC5SrcAddr           = 0x1a0;
    public static final int REG_DMACC5DestAddr          = 0x1a4;
    public static final int REG_DMACC5LLI               = 0x1a8;
    public static final int REG_DMACC5Control           = 0x1ac;
    public static final int REG_DMACC5Configuration     = 0x1b0;
    public static final int REG_DMACC6SrcAddr           = 0x1c0;
    public static final int REG_DMACC6DestAddr          = 0x1c4;
    public static final int REG_DMACC6LLI               = 0x1c8;
    public static final int REG_DMACC6Control           = 0x1cc;
    public static final int REG_DMACC6Configuration     = 0x1d0;
    public static final int REG_DMACC7SrcAddr           = 0x1e0;
    public static final int REG_DMACC7DestAddr          = 0x1e4;
    public static final int REG_DMACC7LLI               = 0x1e8;
    public static final int REG_DMACC7Control           = 0x1ec;
    public static final int REG_DMACC7Configuration     = 0x1f0;
    public static final int REG_DMACITCR                = 0x500;
    public static final int REG_DMACITOP1               = 0x504;
    public static final int REG_DMACITOP2               = 0x508;
    public static final int REG_DMACITOP3               = 0x50c;
    public static final int REG_DMACPeriphID0           = 0xfe0;
    public static final int REG_DMACPeriphID1           = 0xfe4;
    public static final int REG_DMACPeriphID2           = 0xfe8;
    public static final int REG_DMACPeriphID3           = 0xfec;
    public static final int REG_DMACPCellID0            = 0xff0;
    public static final int REG_DMACPCellID1            = 0xff4;
    public static final int REG_DMACPCellID2            = 0xff8;
    public static final int REG_DMACPCellID3            = 0xffc;

    public DMAC() {
        slave = new DMACSlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class DMACSlave extends Controller32 {
        public DMACSlave() {
            //addReg(REG_DMACIntStatus, "DMACIntStatus", 0x00);
            //addReg(REG_DMACIntTCStatus, "DMACIntTCStatus", 0x00);
            //addReg(REG_DMACIntTCClear, "DMACIntTCClear", 0x0);
            //addReg(REG_DMACIntErrorStatus, "DMACIntErrorStatus", 0x00);
            //addReg(REG_DMACIntErrClr, "DMACIntErrClr", 0x0);
            //addReg(REG_DMACRawIntTCStatus, "DMACRawIntTCStatus", 0x0);
            //addReg(REG_DMACRawIntErrorStatus, "DMACRawIntErrorStatus", 0x0);
            //addReg(REG_DMACEnbldChns, "DMACEnbldChns", 0x00);
            //addReg(REG_DMACSoftBReq, "DMACSoftBReq", 0x0000);
            //addReg(REG_DMACSoftSReq, "DMACSoftSReq", 0x0000);
            //addReg(REG_DMACSoftLBReq, "DMACSoftLBReq", 0x0000);
            //addReg(REG_DMACSoftLSReq, "DMACSoftLSReq", 0x0000);
            //addReg(REG_DMACConfiguration, "DMACConfiguration", 0x00);
            //addReg(REG_DMACSync, "DMACSync", 0x0000);
            //addReg(REG_DMACC0SrcAddr, "DMACC0SrcAddr", 0x00000000);
            //addReg(REG_DMACC0DestAddr, "DMACC0DestAddr", 0x00000000);
            //addReg(REG_DMACC0LLI, "DMACC0LLI", 0x00000000);
            //addReg(REG_DMACC0Control, "DMACC0Control", 0x00000000);
            //addReg(REG_DMACC0Configuration, "DMACC0Configuration", 0x00000);
            //addReg(REG_DMACC1SrcAddr, "DMACC1SrcAddr", 0x00000000);
            //addReg(REG_DMACC1DestAddr, "DMACC1DestAddr", 0x00000000);
            //addReg(REG_DMACC1LLI, "DMACC1LLI", 0x00000000);
            //addReg(REG_DMACC1Control, "DMACC1Control", 0x00000000);
            //addReg(REG_DMACC1Configuration, "DMACC1Configuration", 0x00000);
            //addReg(REG_DMACC2SrcAddr, "DMACC2SrcAddr", 0x00000000);
            //addReg(REG_DMACC2DestAddr, "DMACC2DestAddr", 0x00000000);
            //addReg(REG_DMACC2LLI, "DMACC2LLI", 0x00000000);
            //addReg(REG_DMACC2Control, "DMACC2Control", 0x00000000);
            //addReg(REG_DMACC2Configuration, "DMACC2Configuration", 0x00000);
            //addReg(REG_DMACC3SrcAddr, "DMACC3SrcAddr", 0x00000000);
            //addReg(REG_DMACC3DestAddr, "DMACC3DestAddr", 0x00000000);
            //addReg(REG_DMACC3LLI, "DMACC3LLI", 0x00000000);
            //addReg(REG_DMACC3Control, "DMACC3Control", 0x00000000);
            //addReg(REG_DMACC3Configuration, "DMACC3Configuration", 0x00000);
            //addReg(REG_DMACC4SrcAddr, "DMACC4SrcAddr", 0x00000000);
            //addReg(REG_DMACC4DestAddr, "DMACC4DestAddr", 0x00000000);
            //addReg(REG_DMACC4LLI, "DMACC4LLI", 0x00000000);
            //addReg(REG_DMACC4Control, "DMACC4Control", 0x00000000);
            //addReg(REG_DMACC4Configuration, "DMACC4Configuration", 0x00000);
            //addReg(REG_DMACC5SrcAddr, "DMACC5SrcAddr", 0x00000000);
            //addReg(REG_DMACC5DestAddr, "DMACC5DestAddr", 0x00000000);
            //addReg(REG_DMACC5LLI, "DMACC5LLI", 0x00000000);
            //addReg(REG_DMACC5Control, "DMACC5Control", 0x00000000);
            //addReg(REG_DMACC5Configuration, "DMACC5Configuration", 0x00000);
            //addReg(REG_DMACC6SrcAddr, "DMACC6SrcAddr", 0x00000000);
            //addReg(REG_DMACC6DestAddr, "DMACC6DestAddr", 0x00000000);
            //addReg(REG_DMACC6LLI, "DMACC6LLI", 0x00000000);
            //addReg(REG_DMACC6Control, "DMACC6Control", 0x00000000);
            //addReg(REG_DMACC6Configuration, "DMACC6Configuration", 0x00000);
            //addReg(REG_DMACC7SrcAddr, "DMACC7SrcAddr", 0x00000000);
            //addReg(REG_DMACC7DestAddr, "DMACC7DestAddr", 0x00000000);
            //addReg(REG_DMACC7LLI, "DMACC7LLI", 0x00000000);
            //addReg(REG_DMACC7Control, "DMACC7Control", 0x00000000);
            //addReg(REG_DMACC7Configuration, "DMACC7Configuration", 0x00000);
            //addReg(REG_DMACITCR, "DMACITCR", 0x0);
            //addReg(REG_DMACITOP1, "DMACITOP1", 0x0000);
            //addReg(REG_DMACITOP2, "DMACITOP2", 0x0000);
            //addReg(REG_DMACITOP3, "DMACITOP3", 0x0);

            addReg(REG_DMACPeriphID0, "DMACPeriphID0", 0x00000080);
            addReg(REG_DMACPeriphID1, "DMACPeriphID1", 0x00000010);
            addReg(REG_DMACPeriphID2, "DMACPeriphID2", 0x00000004);
            addReg(REG_DMACPeriphID3, "DMACPeriphID3", 0x0000000a);
            addReg(REG_DMACPCellID0, "DMACPCellID0", 0x0000000d);
            addReg(REG_DMACPCellID1, "DMACPCellID1", 0x000000f0);
            addReg(REG_DMACPCellID2, "DMACPCellID2", 0x00000005);
            addReg(REG_DMACPCellID3, "DMACPCellID3", 0x000000b1);
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
            case REG_DMACPeriphID0:
            case REG_DMACPeriphID1:
            case REG_DMACPeriphID2:
            case REG_DMACPeriphID3:
            case REG_DMACPCellID0:
            case REG_DMACPCellID1:
            case REG_DMACPCellID2:
            case REG_DMACPCellID3:
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