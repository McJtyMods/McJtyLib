package mcjty.lib.compat.patchouli;

import mcjty.lib.setup.ModSetup;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import vazkii.patchouli.api.PatchouliAPI;

public class PatchouliCompatibility {

    public static void openBookGUI(ServerPlayer player, ResourceLocation id) {
        if (ModSetup.patchouli) {
            PatchouliAPI.get().openBookGUI(player, id);
        } else {
            player.sendMessage(ComponentFactory.literal(ChatFormatting.RED + "Patchouli is missing! No manual present"), Util.NIL_UUID);
        }
    }

    public static void openBookEntry(ServerPlayer player, ResourceLocation id, ResourceLocation entry, int page) {
        if (ModSetup.patchouli) {
            PatchouliAPI.get().openBookEntry(player, id, entry, page);
        } else {
            player.sendMessage(ComponentFactory.literal(ChatFormatting.RED + "Patchouli is missing! No manual present"), Util.NIL_UUID);
        }
    }
}
