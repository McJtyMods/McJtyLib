package mcjty.lib.gui.widgets;

import mcjty.lib.gui.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ImageLabel<P extends ImageLabel<P>> extends AbstractWidget<P> {
    private ResourceLocation image = null;
    private int u;
    private int v;

    public ImageLabel(Minecraft mc, Gui gui) {
        super(mc, gui);
    }

    public ResourceLocation getImage() {
        return image;
    }

    public P setImage(ResourceLocation image, int u, int v) {
        this.image = image;
        this.u = u;
        this.v = v;
        return (P) this;
    }

    @Override
    public void draw(Window window, int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(window, x, y);

        if (image != null) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(image);
            int xx = x + bounds.x;
            int yy = y + bounds.y;
            gui.drawTexturedModalRect(xx, yy, u, v, bounds.width, bounds.height);
        }
    }

}
