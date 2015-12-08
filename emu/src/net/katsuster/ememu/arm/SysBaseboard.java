package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * システムレジスタ
 *
 * 参考: Versatile Application Baseboard for ARM926EJ-S User Guide
 * ARM DUI0225D
 *
 * @author katsuhiro
 */
public class SysBaseboard implements BusSlave {
    private SysBaseboardSlave slave;

    private long start24MHz;

    public static final int REG_SYS_ID         = 0x000;
    public static final int REG_SYS_SW         = 0x004;
    public static final int REG_SYS_LED        = 0x008;
    public static final int REG_SYS_OSC0       = 0x00c;
    public static final int REG_SYS_OSC1       = 0x010;
    public static final int REG_SYS_OSC2       = 0x014;
    public static final int REG_SYS_OSC3       = 0x018;
    public static final int REG_SYS_OSC4       = 0x01c;
    public static final int REG_SYS_LOCK       = 0x020;
    public static final int REG_SYS_100HZ      = 0x024;
    public static final int REG_SYS_CFGDATA1   = 0x028;
    public static final int REG_SYS_CFGDATA2   = 0x02c;
    public static final int REG_SYS_FLAGS      = 0x030;
    public static final int REG_SYS_FLAGSSET   = 0x030;
    public static final int REG_SYS_FLAGSCLR   = 0x034;
    public static final int REG_SYS_NVFLAGS    = 0x038;
    public static final int REG_SYS_NVFLAGSSET = 0x038;
    public static final int REG_SYS_NVFLAGSCLR = 0x03c;
    public static final int REG_SYS_RESETCTL   = 0x040;
    public static final int REG_SYS_PCICTL     = 0x044;
    public static final int REG_SYS_MCI        = 0x048;
    public static final int REG_SYS_FLASH      = 0x04c;
    public static final int REG_SYS_CLCD       = 0x050;
    public static final int REG_SYS_24MHz      = 0x05c;
    public static final int REG_SYS_MISC       = 0x060;
    public static final int REG_SYS_DMAPSR0    = 0x064;
    public static final int REG_SYS_DMAPSR1    = 0x068;
    public static final int REG_SYS_DMAPSR2    = 0x06c;
    public static final int REG_SYS_OSCRESET0  = 0x08c;
    public static final int REG_SYS_OSCRESET1  = 0x090;
    public static final int REG_SYS_OSCRESET2  = 0x094;
    public static final int REG_SYS_OSCRESET3  = 0x098;
    public static final int REG_SYS_OSCRESET4  = 0x09c;
    public static final int REG_SYS_TEST_OSC0  = 0x0c0;
    public static final int REG_SYS_TEST_OSC1  = 0x0c4;
    public static final int REG_SYS_TEST_OSC2  = 0x0c8;
    public static final int REG_SYS_TEST_OSC3  = 0x0cc;
    public static final int REG_SYS_TEST_OSC4  = 0x0d0;


    public SysBaseboard() {
        start24MHz = System.nanoTime();

        slave = new SysBaseboardSlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class SysBaseboardSlave extends Controller32 {
        public SysBaseboardSlave() {
            //addReg(REG_SYS_ID, "SYS_ID", 0x00000000);
            //addReg(REG_SYS_SW, "SYS_SW", 0x00000000);
            addReg(REG_SYS_LED, "SYS_LED", 0x00000000);
            //addReg(REG_SYS_OSC0, "SYS_OSC0", 0x00000000);
            //addReg(REG_SYS_OSC1, "SYS_OSC1", 0x00000000);
            //addReg(REG_SYS_OSC2, "SYS_OSC2", 0x00000000);
            //addReg(REG_SYS_OSC3, "SYS_OSC3", 0x00000000);
            addReg(REG_SYS_OSC4, "SYS_OSC4", 0x00000000);
            addReg(REG_SYS_LOCK, "SYS_LOCK", 0x00000000);
            //addReg(REG_SYS_100HZ, "SYS_100HZ", 0x00000000);
            //addReg(REG_SYS_CFGDATA1, "SYS_CFGDATA1", 0x00000000);
            //addReg(REG_SYS_CFGDATA2, "SYS_CFGDATA2", 0x00000000);
            //addReg(REG_SYS_FLAGS, "SYS_FLAGS", 0x00000000);
            //addReg(REG_SYS_FLAGSSET, "SYS_FLAGSSET", 0x00000000);
            //addReg(REG_SYS_FLAGSCLR, "SYS_FLAGSCLR", 0x00000000);
            //addReg(REG_SYS_NVFLAGS, "SYS_NVFLAGS", 0x00000000);
            //addReg(REG_SYS_NVFLAGSSET, "SYS_NVFLAGSSET", 0x00000000);
            //addReg(REG_SYS_NVFLAGSCLR, "SYS_NVFLAGSCLR", 0x00000000);
            addReg(REG_SYS_RESETCTL, "SYS_RESETCTL", 0x00000000);
            //addReg(REG_SYS_PCICTL, "SYS_PCICTL", 0x00000000);
            //addReg(REG_SYS_MCI, "SYS_MCI", 0x00000000);
            addReg(REG_SYS_FLASH, "SYS_FLASH", 0x00000000);
            addReg(REG_SYS_CLCD, "SYS_CLCD", 0x00000000);

            addReg(REG_SYS_24MHz, "SYS_24MHz", 0x00000000);
            //addReg(REG_SYS_MISC, "SYS_MISC", 0x00000000);
            //addReg(REG_SYS_DMAPSR0, "SYS_DMAPSR0", 0x00000000);
            //addReg(REG_SYS_DMAPSR1, "SYS_DMAPSR1", 0x00000000);
            //addReg(REG_SYS_DMAPSR2, "SYS_DMAPSR2", 0x00000000);
            //addReg(REG_SYS_OSCRESET0, "SYS_OSCRESET0", 0x00000000);
            //addReg(REG_SYS_OSCRESET1, "SYS_OSCRESET1", 0x00000000);
            //addReg(REG_SYS_OSCRESET2, "SYS_OSCRESET2", 0x00000000);
            //addReg(REG_SYS_OSCRESET3, "SYS_OSCRESET3", 0x00000000);
            //addReg(REG_SYS_OSCRESET4, "SYS_OSCRESET4", 0x00000000);
            //addReg(REG_SYS_TEST_OSC0, "SYS_TEST_OSC0", 0x00000000);
            //addReg(REG_SYS_TEST_OSC1, "SYS_TEST_OSC1", 0x00000000);
            //addReg(REG_SYS_TEST_OSC2, "SYS_TEST_OSC2", 0x00000000);
            //addReg(REG_SYS_TEST_OSC3, "SYS_TEST_OSC3", 0x00000000);
            //addReg(REG_SYS_TEST_OSC4, "SYS_TEST_OSC4", 0x00000000);
        }

        @Override
        public int readWord(long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_SYS_LED:
                //TODO: not implemented
                result = super.readWord(regaddr);
                //System.out.printf("SYS_LED: read 0x%08x\n", result);
                break;
            case REG_SYS_OSC4:
                //TODO: not implemented
                result = 0;
                System.out.printf("SYS_OSC4: read 0x%08x\n", result);
                break;
            case REG_SYS_LOCK:
                //TODO: not implemented
                result = 0;
                System.out.printf("SYS_LOCK: read 0x%08x\n", result);
                break;
            case REG_SYS_RESETCTL:
                //TODO: not implemented
                result = 0x0;
                System.out.printf("SYS_RESETCTL: read 0x%08x\n", result);
                break;
            case REG_SYS_CLCD:
                //TODO: not implemented
                result = 0x1f00;
                System.out.printf("SYS_CLCD: read 0x%08x\n", result);
                break;
            case REG_SYS_24MHz:
                //TODO: 桁あふれ問題が未解決のまま
                result = (int) ((System.nanoTime() - start24MHz) / 1000 * 24);
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
            case REG_SYS_LED:
                //TODO: not implemented
                //System.out.printf("SYS_LED: 0x%08x\n", data);
                super.writeWord(regaddr, data);
                break;
            case REG_SYS_OSC4:
                //TODO: not implemented
                System.out.printf("SYS_OSC4: 0x%08x\n", data);
                break;
            case REG_SYS_LOCK:
                //TODO: not implemented
                System.out.printf("SYS_LOCK: 0x%08x\n", data);
                break;
            case REG_SYS_RESETCTL:
                //TODO: not implemented
                System.out.printf("SYS_RESETCTL: 0x%08x\n", data);
                break;
            case REG_SYS_FLASH:
                //TODO: not implemented
                boolean we = BitOp.getBit32(data, 0);

                //System.out.printf("SYS_FLASH: 0x%08x\n", data);
                //System.out.printf("WriteEnable: %b.\n", we);
                break;
            case REG_SYS_CLCD:
                //TODO: not implemented
                System.out.printf("SYS_CLCD: 0x%08x\n", data);
                break;
            case REG_SYS_24MHz:
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
