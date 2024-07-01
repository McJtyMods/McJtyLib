package mcjty.lib.compat;

import mcjty.lib.McJtyLib;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

@JeiPlugin
public class JeiCompatibility implements IModPlugin {

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "mcjtylib");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(GenericGuiContainer.class, new Handler<GenericContainer>());
    }

    static class Handler<T extends AbstractContainerMenu> implements IGuiContainerHandler<GenericGuiContainer<?,T>> {
        @Nonnull
        @Override
        public List<Rect2i> getGuiExtraAreas(GenericGuiContainer containerScreen) {
            return containerScreen.getExtraWindowBounds();
        }
    }
}
