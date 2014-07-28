package net.katsuster.semu;

/**
 * タイマー
 *
 * 参考: ARM Dual-Timer Module (SP804)
 * ARM DDI0271C
 *
 * @author katsuhiro
 */
public class DualTimer extends Controller64Reg32 {
    public static final int REG_Timer1Load     = 0x000;
    public static final int REG_Timer1Value    = 0x004;
    public static final int REG_Timer1Control  = 0x008;
    public static final int REG_Timer1IntClr   = 0x00c;
    public static final int REG_Timer1RIS      = 0x010;
    public static final int REG_Timer1MIS      = 0x014;
    public static final int REG_Timer1BGLoad   = 0x018;
    public static final int REG_Timer2Load     = 0x020;
    public static final int REG_Timer2Value    = 0x024;
    public static final int REG_Timer2Control  = 0x028;
    public static final int REG_Timer2IntClr   = 0x02c;
    public static final int REG_Timer2RIS      = 0x030;
    public static final int REG_Timer2MIS      = 0x034;
    public static final int REG_Timer2BGLoad   = 0x038;
    public static final int REG_TimerITCR      = 0xf00;
    public static final int REG_TimerITOP      = 0xf04;
    public static final int REG_TimerPeriphID0 = 0xfe0;
    public static final int REG_TimerPeriphID1 = 0xfe4;
    public static final int REG_TimerPeriphID2 = 0xfe8;
    public static final int REG_TimerPeriphID3 = 0xfec;
    public static final int REG_TimerPCellID0  = 0xff0;
    public static final int REG_TimerPCellID1  = 0xff4;
    public static final int REG_TimerPCellID2  = 0xff8;
    public static final int REG_TimerPCellID3  = 0xffc;

    public DualTimer() {
        addReg(REG_Timer1Load, "Timer1Load", 0x00000000);
        addReg(REG_Timer1Value, "Timer1Value", 0x00000000);
        addReg(REG_Timer1Control, "Timer1Control", 0x00000000);
        addReg(REG_Timer1IntClr, "Timer1IntClr", 0x00000000);

        addReg(REG_Timer2Load, "Timer2Load", 0x00000000);
        addReg(REG_Timer2Value, "Timer2Value", 0x00000000);
        addReg(REG_Timer2Control, "Timer2Control", 0x00000000);
        addReg(REG_Timer2IntClr, "Timer2IntClr", 0x00000000);
    }

    @Override
    public boolean tryRead(long addr) {
        return tryAccess(addr);
    }

    @Override
    public boolean tryWrite(long addr) {
        return tryAccess(addr);
    }

    public boolean tryAccess(long addr) {
        int regaddr;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        default:
            return super.isValidReg(regaddr);
        }
    }

    @Override
    public int readWord(long addr) {
        int regaddr;
        int result;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_Timer1Control:
            //TODO: not implemented
            System.out.printf("Timer1Control: read 0x%08x\n", 0);
            result = 0x0;
            break;
        case REG_Timer2Control:
            //TODO: not implemented
            System.out.printf("Timer2Control: read 0x%08x\n", 0);
            result = 0x0;
            break;
        default:
            result = super.getReg(regaddr);
            break;
        }

        return result;
    }

    @Override
    public void writeWord(long addr, int data) {
        int regaddr;

        regaddr = (int) (addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_Timer1Load:
            //TODO: not implemented
            System.out.printf("Timer1Load: 0x%08x\n", data);
            break;
        case REG_Timer1Value:
            //TODO: not implemented
            System.out.printf("Timer1Value: 0x%08x\n", data);
            break;
        case REG_Timer1Control:
            //TODO: not implemented
            System.out.printf("Timer1Control: 0x%08x\n", data);
            break;
        case REG_Timer1IntClr:
            //TODO: not implemented
            System.out.printf("Timer1IntClr: 0x%08x\n", data);
            break;
        case REG_Timer2Load:
            //TODO: not implemented
            System.out.printf("Timer2Load: 0x%08x\n", data);
            break;
        case REG_Timer2Value:
            //TODO: not implemented
            System.out.printf("Timer2Value: 0x%08x\n", data);
            break;
        case REG_Timer2Control:
            //TODO: not implemented
            System.out.printf("Timer2Control: 0x%08x\n", data);
            break;
        case REG_Timer2IntClr:
            //TODO: not implemented
            System.out.printf("Timer2IntClr: 0x%08x\n", data);
            break;
        default:
            super.setReg(regaddr, data);
            break;
        }
    }
}
