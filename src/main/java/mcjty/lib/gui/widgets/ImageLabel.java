package mcjty.lib.gui.widgets;

import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ImageEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageLabel<P extends ImageLabel<P>> extends AbstractWidget<P> {
    private ResourceLocation image = null;
    private int u;
    private int v;
    private int txtWidth = 256;
    private int txtHeight = 256;
    private List<ImageEvent> imageEvents = null;
    private BufferedImage bufferedImage;

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

    public P setTextureDimensions(int tw, int th) {
        this.txtWidth = tw;
        this.txtHeight = th;
        return (P) this;
    }

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        if (isEnabledAndVisible()) {
            int u = x - bounds.x;
            int v = y - bounds.y;
            if (bufferedImage == null) {
                try {
                    IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(image);
                    bufferedImage = ImageIO.read(resource.getInputStream());
                } catch (IOException e) {
                }
            }

            int color = 0;
            if (bufferedImage != null) {
                color = bufferedImage.getRGB(u, v);
            }

            fireImageEvents(u, v, color);
            return this;
        }
        return null;
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
            gui.drawModalRectWithCustomSizedTexture(xx, yy, u, v, bounds.width, bounds.height, txtWidth, txtHeight);
        }
    }

    public ImageLabel<P> addImageEvent(ImageEvent event) {
        if (imageEvents == null) {
            imageEvents = new ArrayList<>();
        }
        imageEvents.add(event);
        return this;
    }

    public void removeImageEvent(ImageEvent event) {
        if (imageEvents != null) {
            imageEvents.remove(event);
        }
    }

    private void fireImageEvents(int u, int v, int color) {
        if (imageEvents != null) {
            for (ImageEvent event : imageEvents) {
                event.imageClicked(this, u, v, color);
            }
        }
    }
}
