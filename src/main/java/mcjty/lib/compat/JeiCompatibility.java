package mcjty.lib.compat;

import mcjty.lib.gui.GenericGuiContainer;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IAdvancedGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

@JEIPlugin
public class JeiCompatibility extends BlankModPlugin {
	@Override
	public void register(@Nonnull IModRegistry registry) {
		registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler<GenericGuiContainer>() {
			@Nonnull
			@Override
			public Class<GenericGuiContainer> getGuiContainerClass() {
				return GenericGuiContainer.class;
			}

			@Nullable
			@Override
			public List<Rectangle> getGuiExtraAreas(GenericGuiContainer guiContainer) {
				GenericGuiContainer<?> container = guiContainer;
				return container.getSideWindowBounds();
			}

			@Nullable
			@Override
			public Object getIngredientUnderMouse(GenericGuiContainer guiContainer, int mouseX, int mouseY) {
				return null;
			}
		});
	}
}
