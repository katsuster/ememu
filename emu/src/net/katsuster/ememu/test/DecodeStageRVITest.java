package net.katsuster.ememu.test;

import java.util.*;

import net.katsuster.ememu.riscv.core.*;
import org.junit.*;


public class DecodeStageRVITest {
    Random rand = new Random();

    @Test
    public void testDecodeStageRVIOnce() throws Exception {
        String msg = "";
        RV64 c = new RV64();
        DecodeStageRVI dec = new DecodeStageRVI(c);

        testRV32I(dec, msg, OpIndex.INS_RV32I_ADD, "0000000_xxxxx_xxxxx_000_xxxxx_0110011");
        testRV32I(dec, msg, OpIndex.INS_RV32I_ADDI, "xxxxxxxxxxxx_xxxxx_000_xxxxx_0010011");
        testRV32I(dec, msg, OpIndex.INS_RV64I_ADDIW, "xxxxxxxxxxxx_xxxxx_000_xxxxx_0011011");
        //testRV32I(dec, msg, OpIndex.INS_RV64I_ADDW, "0000000_xxxxx_xxxxx_000_xxxxx_0111011");
        testRV32I(dec, msg, OpIndex.INS_RV32I_AND, "0000000_xxxxx_xxxxx_111_xxxxx_0110011");
        testRV32I(dec, msg, OpIndex.INS_RV32I_ADDI, "0000000_xxxxx_xxxxx_000_xxxxx_0010011");
        testRV32I(dec, msg, OpIndex.INS_RV32I_AUIPC, "xxxxxxxxxxxxxxxxxxxx_xxxxx_0010111");
        testRV32I(dec, msg, OpIndex.INS_RV32I_BEQ, "xxxxxxx_xxxxx_xxxxx_000_xxxxx_1100011");
        testRV32I(dec, msg, OpIndex.INS_RV32I_BGE, "xxxxxxx_xxxxx_xxxxx_101_xxxxx_1100011");
        testRV32I(dec, msg, OpIndex.INS_RV32I_BGEU, "xxxxxxx_xxxxx_xxxxx_111_xxxxx_1100011");
        testRV32I(dec, msg, OpIndex.INS_RV32I_BLT, "xxxxxxx_xxxxx_xxxxx_100_xxxxx_1100011");
        testRV32I(dec, msg, OpIndex.INS_RV32I_BLTU, "xxxxxxx_xxxxx_xxxxx_110_xxxxx_1100011");
        testRV32I(dec, msg, OpIndex.INS_RV32I_BNE, "xxxxxxx_xxxxx_xxxxx_001_xxxxx_1100011");
    }

    @Test
    public void testDecodeStageRVI() throws Exception {
        for (int i = 0; i < 1000; i++) {
            testDecodeStageRVIOnce();
        }
    }

    private void testRV32I(DecodeStageRVI decoder, String msg, OpIndex ind, String pat) {
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
}