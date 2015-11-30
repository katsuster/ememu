package net.katsuster.ememu.arm;

import net.katsuster.ememu.generic.*;

/**
 * Real Time Clock
 *
 * 参考: ARM PrimeCell Real Time Clock (PL031)
 * ARM DDI0224B
 *
 * @author katsuhiro
 */
public class RTC implements BusSlave {
    private RTCSlave slave;

    public static final int REG_RTCDR        = 0x000;
    public static final int REG_RTCMR        = 0x004;
    public static final int REG_RTCLR        = 0x008;
    public static final int REG_RTCCR        = 0x00c;
    public static final int REG_RTCIMSC      = 0x010;
    public static final int REG_RTCRIS       = 0x014;
    public static final int REG_RTCMIS       = 0x018;
    public static final int REG_RTCICR       = 0x01c;

    public static final int REG_RTCPeriphID0 = 0xfe0;
    public static final int REG_RTCPeriphID1 = 0xfe4;
    public static final int REG_RTCPeriphID2 = 0xfe8;
    public static final int REG_RTCPeriphID3 = 0xfec;
    public static final int REG_RTCPCellID0  = 0xff0;
    public static final int REG_RTCPCellID1  = 0xff4;
    public static final int REG_RTCPCellID2  = 0xff8;
    public static final int REG_RTCPCellID3  = 0xffc;

    public RTC() {
        slave = new RTCSlave();
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class RTCSlave extends Controller32 {
        public RTCSlave() {
            //addReg(REG_RTCDR, "RTCDR", 0x00000000);
            //addReg(REG_RTCMR, "RTCMR", 0x00000000);
            //addReg(REG_RTCLR, "RTCLR", 0x00000000);
            //addReg(REG_RTCCR, "RTCCR", 0x00000000);
            //addReg(REG_RTCIMSC, "RTCIMSC", 0x00000000);
            //addReg(REG_RTCRIS, "RTCRIS", 0x00000000);
            //addReg(REG_RTCMIS, "RTCMIS", 0x00000000);
            //addReg(REG_RTCICR, "RTCICR", 0x00000000);

            addReg(REG_RTCPeriphID0, "RTCPeriphID0", 0x31);
            addReg(REG_RTCPeriphID1, "RTCPeriphID1", 0x10);
            addReg(REG_RTCPeriphID2, "RTCPeriphID2", 0x04);
            addReg(REG_RTCPeriphID3, "RTCPeriphID3", 0x00);
            addReg(REG_RTCPCellID0, "RTCPCellID0", 0x0d);
            addReg(REG_RTCPCellID1, "RTCPCellID1", 0xf0);
            addReg(REG_RTCPCellID2, "RTCPCellID2", 0x05);
            addReg(REG_RTCPCellID3, "RTCPCellID3", 0xb1);
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
            case REG_RTCPeriphID0:
            case REG_RTCPeriphID1:
            case REG_RTCPeriphID2:
            case REG_RTCPeriphID3:
            case REG_RTCPCellID0:
            case REG_RTCPCellID1:
            case REG_RTCPCellID2:
            case REG_RTCPCellID3:
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
