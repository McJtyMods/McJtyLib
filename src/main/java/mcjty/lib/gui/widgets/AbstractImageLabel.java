package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ImageEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractImageLabel<P extends AbstractImageLabel<P>> extends AbstractWidget<P> {

    public static final String TYPE_IMAGELABEL = "imagelabel";
    public static final Key<Integer> PARAM_U = new Key<>("u", Type.INTEGER);
    public static final Key<Integer> PARAM_V = new Key<>("v", Type.INTEGER);

    public static final int DEFAULT_TXTDIM = 256;

    private boolean dragging = false;
    private ResourceLocation image = null;
    private int u;
    private int v;
    private int txtWidth = DEFAULT_TXTDIM;
    private int txtHeight = DEFAULT_TXTDIM;
    private List<ImageEvent> imageEvents = null;
    private BufferedImage bufferedImage;

    public AbstractImageLabel(Minecraft mc, Screen gui) {
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
    public Widget<?> mouseClick(double x, double y, int button) {
        if (isEnabledAndVisible()) {
            dragging = true;
            int u = (int) (x - bounds.x);
            int v = (int) (y - bounds.y);
            fireImageEvents("click", u, v, pickColor(u, v));
            return this;
        }
        return null;
    }

    @Override
    public void mouseMove(double x, double y) {
        if (dragging && in(x, y) && isEnabledAndVisible()) {
            int u = (int) (x - bounds.x);
            int v = (int) (y - bounds.y);
            fireImageEvents("move", u, v, pickColor(u, v));
        }
    }

    @Override
    public void mouseRelease(double x, double y, int button) {
        super.mouseRelease(x, y, button);
        if (dragging) {
            dragging = false;
            if(in(x, y)) {
                int u = (int) (x - bounds.x);
                int v = (int) (y - bounds.y);
                fireImageEvents("release", u, v, pickColor(u, v));
            }
        }
    }

    private int pickColor(int u, int v) {
        if (bufferedImage == null) {
            try {
                IResource resource = Minecraft.getInstance().getResourceManager().getResource(image);
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
    public void draw(int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(x, y);

        if (image != null) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(image);
            int xx = x + bounds.x;
            int yy = y + bounds.y;
            gui.blit(xx, yy, u, v, bounds.width, bounds.height, txtWidth, txtHeight);
        }
    }

    public AbstractImageLabel<P> addImageEvent(ImageEvent event) {
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

    private void fireImageEvents(String id, int u, int v, int color) {
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, id)
                .put(PARAM_U, u)
                .put(PARAM_V, v)
                .build());
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
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        command.findCommand("image").ifPresent(cmd -> image = new ResourceLocation(cmd.getOptionalPar(0, "")));
        command.findCommand("dim").ifPresent(cmd -> {
            u = cmd.getOptionalPar(0, 0);
            v = cmd.getOptionalPar(1, 0);
            txtWidth  = cmd.getOptionalPar(2, DEFAULT_TXTDIM);
            txtHeight = cmd.getOptionalPar(3, DEFAULT_TXTDIM);
        });
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        if (image != null) {
            command.command(new GuiParser.GuiCommand("image").parameter(image.toString()));
        }
        GuiParser.GuiCommand dimCmd = new GuiParser.GuiCommand("dim").parameter(u).parameter(v).parameter(txtWidth).parameter(txtHeight);
        command.command(dimCmd);
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_IMAGELABEL);
    }

    @Override
    public <T> void setGenericValue(T value) {
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        return null;
    }
}
