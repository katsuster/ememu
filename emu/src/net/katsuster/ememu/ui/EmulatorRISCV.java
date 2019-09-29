package net.katsuster.ememu.ui;

import net.katsuster.ememu.riscv.core.*;
import net.katsuster.ememu.board.*;
import net.katsuster.ememu.generic.*;

/**
 * RISCV エミュレータです。
 */
public class EmulatorRISCV extends Emulator {
    public EmulatorRISCV() {
        super(new RISCVUnleashed(), new LinuxOption());
    }

    @Override
    public void run() {
        String rom0, rom1, qspi_flash0;
        RV64 cpu;
        Bus64 bus;

        setName(getClass().getName());

        getBoard().setup();
        cpu = (RV64)getBoard().getMainCPU();
        bus = getBoard().getMainBus();

        rom0 = "file:///home/katsuhiro/share/ememu/unleashed/rom0.bin";
        rom1 = "file:///home/katsuhiro/share/ememu/unleashed/rom1.bin";
        qspi_flash0 = "file:///home/katsuhiro/share/ememu/unleashed/flash0.bin";

        BinaryLoader.loadFromURI(bus, rom0, 0x1000);
        BinaryLoader.loadFromURI(bus, rom1, 0x10000);
        BinaryLoader.loadFromURI(bus, qspi_flash0, 0x20000000);

        getBoard().start();
    }

    @Override
    public void halt() {
        getBoard().stop();
    }
}
