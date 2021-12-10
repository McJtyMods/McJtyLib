package mcjty.lib.compat.patchouli;

import mcjty.lib.setup.ModSetup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import vazkii.patchouli.api.PatchouliAPI;

public class PatchouliCompatibility {

    public static void openBookGUI(ServerPlayer player, ResourceLocation id) {
        if (ModSetup.patchouli) {
            PatchouliAPI.instance.openBookGUI(player, id);
        } else {
            player.sendMessage(new TextComponent(ChatFormatting.RED + "Patchouli is missing! No manual present"), Util.NIL_UUID);
        }
    }

    public static void openBookEntry(ServerPlayer player, ResourceLocation id, ResourceLocation entry, int page) {
        if (ModSetup.patchouli) {
            PatchouliAPI.instance.openBookEntry(player, id, entry, page);
        } else {
            player.sendMessage(new TextComponent(ChatFormatting.RED + "Patchouli is missing! No manual present"), Util.NIL_UUID);
        }
    }
}
