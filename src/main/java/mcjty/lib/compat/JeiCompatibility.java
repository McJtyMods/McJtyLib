package mcjty.lib.compat;

import mcjty.lib.McJtyLib;
import mcjty.lib.gui.GenericGuiContainer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.ResourceLocation;

import java.util.List;

@JeiPlugin
public class JeiCompatibility implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(McJtyLib.MODID, "mcjtylib");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // @todo 1.16
//        registration.addGuiContainerHandler(GenericGuiContainer.class, new IGuiContainerHandler<GenericGuiContainer>() {
//            @Override
//            public List<Rectangle2d> getGuiExtraAreas(GenericGuiContainer containerScreen) {
//                return containerScreen.getExtraWindowBounds();
//            }
//        });
    }
}
