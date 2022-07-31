package mcjty.lib.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;


public class KeyBindings {

    public static KeyMapping openManual;

    public static void init(RegisterKeyMappingsEvent event) {
        openManual = new KeyMapping("key.openManual", KeyConflictContext.GUI, InputConstants.getKey("key.keyboard.f1"), "key.categories.mcjtylib");
        event.register(openManual);
    }
}
