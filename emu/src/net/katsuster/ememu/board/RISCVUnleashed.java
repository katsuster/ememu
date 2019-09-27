package net.katsuster.ememu.board;

import java.io.*;

import net.katsuster.ememu.generic.*;
import net.katsuster.ememu.riscv.*;
import net.katsuster.ememu.riscv.core.*;

public class RISCVUnleashed extends AbstractBoard {
    private RV64[] cpu;
    private Bus64 bus;
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
        return bus;
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
        bus = new Bus64();

        RAM mode_select = new RAM32(4 * 1024);
        RAM mask_rom = new RAM32(32 * 1024);
        CLINT clint = new CLINT(cpu);
        RAM l2lim = new RAM32(32 * 1024 * 1024);
        cl0_ddr = new RAM32(64 * 1024 * 1024);
        PRCI prci = new PRCI(cpu);
        UART uart0 = new UART();
        UART uart1 = new UART();
        SPI spi0 = new SPI();
        SPI spi1 = new SPI();
        SPI spi2 = new SPI();

        //Master core
        for (int i = 0; i < cpu.length; i++) {
            cpu[i] = new RV64();
            cpu[i].setThreadID(i);
            bus.addMasterCore(cpu[i]);
        }

        //Memory map of Unleashed
        //  0x0000_0100 - 0x0000_0fff: Debug
        //  0x0000_1000 - 0x0000_1fff: Mode Select
        //  0x0001_0000 - 0x0001_7fff: Mask ROM
        //  0x0800_0000 - 0x09ff_ffff: L2 LIM
        //  0x1000_0000 - 0x1000_0fff: PRCI
        //  0x1001_0000 - 0x1001_0fff: UART0
        //  0x1001_1000 - 0x1001_1fff: UART1
        //  0x1004_0000 - 0x1004_0fff: QSPI0
        //  0x1004_1000 - 0x1004_1fff: QSPI1
        //  0x1005_0000 - 0x1005_0fff: QSPI2
        bus.addSlaveCore(mode_select, 0x00001000L, 0x00001fffL);
        bus.addSlaveCore(mask_rom, 0x00010000L, 0x00017fffL);
        bus.addSlaveCore(clint.getSlaveCore(), 0x02000000L, 0x0200ffffL);
        bus.addSlaveCore(l2lim, 0x08000000L, 0x09ffffffL);
        bus.addSlaveCore(prci.getSlaveCore(), 0x10000000L, 0x10000fffL);
        bus.addSlaveCore(uart0.getSlaveCore(), 0x10010000L, 0x10010fffL);
        bus.addSlaveCore(uart1.getSlaveCore(), 0x10011000L, 0x10011fffL);
        bus.addSlaveCore(spi0.getSlaveCore(), 0x10040000L, 0x10040fffL);
        bus.addSlaveCore(spi1.getSlaveCore(), 0x10041000L, 0x10041fffL);
        bus.addSlaveCore(spi2.getSlaveCore(), 0x10050000L, 0x10050fffL);

        //reset CPU
        for (int i = 0; i < cpu.length; i++) {
            cpu[i].setEnabledDisasm(false);
            cpu[i].setPrintInstruction(false);
            cpu[i].setPrintRegs(false);
            cpu[i].init();
        }
    }

    @Override
    public void start() {
        //start cores
        bus.startAllSlaveCores();
        bus.startAllMasterCores();

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
    public void stop() {
        bus.haltAllMasterCores();
        bus.haltAllSlaveCores();
    }
}
