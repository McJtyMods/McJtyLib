package mcjty.lib.gui.icons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ImageIcon implements IIcon {

    private ResourceLocation image = null;
    private int u;
    private int v;
    private int width;
    private int height;
    private final String id;

    public ResourceLocation getImage() {
        return image;
    }

    public ImageIcon(String id) {
        this.id = id;
    }

    public ImageIcon setImage(ResourceLocation image, int u, int v) {
        this.image = image;
        this.u = u;
        this.v = v;
        return this;
    }

    public ImageIcon setDimensions(int w, int h) {
        this.width = w;
        this.height = h;
        return this;
    }

    @Override
    public void draw(Minecraft mc, Gui gui, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(image);
        gui.drawTexturedModalRect(x, y, u, v, width, height);
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public IIcon clone() {
        return new ImageIcon(id).setImage(image, u, v).setDimensions(width, height);
    }
}
