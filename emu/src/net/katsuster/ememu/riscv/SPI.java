package net.katsuster.ememu.riscv;

import net.katsuster.ememu.generic.*;

/**
 * Serial Peripheral Interface (SPI)
 *
 * 参考: SiFive FU540-C000 Manual: v1p0
 */
public class SPI extends AbstractParentCore {
    private SPIMaster mc;
    private SPISlave sc;

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

        mc = new SPIMaster();
        setMasterCore(mc);

        sc = new SPISlave(this);
        setSlaveCore(sc);
    }

    class SPIMaster extends MasterCore64 {
        private int select = 0;
        private boolean enableSelect = true;
        private byte[] txFifo;
        private byte[] rxFifo;
        private int lenFifo;
        private int rdTx = 0, wrTx = 0, lenTx = 0;
        private int rdRx = 0, wrRx = 0, lenRx = 0;
        private final Object obj;

        public SPIMaster() {
            lenFifo = 8;
            txFifo = new byte[lenFifo];
            rxFifo = new byte[lenFifo];
            obj = new Object();
        }

        public int getChipSelect() {
            return select;
        }

        public void setChipSelect(int s) {
            select = s;
        }

        public boolean getEnableChipSelect() {
            return enableSelect;
        }

        public void setEnableChipSelect(boolean b) {
            enableSelect = b;
        }

        public boolean isTxFull() {
            return lenTx == lenFifo;
        }

        public boolean isTxEmpty() {
            return lenTx == 0;
        }

        public void pushTx(byte b) {
            synchronized (obj) {
                if (isTxFull()) {
                    throw new IllegalStateException("Tx FIFO is full, cannot push");
                }

                txFifo[wrTx] = b;
                wrTx++;
                wrTx %= lenFifo;
                lenTx++;
                obj.notifyAll();
            }
        }

        public byte popTx() {
            synchronized (obj) {
                if (isTxEmpty()) {
                    throw new IllegalStateException("Tx FIFO is empty, cannot pop");
                }

                byte b = txFifo[rdTx];
                rdTx++;
                rdTx %= lenFifo;
                lenTx--;
                obj.notifyAll();
                return b;
            }
        }

        public boolean isRxFull() {
            return lenRx == lenFifo;
        }

        public boolean isRxEmpty() {
            return lenRx == 0;
        }

        public void pushRx(byte b) {
            synchronized (obj) {
                if (isRxFull()) {
                    throw new IllegalStateException("Rx FIFO is full, cannot push");
                }

                rxFifo[wrRx] = b;
                wrRx++;
                wrRx %= lenFifo;
                lenRx++;
                obj.notifyAll();
            }
        }

        public byte popRx() {
            synchronized (obj) {
                if (isRxEmpty()) {
                    throw new IllegalStateException("Rx FIFO is empty, cannot pop");
                }

                byte b = rxFifo[rdRx];
                rdRx++;
                rdRx %= lenFifo;
                lenRx--;
                obj.notifyAll();
                return b;
            }
        }

        private void process() throws InterruptedException {
            synchronized (obj) {
                while (isTxEmpty()) {
                    obj.wait();
                    if (shouldHalt()) {
                        return;
                    }
                }

                if (!enableSelect) {
                    //not chip select
                    popTx();
                    pushRx((byte)0xff);
                    return;
                }

                SlaveCore64 sc = getSlaveBus().getSlaveCore(select, select);
                if (sc == null) {
                    //not connected
                    popTx();
                    pushRx((byte)0xff);
                } else {
                    write8(select, popTx());
                    byte b = read8(select);
                    pushRx(b);
                }
            }
        }

        public final Object getSync() {
            return obj;
        }

        @Override
        public void run() {
            while (!shouldHalt()) {
                try {
                    process();
                } catch (InterruptedException e) {
                    //ignored
                }
            }
        }
    }

    class SPISlave extends Controller32 {
        private SPI parent;

        public SPISlave(SPI p) {
            parent = p;

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
            int result = 0;

            regaddr = (int) (addr & BitOp.getAddressMask(LEN_WORD_BITS));

            switch (regaddr) {
            case REG_CSID:
                result = mc.getChipSelect();
                System.out.printf("SPI(%s) CSID: read 0x%x\n", parent.getName(), result);
                break;
            case REG_CSDEF:
                System.out.printf("SPI(%s) CSDEF: read 0x%x\n", parent.getName(), result);
                break;
            case REG_CSMODE:
                System.out.printf("SPI(%s) CSMODE: read 0x%x\n", parent.getName(), result);
                break;
            case REG_TXDATA:
                synchronized (mc.getSync()) {
                    if (mc.isTxFull()) {
                        result |= 0x80000000;
                    }
                }
                //System.out.printf("SPI(%s) TXDATA: read 0x%x\n", parent.getName(), result);
                break;
            case REG_RXDATA:
                synchronized (mc.getSync()) {
                    if (mc.isRxEmpty()) {
                        result |= 0x80000000;
                    } else {
                        result |= mc.popRx() & 0xff;
                        System.out.printf("SPI(%s) RXDATA: read 0x%x\n", parent.getName(), result);
                    }
                }
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
                System.out.printf("SPI(%s) SCKDIV: write 0x%x\n", parent.getName(), data);
                break;
            case REG_CSID:
                mc.setChipSelect(data);
                System.out.printf("SPI(%s) CSID: write 0x%x\n", parent.getName(), data);
                break;
            case REG_CSDEF:
                System.out.printf("SPI(%s) CSDEF: write 0x%x\n", parent.getName(), data);
                break;
            case REG_CSMODE:
                int mode = BitOp.getField32(data, 0, 2);

                //Chip select mode
                switch (mode) {
                case 0:
                case 2:
                    //AUTO, HOLD
                    mc.setEnableChipSelect(true);
                    break;
                case 3:
                    //OFF
                    mc.setEnableChipSelect(false);
                    break;
                }

                System.out.printf("SPI(%s) CSMODE: write 0x%x\n" +
                                "  %s: 0x%x, \n",
                        parent.getName(), data,
                        "mode", mode);

                super.writeWord(m, regaddr, data);

                break;
            case REG_FMT:
                System.out.printf("SPI(%s) FMT: write 0x%x\n", parent.getName(), data);
                break;
            case REG_TXDATA:
                synchronized (mc.getSync()) {
                    if (!mc.isTxFull()) {
                        mc.pushTx((byte)data);
                        System.out.printf("SPI(%s) TXDATA: write 0x%x\n", parent.getName(), data);
                    }
                }
                break;
            case REG_RXDATA:
                System.out.printf("SPI(%s) RXDATA: write 0x%x\n", parent.getName(), data);
                break;
            case REG_FCTRL:
                System.out.printf("SPI(%s) FCTRL: write 0x%x\n", parent.getName(), data);
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

                System.out.printf("SPI(%s) FFMT: write 0x%x\n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n",
                        parent.getName(), data,
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
