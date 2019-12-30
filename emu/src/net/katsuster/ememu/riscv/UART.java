package net.katsuster.ememu.riscv;

import java.io.*;

import net.katsuster.ememu.generic.BitOp;
import net.katsuster.ememu.generic.Controller32;
import net.katsuster.ememu.generic.core.AbstractParentCore;
import net.katsuster.ememu.generic.bus.BusMaster64;

/**
 * Universal Asynchronous Receiver/Transmitter (UART)
 *
 * 参考: SiFive FU540-C000 Manual: v1p0
 */
public class UART extends AbstractParentCore {
    private InputStream strInput;
    private OutputStream strOutput;
    private StringBuffer bufInput;

    public static final int REG_TXDATA = 0x0000;
    public static final int REG_RXDATA = 0x0004;
    public static final int REG_TXCTRL = 0x0008;
    public static final int REG_RXCTRL = 0x000c;
    public static final int REG_IE     = 0x0010;
    public static final int REG_IP     = 0x0014;
    public static final int REG_DIV    = 0x0018;

    /**
     * UART を作成します。
     *
     * @param n    コアの名前
     * @param istr UART の入力を得るためのストリーム
     * @param ostr UART に出力された文字を印字するためのストリーム
     */
    public UART(String n, InputStream istr, OutputStream ostr) {
        super(n);

        strInput = istr;
        strOutput = ostr;
        bufInput = new StringBuffer();

        setSlaveCore(new UARTSlave());
    }

    class UARTSlave extends Controller32 {
        public UARTSlave() {
            addReg(REG_TXDATA, "TXDATA", 0x00000000);
            /*
            addReg(REG_RXDATA, "RXDATA", 0x00000000);
            */
            addReg(REG_TXCTRL, "TXCTRL", 0x00000000);
            /*
            addReg(REG_RXCTRL, "RXCTRL", 0x00000000);
            addReg(REG_IE,     "IE", 0x00000000);
            addReg(REG_IP,     "IP", 0x00000000);
            */
            addReg(REG_DIV,     "DIV", 0x00000000);
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

            switch (regaddr) {
            case REG_TXDATA:
                char ascii = (char)(data & 0xff);

                if (ascii == 0x00) {
                    //FIXME: IntelliJ の Console でコピーできないため無視
                    break;
                }
                if (strOutput != null) {
                    try {
                        strOutput.write(ascii);
                        strOutput.flush();
                    } catch (IOException ex) {
                        //ignore
                    }
                }

                break;
            case REG_TXCTRL:
                System.out.printf("UART TXCTRL: 0x%x\n", data);
                break;
            case REG_DIV:
                System.out.printf("UART Divisor: %d\n", data);
                break;
            default:
                super.writeWord(m, regaddr, data);
                break;
            }
        }

    }
}
