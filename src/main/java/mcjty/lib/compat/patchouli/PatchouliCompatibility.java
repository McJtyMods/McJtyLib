package mcjty.lib.compat.patchouli;

import mcjty.lib.setup.ModSetup;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;


public class PatchouliCompatibility {

    // Patchouli has no 26.2 release yet. Keep callers functional and report the
    // unavailable manual without loading an incompatible 1.21 API.
    public static void openBookGUI(ServerPlayer player, Identifier id) {
        missingManual(player);
    }

    public static void openBookEntry(ServerPlayer player, Identifier id, Identifier entry, int page) {
        missingManual(player);
    }

    private static void missingManual(ServerPlayer player) {
        player.sendSystemMessage(ComponentFactory.literal(ChatFormatting.RED + "Patchouli is unavailable for Minecraft 26.2. No manual present"));
    }
}
