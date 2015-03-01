package net.katsuster.ememu.arm;

/**
 * タイマー
 *
 * <p>
 * 参考: ARM Dual-Timer Module (SP804)
 * ARM DDI0271C
 * </p>
 *
 * @author katsuhiro
 */
public class DualTimer extends Controller64Reg32
        implements INTSource {
    private INTDestination intDst = new NullINTDestination();
    private int[] rawInt;
    private int[] intMask;

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

    /**
     * タイマーを作成します。
     */
    public DualTimer() {
        rawInt = new int[2];
        intMask = new int[2];

        addReg(REG_Timer1Load, "Timer1Load", 0x00000000);
        addReg(REG_Timer1Value, "Timer1Value", 0xffffffff);
        addReg(REG_Timer1Control, "Timer1Control", 0x20);
        addReg(REG_Timer1IntClr, "Timer1IntClr", 0x00000000);
        addReg(REG_Timer1RIS, "Timer1RIS", 0x00000000);
        addReg(REG_Timer1MIS, "Timer1MIS", 0x00000000);
        addReg(REG_Timer1BGLoad, "Timer1BGLoad", 0x00000000);

        addReg(REG_Timer2Load, "Timer2Load", 0x00000000);
        addReg(REG_Timer2Value, "Timer2Value", 0xffffffff);
        addReg(REG_Timer2Control, "Timer2Control", 0x20);
        addReg(REG_Timer2IntClr, "Timer2IntClr", 0x00000000);
        addReg(REG_Timer2RIS, "Timer2RIS", 0x00000000);
        addReg(REG_Timer2MIS, "Timer2MIS", 0x00000000);
        addReg(REG_Timer2BGLoad, "Timer2BGLoad", 0x00000000);

        addReg(REG_TimerPeriphID0, "TimerPeriphID0", 0x00000004);
        addReg(REG_TimerPeriphID1, "TimerPeriphID1", 0x00000018);
        addReg(REG_TimerPeriphID2, "TimerPeriphID2", 0x00000014);
        addReg(REG_TimerPeriphID3, "TimerPeriphID3", 0x00000000);
        addReg(REG_TimerPCellID0, "TimerPCellID0", 0x0000000d);
        addReg(REG_TimerPCellID1, "TimerPCellID1", 0x000000f0);
        addReg(REG_TimerPCellID2, "TimerPCellID2", 0x00000005);
        addReg(REG_TimerPCellID3, "TimerPCellID3", 0x000000b1);
    }

    public void changeControl(int id, long regaddr, int val) {
        boolean enabled = BitOp.getBit32(val, 7);
        boolean periodic = BitOp.getBit32(val, 6);
        boolean inten = BitOp.getBit32(val, 5);
        boolean size32 = BitOp.getBit32(val, 1);
        boolean oneshot = BitOp.getBit32(val, 0);

        System.out.printf("Timer%dControl: 0x%x.\n", id + 1, val);
        System.out.printf("  enabled : %b.\n", enabled);
        System.out.printf("  periodic: %b.\n", periodic);
        System.out.printf("  inten   : %b.\n", inten);
        System.out.printf("  size32  : %b.\n", size32);
        System.out.printf("  oneshot : %b.\n", oneshot);

        if (inten) {
            intMask[id] = 0x1;
        } else {
            intMask[id] = 0x0;
        }

        super.writeWord(regaddr, val);
    }

    @Override
    public int readWord(long addr) {
        int regaddr;
        int result;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_Timer1IntClr:
            //write only, ignored
            result = 0;
            break;
        case REG_Timer1RIS:
            result = rawInt[0];
            break;
        case REG_Timer1MIS:
            result = rawInt[0] & intMask[0];
            break;
        case REG_Timer2IntClr:
            //write only, ignored
            result = 0;
            break;
        case REG_Timer2RIS:
            result = rawInt[1];
            break;
        case REG_Timer2MIS:
            result = rawInt[1] & intMask[1];
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
        case REG_Timer1Load:
            //TODO: not implemented
            System.out.printf("Timer1Load: 0x%08x\n", data);
            break;
        case REG_Timer1Value:
            //TODO: not implemented
            System.out.printf("Timer1Value: 0x%08x\n", data);
            break;
        case REG_Timer1Control:
            changeControl(0, regaddr, data);
            break;
        case REG_Timer1IntClr:
            rawInt[0] &= ~data;
            break;
        case REG_Timer1RIS:
        case REG_Timer1MIS:
            //read only, ignored
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
            changeControl(1, regaddr, data);
            break;
        case REG_Timer2IntClr:
            rawInt[1] &= ~data;
            break;
        case REG_Timer2RIS:
        case REG_Timer2MIS:
            //read only, ignored
            break;
        case REG_TimerPeriphID0:
        case REG_TimerPeriphID1:
        case REG_TimerPeriphID2:
        case REG_TimerPeriphID3:
        case REG_TimerPCellID0:
        case REG_TimerPCellID1:
        case REG_TimerPCellID2:
        case REG_TimerPCellID3:
            //read only, ignored
            break;
        default:
            super.writeWord(regaddr, data);
            break;
        }
    }

    @Override
    public INTDestination getINTDestination() {
        return intDst;
    }

    @Override
    public void connectINTDestination(INTDestination c) {
        intDst = c;
    }

    @Override
    public void disconnectINTDestination() {
        intDst = new NullINTDestination();
    }

    @Override
    public boolean isAssert() {
        return (rawInt[0] & intMask[0]) != 0 ||
                (rawInt[1] & intMask[1]) != 0;
    }

    @Override
    public String getIRQMessage() {
        return "Dual-Timer";
    }

    @Override
    public void run() {
        while (!shouldHalt()) {
            try {
                Thread.sleep(4);
                rawInt[0] = 0x1;
                rawInt[1] = 0x1;

                intDst.setRaisedInterrupt(isAssert());
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }
}
