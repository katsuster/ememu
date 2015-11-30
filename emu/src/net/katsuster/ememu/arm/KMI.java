package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * PS2 キーボード、マウスインタフェース
 *
 * 参考: ARM PrimeCell PS2 Keyboard/Mouse Interface (PL050)
 * ARM DDI0143C
 *
 * @author katsuhiro
 */
public class KMI implements BusSlave {
    private KMISlave slave;

    public static final int REG_KMICR        = 0x000;
    public static final int REG_KMISTAT      = 0x004;
    public static final int REG_KMIDATA      = 0x008;
    public static final int REG_KMICLKDIV    = 0x00c;
    public static final int REG_KMIIR        = 0x010;

    //0x040-0x07c: KMITCER

    public static final int REG_KMITCR       = 0x080;
    public static final int REG_KMITMR       = 0x084;
    public static final int REG_KMITISR      = 0x088;
    public static final int REG_KMITOCR      = 0x08c;
    public static final int REG_KMISTG1      = 0x090;
    public static final int REG_KMISTG2      = 0x094;
    public static final int REG_KMISTG3      = 0x098;
    public static final int REG_KMISTATE     = 0x09c;

    public static final int REG_KMIPeriphID0 = 0xfe0;
    public static final int REG_KMIPeriphID1 = 0xfe4;
    public static final int REG_KMIPeriphID2 = 0xfe8;
    public static final int REG_KMIPeriphID3 = 0xfec;
    public static final int REG_KMIPCellID0  = 0xff0;
    public static final int REG_KMIPCellID1  = 0xff4;
    public static final int REG_KMIPCellID2  = 0xff8;
    public static final int REG_KMIPCellID3  = 0xffc;

    public KMI() {
        slave = new KMISlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class KMISlave extends Controller32 {
        public KMISlave() {
            //addReg(REG_KMICR, "KMICR", 0x00);
            //addReg(REG_KMISTAT, "KMISTAT", 0x43);
            //addReg(REG_KMIDATA, "KMIDATA", 0x00);
            //addReg(REG_KMICLKDIV, "KMICLKDIV", 0x00);
            //addReg(REG_KMIIR, "KMIIR", 0x00);

            //0x40-0x7c: KMITCER

            //addReg(REG_KMITCR, "KMITCR", 0x00);
            //addReg(REG_KMITMR, "KMITMR", 0x00);
            //addReg(REG_KMITISR, "KMITISR", 0x00);
            //addReg(REG_KMITOCR, "KMITOCR", 0x03);
            //addReg(REG_KMISTG1, "KMISTG1", 0x00);
            //addReg(REG_KMISTG2, "KMISTG2", 0x00);
            //addReg(REG_KMISTG3, "KMISTG3", 0x00);
            //addReg(REG_KMISTATE, "KMISTATE", 0x06);

            //TODO: 仕様には存在しないが、定義しないと動かない
            addReg(REG_KMIPeriphID0, "KMIPeriphID0", 0x00);
            addReg(REG_KMIPeriphID1, "KMIPeriphID1", 0x00);
            addReg(REG_KMIPeriphID2, "KMIPeriphID2", 0x00);
            addReg(REG_KMIPeriphID3, "KMIPeriphID3", 0x00);
            addReg(REG_KMIPCellID0, "KMIPCellID0", 0x00);
            addReg(REG_KMIPCellID1, "KMIPCellID1", 0x00);
            addReg(REG_KMIPCellID2, "KMIPCellID2", 0x00);
            addReg(REG_KMIPCellID3, "KMIPCellID3", 0x00);
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
            case REG_KMIPeriphID0:
            case REG_KMIPeriphID1:
            case REG_KMIPeriphID2:
            case REG_KMIPeriphID3:
            case REG_KMIPCellID0:
            case REG_KMIPCellID1:
            case REG_KMIPCellID2:
            case REG_KMIPCellID3:
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
