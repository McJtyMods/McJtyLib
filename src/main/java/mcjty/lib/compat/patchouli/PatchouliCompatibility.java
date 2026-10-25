package mcjty.lib.compat.patchouli;

import mcjty.lib.setup.ModSetup;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;


public class PatchouliCompatibility {

    // Preserve the Fabric port's manual fallback until a compatible integration is enabled.
    public static void openBookGUI(ServerPlayer player, Identifier id) {
        missingManual(player);
    }

    public static void openBookEntry(ServerPlayer player, Identifier id, Identifier entry, int page) {
        missingManual(player);
    }

    private static void missingManual(ServerPlayer player) {
        player.sendSystemMessage(ComponentFactory.literal(ChatFormatting.RED + "Patchouli integration is unavailable for Minecraft 26.1.2. No manual present"));
    }
}
