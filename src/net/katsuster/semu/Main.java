package net.katsuster.semu;

import java.io.*;

public class Main {
    public static Word64[] createRAM(long size) {
        int lenWords;
        Word64[] ramWords;

        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Size is too large " +
                    size + ".");
        }

        lenWords = (int)(size / 8);
        ramWords = new Word64[lenWords];
        for (int i = 0; i < ramWords.length; i++) {
            ramWords[i] = new Word64(0);
        }

        return ramWords;
    }

    public static void loadFile(String filename, CPU cpu, int addr) {
        int lenWords;

        try {
            File f = new File(filename);
            DataInputStream s = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(f)));

            if (f.length() > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Size is too large " +
                        f.length() + ".");
            }

            lenWords = (int)f.length();
            for (int i = 0; i < lenWords; i += 8) {
                cpu.write64(addr + i, Long.reverseBytes(s.readLong()));
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String filename = "C:\\Users\\katsuhiro\\Desktop\\Image";

        CPU cpu = new CPU();
        //UART uart = new UART();
        RAM<Word64> ramLow = new RAM<Word64>(createRAM(32 * 1024));
        RAM<Word64> ramMain = new RAM<Word64>(createRAM(16 * 1024 * 1024));
        Bus<Word64> bus = new Bus<Word64>();
        int addrAtags = 0x00004000;

        cpu.setSlaveBus(bus);
        //RAM Image(tentative)
        //  0x00000000 - 0x00007fff: Low mem
        //    0x00000000 - 0x00000fff: vector
        //    0x00004000 - 0x00004fff: ATAG_XXX
        //  0x10000000 - 0x13ffffff: CS5
        //    0x101f1000 - 0x101f1fff: UART
        //  0x80000000 - 0x80ffffff: Main
        //    0x80000000 - 0x80007fff: Linux pagetable
        //    0x80008000 - 0x804fffff: Linux Image
        bus.addSlaveCore(ramLow, 0x00000000L, 0x00008000L);
        //bus.addSlaveCore(uart, 0x101f1000L, 0x101f2000L);
        bus.addSlaveCore(ramMain, 0x80000000L, 0x81000000L);

        //reset
        cpu.setDisasmMode(true);
        cpu.setPrintingDisasm(true);
        cpu.setPrintingRegs(false);
        cpu.doExceptionReset("Init.");

        //tentative boot loader for Linux
        //load Image file
        loadFile(filename, cpu, 0x80008000);

        //r0: 0
        cpu.setReg(0, 0);
        //r1: machine nr
        cpu.setReg(1, 0);
        //r2: atags or dtb pointer.
        cpu.setReg(2, addrAtags);
        {
            //ATAG_CORE, size, tag, flags, pagesize, rootdev
            cpu.write32(addrAtags + 0x00, 0x00000005);
            cpu.write32(addrAtags + 0x04, 0x54410001);
            cpu.write32(addrAtags + 0x08, 0x00000000);
            cpu.write32(addrAtags + 0x0c, 0x00001000);
            cpu.write32(addrAtags + 0x10, 0x00000000);
            addrAtags += 0x14;

            //ATAG_MEM, size, tag, size, start
            cpu.write32(addrAtags + 0x00, 0x00000005);
            cpu.write32(addrAtags + 0x04, 0x54410001);
            cpu.write32(addrAtags + 0x08, 0x01000000);
            cpu.write32(addrAtags + 0x0c, 0x80000000);
            addrAtags += 0x10;

            //ATAG_NONE, size, tag
            cpu.write32(addrAtags + 0x00, 0x00000002);
            cpu.write32(addrAtags + 0x04, 0x00000000);
            addrAtags += 0x08;
        }

        //pc: entry of stext
        cpu.setPC(0x80008000);
        cpu.setJumped(false);

        cpu.run();
    }
}
