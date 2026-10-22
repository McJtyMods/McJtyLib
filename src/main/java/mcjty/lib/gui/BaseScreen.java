package mcjty.lib.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class BaseScreen extends Screen {

    public BaseScreen(Component pTitle) {
        super(pTitle);
    }

    protected abstract void renderInternal(GuiGraphicsExtractor graphics, int pMouseX, int pMouseY, float pPartialTick);

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.extractRenderState(graphics, pMouseX, pMouseY, pPartialTick);
        renderInternal(graphics, pMouseX, pMouseY, pPartialTick);
    }
}
