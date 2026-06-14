package mcjty.lib.compat.patchouli;

import mcjty.lib.setup.ModSetup;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import vazkii.patchouli.api.PatchouliAPI;

public class PatchouliCompatibility {

    public static void openBookGUI(ServerPlayer player, ResourceLocation id) {
        if (ModSetup.patchouli) {
            PatchouliAPI.get().openBookGUI(player, id);
        } else {
            player.sendSystemMessage(ComponentFactory.literal(ChatFormatting.RED + "Patchouli is missing! No manual present"));
        }
    }

    public static void openBookEntry(ServerPlayer player, ResourceLocation id, ResourceLocation entry, int page) {
        if (ModSetup.patchouli) {
            PatchouliAPI.get().openBookEntry(player, id, entry, page);
        } else {
            player.sendSystemMessage(ComponentFactory.literal(ChatFormatting.RED + "Patchouli is missing! No manual present"));
        }
    }

    public static void openBookEntry(ResourceLocation id, ResourceLocation entry, int page) {
        if (ModSetup.patchouli) {
            PatchouliAPI.get().openBookEntry(id, entry, page);
        } else {
            Player player = SafeClientTools.getClientPlayer();
            if (player != null) {
                player.sendSystemMessage(ComponentFactory.literal(ChatFormatting.RED + "Patchouli is missing! No manual present"));
            }
        }
    }
}
