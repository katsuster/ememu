package net.katsuster.ememu.generic;

/**
 * 64 ビットアドレス、16ビットデータ Flush ROM
 *
 * Refer: Intel Strata Flash Synchronous Memory
 *   28F256K18:
 *     64K Word (128KB, 1Mbit) Block,
 *     256Blocks (256Mbits)
 *
 * @author katsuhiro
 */
public class Flush16 extends SlaveCore {
    //データ幅（バイト単位）
    public static final int LEN_WORD = 2;
    //データ幅（ビット単位）
    public static final int LEN_WORD_BITS = LEN_WORD * 8;
    //ブロックサイズ
    public static final int LEN_BLOCK = 128 * 1024;

    private short[] wordsArray;
    private int lenArray;
    private short[] wordsCFI;
    private int lenCFI;

    private short[] words;
    private int len;
    private StateMachine state;
    /**
     * Status Register
     *
     * <ul>
     * <li>7: RDY: 0:Busy, 1:Ready</li>
     * <li>6: ES : 0:Not erase suspend, 1:Erase suspend</li>
     * <li>5: EE : 0:Erase successful, 1:Erase error</li>
     * <li>4: PE : 0:Program successful, 1:Program fail</li>
     * <li>3: VE : 0:Vpp is provided, 1:Vpp is low operation aborted</li>
     * <li>2: PS : 0:Not program suspend, 1:Program suspend</li>
     * <li>1: LE : 0:Block not locked, 1:Block locked and operation aborted</li>
     * <li>0: PS : 0:Buffered-EFP complete, 1:Buffered-EFP in progress</li>
     * </ul>
     */
    private int statusReg;

    //Definitions of Write State Machine

    private final StateMachine STATE_READ_ARRAY = new SMReadArray();
    private final StateMachine STATE_READ_CFI = new SMReadCFI();
    private final StateMachine STATE_READ_STATUS = new SMReadStatus();
    private final StateMachine STATE_WRITE_BUFFER = new SMWriteBuffer();
    private final StateMachine STATE_ERASE_BLOCK = new SMEraseBlock();

    protected interface StateMachine {
        public abstract void setup();
        public abstract short readWord(long addr, int wordAddr);
        public abstract void writeWord(long addr, int wordAddr, short data);
    }

    protected abstract class AbstractStateMachine implements StateMachine {
        public void setup() {
            //Do nothing
        }

        public short readWord(long addr, int wordAddr) {
            return 0;
        }

        public void writeWord(long addr, int wordAddr, short data) {
            //ignored
        }

        public void acceptCommand(long addr, int wordAddr, short data) {
            int cmd = data & 0xff;

            switch (cmd) {
            case 0xf0:
            case 0xff:
                //Read Array
                setStateMachine(STATE_READ_ARRAY);
                break;
            case 0x90:
                //Read Identifier
                //Fall through
            case 0x98:
                //Read Query (Flush16)
                setStateMachine(STATE_READ_CFI);
                break;
            case 0x70:
                //Read Status
                setStateMachine(STATE_READ_STATUS);
                break;
            case 0x50:
                //Clear Status
                statusReg = 0x80;
                setStateMachine(STATE_READ_ARRAY);
                break;
            case 0xe8:
                //Write to Buffer
                setStateMachine(STATE_WRITE_BUFFER);
                break;
            case 0x20:
                //Block Erase Setup
                setStateMachine(STATE_ERASE_BLOCK);
                break;
            default:
                System.err.println(String.format(
                        "Unknown write addr:0x%08x data:0x%04x in %s mode.",
                        addr, data, state.getClass().getName()));
            }
        }
    }

    protected class SMReadArray extends AbstractStateMachine {
        public void setup() {
            words = wordsArray;
            len = lenArray;
        }

        public short readWord(long addr, int wordAddr) {
            return wordsArray[wordAddr];
        }

        public void writeWord(long addr, int wordAddr, short data) {
            acceptCommand(addr, wordAddr, data);
        }
    }

    protected class SMReadCFI extends AbstractStateMachine {
        public void setup() {
            words = wordsCFI;
            len = lenCFI;
        }

        public short readWord(long addr, int wordAddr) {
            return wordsCFI[wordAddr];
        }

        public void writeWord(long addr, int wordAddr, short data) {
            acceptCommand(addr, wordAddr, data);
        }
    }

    protected class SMReadStatus extends AbstractStateMachine {
        public short readWord(long addr, int wordAddr) {
            return (short)statusReg;
        }

        public void writeWord(long addr, int wordAddr, short data) {
            acceptCommand(addr, wordAddr, data);
        }
    }

    protected class SMWriteBuffer extends AbstractStateMachine {
        public static final int STATE_SETUP = 0;
        public static final int STATE_WORD_COUNT = 1;
        public static final int STATE_WRITE_START = 2;
        public static final int STATE_WRITE_DATA = 3;
        public static final int STATE_WRITE_CONFIRM = 4;
        public static final int STATE_RETURN_STATUS = 5;

        private short[] buf = new short[0xff];
        private int innerState;
        private int countMax;
        private int countNow;
        private int start;

        public void setup() {
            innerState = STATE_WORD_COUNT;

            //Valid
            statusReg = BitOp.setBit32(statusReg, 7, true);
        }

        public short readWord(long addr, int wordAddr) {
            short result;

            switch (innerState) {
            case STATE_RETURN_STATUS:
                setStateMachine(STATE_READ_STATUS);
                result = (short)statusReg;
                break;
            default:
                result = (short)statusReg;
                break;
            }

            return result;
        }

        public void writeWord(long addr, int wordAddr, short data) {
            int cmd = data & 0xff;

            switch (innerState) {
            case STATE_WORD_COUNT:
                countMax = data & 0xff;
                countMax += 1;
                countNow = 0;
                innerState = STATE_WRITE_START;
                break;
            case STATE_WRITE_START:
                start = (int)addr;
                buf[countNow] = data;
                countNow += 1;
                innerState = STATE_WRITE_DATA;
                break;
            case STATE_WRITE_DATA:
                if (countNow < countMax) {
                    buf[countNow] = data;
                    countNow += 1;
                }
                if (countNow >= countMax) {
                    innerState = STATE_WRITE_CONFIRM;
                }
                break;
            case STATE_WRITE_CONFIRM:
                switch (cmd) {
                case 0xd0:
                    //Write Confirm
                    writeBuffer(start, countMax, buf);
                    //Ready
                    statusReg = BitOp.setBit32(statusReg, 7, true);
                    innerState = STATE_RETURN_STATUS;
                    break;
                default:
                    System.err.println(String.format(
                            "Unknown write confirm addr:0x%08x data:0x%04x in %s mode.",
                            addr, data, state.getClass().getName()));
                    break;
                }
            }
        }
    }

    protected class SMEraseBlock extends AbstractStateMachine {
        public void setup() {
            //Busy
            statusReg = BitOp.setBit32(statusReg, 7, false);
        }

        public short readWord(long addr, int wordAddr) {
            return (short)statusReg;
        }

        public void writeWord(long addr, int wordAddr, short data) {
            int cmd = data & 0xff;

            switch (cmd) {
            case 0xd0:
                //Block Erase Confirm
                //Erase
                eraseBlock(addr);
                //Ready
                statusReg |= 0x80;
                setStateMachine(STATE_READ_ARRAY);
                break;
            default:
                acceptCommand(addr, wordAddr, data);
                break;
            }
        }
    }

    /**
     * Flush ROM を作成します。
     *
     * @param size 無視されます
     */
    public Flush16(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size is negative.");
        }

        setupCFI();
        lenArray = 256 * LEN_BLOCK;
        wordsArray = new short[lenArray / LEN_WORD];

        //Read array state after reset
        words = wordsArray;
        len = lenArray;
        state = STATE_READ_ARRAY;
        statusReg = 0x80;
    }

    /**
     * メモリのサイズを取得します。
     *
     * @return メモリのサイズ（バイト単位）
     */
    public int getSize() {
        return len;
    }

    /**
     * 指定したアドレスが正当かどうか検査します。
     *
     * 下記の条件を満たすアドレスを正当と見なします。
     *
     * アドレスのアライメントを満たしていること、
     * Integer.MAX_VALUE を超えないこと。
     *
     * @param addr アドレス
     */
    protected void checkAddress(long addr) {
        if (addr % LEN_WORD != 0) {
            throw new IllegalArgumentException(String.format(
                    "addr(0x%08x) is not aligned %d.", addr, LEN_WORD));
        }
        if (addr / LEN_WORD > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(String.format(
                    "addr(0x%08x) is too large.", addr));
        }
    }

    @Override
    public boolean tryRead(long addr, int len) {
        return tryAccess(addr, len);
    }

    @Override
    public boolean tryWrite(long addr, int len) {
        return tryAccess(addr, len);
    }

    /**
     * 指定されたアドレスからの読み書きが可能かどうかを判定します。
     *
     * @param addr アドレス
     * @param len  データのサイズ
     * @return 読み書きが可能な場合は true、不可能な場合は false
     */
    public boolean tryAccess(long addr, int len) {
        int wordAddr;

        wordAddr = (int)(addr / LEN_WORD);

        return words.length > wordAddr;
    }

    @Override
    public byte read8(long addr) {
        throw new IllegalArgumentException("Cannot read 8bit.");
    }

    @Override
    public short read16(long addr) {
        return readWord(addr);
    }

    @Override
    public int read32(long addr) {
        throw new IllegalArgumentException("Cannot read 32bit.");
    }

    @Override
    public long read64(long addr) {
        throw new IllegalArgumentException("Cannot read 64bit.");
    }

    @Override
    public void write8(long addr, byte data) {
        throw new IllegalArgumentException("Cannot write 8bit.");
    }

    @Override
    public void write16(long addr, short data) {
        writeWord(addr, data);
    }

    @Override
    public void write32(long addr, int data) {
        throw new IllegalArgumentException("Cannot write 32bit.");
    }

    @Override
    public void write64(long addr, long data) {
        throw new IllegalArgumentException("Cannot write 64bit.");
    }

    public short readWord(long addr) {
        int wordAddr;
        short result;

        addr &= getAddressMask(LEN_WORD_BITS);
        checkAddress(addr);
        wordAddr = (int) (addr / LEN_WORD);

        return state.readWord(addr, wordAddr);
    }

    public void writeWord(long addr, short data) {
        int wordAddr;

        addr &= getAddressMask(LEN_WORD_BITS);
        checkAddress(addr);
        wordAddr = (int)(addr / LEN_WORD);

        state.writeWord(addr, wordAddr, data);
    }

    protected StateMachine getStateMachine() {
        return state;
    }

    protected void setStateMachine(StateMachine s) {
        s.setup();
        state = s;
    }

    protected int getBlockIndex(long addr) {
        return (int)(addr / LEN_BLOCK);
    }

    protected int getBlockAddress(long addr) {
        return (int)(addr % LEN_BLOCK);
    }

    protected void setupCFI() {
        short addrP = 0x31;
        short addrA = 0x00;

        //Flush16
        lenCFI = 0xff * 2;
        wordsCFI = new short[lenCFI / LEN_WORD];

        //Offset 00h: Manufacturer code: 0089 (Intel)
        wordsCFI[0x00] = (short)0x0089;
        wordsCFI[0x01] = (short)0x8803;

        //Offset 10h: Flush16 Query Identification String
        //Query-unique string: 'QRY'
        wordsCFI[0x10] = 0x51;
        wordsCFI[0x11] = 0x52;
        wordsCFI[0x12] = 0x59;
        //Primary Algorithm Command Set and Control Interface ID
        wordsCFI[0x13] = 0x01;
        wordsCFI[0x14] = 0x00;
        //Address for Primary Algorithm extended Query, Address P
        wordsCFI[0x15] = addrP;
        wordsCFI[0x16] = 0x00;
        //Alternative Algorithm Command Set and Control Interface ID
        wordsCFI[0x17] = 0x00;
        wordsCFI[0x18] = 0x00;
        //Address for Alternative Algorithm extended Query, Address A
        wordsCFI[0x19] = addrA;
        wordsCFI[0x1a] = 0x00;

        //Offset 1Bh: System Interface Information
        //Vcc logic program/erase voltage: min 2.7V, max 3.6V
        wordsCFI[0x1b] = 0x27;
        wordsCFI[0x1c] = 0x36;
        //Vpp program/erase voltage: min 0.0V, max 0.0V
        wordsCFI[0x1d] = 0x00;
        wordsCFI[0x1e] = 0x00;
        //Timeout program(typ): single word: 2^8 = 256us, multi word: 2^9 = 512us
        wordsCFI[0x1f] = 0x08;
        wordsCFI[0x20] = 0x09;
        //Timeout erase(typ): block: 2^10 = 1024ms, full chip: N/A
        wordsCFI[0x21] = 0x0a;
        wordsCFI[0x22] = 0x00;
        //Timeout program(max): single word: 2^1 = 2times, multi word: 2^1 = 2times
        wordsCFI[0x23] = 0x01;
        wordsCFI[0x24] = 0x01;
        //Timeout erase(max): block: 2^2 = 4times, full chip: N/A
        wordsCFI[0x25] = 0x02;
        wordsCFI[0x26] = 0x00;

        //Offset 27h: Device Geometry Definition
        //Number of bytes: 2^25 = 33554432bytes = 256Mbit
        wordsCFI[0x27] = 0x19;
        //Flash Device Interface Code description: x16
        wordsCFI[0x28] = 0x01;
        wordsCFI[0x29] = 0x00;
        //Maximum number of bytes in multi-byte program: 2^6 = 64
        wordsCFI[0x2a] = 0x06;
        wordsCFI[0x2b] = 0x00;
        //Number of Erase Block Regions within device: 1
        wordsCFI[0x2c] = 0x01;
        //Erase Block Region Information
        //Number of Erase Blocks: 255 + 1 = 256blocks
        wordsCFI[0x2d] = 0xff;
        wordsCFI[0x2e] = 0x00;
        //Erase Block size (times 256): 512 * 256 = 128KB
        wordsCFI[0x2f] = 0x00;
        wordsCFI[0x30] = 0x02;

        //Offset P: Primary Algorithm-specific Extended Query
        //Query-unique string: 'PRI'
        wordsCFI[addrP + 0x00] = 0x50;
        wordsCFI[addrP + 0x01] = 0x52;
        wordsCFI[addrP + 0x02] = 0x49;
        //Major, Minor version: '11'
        wordsCFI[addrP + 0x03] = 0x31;
        wordsCFI[addrP + 0x04] = 0x31;

        //Offset A: Alternative Algorithm-specific Extended Query
        //Nothing
    }

    /**
     * バッファを書き込みます。
     *
     * @param addr  バイトアドレス
     * @param words 書き込むワード数
     * @param buf   書き込むワードのバッファ
     */
    public void writeBuffer(long addr, int words, short[] buf) {
        int start = (int)addr / LEN_WORD;

        for (int i = 0; i < words; i++) {
            wordsArray[start + i] = buf[i];
        }
    }

    /**
     * 指定したアドレスを含むブロックを消去します。
     *
     * @param addr バイトアドレス
     */
    public void eraseBlock(long addr) {
        int block = getBlockIndex(addr);
        int start = block * (LEN_BLOCK / LEN_WORD);

        for (int i = 0; i < LEN_BLOCK / LEN_WORD; i++) {
            wordsArray[start + i] = (short)0xffff;
        }

        System.out.printf("flush16: erase: 0x%08x - 0x%08x\n", start * LEN_WORD, start * LEN_WORD + LEN_BLOCK);
    }

    @Override
    public void run() {
        //do nothing
    }
}
