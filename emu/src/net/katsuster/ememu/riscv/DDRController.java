package net.katsuster.ememu.riscv;

import net.katsuster.ememu.generic.*;

/**
 * DDR Controller
 *
 * 参考: SiFive FU540-C000 Manual: v1p0
 */
public class DDRController implements ParentCore {
    private DDRControllerSlave slave;

    public static final int REG_CTRL000 = 0x0000;
    public static final int REG_CTRL264 = 0x0420;

    public static final int REG_PHY0000 = 0x2000;
    public static final int REG_PHY1214 = 0x32f8;

    public static final int REG_BUSBLOCKER = 0x8000;

    public DDRController() {
        slave = new DDRControllerSlave();
    }

    @Override
    public SlaveCore64 getSlaveCore() {
        return slave;
    }

    class DDRControllerSlave extends Controller32 {
        public DDRControllerSlave() {
            for (int i = 0; i <= 264; i++) {
                addReg(REG_CTRL000 + i * 4, "CTRL" + i, 0x00000000);
            }
            for (int i = 0; i <= 1214; i++) {
                addReg(REG_PHY0000 + i * 4, "PHY" + i, 0x00000000);
            }

            addReg(REG_BUSBLOCKER, "BUSBLOCKER", 0x80000000);
        }

        @Override
        public int readWord(BusMaster64 m, long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            default:
                result = super.readWord(m, regaddr);
                break;
            }

            return result;
        }

        @Override
        public void writeWord(BusMaster64 m, long addr, int data) {
            int regaddr;

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            if (REG_CTRL000 <= regaddr && regaddr <= REG_CTRL264) {
                int i = (regaddr - REG_CTRL000) >> 2;
                System.out.printf("DDRC: wr CTRL%d 0x%x\n", i, data);
                return;
            }

            if (REG_PHY0000 <= regaddr && regaddr <= REG_PHY1214) {
                int i = (regaddr - REG_PHY0000) >> 2;
                System.out.printf("DDRC: wr PHY%d 0x%x\n", i, data);
                return;
            }

            switch (regaddr) {
            case REG_BUSBLOCKER:
                System.out.printf("DDRC: wr BUSBLOCKER 0x%x\n", data);
                break;
            default:
                super.writeWord(m, regaddr, data);
                break;
            }
        }
    }

}
