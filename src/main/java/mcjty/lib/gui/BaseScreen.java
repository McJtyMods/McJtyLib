package mcjty.lib.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class BaseScreen extends Screen {

    public BaseScreen(Component pTitle) {
        super(pTitle);
    }

    protected abstract void renderInternal(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick);

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderInternal(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }
}
