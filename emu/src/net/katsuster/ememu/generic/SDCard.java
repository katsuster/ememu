package net.katsuster.ememu.generic;

/**
 * Multimedia Card (MMC)
 *
 * 参考: ?
 */
public class SDCard extends AbstractParentCore {
    public static final int REG_IO  = 0x00;

    public SDCard(String n) {
        super(n);

        setSlaveCore(new MMCSlave());
    }

    class MMCSlave extends Controller32 {
        public MMCSlave() {
            addReg(REG_IO,  "IO",  0x00000000);
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
            default:
                super.writeWord(m, regaddr, data);
                break;
            }
        }
    }
}
