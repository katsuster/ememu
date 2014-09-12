package net.katsuster.semu.ui;

import java.io.*;
import java.net.*;

import net.katsuster.semu.arm.*;

public class Main {
    public static final int ATAG_NONE      = 0x00000000;
    public static final int ATAG_CORE      = 0x54410001;
    public static final int ATAG_MEM       = 0x54410002;
    public static final int ATAG_VIDEOTEXT = 0x54410003;
    public static final int ATAG_RAMDISK   = 0x54410004;
    public static final int ATAG_INITRD2   = 0x54420005;
    public static final int ATAG_SERIAL    = 0x54410006;
    public static final int ATAG_REVISION  = 0x54410007;
    public static final int ATAG_VIDEOLFB  = 0x54410008;
    public static final int ATAG_CMDLINE   = 0x54410009;

    public static int loadFile(String filename, CPU cpu, int addr) {
        int len = 0;
        int i;

        System.out.println("loadFile: " + filename);

        try {
            File f = new File(filename);
            DataInputStream s = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(f)));

            if (f.length() > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Size is too large " +
                        f.length() + ".");
            }

            i = 0;
            len = (int)f.length();
            for (; i < len - 8; i += 8) {
                cpu.write64(addr + i, Long.reverseBytes(s.readLong()));
            }
            for (; i < len; i++) {
                cpu.write8(addr + i, s.readByte());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        System.out.printf("loadFile: '%s' done, %dbytes.\n",
                filename, len);

        return len;
    }

    public static int loadURLResource(URL url, CPU cpu, int addr) {
        int i;

        System.out.println("loadURL: " + url.toExternalForm());

        try {
            DataInputStream s = new DataInputStream(
                    new BufferedInputStream(url.openStream()));

            i = 0;
            try {
                while (true) {
                    cpu.write8(addr + i, s.readByte());
                    i++;
                }
            } catch (EOFException e) {
                //end
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        System.out.printf("loadURL: '%s' done, %dbytes.\n",
                url.toExternalForm(), i);

        return i;
    }

    public static void main(String[] args) {
        String kimage = "Image";
        String initram = "initramfs.gz";
        String cmdline = "console=ttyAMA0 mem=64M lpj=0 root=/dev/ram init=/bin/sh debug printk.time=1\0";

        if (args.length <= 0) {
            System.out.println("usage:\n" +
                    "  semu image initramfs [cmdline]\n");

            return;
        }
        if (args.length >= 1) {
            kimage = args[0];
            initram = "";
        }
        if (args.length >= 2) {
            initram = args[1];
        }
        if (args.length >= 3) {
            cmdline = args[2];
        }

        ARMv5 cpu = new ARMv5();
        Bus64 bus = new Bus64();
        RAM ramMain = new RAM(64 * 1024 * 1024); //64MB

        addVersatileCores(cpu, bus, ramMain);
        bootFromFile(cpu, ramMain, kimage, initram, cmdline);
    }

    public static void addVersatileCores(ARMv5 cpu, Bus64 bus, RAM ramMain) {
        //TODO: MPMC is not implemented
        RAM mpmc_c0_0 = new RAM(128 * 1024);
        RAM mpmc_c0_1 = new RAM(128 * 1024);
        RAM mpmc_c1 = new RAM(128 * 1024);

        SysBaseboard sysBoard = new SysBaseboard();
        SecondaryINTC intc2nd = new SecondaryINTC();
        AACI aaci = new AACI();
        MMCI mci0 = new MMCI();
        KMI kmiKey = new KMI();
        KMI kmiMouse = new KMI();
        UART uart3 = new UART();
        SCard scard1 = new SCard();
        MMCI mci1 = new MMCI();
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
        SCard scard0 = new SCard();
        UART uart0 = new UART();
        UART uart1 = new UART();
        UART uart2 = new UART();
        SSP ssp = new SSP();

        //TODO: SSMC is not implemented
        RAM ssmc_c0 = new RAM(512 * 1024);
        RAM ssmc_c1 = new RAM(512 * 1024);
        RAM ssmc_c2 = new RAM(512 * 1024);

        //RAM Image(tentative)
        //  0x00000000 - 0x03ffffff: MPMC Chip Select0, bottom of SDRAM
        //  0x04000000 - 0x07ffffff: MPMC Chip Select0, top of SDRAM
        //  0x08000000 - 0x0fffffff: MPMC Chip Select1
        //  0x10000000 - 0x13ffffff: CS5
        //    0x10000000 - 0x10000fff: System Registers
        //    0x10003000 - 0x10003fff: Secondary Interrupt Controller
        //    0x10004000 - 0x10004fff: Advanced Audio CODEC Interface (PL041)
        //    0x10005000 - 0x10005fff: Multimedia Card Interface 0 (PL180)
        //    0x10006000 - 0x10006fff: PS2 Keyboard Interface (PL050)
        //    0x10007000 - 0x10007fff: PS2 Mouse Interface (PL050)
        //    0x10009000 - 0x10009fff: UART2 (PL011)
        //    0x1000a000 - 0x1000afff: Smart Card Interface 1 (PL131)
        //    0x1000b000 - 0x1000bfff: Multimedia Card Interface 1 (PL180)
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
        //    0x101f0000 - 0x101f0fff: Smart Card Interface 0 (PL131)
        //    0x101f1000 - 0x101f1fff: UART0 (PL011)
        //    0x101f2000 - 0x101f2fff: UART1 (PL011)
        //    0x101f3000 - 0x101f3fff: UART2 (PL011)
        //    0x101f4000 - 0x101f4fff: SSP (PL022)
        //  0x30000000 - 0x33ffffff: SSMC Chip Select0
        //  0x34000000 - 0x37ffffff: SSMC Chip Select1
        //  0x38000000 - 0x3bffffff: SSMC Chip Select2
        //  0x80000000 - 0x82ffffff: Main
        //    0x80000000 - 0x80007fff: Linux pagetable
        //    0x80008000 - 0x807fffff: Linux Image
        //    0x80800000 - 0x80ffffff: Linux initramfs
        //    0x81fff000 - 0x81ffffff: ATAG_XXX
        cpu.setSlaveBus(bus);

        bus.addSlaveCore(mpmc_c0_0, 0x00000000L, 0x04000000L);
        bus.addSlaveCore(mpmc_c0_1, 0x04000000L, 0x08000000L);
        bus.addSlaveCore(mpmc_c1, 0x08000000L, 0x10000000L);

        bus.addSlaveCore(sysBoard, 0x10000000L, 0x10001000L);
        bus.addSlaveCore(intc2nd, 0x10003000L, 0x10004000L);
        bus.addSlaveCore(aaci, 0x10004000L, 0x10005000L);
        bus.addSlaveCore(mci0, 0x10005000L, 0x10006000L);
        bus.addSlaveCore(kmiKey, 0x10006000L, 0x10007000L);
        bus.addSlaveCore(kmiMouse, 0x10007000L, 0x10008000L);
        bus.addSlaveCore(uart3, 0x10009000L, 0x1000a000L);
        bus.addSlaveCore(scard1, 0x1000a000L, 0x1000b000L);
        bus.addSlaveCore(mci1, 0x1000b000L, 0x1000c000L);
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
        bus.addSlaveCore(scard0, 0x101f0000L, 0x101f1000L);
        bus.addSlaveCore(uart0, 0x101f1000L, 0x101f2000L);
        bus.addSlaveCore(uart1, 0x101f2000L, 0x101f3000L);
        bus.addSlaveCore(uart2, 0x101f3000L, 0x101f4000L);
        bus.addSlaveCore(ssp, 0x101f4000L, 0x101f5000L);

        bus.addSlaveCore(ssmc_c0, 0x30000000L, 0x34000000L);
        bus.addSlaveCore(ssmc_c1, 0x34000000L, 0x38000000L);
        bus.addSlaveCore(ssmc_c2, 0x38000000L, 0x3c000000L);

        bus.addSlaveCore(ramMain, 0x80000000L, 0x80000000L + (ramMain.getSize() & 0xffffffffL));

        //TODO: implement ethernet controller...
        bus.addSlaveCore(ssmc_c0, 0x10010000L, 0x10020000L);

        //INTC
        cpu.setINTCForIRQ(intc1st.getSubINTCForIRQ());
        cpu.setINTCForFIQ(intc1st.getSubINTCForFIQ());

        intc1st.connectINTC(4, timer0_1);
        intc1st.connectINTC(12, uart0);
        intc1st.connectINTC(13, uart1);
        intc1st.connectINTC(14, uart2);

        //run other cores
        Thread thTimer0_1 = new Thread(timer0_1);
        thTimer0_1.start();
        Thread thUart0 = new Thread(uart0);
        thUart0.start();

        //reset CPU
        cpu.setDisasmMode(false);
        cpu.setPrintDisasm(false);
        cpu.setPrintRegs(false);
        cpu.doExceptionReset("Init.");
    }

    public static void bootFromFile(ARMv5 cpu, RAM ramMain, String kimage, String initram, String cmdline) {
        byte[] cmdlb = cmdline.getBytes();
        byte[] cmdalign = new byte[(cmdlb.length + 3) & ~0x3];
        System.arraycopy(cmdlb, 0, cmdalign, 0, cmdlb.length);

        int addrRAM = 0x80000000;
        int addrAtags = addrRAM + ramMain.getSize() - 4096;

        //Cannot change this address
        final int addrImage = 0x80008000;
        int sizeImage = 0;
        int addrInitram = 0x80800000;
        int sizeInitram = 0;
        boolean initramExist = !initram.equals("");

        //tentative boot loader for Linux
        //load Image file
        sizeImage = loadFile(kimage, cpu, addrImage);
        //load initramfs file
        if (initramExist) {
            sizeInitram = loadFile(initram, cpu, addrInitram);
        }

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
            //ATAG_CORE, size, tag, [flags, pagesize, rootdev]
            cpu.write32(addrAtags + 0x00, 0x00000002);
            cpu.write32(addrAtags + 0x04, ATAG_CORE);
            //cpu.write32(addrAtags + 0x08, 0x00000000);
            //cpu.write32(addrAtags + 0x0c, 0x00001000);
            //cpu.write32(addrAtags + 0x10, 0x00000000);
            //addrAtags += 0x14;
            addrAtags += 0x08;

            //ATAG_MEM, size, tag, size, start
            cpu.write32(addrAtags + 0x00, 0x00000004);
            cpu.write32(addrAtags + 0x04, ATAG_MEM);
            cpu.write32(addrAtags + 0x08, ramMain.getSize());
            cpu.write32(addrAtags + 0x0c, addrRAM);
            addrAtags += 0x10;

            //ATAG_INITRD2, size, tag, size, start
            if (initramExist) {
                cpu.write32(addrAtags + 0x00, 0x00000004);
                cpu.write32(addrAtags + 0x04, ATAG_INITRD2);
                cpu.write32(addrAtags + 0x08, addrInitram);
                cpu.write32(addrAtags + 0x0c, sizeInitram);
                addrAtags += 0x10;
            }

            //ATAG_CMDLINE
            cpu.write32(addrAtags + 0x00, 0x00000002 + cmdalign.length / 4);
            cpu.write32(addrAtags + 0x04, ATAG_CMDLINE);
            for (int i = 0; i < cmdalign.length; i++) {
                cpu.write8(addrAtags + 0x08 + i, cmdalign[i]);
            }
            addrAtags += 0x08 + cmdalign.length;

            //ATAG_NONE, size, tag
            cpu.write32(addrAtags + 0x00, 0x00000002);
            cpu.write32(addrAtags + 0x04, ATAG_NONE);
            addrAtags += 0x08;
        }

        //pc: entry of stext
        cpu.setPC(addrImage);
        cpu.setJumped(false);

        //run CPU
        cpu.run();
    }

    public static void bootFromURL(ARMv5 cpu, RAM ramMain, String kimage, String initram, String cmdline) {
        byte[] cmdlb = cmdline.getBytes();
        byte[] cmdalign = new byte[(cmdlb.length + 3) & ~0x3];
        System.arraycopy(cmdlb, 0, cmdalign, 0, cmdlb.length);

        int addrRAM = 0x80000000;
        int addrAtags = addrRAM + ramMain.getSize() - 4096;

        //Cannot change this address
        final int addrImage = 0x80008000;
        int sizeImage = 0;
        int addrInitram = 0x80800000;
        int sizeInitram = 0;
        boolean initramExist = !initram.equals("");

        //tentative boot loader for Linux
        try {
            //load Image file
            sizeImage = loadURLResource(new URL(kimage), cpu, addrImage);
            //load initramfs file
            if (initramExist) {
                sizeInitram = loadURLResource(new URL(initram), cpu, addrInitram);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

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
            //ATAG_CORE, size, tag, [flags, pagesize, rootdev]
            cpu.write32(addrAtags + 0x00, 0x00000002);
            cpu.write32(addrAtags + 0x04, ATAG_CORE);
            //cpu.write32(addrAtags + 0x08, 0x00000000);
            //cpu.write32(addrAtags + 0x0c, 0x00001000);
            //cpu.write32(addrAtags + 0x10, 0x00000000);
            //addrAtags += 0x14;
            addrAtags += 0x08;

            //ATAG_MEM, size, tag, size, start
            cpu.write32(addrAtags + 0x00, 0x00000004);
            cpu.write32(addrAtags + 0x04, ATAG_MEM);
            cpu.write32(addrAtags + 0x08, ramMain.getSize());
            cpu.write32(addrAtags + 0x0c, addrRAM);
            addrAtags += 0x10;

            //ATAG_INITRD2, size, tag, size, start
            if (initramExist) {
                cpu.write32(addrAtags + 0x00, 0x00000004);
                cpu.write32(addrAtags + 0x04, ATAG_INITRD2);
                cpu.write32(addrAtags + 0x08, addrInitram);
                cpu.write32(addrAtags + 0x0c, sizeInitram);
                addrAtags += 0x10;
            }

            //ATAG_CMDLINE
            cpu.write32(addrAtags + 0x00, 0x00000002 + cmdalign.length / 4);
            cpu.write32(addrAtags + 0x04, ATAG_CMDLINE);
            for (int i = 0; i < cmdalign.length; i++) {
                cpu.write8(addrAtags + 0x08 + i, cmdalign[i]);
            }
            addrAtags += 0x08 + cmdalign.length;

            //ATAG_NONE, size, tag
            cpu.write32(addrAtags + 0x00, 0x00000002);
            cpu.write32(addrAtags + 0x04, ATAG_NONE);
            addrAtags += 0x08;
        }

        //pc: entry of stext
        cpu.setPC(addrImage);
        cpu.setJumped(false);

        //run CPU
        cpu.run();
    }
}
