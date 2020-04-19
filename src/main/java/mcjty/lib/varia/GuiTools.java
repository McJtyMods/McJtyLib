package mcjty.lib.varia;

import mcjty.lib.api.container.CapabilityContainerProvider;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

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

    public static boolean openRemoteGui(@Nonnull PlayerEntity player, @Nullable DimensionType dimensionType, @Nonnull BlockPos pos) {
        return openRemoteGui(player, dimensionType, pos,
                te -> new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new StringTextComponent("Remote Gui");
                    }

                    @Nullable
                    @Override
                    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
                        return te.getCapability(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY)
                                .map(h -> h.createMenu(id, inventory, player))
                                .orElse(null);
                    }
                });
    }

    public static boolean openRemoteGui(@Nonnull PlayerEntity player, @Nullable DimensionType dimensionType, @Nonnull BlockPos pos,
                                        Function<TileEntity, INamedContainerProvider> provider) {
        if (dimensionType == null) {
            dimensionType = player.getEntityWorld().getDimension().getType();
        }
        World world = WorldTools.getWorld(player.getEntityWorld(), dimensionType);
        if (!WorldTools.isLoaded(world, pos)) {
            player.sendStatusMessage(new StringTextComponent(TextFormatting.RED + "Position is not loaded!"), false);
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te == null) {
            player.sendStatusMessage(new StringTextComponent(TextFormatting.RED + "Tile entity is missing!"), false);
            return false;
        }

        NetworkHooks.openGui((ServerPlayerEntity) player, provider.apply(te), pos);
        return true;
    }

}
