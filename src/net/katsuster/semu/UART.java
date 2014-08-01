package net.katsuster.semu;

/**
 * UART
 *
 * 参考: ARM PrimeCell UART (PL011)
 * 日本語版は ARM DDI0183AJ, 英語版は ARM DDI0183G
 *
 * @author katsuhiro
 */
public class UART extends Controller64Reg32 {
    public static final int REG_UARTDR        = 0x000;
    public static final int REG_UARTRSR       = 0x004;
    public static final int REG_UARTFR        = 0x018;
    public static final int REG_UARTILPR      = 0x020;
    public static final int REG_UARTIBRD      = 0x024;
    public static final int REG_UARTFBRD      = 0x028;
    public static final int REG_UARTLCR_H     = 0x02c;
    public static final int REG_UARTCR        = 0x030;
    public static final int REG_UARTIFLS      = 0x034;
    public static final int REG_UARTIMSC      = 0x038;
    public static final int REG_UARTRIS       = 0x03c;
    public static final int REG_UARTMIS       = 0x040;
    public static final int REG_UARTICR       = 0x044;
    public static final int REG_UARTDMACR     = 0x048;
    public static final int REG_UARTPeriphID0 = 0xfe0;
    public static final int REG_UARTPeriphID1 = 0xfe4;
    public static final int REG_UARTPeriphID2 = 0xfe8;
    public static final int REG_UARTPeriphID3 = 0xfec;
    public static final int REG_UARTPCellID0  = 0xff0;
    public static final int REG_UARTPCellID1  = 0xff4;
    public static final int REG_UARTPCellID2  = 0xff8;
    public static final int REG_UARTPCellID3  = 0xffc;

    private StringBuilder strBuffer;

    public UART() {
        strBuffer = new StringBuilder();

        addReg(REG_UARTDR, "UARTDR", 0x00000000);
        addReg(REG_UARTFR, "UARTFR", 0x00000000);

        addReg(REG_UARTIBRD, "UARTIBRD", 0x00000000);
        addReg(REG_UARTFBRD, "UARTFBRD", 0x00000000);
        addReg(REG_UARTLCR_H, "UARTLCR_H", 0x00000000);
        addReg(REG_UARTCR, "UARTCR", 0x00000000);

        addReg(REG_UARTIMSC, "UARTIMSC", 0x00000000);
        addReg(REG_UARTICR, "UARTICR", 0x00000000);

        addReg(REG_UARTPeriphID0, "UARTPeriphID0", 0x00000011);
        addReg(REG_UARTPeriphID1, "UARTPeriphID1", 0x00000010);
        addReg(REG_UARTPeriphID2, "UARTPeriphID2", 0x00000014);
        addReg(REG_UARTPeriphID3, "UARTPeriphID3", 0x00000000);
        addReg(REG_UARTPCellID0, "UARTPCellID0", 0x0000000d);
        addReg(REG_UARTPCellID1, "UARTPCellID1", 0x000000f0);
        addReg(REG_UARTPCellID2, "UARTPCellID2", 0x00000005);
        addReg(REG_UARTPCellID3, "UARTPCellID3", 0x000000b1);
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
        case REG_UARTDR:
            //TODO: Not implemented
            throw new IllegalArgumentException("Sorry, not implemented.");
            //break;
        case REG_UARTFR:
            result = 0;
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

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_UARTDR:
            char ascii = (char)(data & 0xff);

            strBuffer.append(ascii);
            System.out.printf("%c", ascii);

            break;
        case REG_UARTFR:
            //TODO: Not implemented
            System.out.printf("UARTFR: 0x%08x\n", data);
            break;
        case REG_UARTIBRD:
            //TODO: Not implemented
            System.out.printf("UARTIBRD: 0x%08x\n", data);
            break;
        case REG_UARTFBRD:
            //TODO: Not implemented
            System.out.printf("UARTFBRD: 0x%08x\n", data);
            break;
        case REG_UARTLCR_H:
            //TODO: Not implemented
            System.out.printf("UARTLCR_H: 0x%08x\n", data);
            break;
        case REG_UARTCR:
            //TODO: Not implemented
            //System.out.printf("UARTCR: 0x%08x\n", data);
            break;
        case REG_UARTIMSC:
            //TODO: Not implemented
            System.out.printf("UARTIMSC: 0x%08x\n", data);
            break;
        case REG_UARTICR:
            //TODO: Not implemented
            System.out.printf("UARTICR: 0x%08x\n", data);
            break;
        case REG_UARTPeriphID0:
        case REG_UARTPeriphID1:
        case REG_UARTPeriphID2:
        case REG_UARTPeriphID3:
        case REG_UARTPCellID0:
        case REG_UARTPCellID1:
        case REG_UARTPCellID2:
        case REG_UARTPCellID3:
            //read only, ignored
            break;
        default:
            super.setReg(regaddr, data);
            break;
        }
    }
}
