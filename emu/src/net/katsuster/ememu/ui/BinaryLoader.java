package net.katsuster.ememu.ui;

import net.katsuster.ememu.generic.*;

import java.io.*;
import java.net.*;

/**
 * バイナリファイルのローダです。
 */
public class BinaryLoader {
    public static int loadURIResource(URI uri, Bus64 bus, long addr) {
        int i = 0;

        System.out.printf("loadURL: %s\n" +
                        "  addr : 0x%08x\n",
                uri.toString(), addr);

        try {
            DataInputStream s = new DataInputStream(
                    new BufferedInputStream(uri.toURL().openStream()));

            try {
                while (true) {
                    bus.write8(null, addr + i, s.readByte());
                    i++;
                }
            } catch (EOFException e) {
                //end
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new IllegalArgumentException(e);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace(System.err);
            //throw new IllegalArgumentException(e);
        }

        System.out.printf("loadURL: '%s' done, %dbytes.\n",
                uri.toString(), i);

        return i;
    }

    public static int loadFromURI(Bus64 bus, String uri, long addr) {
        int size = 0;

        try {
            size = loadURIResource(new URI(uri), bus, addr);
        } catch (URISyntaxException e) {
            e.printStackTrace(System.err);
        }

        return 0;
    }
}
