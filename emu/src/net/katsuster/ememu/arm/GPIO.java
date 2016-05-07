package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * GPIO
 *
 * 参考: ARM PrimeCell General Purpose Input/Output (PL061)
 * ARM DDI0190B
 *
 * @author katsuhiro
 */
public class GPIO implements BusSlave {
    private GPIOSlave slave;

    //0x000-0x3fc: REG_GPIODATA

    public static final int REG_GPIODIR       = 0x400;
    public static final int REG_GPIOIS        = 0x404;
    public static final int REG_GPIOIBE       = 0x408;
    public static final int REG_GPIOIEV       = 0x40c;
    public static final int REG_GPIOIE        = 0x410;
    public static final int REG_GPIORIS       = 0x414;
    public static final int REG_GPIOMIS       = 0x418;
    public static final int REG_GPIOIC        = 0x41c;
    public static final int REG_GPIOAFSEL     = 0x420;

    //0x424-0xfcc: Reserved for future use and test purposes
    //0xfd0-0xfdc: Reserved for future ID

    public static final int REG_GPIOPeriphID0 = 0xfe0;
    public static final int REG_GPIOPeriphID1 = 0xfe4;
    public static final int REG_GPIOPeriphID2 = 0xfe8;
    public static final int REG_GPIOPeriphID3 = 0xfec;
    public static final int REG_GPIOPCellID0  = 0xff0;
    public static final int REG_GPIOPCellID1  = 0xff4;
    public static final int REG_GPIOPCellID2  = 0xff8;
    public static final int REG_GPIOPCellID3  = 0xffc;

    public GPIO() {
        slave = new GPIOSlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class GPIOSlave extends Controller32 {
        public GPIOSlave() {
            //0x000-0x3fc: REG_GPIODATA

            addReg(REG_GPIODIR, "GPIODIR", 0x00);
            addReg(REG_GPIOIS, "GPIOIS", 0x00);
            addReg(REG_GPIOIBE, "GPIOIBE", 0x00);
            addReg(REG_GPIOIEV, "GPIOIEV", 0x00);
            addReg(REG_GPIOIE, "GPIOIE", 0x00);
            //addReg(REG_GPIORIS, "GPIORIS", 0x00);
            //addReg(REG_GPIOMIS, "GPIOMIS", 0x00);
            addReg(REG_GPIOIC, "GPIOIC", 0x00);
            //addReg(REG_GPIOAFSEL, "GPIOAFSEL", 0x00);

            //0x424-0xfcc: Reserved for future use and test purposes
            //0xfd0-0xfdc: Reserved for future ID

            addReg(REG_GPIOPeriphID0, "GPIOPeriphID0", 0x61);
            addReg(REG_GPIOPeriphID1, "GPIOPeriphID1", 0x10);
            addReg(REG_GPIOPeriphID2, "GPIOPeriphID2", 0x04);
            addReg(REG_GPIOPeriphID3, "GPIOPeriphID3", 0x00);
            addReg(REG_GPIOPCellID0, "GPIOPCellID0", 0x0d);
            addReg(REG_GPIOPCellID1, "GPIOPCellID1", 0xf0);
            addReg(REG_GPIOPCellID2, "GPIOPCellID2", 0x05);
            addReg(REG_GPIOPCellID3, "GPIOPCellID3", 0xb1);
        }

        @Override
        public int readWord(long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_GPIODIR:
                //TODO: not implemented
                System.out.printf("GPIODIR: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_GPIOIS:
                //TODO: not implemented
                System.out.printf("GPIOIS: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_GPIOIBE:
                //TODO: not implemented
                System.out.printf("GPIOIBE: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_GPIOIEV:
                //TODO: not implemented
                System.out.printf("GPIOIEV: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_GPIOIE:
                //TODO: not implemented
                System.out.printf("GPIOIE: read 0x%08x\n", 0);
                result = 0;
                break;
            case REG_GPIOIC:
                //TODO: not implemented
                System.out.printf("GPIOIC: read 0x%08x\n", 0);
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
            case REG_GPIODIR:
                //TODO: not implemented
                System.out.printf("GPIODIR: 0x%08x\n", data);
                break;
            case REG_GPIOIS:
                //TODO: not implemented
                System.out.printf("GPIOIS: 0x%08x\n", data);
                break;
            case REG_GPIOIBE:
                //TODO: not implemented
                System.out.printf("GPIOIBE: 0x%08x\n", data);
                break;
            case REG_GPIOIEV:
                //TODO: not implemented
                System.out.printf("GPIOIEV: 0x%08x\n", data);
                break;
            case REG_GPIOIE:
                //TODO: not implemented
                System.out.printf("GPIOIE: 0x%08x\n", data);
                break;
            case REG_GPIOIC:
                //TODO: not implemented
                System.out.printf("GPIOIC: 0x%08x\n", data);
                break;
            case REG_GPIOPeriphID0:
            case REG_GPIOPeriphID1:
            case REG_GPIOPeriphID2:
            case REG_GPIOPeriphID3:
            case REG_GPIOPCellID0:
            case REG_GPIOPCellID1:
            case REG_GPIOPCellID2:
            case REG_GPIOPCellID3:
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
