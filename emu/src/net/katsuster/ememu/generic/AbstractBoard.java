package net.katsuster.ememu.generic;

import java.io.*;

import net.katsuster.ememu.ui.*;

public abstract class AbstractBoard implements Board, Configurable {
    private EmuPropertyMap props;

    @Override
    public abstract InputStream getUARTInputStream(int index);

    @Override
    public abstract void setUARTInputStream(int index, InputStream is);

    @Override
    public abstract OutputStream getUARTOutputStream(int index);

    @Override
    public abstract void setUARTOutputStream(int index, OutputStream os);

    @Override
    public void setup() {

    }

    @Override
    public void boot() {

    }

    @Override
    public void halt() {

    }

    @Override
    public void initProperties(EmuPropertyMap p) {

    }

    @Override
    public EmuPropertyMap getProperties() {
        return props;
    }

    @Override
    public void setProperties(EmuPropertyMap p) {
        props = p;
    }
}
