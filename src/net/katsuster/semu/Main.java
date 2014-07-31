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
        String cmdl = "console=ttyS0 lpj=10000 mem=32M debug root=/dev/nfs \0";

        byte[] cmdlb = cmdl.getBytes();
        byte[] cmdline = new byte[(cmdlb.length + 3) & ~0x3];
        System.arraycopy(cmdlb, 0, cmdline, 0, cmdlb.length);

        ARMv5 cpu = new ARMv5();
        Bus64 bus = new Bus64();
        SysBaseboard sysBoard = new SysBaseboard();
        SecondaryINTC intc2nd = new SecondaryINTC();
        SSMC ssmc = new SSMC();
        MPMC mpmc = new MPMC();
        LCDC clcdc = new LCDC();
        DMAC dmac = new DMAC();
        PrimaryINTC intc1st = new PrimaryINTC();
        SysController sysCtrl = new SysController();
        Watchdog watchdog = new Watchdog();
        DualTimer timer0_1 = new DualTimer();
        DualTimer timer2_3 = new DualTimer();
        GPIO gpio0 = new GPIO();
        GPIO gpio1 = new GPIO();
        GPIO gpio2 = new GPIO();
        GPIO gpio3 = new GPIO();
        RTC rtc = new RTC();
        SCard scard = new SCard();
        UART uart0 = new UART();
        UART uart1 = new UART();
        UART uart2 = new UART();
        SSP ssp = new SSP();
        RAM ramMain = new RAM(8 * 1024 * 1024); //32MB
        int addrAtags = 0x81fff000;

        cpu.setSlaveBus(bus);
        cpu.setINTCForIRQ(intc1st.getSubINTCForIRQ());
        cpu.setINTCForFIQ(intc1st.getSubINTCForFIQ());

        //RAM Image(tentative)
        //  0x10000000 - 0x13ffffff: CS5
        //    0x10000000 - 0x10000fff: System Registers
        //    0x10003000 - 0x10003fff: Secondary Interrupt Controller
        //    0x10100000 - 0x1010ffff: Synchronous Static Memory Controller (PL093)
        //    0x10110000 - 0x1011ffff: MultiPort Memory Controller (GX175)
        //    0x10120000 - 0x1012ffff: Color LCD Controller (PL110)
        //    0x10130000 - 0x1013ffff: DMA Contoroller (PL080)
        //    0x10140000 - 0x1014ffff: Primary Interrupt Contoroller (PL190)
        //    0x101e0000 - 0x101e1000: System Controller (SP810)
        //    0x101e1000 - 0x101e2000: Watchdog Module (SP805)
        //    0x101e2000 - 0x101e2fff: Dual-Timer 0 and 1 (SP804)
        //    0x101e3000 - 0x101e3fff: Dual-Timer 2 and 3 (SP804)
        //    0x101e4000 - 0x101e4fff: General Purpose I/O 0 (PL061)
        //    0x101e5000 - 0x101e5fff: General Purpose I/O 1 (PL061)
        //    0x101e6000 - 0x101e6fff: General Purpose I/O 2 (PL061)
        //    0x101e7000 - 0x101e7fff: General Purpose I/O 3 (PL061)
        //    0x101e8000 - 0x101e8fff: Real Time Clock (PL031)
        //    0x101f0000 - 0x101f0fff: Smart Card Interface (PL131)
        //    0x101f1000 - 0x101f1fff: UART0 (PL011)
        //    0x101f2000 - 0x101f2fff: UART1 (PL011)
        //    0x101f3000 - 0x101f3fff: UART2 (PL011)
        //    0x101f4000 - 0x101f4fff: SSP (PL022)
        //  0x80000000 - 0x82ffffff: Main
        //    0x80000000 - 0x80007fff: Linux pagetable
        //    0x80008000 - 0x804fffff: Linux Image
        //    0x81fff000 - 0x81ffffff: ATAG_XXX
        bus.addSlaveCore(sysBoard, 0x10000000L, 0x10001000L);
        bus.addSlaveCore(intc2nd, 0x10003000L, 0x10004000L);
        bus.addSlaveCore(ssmc, 0x10100000L, 0x10110000L);
        bus.addSlaveCore(mpmc, 0x10110000L, 0x10120000L);
        bus.addSlaveCore(clcdc, 0x10120000L, 0x10130000L);
        bus.addSlaveCore(dmac, 0x10130000L, 0x10140000L);
        bus.addSlaveCore(intc1st, 0x10140000L, 0x10150000L);
        bus.addSlaveCore(sysCtrl, 0x101e0000L, 0x101e1000L);
        bus.addSlaveCore(watchdog, 0x101e1000L, 0x101e2000L);
        bus.addSlaveCore(timer0_1, 0x101e2000L, 0x101e3000L);
        bus.addSlaveCore(timer2_3, 0x101e3000L, 0x101e4000L);
        bus.addSlaveCore(gpio0, 0x101e4000L, 0x101e5000L);
        bus.addSlaveCore(gpio1, 0x101e5000L, 0x101e6000L);
        bus.addSlaveCore(gpio2, 0x101e6000L, 0x101e7000L);
        bus.addSlaveCore(gpio3, 0x101e7000L, 0x101e8000L);
        bus.addSlaveCore(rtc, 0x101e8000L, 0x101e9000L);
        bus.addSlaveCore(scard, 0x101f0000L, 0x101f1000L);
        bus.addSlaveCore(uart0, 0x101f1000L, 0x101f2000L);
        bus.addSlaveCore(uart1, 0x101f2000L, 0x101f3000L);
        bus.addSlaveCore(uart2, 0x101f3000L, 0x101f4000L);
        bus.addSlaveCore(ssp, 0x101f4000L, 0x101f5000L);
        bus.addSlaveCore(ramMain, 0x80000000L, 0x82000000L);

        //reset
        cpu.setDisasmMode(false);
        cpu.setPrintDisasm(false);
        cpu.setPrintRegs(false);
        cpu.doExceptionReset("Init.");

        //tentative boot loader for Linux
        //load Image file
        loadFile(filename, cpu, 0x80008000);

        //r0: 0
        cpu.setReg(0, 0);

        //r1: machine type
        //ARM-Versatile PB
        //cpu.setReg(1, 0x00000183);
        //ARM-Versatile AB
        cpu.setReg(1, 0x0000025e);

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
            cpu.write32(addrAtags + 0x08, 0x02000000);
            cpu.write32(addrAtags + 0x0c, 0x80000000);
            addrAtags += 0x10;

            //ATAG_REVISION, size, tag, rev
            cpu.write32(addrAtags + 0x00, 0x00000003);
            cpu.write32(addrAtags + 0x04, 0x54410007);
            //ARM-Versatile PB
            //cpu.write32(addrAtags + 0x08, 0x00000183);
            //ARM-Versatile AB
            cpu.write32(addrAtags + 0x08, 0x0000025e);
            addrAtags += 0x0c;

            //ATAG_CMDLINE
            cpu.write32(addrAtags + 0x00, 0x00000002 + cmdline.length / 4);
            cpu.write32(addrAtags + 0x04, 0x54410009);
            for (int i = 0; i < cmdline.length; i++) {
                cpu.write8(addrAtags + 0x08 + i, cmdline[i]);
            }
            addrAtags += 0x08 + cmdline.length;

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
