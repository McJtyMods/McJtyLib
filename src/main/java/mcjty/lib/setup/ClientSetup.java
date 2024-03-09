package mcjty.lib.setup;

import mcjty.lib.ClientEventHandler;
import mcjty.lib.keys.KeyBindings;
import mcjty.lib.keys.KeyInputHandler;
import mcjty.lib.tooltips.ClientTooltipIcon;
import mcjty.lib.tooltips.TooltipRender;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {

    public static void init(FMLClientSetupEvent e) {
        NeoForge.EVENT_BUS.register(new TooltipRender());
        NeoForge.EVENT_BUS.register(new ClientEventHandler());

        NeoForge.EVENT_BUS.register(new KeyInputHandler());

//        ItemBlockRenderTypes.setRenderLayer(Registration.MULTIPART_BLOCK, (RenderType) -> true);
//        Arrays.stream(new RenderType[]{
//                        CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS, CustomRenderTypes.TRANSLUCENT_ADD_NOLIGHTMAPS,
//                        CustomRenderTypes.TRANSLUCENT_ADD, CustomRenderTypes.OVERLAY_LINES, CustomRenderTypes.QUADS_NOTEXTURE})
//                .forEach(type -> {
//                    Minecraft.getInstance().renderBuffers().fixedBuffers.put(type, new BufferBuilder(type.bufferSize()));
//                });
    }

    public static void registerKeyBinds(RegisterKeyMappingsEvent event) {
        KeyBindings.init(event);
    }

    public static void registerClientComponentTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ClientTooltipIcon.class, (a) -> a);
    }

}
