package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.GuiParser.GuiCommand;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.LayoutHint;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.ItemStackTools;
import mcjty.lib.varia.StringRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.awt.Rectangle;
import java.util.*;

public abstract class AbstractWidget<P extends AbstractWidget<P>> implements Widget<P> {

    public static final int DEFAULT_BACKGROUND_OFFSET = 256;
    public static final int DEFAULT_FILLED_RECT_THICKNESS = 0;
    public static final int DEFAULT_FILLED_BACKGROUND = -1;
    public static final boolean DEFAULT_BACKGROUND_HORIZONTAL = true;

    protected Rectangle bounds;
    protected int desiredWidth = SIZE_UNKNOWN;
    protected int desiredHeight = SIZE_UNKNOWN;

    protected Minecraft mc;
    protected Screen gui;
    protected Window window;

    private LayoutHint layoutHint = null;
    private boolean enabled = true;
    private boolean hovering = false;
    protected boolean visible = true;
    protected List<String> tooltips = null;
    protected List<ItemStack> items = null;
    private Set<Integer> enableFlags = new HashSet<>();
    private String name;
    private String channel;

    private boolean layoutDirty = true;
    private Object userObject = null;

    private ResourceLocation background1 = null;
    private ResourceLocation background2 = null;
    private boolean background2Horizontal = DEFAULT_BACKGROUND_HORIZONTAL;
    private int backgroundOffset = DEFAULT_BACKGROUND_OFFSET;
    private int filledRectThickness = DEFAULT_FILLED_RECT_THICKNESS;
    private int filledBackground = DEFAULT_FILLED_BACKGROUND;
    private int filledBackground2 = DEFAULT_FILLED_BACKGROUND;

    // Bevel:           vvv
    // Bevel gradient:  vvvv
    // Flat:            vvvvvvvvvvvvvv
    // Flat gradient:   vvvvvvvv
    // Thick:           vv

    @Override
    public boolean containsWidget(Widget<?> w) {
        return w == this;
    }

    protected AbstractWidget(Minecraft mc, Screen gui) {
        this.mc = mc;
        this.gui = gui;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Window getWindow() {
        return window;
    }

    @Override
    public void setWindow(Window window) {
        this.window = window;
    }

    @Override
    public P setName(String name) {
        this.name = name;
        if (channel == null) {
            channel = name;     // Automatic channel
        }
        return (P) this;
    }

    @Override
    public P setChannel(String channel) {
        this.channel = channel;
        return (P) this;
    }

    protected void fireChannelEvents() {
        fireChannelEvents(TypedMap.EMPTY);
    }

    protected void fireChannelEvents(String id) {
        fireChannelEvents(TypedMap.builder().put(Window.PARAM_ID, id).build());
    }

    protected void fireChannelEvents(@Nonnull TypedMap params) {
        if (window != null && channel != null) {
            window.fireChannelEvents(channel, this, params);
        }
    }

    protected void drawBox(int xx, int yy, int color) {
        Screen.fill(xx, yy, xx, yy + bounds.height, color);
        Screen.fill(xx + bounds.width, yy, xx + bounds.width, yy + bounds.height, color);
        Screen.fill(xx, yy, xx + bounds.width, yy, color);
        Screen.fill(xx, yy + bounds.height, xx + bounds.width, yy + bounds.height, color);
    }

    @Override
    public Widget<?> getWidgetAtPosition(int x, int y) {
        return this;
    }

    @Override
    public P setTooltips(String... tooltips) {
        if (tooltips.length > 0) {
            this.tooltips = new ArrayList<>(tooltips.length);
            Collections.addAll(this.tooltips, tooltips);
        } else {
            this.tooltips = null;
        }
        return (P) this;
    }

    @Override
    public P setTooltipItems(ItemStack... items) {
        if (items.length > 0) {
            this.items = new ArrayList<>(items.length);
            Collections.addAll(this.items, items);
        } else {
            this.items = Collections.emptyList();
        }
        return (P) this;
    }

    @Override
    public List<String> getTooltips() {
        return tooltips;
    }

    @Override
    public List<ItemStack> getTooltipItems() {
        return items;
    }

    @Override
    public boolean isHovering() {
        return hovering;
    }

    @Override
    public P setHovering(boolean hovering) {
        this.hovering = hovering;
        return (P) this;
    }

    @Override
    public P setEnabled(boolean enabled) {
        this.enabled = enabled;
        return (P) this;
    }

    @Override
    public P setEnabledFlags(String... flags) {
        for (String flag : flags) {
            enableFlags.add(StringRegister.STRINGS.get(flag));
        }
        return (P) this;
    }

    @Nonnull
    @Override
    public Set<Integer> getEnabledFlags() {
        return enableFlags;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isEnabledAndVisible() {
        return enabled && visible;
    }

    @Override
    public P setVisible(boolean visible) {
        this.visible = visible;
        return (P) this;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public int getDesiredSize(Dimension dimension) {
        if (dimension == Dimension.DIMENSION_WIDTH) {
            return getDesiredWidth();
        } else {
            return getDesiredHeight();
        }
    }

    @Override
    public int getDesiredWidth() {
        return desiredWidth;
    }

    @Override
    public P setDesiredWidth(int desiredWidth) {
        this.desiredWidth = desiredWidth;
        return (P) this;
    }

    @Override
    public int getDesiredHeight() {
        return desiredHeight;
    }

    @Override
    public P setDesiredHeight(int desiredHeight) {
        this.desiredHeight = desiredHeight;
        return (P) this;
    }

    /**
     * Use this for a textured background.
     * @param bg
     * @return
     */
    public P setBackground(ResourceLocation bg) {
        return setBackgrounds(bg, null);
    }

    public P setBackgrounds(ResourceLocation bg1, ResourceLocation bg2) {
        this.background1 = bg1;
        this.background2 = bg2;
        this.background2Horizontal = true;
        this.backgroundOffset = 256;
        return (P) this;
    }

    public P setBackgroundLayout(boolean horizontal, int offset) {
        this.background2Horizontal = horizontal;
        this.backgroundOffset = offset;
        return (P) this;
    }

    /**
     * Use this instead of a textured background.
     * @param thickness use 0 to disable
     * @return
     */
    public P setFilledRectThickness(int thickness) {
        filledRectThickness = thickness;
        return (P) this;
    }

    public int getFilledBackground() {
        return filledBackground;
    }

    public int getFilledBackground2() {
        return filledBackground2;
    }

    public P setFilledBackground(int filledBackground) {
        this.filledBackground = filledBackground;
        this.filledBackground2 = -1;
        return (P) this;
    }

    public P setFilledBackground(int filledBackground, int filledBackground2) {
        this.filledBackground = filledBackground;
        this.filledBackground2 = filledBackground2;
        return (P) this;
    }

    @Override
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public boolean in(int x, int y) {
        if (bounds == null) {
            return false;
        } else {
            return bounds.contains(x, y);
        }
    }

    protected void drawBackground(int x, int y, int w, int h) {
        if (!visible) {
            return;
        }
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        int xx = x + bounds.x;
        int yy = y + bounds.y;
        if (background1 != null) {
            mc.getTextureManager().bindTexture(background1);
            if (background2 == null) {
                gui.blit(xx, yy, 0, 0, w, h);
            } else {
                if (background2Horizontal) {
                    gui.blit(xx, yy, 0, 0, backgroundOffset, h);
                    mc.getTextureManager().bindTexture(background2);
                    gui.blit(xx + backgroundOffset, yy, 0, 0, w - backgroundOffset, h);
                } else {
                    gui.blit(xx, yy, 0, 0, w, backgroundOffset);
                    mc.getTextureManager().bindTexture(background2);
                    gui.blit(xx, yy + backgroundOffset, 0, 0, w, h - backgroundOffset);
                }
            }
        } else if (filledRectThickness > 0) {
            RenderHelper.drawThickBeveledBox(xx, yy, xx + w - 1, yy + h - 1, filledRectThickness, StyleConfig.colorBackgroundBevelBright, StyleConfig.colorBackgroundBevelDark, filledBackground == -1 ? StyleConfig.colorBackgroundFiller : filledBackground);
        } else if (filledRectThickness < 0) {
            RenderHelper.drawThickBeveledBox(xx, yy, xx + w - 1, yy + h - 1, -filledRectThickness, StyleConfig.colorBackgroundBevelDark, StyleConfig.colorBackgroundBevelBright, filledBackground == -1 ? StyleConfig.colorBackgroundFiller : filledBackground);
        } else if (filledBackground != -1) {
            RenderHelper.drawHorizontalGradientRect(xx, yy, xx + w - 1, yy + h - 1, filledBackground, filledBackground2 == -1 ? filledBackground : filledBackground2);
        }
    }

    protected void drawBackground(int x, int y) {
        if (!visible) {
            return;
        }
        drawBackground(x, y, bounds.width, bounds.height);
    }

    protected void drawStyledBoxNormal(Window window, int x1, int y1, int x2, int y2) {
        drawStyledBox(window, x1, y1, x2, y2,
                StyleConfig.colorButtonBorderTopLeft, StyleConfig.colorButtonFiller, StyleConfig.colorButtonFillerGradient1, StyleConfig.colorButtonFillerGradient2, StyleConfig.colorButtonBorderBottomRight);
    }

    protected void drawStyledBoxNormal(Window window, int x1, int y1, int x2, int y2, int averageOverride) {
        drawStyledBox(window, x1, y1, x2, y2,
                StyleConfig.colorButtonBorderTopLeft, averageOverride, averageOverride, averageOverride, StyleConfig.colorButtonBorderBottomRight);
    }

    protected void drawStyledBoxSelected(Window window, int x1, int y1, int x2, int y2) {
        drawStyledBox(window, x1, y1, x2, y2,
                StyleConfig.colorButtonSelectedBorderTopLeft, StyleConfig.colorButtonSelectedFiller, StyleConfig.colorButtonSelectedFillerGradient1, StyleConfig.colorButtonSelectedFillerGradient2, StyleConfig.colorButtonSelectedBorderBottomRight);
    }

    protected void drawStyledBoxHovering(Window window, int x1, int y1, int x2, int y2) {
        drawStyledBox(window, x1, y1, x2, y2,
                StyleConfig.colorButtonHoveringBorderTopLeft, StyleConfig.colorButtonHoveringFiller, StyleConfig.colorButtonHoveringFillerGradient1, StyleConfig.colorButtonHoveringFillerGradient2, StyleConfig.colorButtonHoveringBorderBottomRight);
    }

    protected void drawStyledBoxDisabled(Window window, int x1, int y1, int x2, int y2) {
        drawStyledBox(window, x1, y1, x2, y2,
                StyleConfig.colorButtonDisabledBorderTopLeft, StyleConfig.colorButtonDisabledFiller, StyleConfig.colorButtonDisabledFillerGradient1, StyleConfig.colorButtonDisabledFillerGradient2, StyleConfig.colorButtonDisabledBorderBottomRight);
    }

    private void drawStyledBox(Window window, int x1, int y1, int x2, int y2, int bright, int average, int average1, int average2, int dark) {
        switch (window.getCurrentStyle()) {
            case STYLE_BEVEL:
                RenderHelper.drawThinButtonBox(x1, y1, x2, y2, bright, average, dark);
                break;
            case STYLE_BEVEL_GRADIENT:
                RenderHelper.drawThinButtonBoxGradient(x1, y1, x2, y2, bright, average1, average2, dark);
                break;
            case STYLE_FLAT:
                RenderHelper.drawFlatButtonBox(x1, y1, x2, y2, bright, average, dark);
                break;
            case STYLE_FLAT_GRADIENT:
                RenderHelper.drawFlatButtonBoxGradient(x1, y1, x2, y2, bright, average1, average2, dark);
                break;
            case STYLE_THICK:
                RenderHelper.drawThickButtonBox(x1, y1, x2, y2, bright, average, dark);
                break;
        }
    }

    @Override
    public void draw(int x, int y) {
        drawBackground(x, y);
    }

    @Override
    public void drawPhase2(int x, int y) {

    }

    @Override
    public Widget<?> mouseClick(int x, int y, int button) {
        return null;
    }

    @Override
    public void mouseRelease(int x, int y, int button) {
    }

    @Override
    public void mouseMove(int x, int y) {
    }

    @Override
    public boolean mouseWheel(int amount, int x, int y) {
        return false;
    }

    @Override
    public boolean keyTyped(int keyCode, int scanCode) {
        return false;
    }

    @Override
    public boolean charTyped(char codePoint) {
        return false;
    }

    /**
     * Mark this widget as dirty so that the system knows a new relayout is needed.
     */
    void markDirty() {
        layoutDirty = true;
    }

    void markClean() {
        layoutDirty = false;
    }

    boolean isDirty() {
        return layoutDirty;
    }

    @Override
    public P setLayoutHint(LayoutHint hint) {
        layoutHint = hint;
        layoutDirty = true;
        return (P) this;
    }

    @Override
    public LayoutHint getLayoutHint() {
        return layoutHint;
    }

    @Override
    public Object getUserObject() {
        return userObject;
    }

    @Override
    public P setUserObject(Object obj) {
        userObject = obj;
        return (P) this;
    }

    @Override
    public void readFromGuiCommand(GuiCommand command) {
        name = command.getOptionalPar(0, null);
        if (name != null && name.isEmpty()) {
            name = null;
        }
        channel = GuiParser.get(command, "channel", null);
        if (channel == null) {
            channel = name;
        }
        command.findCommand("desired").ifPresent(cmd -> {
            desiredWidth = cmd.getOptionalPar(0, SIZE_UNKNOWN);
            desiredHeight = cmd.getOptionalPar(1, SIZE_UNKNOWN);
        });
        command.findCommand("enableon").ifPresent(cmd -> {
            enableFlags.clear();
            cmd.parameters().forEach(flag -> enableFlags.add(StringRegister.STRINGS.get((String)flag)));
        });
        command.findCommand("tooltips").ifPresent(cmd -> {
            tooltips = new ArrayList<>();
            for (Object par : cmd.getParameters()) {
                tooltips.add(par.toString());
            }
        });
        command.findCommand("items").ifPresent(cmd -> {
            items = new ArrayList<>();
            for (GuiCommand itemCmd : cmd.getGuiCommands()) {
                items.add(ItemStackTools.guiCommandToItemStack(itemCmd));
            }
        });
        command.findCommand("hint").ifPresent(cmd -> setLayoutHint(new PositionalLayout.PositionalHint(
                cmd.getOptionalPar(0, 0),
                cmd.getOptionalPar(1, 0),
                cmd.getOptionalPar(2, 40),
                cmd.getOptionalPar(3, 40)
        )));
        command.findCommand("bg1").ifPresent(cmd -> background1 = new ResourceLocation(cmd.getOptionalPar(0, "")));
        command.findCommand("bg2").ifPresent(cmd -> background2 = new ResourceLocation(cmd.getOptionalPar(0, "")));
        background2Horizontal = GuiParser.get(command, "bghoriz", DEFAULT_BACKGROUND_HORIZONTAL);
        backgroundOffset = GuiParser.get(command, "bgoffset", DEFAULT_BACKGROUND_OFFSET);
        filledRectThickness = GuiParser.get(command, "bgthickness", DEFAULT_FILLED_RECT_THICKNESS);
        filledBackground = GuiParser.get(command, "bgfilled1", DEFAULT_FILLED_BACKGROUND);
        filledBackground2 = GuiParser.get(command, "bgfilled2", DEFAULT_FILLED_BACKGROUND);
    }

    @Override
    public void fillGuiCommand(GuiCommand command) {
        command.parameter(name != null ? name : "");
        GuiParser.put(command, "channel", channel, null);

        if (desiredWidth != SIZE_UNKNOWN || desiredHeight != SIZE_UNKNOWN) {
            command.command(new GuiCommand("desired").parameter(desiredWidth).parameter(desiredHeight));
        }

        if (layoutHint instanceof PositionalLayout.PositionalHint) {
            PositionalLayout.PositionalHint hint = (PositionalLayout.PositionalHint) layoutHint;
            command.command(new GuiCommand("hint").parameter(hint.getX()).parameter(hint.getY()).parameter(hint.getWidth()).parameter(hint.getHeight()));
        }

        if (!enableFlags.isEmpty()) {
            GuiCommand enabledCmd = new GuiCommand("enableon");
            command.command(enabledCmd);
            for (Integer flag : enableFlags) {
                enabledCmd.parameter(StringRegister.STRINGS.get(flag));
            }
        }

        if (tooltips != null && !tooltips.isEmpty()) {
            GuiCommand ttCmd = new GuiCommand("tooltips");
            command.command(ttCmd);
            for (String s : tooltips) {
                ttCmd.parameter(s);
            }
        }

        if (items != null && !items.isEmpty()) {
            GuiCommand itemsCmd = new GuiCommand("items");
            command.command(itemsCmd);
            for (ItemStack stack : this.items) {
                itemsCmd.command(ItemStackTools.itemStackToGuiCommand("item", stack));
            }
        }
        if (background1 != null) {
            command.command(new GuiCommand("bg1").parameter(background1.toString()));
        }
        if (background2 != null) {
            command.command(new GuiCommand("bg2").parameter(background2.toString()));
        }
        GuiParser.put(command, "bghoriz", background2Horizontal, DEFAULT_BACKGROUND_HORIZONTAL);
        GuiParser.put(command, "bgoffset", backgroundOffset, DEFAULT_BACKGROUND_OFFSET);
        GuiParser.put(command, "bgthickness", filledRectThickness, DEFAULT_FILLED_RECT_THICKNESS);
        GuiParser.put(command, "bgfilled1", filledBackground, DEFAULT_FILLED_BACKGROUND);
        GuiParser.put(command, "bgfilled2", filledBackground2, DEFAULT_FILLED_BACKGROUND);
    }
}
