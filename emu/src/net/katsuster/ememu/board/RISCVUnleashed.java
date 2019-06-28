package net.katsuster.ememu.board;

import java.io.*;

import net.katsuster.ememu.generic.*;
import net.katsuster.ememu.riscv.*;
import net.katsuster.ememu.riscv.core.*;

public class RISCVUnleashed extends AbstractBoard {
    private CPU cpu;
    private Bus64 bus;
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
        cpu = new RV64();
        bus = new Bus64();

        RAM mode_select = new RAM32(4 * 1024);
        RAM mask_rom = new RAM32(8 * 1024);
        CLINT clint = new CLINT();
        RAM l2lim = new RAM32(32 * 1024 * 1024);
        cl0_ddr = new RAM32(64 * 1024 * 1024);

        //Master core
        bus.addMasterCore(cpu);

        //Memory map of Unleashed
        //  0x0000_0100 - 0x0000_0fff: Debug
        //  0x0000_1000 - 0x0000_1fff: Mode Select
        //  0x0001_0000 - 0x0001_7fff: Mask ROM
        //  0x0800_0000 - 0x09ff_ffff: L2 LIM
        bus.addSlaveCore(mode_select, 0x00001000L, 0x00001fffL);
        bus.addSlaveCore(mask_rom, 0x00010000L, 0x00017fffL);
        bus.addSlaveCore(clint.getSlaveCore(), 0x02000000L, 0x0200ffffL);
        bus.addSlaveCore(l2lim, 0x08000000L, 0x09ffffffL);

        //reset CPU
        cpu.setEnabledDisasm(false);
        cpu.setPrintInstruction(false);
        cpu.setPrintRegs(false);
        cpu.init();
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
