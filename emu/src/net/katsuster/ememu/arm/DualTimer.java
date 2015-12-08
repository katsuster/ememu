package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

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
public class DualTimer implements INTSource, BusSlave {
    private INTDestination intDst = new NullINTDestination();
    private DualTimerSlave slave;

    private int clock;
    private boolean[] timerEn;
    private boolean[] timerPeriodic;
    private int[] intEnable;
    private int[] prescale;
    private boolean[] timerSize32;
    private boolean[] oneshot;
    private int[] rawInt;
    private int[] loadValue;
    private int[] currentValue;

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
     * 1MHz 駆動のタイマーを作成します。
     */
    public DualTimer() {
        this(1000000);
    }

    /**
     * タイマーを作成します。
     *
     * @param ck タイマーを駆動するクロックの周波数
     */
    public DualTimer(int ck) {
        clock = ck;
        timerEn = new boolean[2];
        timerPeriodic = new boolean[2];
        prescale = new int[2];
        timerSize32 = new boolean[2];
        oneshot = new boolean[2];
        rawInt = new int[2];
        intEnable = new int[2];
        loadValue = new int[2];
        currentValue = new int[2];

        slave = new DualTimerSlave();
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
        return (rawInt[0] & intEnable[0]) != 0 ||
                (rawInt[1] & intEnable[1]) != 0;
    }

    @Override
    public String getIRQMessage() {
        return "Dual-Timer";
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class DualTimerSlave extends Controller32 {
        public DualTimerSlave() {
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

            //コントロールレジスタの設定を反映する
            updateControl(0, REG_Timer1Control, 0x20);
            updateControl(1, REG_Timer2Control, 0x20);
        }

        /**
         * コントロールレジスタの設定を反映する。
         *
         * @param id タイマー ID
         * @param regaddr レジスタのアドレス
         * @param val レジスタの値
         */
        public void updateControl(int id, long regaddr, int val) {
            boolean en = BitOp.getBit32(val, 7);
            boolean peri = BitOp.getBit32(val, 6);
            boolean inten = BitOp.getBit32(val, 5);
            int pre = BitOp.getField32(val, 2, 2);
            boolean size32 = BitOp.getBit32(val, 1);
            boolean one = BitOp.getBit32(val, 0);

            System.out.printf("Timer%dControl: 0x%x.\n", id + 1, val);
            System.out.printf("  timerEn      : %b\n", en);
            System.out.printf("  timerPeriodic: %b\n", peri);
            System.out.printf("  intEnable    : %b\n", inten);
            System.out.printf("  timerPre     : %d\n", pre);
            System.out.printf("  timerSize32  : %b\n", size32);
            System.out.printf("  oneshot      : %b\n", one);

            timerEn[id] = en;
            timerPeriodic[id] = peri;

            if (inten) {
                intEnable[id] = 0x1;
            } else {
                intEnable[id] = 0x0;
            }

            switch (pre) {
            case 0:
                //clock is divided by 1
                prescale[id] = 1;
                break;
            case 1:
                //clock is divided by 16
                prescale[id] = 16;
                break;
            case 2:
                //clock is divided by 256
                prescale[id] = 256;
                break;
            case 3:
                //undefined, ignored
                break;
            }

            timerSize32[id] = size32;
            oneshot[id] = one;

            super.writeWord(regaddr, val);
        }

        @Override
        public int readWord(long addr) {
            int regaddr;
            int result;

            regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_Timer1Load:
                result = super.readWord(regaddr);
                break;
            case REG_Timer1Value:
                result = currentValue[0];
                break;
            case REG_Timer1IntClr:
                //write only, ignored
                result = 0;
                break;
            case REG_Timer1RIS:
                result = rawInt[0];
                break;
            case REG_Timer1MIS:
                result = rawInt[0] & intEnable[0];
                break;
            case REG_Timer1BGLoad:
                result = super.readWord(regaddr);
                break;
            case REG_Timer2Load:
                result = super.readWord(regaddr);
                break;
            case REG_Timer2Value:
                result = currentValue[1];
                break;
            case REG_Timer2IntClr:
                //write only, ignored
                result = 0;
                break;
            case REG_Timer2RIS:
                result = rawInt[1];
                break;
            case REG_Timer2MIS:
                result = rawInt[1] & intEnable[1];
                break;
            case REG_Timer2BGLoad:
                result = super.readWord(regaddr);
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

            synchronized(this) {
                switch (regaddr) {
                case REG_Timer1Load:
                    loadValue[0] = data;
                    currentValue[0] = 0;
                    super.writeWord(regaddr, data);
                    break;
                case REG_Timer1Value:
                    //read only, ignored
                    break;
                case REG_Timer1Control:
                    updateControl(0, regaddr, data);
                    break;
                case REG_Timer1IntClr:
                    rawInt[0] &= ~data;
                    break;
                case REG_Timer1RIS:
                case REG_Timer1MIS:
                    //read only, ignored
                    break;
                case REG_Timer1BGLoad:
                    loadValue[0] = data;
                    super.writeWord(regaddr, data);
                    break;
                case REG_Timer2Load:
                    loadValue[1] = data;
                    currentValue[1] = 0;
                    super.writeWord(regaddr, data);
                    break;
                case REG_Timer2Value:
                    //read only, ignored
                    break;
                case REG_Timer2Control:
                    updateControl(1, regaddr, data);
                    break;
                case REG_Timer2IntClr:
                    rawInt[1] &= ~data;
                    break;
                case REG_Timer2RIS:
                case REG_Timer2MIS:
                    //read only, ignored
                    break;
                case REG_Timer2BGLoad:
                    loadValue[1] = data;
                    super.writeWord(regaddr, data);
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
        }

        @Override
        public void run() {
            while (!shouldHalt()) {
                //FIXME: 100Hz polling
                int hz = 100;

                try {
                    Thread.sleep(1000 / hz);

                    //NOTE: タイマーカウンタのクリアと排他する必要がある
                    synchronized (this) {
                        for (int id = 0; id < 2; id++) {
                            if (!timerEn[id]) {
                                //hold value
                                continue;
                            }

                            int dec = clock / (hz * prescale[id]);
                            int after = currentValue[id] - dec;

                            if (currentValue[id] == 0 ||
                                    after == 0 || after > currentValue[id]) {
                                //0 または 0 以下に達した
                                currentValue[id] = loadValue[id];
                                rawInt[id] = 0x1;
                            } else {
                                currentValue[id] = after;
                            }
                        }

                        intDst.setRaisedInterrupt(isAssert());
                    }
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        }
    }

}
