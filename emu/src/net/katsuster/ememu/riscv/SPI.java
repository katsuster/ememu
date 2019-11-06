package net.katsuster.ememu.riscv;

import net.katsuster.ememu.generic.*;

/**
 * Serial Peripheral Interface (SPI)
 *
 * 参考: SiFive FU540-C000 Manual: v1p0
 */
public class SPI extends AbstractParentCore {
    public static final int REG_SCKDIV  = 0x0000;
    public static final int REG_SCKMODE = 0x0004;
    public static final int REG_CSID    = 0x0010;
    public static final int REG_CSDEF   = 0x0014;
    public static final int REG_CSMODE  = 0x0018;
    public static final int REG_DELAY0  = 0x0028;
    public static final int REG_DELAY1  = 0x002c;
    public static final int REG_FMT     = 0x0040;
    public static final int REG_TXDATA  = 0x0048;
    public static final int REG_RXDATA  = 0x004c;
    public static final int REG_TXMARK  = 0x0050;
    public static final int REG_RXMARK  = 0x0054;
    public static final int REG_FCTRL   = 0x0060;
    public static final int REG_FFMT    = 0x0064;
    public static final int REG_IE      = 0x0070;
    public static final int REG_IP      = 0x0074;

    public SPI(String n) {
        super(n);

        setSlaveCore(new SPISlave());
    }

    class SPISlave extends Controller32 {
        public SPISlave() {
            addReg(REG_SCKDIV,  "SCKDIV", 0x00000000);
            /*
            addReg(REG_SCKMODE, "SCKMODE", 0x00000000);
            */
            addReg(REG_CSID,    "CSID", 0x00000000);
            addReg(REG_CSDEF,   "CSDEF", 0x00000000);
            addReg(REG_CSMODE,  "CSMODE", 0x00000000);
            /*
            addReg(REG_DELAY0,  "DELAY0", 0x00000000);
            addReg(REG_DELAY1,  "DELAY1", 0x00000000);
            */
            addReg(REG_FMT,     "FMT", 0x00000000);
            addReg(REG_TXDATA,  "TXDATA", 0x00000000);
            addReg(REG_RXDATA,  "RXDATA", 0x00000000);
            /*
            addReg(REG_TXMARK,  "TXMARK", 0x00000000);
            addReg(REG_RXMARK,  "RXMARK", 0x00000000);
            */
            addReg(REG_FCTRL,   "FCTRL", 0x00000000);
            addReg(REG_FFMT,    "FFMT", 0x00000000);
            /*
            addReg(REG_IE,      "IE", 0x00000000);
            addReg(REG_IP,      "IP", 0x00000000);
            */
        }

        @Override
        public int readWord(BusMaster64 m, long addr) {
            int regaddr;
            int result;

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_CSID:
                result = 0;
                System.out.printf("SPI CSID: read 0x%x\n", result);
                break;
            case REG_CSDEF:
                result = 0;
                System.out.printf("SPI CSDEF: read 0x%x\n", result);
                break;
            case REG_CSMODE:
                result = 0;
                System.out.printf("SPI CSMODE: read 0x%x\n", result);
                break;
            case REG_TXDATA:
                result = 0;
                System.out.printf("SPI TXDATA: read 0x%x\n", result);
                break;
            case REG_RXDATA:
                result = 0;
                System.out.printf("SPI RXDATA: read 0x%x\n", result);
                break;
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

            switch (regaddr) {
            case REG_SCKDIV:
                System.out.printf("SPI SCKDIV: write 0x%x\n", data);
                break;
            case REG_CSID:
                System.out.printf("SPI CSID: write 0x%x\n", data);
                break;
            case REG_CSDEF:
                System.out.printf("SPI CSDEF: write 0x%x\n", data);
                break;
            case REG_CSMODE:
                System.out.printf("SPI CSMODE: write 0x%x\n", data);
                break;
            case REG_FMT:
                System.out.printf("SPI FMT: write 0x%x\n", data);
                break;
            case REG_TXDATA:
                System.out.printf("SPI TXDATA: write 0x%x\n", data);
                break;
            case REG_RXDATA:
                System.out.printf("SPI RXDATA: write 0x%x\n", data);
                break;
            case REG_FCTRL:
                System.out.printf("SPI FCTRL: write 0x%x\n", data);
                break;
            case REG_FFMT:
                int cmd_en = BitOp.getField32(data, 0, 1);
                int addr_len = BitOp.getField32(data, 1, 3);
                int pad_cnt = BitOp.getField32(data, 4, 4);
                int cmd_proto = BitOp.getField32(data, 8, 2);
                int addr_proto = BitOp.getField32(data, 10, 2);
                int data_proto = BitOp.getField32(data, 12, 2);
                int cmd_code = BitOp.getField32(data, 16, 8);
                int pad_code = BitOp.getField32(data, 24, 8);

                System.out.printf("SPI FFMT: write 0x%x\n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n",
                        data,
                        "cmd_en", cmd_en,
                        "addr_len", addr_len,
                        "pad_cnt", pad_cnt,
                        "cmd_proto", cmd_proto,
                        "addr_proto", addr_proto,
                        "data_proto", data_proto,
                        "cmd_code", cmd_code,
                        "pad_code", pad_code);
                break;
            default:
                super.writeWord(m, regaddr, data);
                break;
            }
        }
    }
}
