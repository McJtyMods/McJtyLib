package mcjty.lib.gui.widgets;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ImageEvent;
import mcjty.lib.varia.JSonTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageLabel<P extends ImageLabel<P>> extends AbstractWidget<P> {

    public static final String TYPE_IMAGELABEL = "imagelabel";
    public static final int DEFAULT_TXTDIM = 256;

    private boolean dragging = false;
    private ResourceLocation image = null;
    private int u;
    private int v;
    private int txtWidth = DEFAULT_TXTDIM;
    private int txtHeight = DEFAULT_TXTDIM;
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
            dragging = true;
            int u = x - bounds.x;
            int v = y - bounds.y;
            fireImageEvents(u, v, pickColor(u, v));
            return this;
        }
        return null;
    }

    @Override
    public void mouseMove(int x, int y) {
        if (dragging && isEnabledAndVisible()) {
            int u = x - bounds.x;
            int v = y - bounds.y;
            fireImageEvents(u, v, pickColor(u, v));
        }
    }

    @Override
    public void mouseRelease(int x, int y, int button) {
        super.mouseRelease(x, y, button);
        if (dragging) {
            dragging = false;
            int u = x - bounds.x;
            int v = y - bounds.y;
            fireImageEvents(u, v, pickColor(u, v));
        }
    }

    private int pickColor(int u, int v) {
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
        return color;
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
            if (u < 0 || v < 0 || u >= txtWidth || v >= txtHeight) {
                return;
            }
            for (ImageEvent event : imageEvents) {
                event.imageClicked(this, u, v, color);
            }
        }
    }


    @Override
    public void readFromJSon(JsonObject object) {
        super.readFromJSon(object);
        if (object.has("image")) {
            image = new ResourceLocation(object.get("image").getAsString());
        }
        u = JSonTools.get(object, "u", 0);
        v = JSonTools.get(object, "v", 0);
        txtWidth = JSonTools.get(object, "txtw", DEFAULT_TXTDIM);
        txtHeight = JSonTools.get(object, "txth", DEFAULT_TXTDIM);
    }

    @Override
    public JsonObject writeToJSon() {
        JsonObject object = super.writeToJSon();
        object.add("type", new JsonPrimitive(TYPE_IMAGELABEL));
        if (image != null) {
            object.add("image", new JsonPrimitive(image.toString()));
        }
        JSonTools.put(object, "u", u, null);
        JSonTools.put(object, "v", v, null);
        JSonTools.put(object, "txtw", txtWidth, DEFAULT_TXTDIM);
        JSonTools.put(object, "txth", txtHeight, DEFAULT_TXTDIM);
        return object;
    }
}
