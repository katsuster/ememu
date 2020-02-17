package net.katsuster.ememu.ui;

import net.katsuster.ememu.arm.core.*;
import net.katsuster.ememu.board.*;
import net.katsuster.ememu.generic.*;

/**
 * ARM エミュレータです。
 */
public class EmulatorARM extends Emulator {
    public EmulatorARM() {

    }

    @Override
    public void setup() {
        setBoard(new ARMVersatile());
    }

    @Override
    public void boot() {
        String dtree, kimage, initrd, cmdline;
        ARMv5 cpu;
        RAM ram;

        setName(getClass().getName());

        getBoard().setup();
        cpu = (ARMv5)getBoard().getMainCPU();
        ram = getBoard().getMainRAM();

        dtree = getProperties().getValue(LinuxOption.LINUX_DTB, 0);
        kimage = getProperties().getValue(LinuxOption.LINUX_KIMAGE, 0);
        initrd = getProperties().getValue(LinuxOption.LINUX_INITRD, 0);
        cmdline = getProperties().getValue(LinuxOption.LINUX_CMDLINE, 0);
        if (dtree.equals("")) {
            ARMLinuxLoader.bootFromURI(cpu, ram, kimage, initrd, cmdline);
        } else {
            ARMLinuxLoader.bootFromURIWithDT(cpu, ram, dtree, kimage, initrd, cmdline);
        }

        getBoard().boot();
    }
}
