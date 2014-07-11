package net.katsuster.semu;

import java.io.*;

public class Main {
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

        ARM9 cpu = new ARM9();
        SysBaseboard sys = new SysBaseboard();
        UART uart0 = new UART();
        RAM ramMain = new RAM(16 * 1024 * 1024); //64MB
        Bus64 bus = new Bus64();
        int addrAtags = 0x83ffff00;

        cpu.setSlaveBus(bus);

        //RAM Image(tentative)
        //  0x10000000 - 0x13ffffff: CS5
        //    0x10000000 - 0x10000fff: System Registers
        //    0x101f1000 - 0x101f1fff: UART0
        //    0x101f2000 - 0x101f2fff: UART1
        //    0x101f3000 - 0x101f3fff: UART2
        //  0x80000000 - 0x82ffffff: Main
        //    0x80000000 - 0x80007fff: Linux pagetable
        //    0x80008000 - 0x804fffff: Linux Image
        //    0x80ffff00 - 0x83ffffff: ATAG_XXX
        bus.addSlaveCore(sys, 0x10000000L, 0x10001000L);
        bus.addSlaveCore(uart0, 0x101f1000L, 0x101f2000L);
        bus.addSlaveCore(ramMain, 0x80000000L, 0x84000000L);

        //reset
        cpu.setDisasmMode(false);
        cpu.setPrintingDisasm(false);
        cpu.setPrintingRegs(false);
        cpu.doExceptionReset("Init.");

        //tentative boot loader for Linux
        //load Image file
        loadFile(filename, cpu, 0x80008000);

        //r0: 0
        cpu.setReg(0, 0);

        //r1: machine type
        //ARM-Versatile PB
        cpu.setReg(1, 0x00000183);
        //ARM-Versatile AB
        //cpu.setReg(1, 0x0000025e);

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
            cpu.write32(addrAtags + 0x00, 0x00000004);
            cpu.write32(addrAtags + 0x04, 0x54410002);
            cpu.write32(addrAtags + 0x08, 0x04000000);
            cpu.write32(addrAtags + 0x0c, 0x80000000);
            addrAtags += 0x10;

            //ATAG_REVISION, size, tag, rev
            cpu.write32(addrAtags + 0x00, 0x00000003);
            cpu.write32(addrAtags + 0x04, 0x54410007);
            //ARM-Versatile PB
            cpu.write32(addrAtags + 0x08, 0x00000183);
            //ARM-Versatile AB
            //cpu.write32(addrAtags + 0x08, 0x0000025e);
            addrAtags += 0x0c;

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
