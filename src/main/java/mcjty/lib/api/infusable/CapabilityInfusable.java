package mcjty.lib.api.infusable;

import mcjty.lib.McJtyLib;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class CapabilityInfusable {
    public static final BlockCapability<IInfusable, @Nullable Direction> INFUSABLE_CAPABILITY = BlockCapability.createSided(new ResourceLocation(McJtyLib.MODID, "infusable"), IInfusable.class);
}
