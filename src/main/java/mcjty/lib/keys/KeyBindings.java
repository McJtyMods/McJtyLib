package mcjty.lib.keys;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;


public class KeyBindings {

    public static KeyBinding openManual;

    public static void init() {
        openManual = new KeyBinding("key.openManual", KeyConflictContext.GUI, InputMappings.getKey("key.keyboard.f1"), "key.categories.mcjtylib");
        ClientRegistry.registerKeyBinding(openManual);
    }
}
