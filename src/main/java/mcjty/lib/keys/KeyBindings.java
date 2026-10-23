package mcjty.lib.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;


public class KeyBindings {

    public static KeyMapping openManual;

    public static void init() {
        openManual = new KeyMapping("key.openManual", InputConstants.Type.KEYBOARD, InputConstants.KEY_F1, KeyMapping.Category.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("mcjtylib", "keys")));
        net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping(openManual);
    }
}
