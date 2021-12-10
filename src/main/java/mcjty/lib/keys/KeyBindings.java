package mcjty.lib.keys;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;


public class KeyBindings {

    public static KeyMapping openManual;

    public static void init() {
        openManual = new KeyMapping("key.openManual", KeyConflictContext.GUI, InputConstants.getKey("key.keyboard.f1"), "key.categories.mcjtylib");
        ClientRegistry.registerKeyBinding(openManual);
    }
}
