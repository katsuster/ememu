package net.katsuster.ememu.board;

import java.io.*;

import net.katsuster.ememu.generic.*;
import net.katsuster.ememu.riscv.core.RV64;

public class RISCVUnleashed extends AbstractBoard {
    private CPU cpu;
    private Bus bus;
    private RAM cl0_ddr;
    private InputStream[] uartIn = new InputStream[4];
    private OutputStream[] uartOut = new OutputStream[4];

    public RISCVUnleashed() {
        //do nothing
    }

    @Override
    public CPU getMainCPU() {
        return cpu;
    }

    @Override
    public Bus getMainBus() {
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
        cpu = new RV64();
        bus = new Bus();

        RAM mode_select = new RAM32(4 * 1024);
        cl0_ddr = new RAM32(64 * 1024 * 1024);

        //Master core
        bus.addMasterCore(cpu);

        //Memory map of Unleashed
        //  0x0000_0100 - 0x0000_0fff: Debug
        //  0x0000_1000 - 0x0000_1fff: Mode Select
        bus.addSlaveCore(mode_select, 0x00001000L, 0x00001fffL);
    }

    @Override
    public void start() {
        //start cores
        bus.startAllSlaveCores();
        bus.startAllMasterCores();

        //wait CPU halted
        try {
            cpu.join();
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