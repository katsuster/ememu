package net.katsuster.ememu.ui;

import net.katsuster.ememu.arm.core.*;
import net.katsuster.ememu.board.*;
import net.katsuster.ememu.generic.*;

/**
 * ARM エミュレータです。
 */
public class EmulatorARM extends Emulator {
    public EmulatorARM() {
        super(new ARMVersatile(), new LinuxOption());
    }

    @Override
    public void run() {
        String dtree, kimage, initrd, cmdline;
        ARMv5 cpu;
        RAM ram;

        setName(getClass().getName());

        getBoard().setup();
        cpu = (ARMv5)getBoard().getMainCPU();
        ram = getBoard().getMainRAM();

        dtree = getOption().getValue(LinuxOption.LINUX_DTB);
        kimage = getOption().getValue(LinuxOption.LINUX_KIMAGE);
        initrd = getOption().getValue(LinuxOption.LINUX_INITRD);
        cmdline = getOption().getValue(LinuxOption.LINUX_CMDLINE);
        if (dtree.equals("")) {
            ARMLinuxLoader.bootFromURI(cpu, ram, kimage, initrd, cmdline);
        } else {
            ARMLinuxLoader.bootFromURIWithDT(cpu, ram, dtree, kimage, initrd, cmdline);
        }

        getBoard().start();
    }

    @Override
    public void halt() {
        getBoard().stop();
    }
}
