package mcjty.lib.client;

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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public class GuiTools {

    public static int getRelativeX(Screen gui) {
        Minecraft mc = Minecraft.getInstance();
        int mainWidth = mc.getWindow().getScreenWidth();
        if (mainWidth <= 0) {
            // Safety
            return 0;
        }

        MouseHelper mouse = mc.mouseHandler;
        int mouseX = (int) (mouse.xpos());
        return mouseX * gui.width / mainWidth;
    }

    public static int getRelativeY(Screen gui) {
        Minecraft mc = Minecraft.getInstance();
        int mainHeight = mc.getWindow().getScreenHeight();
        if (mainHeight <= 0) {
            // Safety
            return 0;
        }

        MouseHelper mouse = mc.mouseHandler;
        int mouseY = (int) (mouse.ypos());
        return mouseY * gui.height / mainHeight;
    }

    public static boolean openRemoteGui(@Nonnull PlayerEntity player, @Nullable RegistryKey<World> dimensionType, @Nonnull BlockPos pos) {
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

    public static boolean openRemoteGui(@Nonnull PlayerEntity player, @Nullable RegistryKey<World> dimensionType, @Nonnull BlockPos pos,
                                        Function<TileEntity, INamedContainerProvider> provider) {
        if (dimensionType == null) {
            dimensionType = player.getCommandSenderWorld().dimension();
        }
        World world = WorldTools.getWorld(player.getCommandSenderWorld(), dimensionType);
        if (!WorldTools.isLoaded(world, pos)) {
            player.displayClientMessage(new StringTextComponent(TextFormatting.RED + "Position is not loaded!"), false);
            return false;
        }
        TileEntity te = world.getBlockEntity(pos);
        if (te == null) {
            player.displayClientMessage(new StringTextComponent(TextFormatting.RED + "Tile entity is missing!"), false);
            return false;
        }

        CompoundNBT compound = new CompoundNBT();
        CompoundNBT written = te.save(compound);

        NetworkHooks.openGui((ServerPlayerEntity) player, provider.apply(te), buf -> {
            buf.writeBlockPos(pos);
            buf.writeResourceLocation(world.dimension().location());
            buf.writeNbt(written);
        });
        return true;
    }

}
