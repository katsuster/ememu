package net.katsuster.ememu.riscv;

import net.katsuster.ememu.generic.BitOp;
import net.katsuster.ememu.generic.Controller32;
import net.katsuster.ememu.generic.core.AbstractParentCore;
import net.katsuster.ememu.generic.bus.BusMaster64;

/**
 * DDR Controller
 *
 * 参考: SiFive FU540-C000 Manual: v1p0
 */
public class DDRController extends AbstractParentCore {
    public static final int REG_CTRL000 = 0x0000;
    public static final int REG_CTRL264 = 0x0420;

    public static final int REG_PHY0000 = 0x2000;
    public static final int REG_PHY1214 = 0x32f8;

    //8bytes registers
    public static final int REG_BUSBLOCKER_L = 0x8000;
    public static final int REG_BUSBLOCKER_H = 0x8004;

    public DDRController(String n) {
        super(n);

        setSlaveCore(new DDRControllerSlave());
    }

    class DDRControllerSlave extends Controller32 {
        public DDRControllerSlave() {
            for (int i = 0; i <= 264; i++) {
                addReg(REG_CTRL000 + i * 4, "CTRL" + i, 0x00000000);
            }
            for (int i = 0; i <= 1214; i++) {
                addReg(REG_PHY0000 + i * 4, "PHY" + i, 0x00000000);
            }

            addReg(REG_BUSBLOCKER_L, "BUSBLOCKER_L", 0x80000000);
            addReg(REG_BUSBLOCKER_H, "BUSBLOCKER_H", 0x80000000);
        }

        @Override
        public int readWord(BusMaster64 m, long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            if (REG_CTRL000 <= regaddr && regaddr <= REG_CTRL264) {
                int i = (regaddr - REG_CTRL000) >> 2;

                switch (i) {
                case 0:
                case 19:
                case 21:
                case 120:
                    result = super.readWord(m, regaddr);
                    break;
                case 132:
                    result = super.readWord(m, regaddr);
                    //int_status[8]: Initialization has been completed
                    result |= 0x100;
                    break;
                case 136:
                    result = super.readWord(m, regaddr);
                    break;
                default:
                    result = super.readWord(m, regaddr);
                    break;
                }

                return result;
            }

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

                switch (i) {
                case 0:
                case 19:
                case 21:
                case 120:
                case 132:
                case 136:
                    System.out.printf("DDRC: wr CTRL%d 0x%x\n", i, data);
                    break;
                }

                return;
            }

            if (REG_PHY0000 <= regaddr && regaddr <= REG_PHY1214) {
                int i = (regaddr - REG_PHY0000) >> 2;
                //System.out.printf("DDRC: wr PHY%d 0x%x\n", i, data);
                return;
            }

            switch (regaddr) {
            default:
                super.writeWord(m, regaddr, data);
                break;
            }
        }

        @Override
        public long read64(BusMaster64 m, long addr) {
            int regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));
            long data;

            switch (regaddr) {
            case REG_BUSBLOCKER_L:
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "Cannot read 64bit from 0x%08x.", addr));
            }

            data = (((long)readWord(m, addr + 0) & 0xffffffffL) << 0) |
                    (((long)readWord(m, addr + 4) & 0xffffffffL) << 32);

            return data;
        }

        @Override
        public void write64(BusMaster64 m, long addr, long data) {
            int regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_BUSBLOCKER_L:
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "Cannot write 64bit to 0x%08x.", addr));
            }

            writeWord(m, addr + 0, (int)(data >>> 0));
            writeWord(m, addr + 4, (int)(data >>> 32));
        }
    }

}
