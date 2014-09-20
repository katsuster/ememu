package net.katsuster.ememu.board;

import net.katsuster.ememu.arm.*;
import net.katsuster.ememu.ui.*;

/**
 * ARM Versatile Application Baseboards (AB) and Platform Baseboards (PB).
 *
 * @author katsuhiro
 */
public class ARMVersatile {
    public ARMVersatile() {
        //do nothing
    }

    public static void setupBoard(ARMv5 cpu, Bus64 bus, RAM ramMain) {
        MPMC mpmc_c0_0 = new MPMC();
        MPMC mpmc_c0_1 = new MPMC();
        MPMC mpmc_c1 = new MPMC();

        SysBaseboard sysBoard = new SysBaseboard();
        SecondaryINTC intc2nd = new SecondaryINTC();
        AACI aaci = new AACI();
        MMCI mci0 = new MMCI();
        KMI kmiKey = new KMI();
        KMI kmiMouse = new KMI();
        UART uart3 = new UART(System.in, SystemPane.out);
        SCard scard1 = new SCard();
        MMCI mci1 = new MMCI();
        //TODO: implement Ethernet controller...
        RAM ether = new RAM(4 * 1024);
        //TODO: implement USB controller...
        RAM usb = new RAM(4 * 1024);

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
        UART uart0 = new UART(System.in, SystemPane.out);
        UART uart1 = new UART(System.in, SystemPane.out);
        UART uart2 = new UART(System.in, SystemPane.out);
        SSP ssp = new SSP();

        //TODO: implement SSMC controller...
        RAM ssmc_c0 = new RAM(256 * 1024);
        RAM ssmc_c1 = new RAM(256 * 1024);
        RAM ssmc_c2 = new RAM(256 * 1024);

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
        //    0x10010000 - 0x1001ffff: Ethernet Interface (SMC LAN91C111)
        //    0x10020000 - 0x1002ffff: USB Interface (OTG243)
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
        bus.addSlaveCore(ether, 0x10010000L, 0x10020000L);
        bus.addSlaveCore(usb, 0x10020000L, 0x10030000L);

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

        //INTC
        cpu.setINTCForIRQ(intc1st.getSubINTCForIRQ());
        cpu.setINTCForFIQ(intc1st.getSubINTCForFIQ());

        intc1st.connectINTC(4, timer0_1);
        intc1st.connectINTC(12, uart0);
        intc1st.connectINTC(13, uart1);
        intc1st.connectINTC(14, uart2);

        //run other cores
        timer0_1.setName(timer0_1.getClass().getName());
        timer0_1.start();
        uart0.setName(uart0.getClass().getName());
        uart0.start();

        //reset CPU
        cpu.setDisasmMode(false);
        cpu.setPrintDisasm(false);
        cpu.setPrintRegs(false);
        cpu.doExceptionReset("Init.");
    }
}
