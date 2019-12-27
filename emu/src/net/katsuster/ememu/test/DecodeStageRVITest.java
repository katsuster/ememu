package net.katsuster.ememu.test;

import java.util.*;

import net.katsuster.ememu.riscv.core.*;
import org.junit.*;


public class DecodeStageRVITest {
    Random rand = new Random();

    @Test
    public void testDecodeStageRVI() throws Exception {
        for (int i = 0; i < 1000; i++) {
            testRV32I();
            testRV64I();
            testRV32M();
            testRV64M();
            testRV32A();
        }
    }

    private int createBitPattern32(String pat) {
        int bits = 0;

        for (int i = 0; i < pat.length(); i++) {
            char c = pat.charAt(i);

            switch (c) {
            case '0':
                bits <<= 1;
                break;
            case '1':
                bits <<= 1;
                bits |= 1;
                break;
            case 'x':
            case 'X':
                bits <<= 1;
                if (rand.nextBoolean()) {
                    bits |= 1;
                }
                break;
            }
        }

        return bits;
    }

    private void testInst(DecodeStageRVI decoder, String msg, OpIndex ind, String pat) {
        int bitpat = createBitPattern32(pat);
        String msgAssert = msg + ", pat:" + pat + ", bitpat:" + Integer.toBinaryString(bitpat);

        try {
            Assert.assertEquals(msgAssert, ind,
                    decoder.decode(new InstructionRV32(bitpat)));
        } catch (Exception e) {
            System.err.println("expected: " + ind + ", " + msgAssert);

            throw e;
        }
    }

    private void testRV32I() throws Exception {
        String msg = "RV32I";
        RV64 c = new RV64();
        DecodeStageRVI dec = new DecodeStageRVI(c);

        testInst(dec, msg, OpIndex.INS_RV32I_LUI,   "xxxxxxxxxxxxxxxxx_xxx_xxxxx_0110111");
        testInst(dec, msg, OpIndex.INS_RV32I_AUIPC, "xxxxxxxxxxxxxxxxx_xxx_xxxxx_0010111");
        testInst(dec, msg, OpIndex.INS_RV32I_JAL,   "xxxxxxxxxxxxxxxxx_xxx_xxxxx_1101111");
        testInst(dec, msg, OpIndex.INS_RV32I_JALR,  "xxxxxxxxxxxxxxxxx_000_xxxxx_1100111");

        testInst(dec, msg, OpIndex.INS_RV32I_BEQ,  "xxxxxxx_xxxxx_xxxxx_000_xxxxx_1100011");
        testInst(dec, msg, OpIndex.INS_RV32I_BNE,  "xxxxxxx_xxxxx_xxxxx_001_xxxxx_1100011");
        testInst(dec, msg, OpIndex.INS_RV32I_BLT,  "xxxxxxx_xxxxx_xxxxx_100_xxxxx_1100011");
        testInst(dec, msg, OpIndex.INS_RV32I_BGE,  "xxxxxxx_xxxxx_xxxxx_101_xxxxx_1100011");
        testInst(dec, msg, OpIndex.INS_RV32I_BLTU, "xxxxxxx_xxxxx_xxxxx_110_xxxxx_1100011");
        testInst(dec, msg, OpIndex.INS_RV32I_BGEU, "xxxxxxx_xxxxx_xxxxx_111_xxxxx_1100011");

        testInst(dec, msg, OpIndex.INS_RV32I_LB,  "xxxxxxx_xxxxx_xxxxx_000_xxxxx_0000011");
        testInst(dec, msg, OpIndex.INS_RV32I_LH,  "xxxxxxx_xxxxx_xxxxx_001_xxxxx_0000011");
        testInst(dec, msg, OpIndex.INS_RV32I_LW,  "xxxxxxx_xxxxx_xxxxx_010_xxxxx_0000011");
        testInst(dec, msg, OpIndex.INS_RV32I_LBU, "xxxxxxx_xxxxx_xxxxx_100_xxxxx_0000011");
        testInst(dec, msg, OpIndex.INS_RV32I_LHU, "xxxxxxx_xxxxx_xxxxx_101_xxxxx_0000011");

        testInst(dec, msg, OpIndex.INS_RV32I_SB, "xxxxxxx_xxxxx_xxxxx_000_xxxxx_0100011");
        testInst(dec, msg, OpIndex.INS_RV32I_SH, "xxxxxxx_xxxxx_xxxxx_001_xxxxx_0100011");
        testInst(dec, msg, OpIndex.INS_RV32I_SW, "xxxxxxx_xxxxx_xxxxx_010_xxxxx_0100011");

        testInst(dec, msg, OpIndex.INS_RV32I_ADDI,  "xxxxxxx_xxxxx_xxxxx_000_xxxxx_0010011");
        testInst(dec, msg, OpIndex.INS_RV32I_SLTI,  "xxxxxxx_xxxxx_xxxxx_010_xxxxx_0010011");
        testInst(dec, msg, OpIndex.INS_RV32I_SLTIU, "xxxxxxx_xxxxx_xxxxx_011_xxxxx_0010011");
        testInst(dec, msg, OpIndex.INS_RV32I_XORI,  "xxxxxxx_xxxxx_xxxxx_100_xxxxx_0010011");
        testInst(dec, msg, OpIndex.INS_RV32I_ORI,   "xxxxxxx_xxxxx_xxxxx_110_xxxxx_0010011");
        testInst(dec, msg, OpIndex.INS_RV32I_ANDI,  "xxxxxxx_xxxxx_xxxxx_111_xxxxx_0010011");

        if (c.getRVBits() == 32) {
            testInst(dec, msg, OpIndex.INS_RV32I_SLLI, "0000000_xxxxx_xxxxx_001_xxxxx_0010011");
            testInst(dec, msg, OpIndex.INS_RV32I_SRLI, "0000000_xxxxx_xxxxx_101_xxxxx_0010011");
            testInst(dec, msg, OpIndex.INS_RV32I_SRAI, "0100000_xxxxx_xxxxx_101_xxxxx_0010011");
        }

        testInst(dec, msg, OpIndex.INS_RV32I_ADD,  "0000000_xxxxx_xxxxx_000_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32I_SUB,  "0100000_xxxxx_xxxxx_000_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32I_SLL,  "0000000_xxxxx_xxxxx_001_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32I_SLT,  "0000000_xxxxx_xxxxx_010_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32I_SLTU, "0000000_xxxxx_xxxxx_011_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32I_XOR,  "0000000_xxxxx_xxxxx_100_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32I_SRL,  "0000000_xxxxx_xxxxx_101_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32I_SRA,  "0100000_xxxxx_xxxxx_101_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32I_OR,   "0000000_xxxxx_xxxxx_110_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32I_AND,  "0000000_xxxxx_xxxxx_111_xxxxx_0110011");

        testInst(dec, msg, OpIndex.INS_RV32I_FENCE,   "0000_xxxx_xxxx_00000_000_00000_0001111");
        testInst(dec, msg, OpIndex.INS_RV32I_FENCE_I, "0000_0000_0000_00000_001_00000_0001111");

        testInst(dec, msg, OpIndex.INS_RV32I_ECALL,  "000000000000_00000_000_00000_1110011");
        testInst(dec, msg, OpIndex.INS_RV32I_EBREAK, "000000000001_00000_000_00000_1110011");
        testInst(dec, msg, OpIndex.INS_RV32I_CSRRW,  "xxxxxxxxxxxx_xxxxx_001_xxxxx_1110011");
        testInst(dec, msg, OpIndex.INS_RV32I_CSRRS,  "xxxxxxxxxxxx_xxxxx_010_xxxxx_1110011");
        testInst(dec, msg, OpIndex.INS_RV32I_CSRRC,  "xxxxxxxxxxxx_xxxxx_011_xxxxx_1110011");
        testInst(dec, msg, OpIndex.INS_RV32I_CSRRWI, "xxxxxxxxxxxx_xxxxx_101_xxxxx_1110011");
        testInst(dec, msg, OpIndex.INS_RV32I_CSRRSI, "xxxxxxxxxxxx_xxxxx_110_xxxxx_1110011");
        testInst(dec, msg, OpIndex.INS_RV32I_CSRRCI, "xxxxxxxxxxxx_xxxxx_111_xxxxx_1110011");
    }

    private void testRV64I() throws Exception {
        String msg = "RV64I";
        RV64 c = new RV64();
        DecodeStageRVI dec = new DecodeStageRVI(c);

        testInst(dec, msg, OpIndex.INS_RV64I_LWU,  "xxxxxxxxxxxx_xxxxx_110_xxxxx_0000011");
        testInst(dec, msg, OpIndex.INS_RV64I_LD,   "xxxxxxxxxxxx_xxxxx_011_xxxxx_0000011");
        testInst(dec, msg, OpIndex.INS_RV64I_SD,   "xxxxxxxxxxxx_xxxxx_011_xxxxx_0100011");

        testInst(dec, msg, OpIndex.INS_RV64I_SLLI,  "000000_xxxxxx_xxxxx_001_xxxxx_0010011");
        testInst(dec, msg, OpIndex.INS_RV64I_SRLI,  "000000_xxxxxx_xxxxx_101_xxxxx_0010011");
        testInst(dec, msg, OpIndex.INS_RV64I_SRAI,  "010000_xxxxxx_xxxxx_101_xxxxx_0010011");
        testInst(dec, msg, OpIndex.INS_RV64I_ADDIW, "xxxxxxx_xxxxx_xxxxx_000_xxxxx_0011011");
        testInst(dec, msg, OpIndex.INS_RV64I_SLLIW, "0000000_xxxxx_xxxxx_001_xxxxx_0011011");
        testInst(dec, msg, OpIndex.INS_RV64I_SRLIW, "0000000_xxxxx_xxxxx_101_xxxxx_0011011");
        testInst(dec, msg, OpIndex.INS_RV64I_SRAIW, "0100000_xxxxx_xxxxx_101_xxxxx_0011011");

        testInst(dec, msg, OpIndex.INS_RV64I_ADDW, "0000000_xxxxx_xxxxx_000_xxxxx_0111011");
        testInst(dec, msg, OpIndex.INS_RV64I_SUBW, "0100000_xxxxx_xxxxx_000_xxxxx_0111011");
        testInst(dec, msg, OpIndex.INS_RV64I_SLLW, "0000000_xxxxx_xxxxx_001_xxxxx_0111011");
        testInst(dec, msg, OpIndex.INS_RV64I_SRLW, "0000000_xxxxx_xxxxx_101_xxxxx_0111011");
        testInst(dec, msg, OpIndex.INS_RV64I_SRAW, "0100000_xxxxx_xxxxx_101_xxxxx_0111011");
    }

    private void testRV32M() throws Exception {
        String msg = "RV32M";
        RV64 c = new RV64();
        DecodeStageRVI dec = new DecodeStageRVI(c);

        testInst(dec, msg, OpIndex.INS_RV32M_MUL,    "0000001_xxxxx_xxxxx_000_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32M_MULH,   "0000001_xxxxx_xxxxx_001_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32M_MULHSU, "0000001_xxxxx_xxxxx_010_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32M_MULHU,  "0000001_xxxxx_xxxxx_011_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32M_DIV,    "0000001_xxxxx_xxxxx_100_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32M_DIVU,   "0000001_xxxxx_xxxxx_101_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32M_REM,    "0000001_xxxxx_xxxxx_110_xxxxx_0110011");
        testInst(dec, msg, OpIndex.INS_RV32M_REMU,   "0000001_xxxxx_xxxxx_111_xxxxx_0110011");
    }

    private void testRV64M() throws Exception {
        String msg = "RV64M";
        RV64 c = new RV64();
        DecodeStageRVI dec = new DecodeStageRVI(c);

        testInst(dec, msg, OpIndex.INS_RV64M_MULW,  "0000001_xxxxx_xxxxx_000_xxxxx_0111011");
        testInst(dec, msg, OpIndex.INS_RV64M_DIVW,  "0000001_xxxxx_xxxxx_100_xxxxx_0111011");
        testInst(dec, msg, OpIndex.INS_RV64M_DIVUW, "0000001_xxxxx_xxxxx_101_xxxxx_0111011");
        testInst(dec, msg, OpIndex.INS_RV64M_REMW,  "0000001_xxxxx_xxxxx_110_xxxxx_0111011");
        testInst(dec, msg, OpIndex.INS_RV64M_REMUW, "0000001_xxxxx_xxxxx_111_xxxxx_0111011");
    }

    private void testRV32A() throws Exception {
        String msg = "RV32A";
        RV64 c = new RV64();
        DecodeStageRVI dec = new DecodeStageRVI(c);

        testInst(dec, msg, OpIndex.INS_RV32A_LR_W,       "00010_xx_xxxxx_xxxxx_010_xxxxx_0101111");
        testInst(dec, msg, OpIndex.INS_RV32A_SC_W,       "00011_xx_xxxxx_xxxxx_010_xxxxx_0101111");
        testInst(dec, msg, OpIndex.INS_RV32A_AMOSWAP_W,  "00001_xx_xxxxx_xxxxx_010_xxxxx_0101111");
        testInst(dec, msg, OpIndex.INS_RV32A_AMOADD_W,   "00000_xx_xxxxx_xxxxx_010_xxxxx_0101111");
        testInst(dec, msg, OpIndex.INS_RV32A_AMOXOR_W,   "00100_xx_xxxxx_xxxxx_010_xxxxx_0101111");
        testInst(dec, msg, OpIndex.INS_RV32A_AMOAND_W,   "01100_xx_xxxxx_xxxxx_010_xxxxx_0101111");
        testInst(dec, msg, OpIndex.INS_RV32A_AMOOR_W,    "01000_xx_xxxxx_xxxxx_010_xxxxx_0101111");
        testInst(dec, msg, OpIndex.INS_RV32A_AMOMIN_W,   "10000_xx_xxxxx_xxxxx_010_xxxxx_0101111");
        testInst(dec, msg, OpIndex.INS_RV32A_AMOMAX_W,   "10100_xx_xxxxx_xxxxx_010_xxxxx_0101111");
        testInst(dec, msg, OpIndex.INS_RV32A_AMOMINU_W,  "11000_xx_xxxxx_xxxxx_010_xxxxx_0101111");
        testInst(dec, msg, OpIndex.INS_RV32A_AMOMAXU_W,  "11100_xx_xxxxx_xxxxx_010_xxxxx_0101111");
    }
}