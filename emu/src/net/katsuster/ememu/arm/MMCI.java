package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * マルチメディアカードインタフェース
 *
 * 参考: ARM PrimeCell Multimedia Card Interface (PL180)
 * ARM DDI0172A
 *
 * @author katsuhiro
 */
public class MMCI implements BusSlave {
    private MMCISlave slave;

    public static final int REG_MCIPower      = 0x000;
    public static final int REG_MCIClock      = 0x004;
    public static final int REG_MCIArgument   = 0x008;
    public static final int REG_MMCCommand    = 0x00c;
    public static final int REG_MCIRepCmd     = 0x010;
    public static final int REG_MCIResponse0  = 0x014;
    public static final int REG_MCIResponse1  = 0x018;
    public static final int REG_MCIResponse2  = 0x01c;
    public static final int REG_MCIResponse3  = 0x020;
    public static final int REG_MCIDataTimer  = 0x024;
    public static final int REG_MCIDataLength = 0x028;
    public static final int REG_MCIDataCtrl   = 0x02c;
    public static final int REG_MCIDataCnt    = 0x030;
    public static final int REG_MCIStatus     = 0x034;
    public static final int REG_MCIClear      = 0x038;
    public static final int REG_MCIMask0      = 0x03c;
    public static final int REG_MCIMask1      = 0x040;
    public static final int REG_MCISelect     = 0x044;
    public static final int REG_MCIFifoCnt    = 0x048;

//0x080-0x0bc: MCIFIFO

    public static final int REG_MCIPeriphID0  = 0xfe0;
    public static final int REG_MCIPeriphID1  = 0xfe4;
    public static final int REG_MCIPeriphID2  = 0xfe8;
    public static final int REG_MCIPeriphID3  = 0xfec;
    public static final int REG_MCIPCellID0   = 0xff0;
    public static final int REG_MCIPCellID1   = 0xff4;
    public static final int REG_MCIPCellID2   = 0xff8;
    public static final int REG_MCIPCellID3   = 0xffc;

    public MMCI() {
        slave = new MMCISlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class MMCISlave extends Controller32 {
        public MMCISlave() {
            //addReg(REG_MCIPower, "MCIPower", 0x00);
            //addReg(REG_MCIClock, "MCIClock", 0x000);
            //addReg(REG_MCIArgument, "MCIArgument", 0x00000000);
            //addReg(REG_MMCCommand, "MMCCommand", 0x000);
            //addReg(REG_MCIRepCmd, "MCIRepCmd", 0x00);
            //addReg(REG_MCIResponse0, "MCIResponse0", 0x00000000);
            //addReg(REG_MCIResponse1, "MCIResponse1", 0x00000000);
            //addReg(REG_MCIResponse2, "MCIResponse2", 0x00000000);
            //addReg(REG_MCIResponse3, "MCIResponse3", 0x00000000);
            //addReg(REG_MCIDataTimer, "MCIDataTimer", 0x00000000);
            //addReg(REG_MCIDataLength, "MCIDataLength", 0x0000);
            //addReg(REG_MCIDataCtrl, "MCIDataCtrl", 0x00);
            //addReg(REG_MCIDataCnt, "MCIDataCnt", 0x0000);
            //addReg(REG_MCIStatus, "MCIStatus", 0x000000);
            //addReg(REG_MCIClear, "MCIClear", 0x0);
            //addReg(REG_MCIMask0, "MCIMask0", 0x000000);
            //addReg(REG_MCIMask1, "MCIMask1", 0x000000);
            //addReg(REG_MCISelect, "MCISelect", 0x0);
            //addReg(REG_MCIFifoCnt, "MCIFifoCnt", 0x0000);

            //0x080-0x0bc: MCIFIFO

            addReg(REG_MCIPeriphID0, "MCIPeriphID0", 0x80);
            addReg(REG_MCIPeriphID1, "MCIPeriphID1", 0x11);
            addReg(REG_MCIPeriphID2, "MCIPeriphID2", 0x04);
            addReg(REG_MCIPeriphID3, "MCIPeriphID3", 0x00);
            addReg(REG_MCIPCellID0, "MCIPCellID0", 0x0d);
            addReg(REG_MCIPCellID1, "MCIPCellID1", 0xf0);
            addReg(REG_MCIPCellID2, "MCIPCellID2", 0x05);
            addReg(REG_MCIPCellID3, "MCIPCellID3", 0xb1);
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
            case REG_MCIPeriphID0:
            case REG_MCIPeriphID1:
            case REG_MCIPeriphID2:
            case REG_MCIPeriphID3:
            case REG_MCIPCellID0:
            case REG_MCIPCellID1:
            case REG_MCIPCellID2:
            case REG_MCIPCellID3:
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
