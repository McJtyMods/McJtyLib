package mcjty.lib.gui;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.varia.WorldTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuiTools {

    public static int getRelativeX(Screen gui) {
        Minecraft mc = Minecraft.getInstance();
        int mainWidth = mc.getMainWindow().getWidth();
        if (mainWidth <= 0) {
            // Safety
            return 0;
        }

        MouseHelper mouse = mc.mouseHelper;
        int mouseX = (int) (mouse.getMouseX());
        return mouseX * gui.width / mainWidth;
    }

    public static int getRelativeY(Screen gui) {
        Minecraft mc = Minecraft.getInstance();
        int mainHeight = mc.getMainWindow().getHeight();
        if (mainHeight <= 0) {
            // Safety
            return 0;
        }

        MouseHelper mouse = mc.mouseHelper;
        int mouseY = (int) (mouse.getMouseY());
        return mouseY * gui.height / mainHeight;
    }

    public static boolean openRemoteGui(@Nonnull PlayerEntity player, DimensionType dimensionType, @Nonnull BlockPos pos) {
        if (dimensionType == null) {
            dimensionType = player.getEntityWorld().getDimension().getType();
        }
        World world = WorldTools.getWorld(player.getEntityWorld(), dimensionType);
        if (world == null) {
            // Not loaded
            player.sendStatusMessage(new StringTextComponent("World is not loaded!"), false);
            return false;
        }
        if (!world.isBlockLoaded(pos)) {
            player.sendStatusMessage(new StringTextComponent("Position is not loaded!"), false);
            return false;
        }

        NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new StringTextComponent("Remote Gui");
            }

            @Nullable
            @Override
            public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
                TileEntity te = world.getTileEntity(pos);
                if (te != null) {
                    return te.getCapability(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY)
                            .map(h -> h.createMenu(id, inventory, player))
                            .orElse(null);
                }
                return null;
            }
        }, pos);
        return true;
    }
}
