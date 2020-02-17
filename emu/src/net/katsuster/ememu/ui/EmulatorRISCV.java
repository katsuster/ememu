package net.katsuster.ememu.ui;

import net.katsuster.ememu.board.ARMVersatile;
import net.katsuster.ememu.board.RISCVUnleashed;
import net.katsuster.ememu.generic.bus.Bus64;
import net.katsuster.ememu.riscv.core.RV64;

/**
 * RISC-V エミュレータです。
 */
public class EmulatorRISCV extends Emulator {
    public EmulatorRISCV() {

    }

    @Override
    public void setup() {
        setBoard(new RISCVUnleashed());
    }

    @Override
    public void boot() {
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

        getBoard().boot();
    }
}
