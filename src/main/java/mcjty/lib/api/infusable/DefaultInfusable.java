package mcjty.lib.api.infusable;

import net.minecraft.world.level.block.entity.BlockEntity;

public class DefaultInfusable implements IInfusable {

    private final BlockEntity owner;
    private int infused = 0;

    public DefaultInfusable(BlockEntity owner) {
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
