package net.katsuster.ememu.arm;

import java.io.*;

import net.katsuster.ememu.generic.*;

/**
 * UART
 *
 * 参考: ARM PrimeCell UART (PL011)
 * 日本語版は ARM DDI0183AJ, 英語版は ARM DDI0183G
 *
 * @author katsuhiro
 */
public class UART implements INTSource, BusSlave {
    private INTDestination intDst = new NullINTDestination();
    private UARTSlave slave;

    private int rawInt;
    private int maskInt;

    private InputStream strInput;
    private OutputStream strOutput;
    private StringBuffer bufInput;

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

    //flag bit fields
    public static final int FR_RI   = 8;
    public static final int FR_TXFE = 7;
    public static final int FR_RXFF = 6;
    public static final int FR_TXFF = 5;
    public static final int FR_RXFE = 4;
    public static final int FR_BUSY = 3;
    public static final int FR_DCD  = 2;
    public static final int FR_DSR  = 1;
    public static final int FR_CTS  = 0;

    //interrupt bit fields
    public static final int INTR_OE  = 10;
    public static final int INTR_BE  = 9;
    public static final int INTR_PE  = 8;
    public static final int INTR_FE  = 7;
    public static final int INTR_RT  = 6;
    public static final int INTR_TX  = 5;
    public static final int INTR_RX  = 4;
    public static final int INTR_DSR = 3;
    public static final int INTR_DCD = 2;
    public static final int INTR_CTS = 1;
    public static final int INTR_RII = 0;

    /**
     * UART を作成します。
     *
     * @param istr UART の入力を得るためのストリーム
     * @param ostr UART に出力された文字を印字するためのストリーム
     */
    public UART(InputStream istr, OutputStream ostr) {
        rawInt = 0;
        maskInt = 0;

        strInput = istr;
        strOutput = ostr;
        bufInput = new StringBuffer();

        slave = new UARTSlave();
    }

    /**
     * UART の各割り込み要因のうち、要因が成立している割り込みを取得します。
     *
     * 状態の各ビットには、割り込みの要因が成立していれば 1、
     * そうでなければ 0 が設定されます。
     *
     * @return 有効な割り込みの状態
     */
    public int getRawInt() {
        return rawInt;
    }

    /**
     * UART の各割り込み要因のうち、要因が成立していて、なおかつ、
     * マスクされていない割り込みを取得します。
     * （マスクが 1 ならば有効、0 ならば無効）
     *
     * 状態の各ビットには、割り込みの要因が成立していれば 1、
     * そうでなければ 0 が設定されます。
     *
     * @return 有効な割り込みの状態
     */
    public int getMaskedInt() {
        return getRawInt() & maskInt;
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
        //送信 FIFO は常に空いていることにする
        rawInt = BitOp.setBit32(rawInt, INTR_TX, true);
        //受信 FIFO
        rawInt = BitOp.setBit32(rawInt, INTR_RX, bufInput.length() > 0);

        return getMaskedInt() != 0;
    }

    @Override
    public String getIRQMessage() {
        return "UART";
    }

    @Override
    public SlaveCore getSlaveCore() {
        return slave;
    }

    class UARTSlave extends Controller32 {
        public UARTSlave() {
            addReg(REG_UARTDR, "UARTDR", 0x00000000);
            addReg(REG_UARTFR, "UARTFR", 0x00000000);

            addReg(REG_UARTIBRD, "UARTIBRD", 0x00000000);
            addReg(REG_UARTFBRD, "UARTFBRD", 0x00000000);
            addReg(REG_UARTLCR_H, "UARTLCR_H", 0x00000000);
            addReg(REG_UARTCR, "UARTCR", 0x00000000);
            addReg(REG_UARTIFLS, "UARTIFLS", 0x00000000);

            addReg(REG_UARTIMSC, "UARTIMSC", 0x00000000);
            addReg(REG_UARTRIS, "UARTRIS", 0x00000000);
            addReg(REG_UARTMIS, "UARTMIS", 0x00000000);
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
        public int readWord(long addr) {
            int regaddr;
            int result;

            regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_UARTDR:
                if (bufInput.length() > 0) {
                    result = bufInput.charAt(0);
                    bufInput.deleteCharAt(0);
                } else {
                    result = 0;
                }
                break;
            case REG_UARTFR:
                result = 0;

                //送信 FIFO は常に空いていることにする
                result = BitOp.setBit32(result, FR_TXFE, true);
                //受信 FIFO はバッファ残量に応じて設定する
                result = BitOp.setBit32(result, FR_RXFE, bufInput.length() == 0);

                break;
            case REG_UARTLCR_H:
                result = super.readWord(regaddr);
                //System.out.printf("UARTLCR_H: read 0x%08x\n", result);
                break;
            case REG_UARTCR:
                result = super.readWord(regaddr);
                //System.out.printf("UARTCR: read 0x%08x\n", result);
                break;
            case REG_UARTIMSC:
                result = maskInt;
                break;
            case REG_UARTRIS:
                result = getRawInt();
                break;
            case REG_UARTMIS:
                result = getMaskedInt();
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

            regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_UARTDR:
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
            case REG_UARTFR:
                //read only, ignored
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
                super.writeWord(regaddr, data);
                break;
            case REG_UARTCR:
                //TODO: Not implemented
                //System.out.printf("UARTCR: 0x%08x\n", data);
                super.writeWord(regaddr, data);
                break;
            case REG_UARTIFLS:
                //TODO: Not implemented
                System.out.printf("UARTIFLS: 0x%08x\n", data);
                break;
            case REG_UARTIMSC:
                maskInt = data;
                break;
            case REG_UARTRIS:
            case REG_UARTMIS:
                //read only, ignored
                break;
            case REG_UARTICR:
                rawInt &= ~data;
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
                super.writeWord(regaddr, data);
                break;
            }
        }

        @Override
        public void run() {
            mainLoop:
            while (!shouldHalt()) {
                try {
                    while (strInput == null) {
                        Thread.sleep(50);
                        if (shouldHalt()) {
                            break mainLoop;
                        }
                    }

                    //NOTE: InputStream をポーリングします。
                    //strInput が System.in かつ read() がブロックしたとき、
                    //他のスレッドからブロッキングをキャンセルする方法がないため、
                    //read() のブロックを避ける必要があるためです。
                    //FIXME: この実装はマルチスレッドセーフではありません。
                    //他スレッドが同時に strInput にアクセスする場合、
                    //read() でブロックする可能性があります。
                    while (strInput.available() == 0) {
                        Thread.sleep(50);
                        if (shouldHalt()) {
                            break mainLoop;
                        }
                    }

                    int c = strInput.read();
                    if (c == -1) {
                        //EOF
                        break;
                    }
                    bufInput.append((char)c);

                    intDst.setRaisedInterrupt(true);
                } catch (InterruptedException e) {
                    //ignored
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                    break;
                }
            }
        }
    }

}
