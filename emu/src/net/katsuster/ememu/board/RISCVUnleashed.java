package net.katsuster.ememu.board;

import java.io.*;

import net.katsuster.ememu.generic.*;
import net.katsuster.ememu.generic.bus.Bus64;
import net.katsuster.ememu.generic.core.CPU;
import net.katsuster.ememu.riscv.*;
import net.katsuster.ememu.riscv.core.*;
import net.katsuster.ememu.ui.EmuPropertyMap;

public class RISCVUnleashed extends AbstractBoard {
    private RV64[] cpu;
    private Bus64[] buses;
    private RAM cl0_ddr;
    private InputStream[] uartIn = new InputStream[4];
    private OutputStream[] uartOut = new OutputStream[4];

    public RISCVUnleashed() {
        //do nothing
    }

    @Override
    public CPU getMainCPU() {
        return cpu[0];
    }

    @Override
    public Bus64 getMainBus() {
        return buses[0];
    }

    @Override
    public RAM getMainRAM() {
        return cl0_ddr;
    }

    @Override
    public InputStream getUARTInputStream(int index) {
        return uartIn[index];
    }

    @Override
    public void setUARTInputStream(int index, InputStream is) {
        uartIn[index] = is;
    }

    @Override
    public OutputStream getUARTOutputStream(int index) {
        return uartOut[index];
    }

    @Override
    public void setUARTOutputStream(int index, OutputStream os) {
        uartOut[index] = os;
    }

    @Override
    public void setup() {
        cpu = new RV64[5];
        Bus64 busMain = new Bus64();
        Bus64 busSpi0 = new Bus64();
        Bus64 busSpi1 = new Bus64();
        Bus64 busSpi2 = new Bus64();

        buses = new Bus64[4];
        buses[0] = busMain;
        buses[1] = busSpi0;
        buses[2] = busSpi1;
        buses[3] = busSpi2;

        RAM mode_select = new RAM32(8 * 1024);
        RAM reserved2 = new RAM32(56 * 1024);
        RAM mask_rom = new RAM32(32 * 1024);
        CLINT clint = new CLINT("clint", cpu);
        RAM l2lim = new RAM32(32 * 1024 * 1024);
        cl0_ddr = new RAM32(64 * 1024 * 1024);
        PRCI prci = new PRCI("pcri");
        UART uart0 = new UART("uart0", uartIn[0], uartOut[0]);
        UART uart1 = new UART("uart1", uartIn[1], uartOut[1]);
        I2C i2c = new I2C("i2c");
        SPI spi0 = new SPI("spi0");
        SPI spi1 = new SPI("spi1");
        SPI spi2 = new SPI("spi2");
        GPIO gpio = new GPIO("gpio");
        DDRController ddrc = new DDRController("ddrc");
        RAM qspi_flash0 = new RAM32(33 * 1024 * 1024);

        SDCard sdcard = new SDCard("sdcard");

        //Main bus
        for (int i = 0; i < cpu.length; i++) {
            cpu[i] = new RV64();
            cpu[i].setThreadID(i);
            busMain.addMasterCore(cpu[i]);
        }

        //Memory map of Unleashed
        //  0x0000_0100 - 0x0000_0fff: Debug
        //  0x0000_1000 - 0x0000_1fff: Mode Select
        //  0x0000_2000 - 0x0000_ffff: Reserved 2
        //  0x0001_0000 - 0x0001_7fff: Mask ROM
        //  0x0800_0000 - 0x09ff_ffff: L2 LIM
        //  0x1000_0000 - 0x1000_0fff: PRCI
        //  0x1001_0000 - 0x1001_0fff: UART0
        //  0x1001_1000 - 0x1001_1fff: UART1
        //  0x1003_0000 - 0x1003_0fff: I2C
        //  0x1004_0000 - 0x1004_0fff: QSPI0
        //  0x1004_1000 - 0x1004_1fff: QSPI1
        //  0x1005_0000 - 0x1005_0fff: QSPI2
        //  0x1006_0000 - 0x1006_0fff: GPIO
        //  0x100b_0000 - 0x100b_ffff: DDR Controller
        //  0x2000_0000 - 0x2fff_ffff: QSPI0 flash
        busMain.addSlaveCore(mode_select, 0x00001000L, 0x00001fffL);
        busMain.addSlaveCore(reserved2, 0x00002000L, 0x0000ffffL);
        busMain.addSlaveCore(mask_rom, 0x00010000L, 0x00017fffL);
        busMain.addSlaveCore(clint.getSlaveCore(), 0x02000000L, 0x0200ffffL);
        busMain.addSlaveCore(l2lim, 0x08000000L, 0x09ffffffL);
        busMain.addSlaveCore(prci.getSlaveCore(), 0x10000000L, 0x10000fffL);
        busMain.addSlaveCore(uart0.getSlaveCore(), 0x10010000L, 0x10010fffL);
        busMain.addSlaveCore(uart1.getSlaveCore(), 0x10011000L, 0x10011fffL);
        busMain.addSlaveCore(i2c.getSlaveCore(), 0x10030000L, 0x10030fffL);
        busMain.addSlaveCore(spi0.getSlaveCore(), 0x10040000L, 0x10040fffL);
        busMain.addSlaveCore(spi1.getSlaveCore(), 0x10041000L, 0x10041fffL);
        busMain.addSlaveCore(spi2.getSlaveCore(), 0x10050000L, 0x10050fffL);
        busMain.addSlaveCore(gpio.getSlaveCore(), 0x10060000L, 0x10060fffL);
        busMain.addSlaveCore(ddrc.getSlaveCore(), 0x100b0000L, 0x100bffffL);
        //TODO: tentative 33MB
        busMain.addSlaveCore(qspi_flash0, 0x20000000L, 0x221fffffL);

        //SPI bus
        busSpi0.addMasterCore(spi0.getMasterCore());
        busSpi1.addMasterCore(spi1.getMasterCore());
        busSpi2.addMasterCore(spi2.getMasterCore());
        busSpi2.addSlaveCore(sdcard.getSlaveCore(), 0, 0);

        //reset CPU
        for (int i = 0; i < cpu.length; i++) {
            cpu[i].setEnabledDisasm(false);
            cpu[i].setPrintInstruction(false);
            cpu[i].setPrintRegs(false);
            cpu[i].init();
        }
    }

    @Override
    public void boot() {
        //start cores
        for (int i = 0; i < buses.length; i++) {
            buses[i].startAllSlaveCores();
            buses[i].startAllMasterCores();
        }

        //wait CPU halted
        try {
            for (int i = 0; i < cpu.length; i++) {
                cpu[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
            //ignored
        }
    }

    @Override
    public void halt() {
        for (int i = 0; i < buses.length; i++) {
            buses[i].haltAllMasterCores();
            buses[i].haltAllSlaveCores();
        }
    }
}
