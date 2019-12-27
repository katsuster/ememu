package net.katsuster.ememu.generic;

/**
 * SD Card
 *
 * 参考: SD Specifications Part 1 Physical Layer
 * Simplified Specification Version 6.00
 * August 29, 2018
 */
public class SDCard extends AbstractParentCore {
    public static final int REG_IO  = 0x00;
    private SDCardState st;
    private int blockAddr;
    private int blockLen;

    public SDCard(String n) {
        super(n);

        setSlaveCore(new SDCardSlave());

        st = new CmdState();
    }

    class SDCardState {
        public SDCardState() {

        }

        public int readData() {
            return 0;
        }

        public void writeData(int b) {

        }
    }

    class CmdStateCommon extends SDCardState {
        protected int cmd = 0;
        protected int arg = 0;
        protected int crc = 0;
        protected int resp = 0xff;
        protected int pos = 0;

        public CmdStateCommon() {

        }

        public void reset() {
            cmd = 0;
            arg = 0;
            crc = 0;
            resp = 0xff;
            pos = 0;
        }

        public void recvCommand() {
            int[] dat;

            //Do not support
            dat = new int[1];
            dat[0] = 0x3;
            st = new RespState(dat, new CmdState());
        }

        @Override
        public int readData() {
            return resp;
        }

        @Override
        public void writeData(int b) {
            if (pos == 0 && b == 0xff) {
                resp = 0xff;
                return;
            }

            resp = 0xff;
            switch (pos) {
            case 0:
                cmd = b & 0x3f;
                break;
            case 1:
                arg |= (b & 0xff) << 24;
                break;
            case 2:
                arg |= (b & 0xff) << 16;
                break;
            case 3:
                arg |= (b & 0xff) << 8;
                break;
            case 4:
                arg |= b & 0xff;
                break;
            case 5:
                crc = b >> 1;
                break;
            case 7:
                recvCommand();
                break;
            }
            pos++;
        }
    }

    class CmdState extends CmdStateCommon {
        public CmdState() {

        }

        @Override
        public void recvCommand() {
            int[] dat;

            switch (cmd) {
            case 0x00:
                //CMD 0: GO_IDLE_STATE
                dat = new int[1];
                dat[0] = 0x1;
                st = new RespState(dat, new CmdState());
                break;
            case 0x08:
                //CMD 8: SEND_EXT_CSD
                dat = new int[5];
                dat[0] = 0x01;
                dat[1] = 0x00;
                dat[2] = 0x00;
                //voltage accepted: 2.6-3.7V
                dat[3] = 0x01;
                //echo back
                dat[4] = arg & 0xff;
                st = new RespState(dat, new CmdState());
                break;
            case 0x10:
                //CMD 16: SET_BLOCKLEN
                System.out.printf("CMD16: len 0x%x\n", arg);

                blockLen = arg;

                dat = new int[1];
                dat[0] = 0x00;
                st = new RespState(dat, new CmdState());
                break;
            case 0x12:
                //CMD 18: READ_MULTIPLE_BLOCK
                System.out.printf("CMD18: addr 0x%x\n", arg);

                blockAddr = arg;

                dat = new int[1];
                dat[0] = 0x01;
                st = new RespState(dat, new CmdState());
                break;
            case 0x37:
                //CMD 55: APP_CMD
                dat = new int[1];
                dat[0] = 0x1;
                st = new RespState(dat, new AcmdState());
                break;
            default:
                //Do not support
                super.recvCommand();
                break;
            }
        }
    }

    class AcmdState extends CmdStateCommon {
        public AcmdState() {

        }

        @Override
        public void recvCommand() {
            int[] dat;

            switch (cmd) {
            case 0x29:
                //ACMD 41: SD_SEND_OP_COND
                int hsc = BitOp.getField32(arg, 30, 1);
                int xpc = BitOp.getField32(arg, 28, 1);
                int s18r = BitOp.getField32(arg, 24, 1);
                int ocr = BitOp.getField32(arg, 8, 16);

                System.out.printf("ACMD41: arg 0x%x\n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n" +
                                "  %s: 0x%x, \n",
                        arg,
                        "hsc", hsc,
                        "xpc", xpc,
                        "s18r", s18r,
                        "ocr", ocr);

                dat = new int[1];
                dat[0] = 0x00;
                st = new RespState(dat, new CmdState());
                break;
            default:
                //Do not support
                super.recvCommand();
                break;
            }
        }
    }

    class RespState extends SDCardState {
        private int[] resp;
        private SDCardState nextState;
        private int pos;

        public RespState(int[] r, SDCardState n) {
            resp = r;
            nextState = n;
            pos = 0;
        }

        @Override
        public int readData() {
            if (pos >= resp.length) {
                st = nextState;
                return 0xff;
            } else {
                int result = resp[pos];
                pos++;
                return result;
            }
        }

        @Override
        public void writeData(int b) {

        }
    }

    class SDCardSlave extends Controller32 {
        public SDCardSlave() {
            addReg(REG_IO,  "IO",  0x00000000);
        }

        @Override
        public int readWord(BusMaster64 m, long addr) {
            int result = st.readData() & 0xff;

            return result;
        }

        @Override
        public void writeWord(BusMaster64 m, long addr, int data) {
            st.writeData(data & 0xff);
        }
    }
}
