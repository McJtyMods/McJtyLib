package mcjty.lib.api.infusable;

import mcjty.lib.setup.Registration;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DefaultInfusable implements IInfusable {

    private final BlockEntity owner;
    private int infused = 0;

    public DefaultInfusable(BlockEntity owner) {
        this.owner = owner;
    }

    public void applyImplicitComponents(ItemInfusable infusable) {
        if (infusable != null) {
            setInfused(infusable.infused());
        }
    }

    public void collectImplicitComponents(DataComponentMap.Builder builder) {
        builder.set(Registration.ITEM_INFUSABLE.get(), new ItemInfusable(getInfused()));
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

    public void save(CompoundTag tag, String tagName) {
        tag.putInt(tagName, infused);
    }

    public void load(CompoundTag tag, String tagName) {
        infused = tag.getInt(tagName);
    }
}
