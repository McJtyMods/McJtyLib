package mcjty.lib.api.infusable;

import net.minecraft.tileentity.TileEntity;

public class DefaultInfusable implements IInfusable {

    private final TileEntity owner;
    private int infused = 0;

    public DefaultInfusable(TileEntity owner) {
        this.owner = owner;
    }

    @Override
    public int getInfused() {
        return infused;
    }

    @Override
    public void setInfused(int i) {
        infused = i;
        owner.setChanged();
    }
}
