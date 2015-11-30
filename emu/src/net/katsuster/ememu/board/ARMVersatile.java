package net.katsuster.ememu.board;

import java.io.*;

import net.katsuster.ememu.arm.*;
import net.katsuster.ememu.arm.core.*;
import net.katsuster.ememu.generic.*;

/**
 * ARM Versatile Application Baseboards (AB) and Platform Baseboards (PB).
 *
 * @author katsuhiro
 */
public class ARMVersatile {
    private InputStream[] uartIn = new InputStream[4];
    private OutputStream[] uartOut = new OutputStream[4];

    public ARMVersatile() {
        //do nothing
    }

    public InputStream getUARTInputStream(int index) {
        return uartIn[index];
    }

    public void setUARTInputStream(int index, InputStream is) {
        uartIn[index] = is;
    }

    public OutputStream getUARTOutputStream(int index) {
        return uartOut[index];
    }

    public void setUARTOutputStream(int index, OutputStream os) {
        uartOut[index] = os;
    }

    public void setup(ARMv5 cpu, Bus bus, RAM ramMain) {
        //TODO: implement MPMC controller...
        RAM mpmc_c0_c1 = ramMain;

        SysBaseboard sysBoard = new SysBaseboard();

        //TODO: implement PCI controller...
        RAM pci_conf = new RAM64(4 * 1024);
        //TODO: implement Serial Bus controller...
        RAM serial_bus = new RAM64(4 * 1024);

        SecondaryINTC intc2nd = new SecondaryINTC();
        AACI aaci = new AACI();
        MMCI mci0 = new MMCI();
        KMI kmiKey = new KMI();
        KMI kmiMouse = new KMI();
        UART uart3 = new UART(uartIn[3], uartOut[3]);
        SCard scard1 = new SCard();
        MMCI mci1 = new MMCI();
        //TODO: implement Ethernet controller...
        RAM ether = new RAM64(4 * 1024);
        //TODO: implement USB controller...
        RAM usb = new RAM64(4 * 1024);

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
        UART uart0 = new UART(uartIn[0], uartOut[0]);
        UART uart1 = new UART(uartIn[1], uartOut[1]);
        UART uart2 = new UART(uartIn[2], uartOut[2]);
        SSP ssp = new SSP();

        //TODO: implement SSMC controller...
        RAM ssmc_c4_7 = new RAM64(4 * 1024);
        RAM ssmc_c0 = new RAM64(4 * 1024);
        Flush16 ssmc_c1_0 = new Flush16(256 * 1024);
        Flush16 ssmc_c1_1 = new Flush16(256 * 1024);
        BankedFlush16_16 ssmc_c1 = new BankedFlush16_16(ssmc_c1_0, ssmc_c1_1);
        RAM ssmc_c2 = new RAM64(4 * 1024);
        RAM ssmc_c3 = new RAM64(4 * 1024);
        RAM pci_area = new RAM64(4 * 1024);
        //TODO: implement MBX Graphics controller...
        RAM mbx = new RAM64(4 * 1024);
        //TODO: implement MPMC controller...
        RAM mpmc_c2_3 = new RAM64(4 * 1024);

        //Master core
        cpu.setSlaveBus(bus);
        bus.addMasterCore(cpu);

        //Memory map of versatile
        //  0x00000000 - 0x0fffffff: MPMC Chip Select0-1, SDRAM
        //    (tentative main RAM map)
        //    0x00000100 - 0x000010ff: ATAG_XXX -> pagetable
        //    0x00000000 - 0x00007fff: Linux pagetable
        //    0x00008000 - 0x007fffff: Linux Image
        //    0x00800000 - 0x00ffffff: Linux initramfs
        //  0x10000000 - 0x13ffffff: Registers
        //    0x10000000 - 0x10000fff: System Registers
        //    0x10001000 - 0x10001fff: PCI configuration registers
        //    0x10002000 - 0x10002fff: Serial Bus Interface(for Real Time Clock, DS1338)
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
        //    0x101e0000 - 0x101e0fff: System Controller (SP810)
        //    0x101e1000 - 0x101e1fff: Watchdog Module (SP805)
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
        //  0x20000000 - 0x2fffffff: SSMC Chip Select4-7(static expansion mem)
        //  0x30000000 - 0x33ffffff: SSMC Chip Select0(disk on chip)
        //  0x34000000 - 0x37ffffff: SSMC Chip Select1(NOR flash)
        //  0x38000000 - 0x3bffffff: SSMC Chip Select2(SRAM)
        //  0x3c000000 - 0x3fffffff: SSMC Chip Select3(SRAM)
        //  0x40000000 - 0x40ffffff: MBX Graphics Accelerator Interface
        //  0x41000000 - 0x6fffffff: PCI interface
        //  0x70000000 - 0x7fffffff: MPMC Chip Select2-3(dynamic memory)
        //  0x80000000 - 0xffffffff: Reserved for Logic Tile Expansion
        bus.addSlaveCore(mpmc_c0_c1, 0x00000000L, 0x0fffffffL);

        bus.addSlaveCore(sysBoard.getSlaveCore(), 0x10000000L, 0x10000fffL);
        bus.addSlaveCore(pci_conf, 0x10001000L, 0x10001fffL);
        bus.addSlaveCore(serial_bus, 0x10002000L, 0x10002fffL);
        bus.addSlaveCore(intc2nd, 0x10003000L, 0x10003fffL);
        bus.addSlaveCore(aaci.getSlaveCore(), 0x10004000L, 0x10004fffL);
        bus.addSlaveCore(mci0.getSlaveCore(), 0x10005000L, 0x10005fffL);
        bus.addSlaveCore(kmiKey.getSlaveCore(), 0x10006000L, 0x10006fffL);
        bus.addSlaveCore(kmiMouse.getSlaveCore(), 0x10007000L, 0x10007fffL);
        bus.addSlaveCore(uart3.getSlaveCore(), 0x10009000L, 0x10009fffL);
        bus.addSlaveCore(scard1.getSlaveCore(), 0x1000a000L, 0x1000afffL);
        bus.addSlaveCore(mci1.getSlaveCore(), 0x1000b000L, 0x1000bfffL);
        bus.addSlaveCore(ether, 0x10010000L, 0x1001ffffL);
        bus.addSlaveCore(usb, 0x10020000L, 0x1002ffffL);

        bus.addSlaveCore(ssmc.getSlaveCore(), 0x10100000L, 0x1010ffffL);
        bus.addSlaveCore(mpmc.getSlaveCore(), 0x10110000L, 0x1011ffffL);
        bus.addSlaveCore(clcdc.getSlaveCore(), 0x10120000L, 0x1012ffffL);
        bus.addSlaveCore(dmac.getSlaveCore(), 0x10130000L, 0x1013ffffL);
        bus.addSlaveCore(intc1st, 0x10140000L, 0x1014ffffL);
        bus.addSlaveCore(sysCtrl.getSlaveCore(), 0x101e0000L, 0x101e0fffL);
        bus.addSlaveCore(watchdog.getSlaveCore(), 0x101e1000L, 0x101e1fffL);
        bus.addSlaveCore(timer0_1.getSlaveCore(), 0x101e2000L, 0x101e2fffL);
        bus.addSlaveCore(timer2_3.getSlaveCore(), 0x101e3000L, 0x101e3fffL);
        bus.addSlaveCore(gpio0.getSlaveCore(), 0x101e4000L, 0x101e4fffL);
        bus.addSlaveCore(gpio1.getSlaveCore(), 0x101e5000L, 0x101e5fffL);
        bus.addSlaveCore(gpio2.getSlaveCore(), 0x101e6000L, 0x101e6fffL);
        bus.addSlaveCore(gpio3.getSlaveCore(), 0x101e7000L, 0x101e7fffL);
        bus.addSlaveCore(rtc.getSlaveCore(), 0x101e8000L, 0x101e8fffL);
        bus.addSlaveCore(scard0.getSlaveCore(), 0x101f0000L, 0x101f0fffL);
        bus.addSlaveCore(uart0.getSlaveCore(), 0x101f1000L, 0x101f1fffL);
        bus.addSlaveCore(uart1.getSlaveCore(), 0x101f2000L, 0x101f2fffL);
        bus.addSlaveCore(uart2.getSlaveCore(), 0x101f3000L, 0x101f3fffL);
        bus.addSlaveCore(ssp.getSlaveCore(), 0x101f4000L, 0x101f4fffL);

        bus.addSlaveCore(ssmc_c4_7, 0x20000000L, 0x2fffffffL);
        bus.addSlaveCore(ssmc_c0, 0x30000000L, 0x33ffffffL);
        bus.addSlaveCore(ssmc_c1, 0x34000000L, 0x37ffffffL);
        bus.addSlaveCore(ssmc_c2, 0x38000000L, 0x3bffffffL);
        bus.addSlaveCore(ssmc_c3, 0x3c000000L, 0x3fffffffL);
        bus.addSlaveCore(mbx, 0x40000000L, 0x40ffffffL);
        bus.addSlaveCore(pci_area, 0x41000000L, 0x6fffffffL);
        //main RAM
        bus.addSlaveCore(mpmc_c2_3, 0x70000000L, 0x7fffffffL);

        //INTC
        cpu.connectINTSource(ARMv5.INTSRC_IRQ, intc1st.getIRQSource());
        cpu.connectINTSource(ARMv5.INTSRC_FIQ, intc1st.getFIQSource());

        intc1st.connectINTSource(4, timer0_1);
        intc1st.connectINTSource(12, uart0);
        intc1st.connectINTSource(13, uart1);
        intc1st.connectINTSource(14, uart2);

        //reset CPU
        cpu.setEnabledDisasm(false);
        cpu.setPrintInstruction(false);
        cpu.setPrintRegs(false);
        cpu.doExceptionReset("Init.");
    }
}
