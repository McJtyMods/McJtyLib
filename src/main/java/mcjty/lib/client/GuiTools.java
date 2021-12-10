package mcjty.lib.client;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.varia.LevelTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
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

        MouseHandler mouse = mc.mouseHandler;
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

        MouseHandler mouse = mc.mouseHandler;
        int mouseY = (int) (mouse.ypos());
        return mouseY * gui.height / mainHeight;
    }

    public static boolean openRemoteGui(@Nonnull Player player, @Nullable ResourceKey<Level> dimensionType, @Nonnull BlockPos pos) {
        return openRemoteGui(player, dimensionType, pos,
                te -> new MenuProvider() {
                    @Nonnull
                    @Override
                    public Component getDisplayName() {
                        return new TextComponent("Remote Gui");
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int id, @Nonnull Inventory inventory, @Nonnull Player player) {
                        return te.getCapability(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY)
                                .map(h -> h.createMenu(id, inventory, player))
                                .orElse(null);
                    }
                });
    }

    public static boolean openRemoteGui(@Nonnull Player player, @Nullable ResourceKey<Level> dimensionType, @Nonnull BlockPos pos,
                                        Function<BlockEntity, MenuProvider> provider) {
        if (dimensionType == null) {
            dimensionType = player.getCommandSenderWorld().dimension();
        }
        Level world = LevelTools.getLevel(player.getCommandSenderWorld(), dimensionType);
        if (!LevelTools.isLoaded(world, pos)) {
            player.displayClientMessage(new TextComponent(ChatFormatting.RED + "Position is not loaded!"), false);
            return false;
        }
        BlockEntity te = world.getBlockEntity(pos);
        if (te == null) {
            player.displayClientMessage(new TextComponent(ChatFormatting.RED + "Tile entity is missing!"), false);
            return false;
        }

        CompoundTag compound = new CompoundTag();
        CompoundTag written = te.save(compound);

        NetworkHooks.openGui((ServerPlayer) player, provider.apply(te), buf -> {
            buf.writeBlockPos(pos);
            buf.writeResourceLocation(world.dimension().location());
            buf.writeNbt(written);
        });
        return true;
    }

}
