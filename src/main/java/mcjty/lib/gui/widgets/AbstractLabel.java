package mcjty.lib.gui.widgets;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.VerticalAlignment;
import mcjty.lib.typed.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractLabel<P extends AbstractLabel<P>> extends AbstractWidget<P> {

    public static final String TYPE_LABEL = "label";

    public static final HorizontalAlignment DEFAULT_HORIZONTAL_ALIGN = HorizontalAlignment.ALIGN_CENTER;
    public static final VerticalAlignment DEFAULT_VERTICAL_ALIGN = VerticalAlignment.ALIGN_CENTER;
    public static final boolean DEFAULT_DYNAMIC = false;

    private String text;
    private Integer color = null;
    private Integer disabledColor = null;
    private HorizontalAlignment horizontalAlignment = DEFAULT_HORIZONTAL_ALIGN;
    private VerticalAlignment verticalAlignment = DEFAULT_VERTICAL_ALIGN;
    private boolean dynamic = DEFAULT_DYNAMIC;        // The size of this label is dynamic and not based on the contents

    private int txtDx = 0;
    private int txtDy = 0;

    private ResourceLocation image = null;
    private int u;
    private int v;
    private int iw;
    private int ih;


    public AbstractLabel(Minecraft mc, Gui gui) {
        super(mc, gui);
    }

    public ResourceLocation getImage() {
        return image;
    }

    public P setImage(ResourceLocation image, int u, int v, int iw, int ih) {
        this.image = image;
        this.u = u;
        this.v = v;
        this.iw = iw;
        this.ih = ih;
        return (P) this;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public P setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
        return (P) this;
    }

    @Override
    public int getDesiredWidth() {
        int w = super.getDesiredWidth();
        if (dynamic) {
            return w;
        }
        if (w == -1) {
            w = mc.fontRenderer.getStringWidth(text)+6;
        }
        return w;
    }

    @Override
    public int getDesiredHeight() {
        int h = super.getDesiredHeight();
        if (dynamic) {
            return h;
        }
        if (h == -1) {
            h = mc.fontRenderer.FONT_HEIGHT+2;
        }
        return h;
    }

    public String getText() {
        return text;
    }

    public P setText(String text) {
        this.text = text;
        return (P) this;
    }

    public P setTextOffset(int ox, int oy) {
        txtDx = ox;
        txtDy = oy;
        return (P) this;
    }

    public int getColor() {
        return color == null ? StyleConfig.colorTextNormal : color;
    }

    public P setColor(int color) {
        this.color = color;
        return (P) this;
    }

    public int getDisabledColor() {
        return disabledColor == null ? StyleConfig.colorTextDisabled : disabledColor;
    }

    public P setDisabledColor(int disabledColor) {
        this.disabledColor = disabledColor;
        return (P) this;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public P setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return (P) this;
    }

    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public P setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        return (P) this;
    }

    @Override
    public void draw(int x, int y) {
        drawOffset(x, y, 0, 0);
    }

    public void drawOffset(int x, int y, int offsetx, int offsety) {
        if (!visible) {
            return;
        }
        super.draw(x, y);

        int dx = calculateHorizontalOffset() + offsetx + txtDx;
        int dy = calculateVerticalOffset() + offsety + txtDy;

        if (image != null) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(image);
            int xx = x + bounds.x + (bounds.width-iw) / 2;
            int yy = y + bounds.y + (bounds.height-ih) / 2;
            gui.drawTexturedModalRect(xx, yy, u, v, iw, ih);
        }

        int col = getColor();
        if (!isEnabled()) {
            col = getDisabledColor();
        }

        if (text == null) {
            mc.fontRenderer.drawString("", x+dx+bounds.x, y+dy+bounds.y, col);
        } else {
            mc.fontRenderer.drawString(mc.fontRenderer.trimStringToWidth(text, bounds.width), x + dx + bounds.x, y + dy + bounds.y, col);
        }
    }

    private int calculateVerticalOffset() {
        if (verticalAlignment != VerticalAlignment.ALIGN_TOP) {
            int h = mc.fontRenderer.FONT_HEIGHT;
            if (verticalAlignment == VerticalAlignment.ALIGN_BOTTOM) {
                return bounds.height - h;
            } else {
                return (bounds.height - h)/2;
            }
        } else {
            return 0;
        }
    }

    private int calculateHorizontalOffset() {
        if (horizontalAlignment != HorizontalAlignment.ALIGN_LEFT) {
            int w = mc.fontRenderer.getStringWidth(text);
            if (horizontalAlignment == HorizontalAlignment.ALIGN_RIGHT) {
                return bounds.width - w;
            } else {
                return (bounds.width - w)/2;
            }
        } else {
            return 0;
        }
    }


    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        text = command.getOptionalPar(1, "");
        color = GuiParser.get(command, "color", null);
        disabledColor = GuiParser.get(command, "disabledcolor", null);
        horizontalAlignment = HorizontalAlignment.getByName(GuiParser.get(command, "horizalign", DEFAULT_HORIZONTAL_ALIGN.name()));
        verticalAlignment = VerticalAlignment.getByName(GuiParser.get(command, "vertalign", DEFAULT_VERTICAL_ALIGN.name()));
        dynamic = GuiParser.get(command, "dynamic", DEFAULT_DYNAMIC);
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        command.parameter(text);
        GuiParser.put(command, "color", color, null);
        GuiParser.put(command, "disabledcolor", disabledColor, null);
        GuiParser.put(command, "horizalign", horizontalAlignment.name(), DEFAULT_HORIZONTAL_ALIGN.name());
        GuiParser.put(command, "vertalign", verticalAlignment.name(), DEFAULT_VERTICAL_ALIGN.name());
        GuiParser.put(command, "dynamic", dynamic, DEFAULT_DYNAMIC);
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_LABEL);
    }

    @Override
    public <T> void setGenericValue(T value) {
        if (value == null) {
            setText("");
        } else {
            setText(value.toString());
        }
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        return getText();
    }
}
