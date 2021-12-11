package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;

import mcjty.lib.gui.widgets.Widget.Dimension;

public abstract class AbstractWidget<P extends AbstractWidget<P>> implements Widget<P> {

    public static final int DEFAULT_BACKGROUND_OFFSET = 256;
    public static final int DEFAULT_FILLED_RECT_THICKNESS = 0;
    public static final int DEFAULT_FILLED_BACKGROUND = -1;
    public static final boolean DEFAULT_BACKGROUND_HORIZONTAL = true;

    protected Rectangle bounds;
    protected int desiredWidth = SIZE_UNKNOWN;
    protected int desiredHeight = SIZE_UNKNOWN;

    protected Minecraft mc;
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

    protected AbstractWidget() {
        this.mc = Minecraft.getInstance();
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
    public P name(String name) {
        this.name = name;
        if (channel == null) {
            channel = name;     // Automatic channel
        }
        return getThis();
    }

    protected P getThis() {
        //noinspection unchecked
        return (P) this;
    }

    @Override
    public P channel(String channel) {
        this.channel = channel;
        return getThis();
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

    protected void drawBox(PoseStack matrixStack, int xx, int yy, int color) {
        GuiComponent.fill(matrixStack, xx, yy, xx, yy + bounds.height, color);
        GuiComponent.fill(matrixStack, xx + bounds.width, yy, xx + bounds.width, yy + bounds.height, color);
        GuiComponent.fill(matrixStack, xx, yy, xx + bounds.width, yy, color);
        GuiComponent.fill(matrixStack, xx, yy + bounds.height, xx + bounds.width, yy + bounds.height, color);
    }

    @Override
    public Widget<?> getWidgetAtPosition(double x, double y) {
        return this;
    }

    @Override
    public P tooltips(String... tooltips) {
        if (tooltips.length > 0) {
            this.tooltips = new ArrayList<>(tooltips.length);
            Collections.addAll(this.tooltips, tooltips);
        } else {
            this.tooltips = null;
        }
        return getThis();
    }

    @Override
    public P tooltipItems(ItemStack... items) {
        if (items.length > 0) {
            this.items = new ArrayList<>(items.length);
            Collections.addAll(this.items, items);
        } else {
            this.items = Collections.emptyList();
        }
        return getThis();
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
    public P hovering(boolean hovering) {
        this.hovering = hovering;
        return getThis();
    }

    @Override
    public P enabled(boolean enabled) {
        this.enabled = enabled;
        return getThis();
    }

    @Override
    public P enabledFlags(String... flags) {
        for (String flag : flags) {
            enableFlags.add(StringRegister.STRINGS.get(flag));
        }
        return getThis();
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
    public P visible(boolean visible) {
        this.visible = visible;
        return getThis();
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
    public P desiredWidth(int desiredWidth) {
        this.desiredWidth = desiredWidth;
        return getThis();
    }

    @Override
    public int getDesiredHeight() {
        return desiredHeight;
    }

    @Override
    public P desiredHeight(int desiredHeight) {
        this.desiredHeight = desiredHeight;
        return getThis();
    }

    /**
     * Use this for a textured background.
     * @param bg
     * @return
     */
    public P background(ResourceLocation bg) {
        return setBackgrounds(bg, null);
    }

    public P setBackgrounds(ResourceLocation bg1, ResourceLocation bg2) {
        this.background1 = bg1;
        this.background2 = bg2;
        this.background2Horizontal = true;
        this.backgroundOffset = 256;
        return getThis();
    }

    public P setBackgroundLayout(boolean horizontal, int offset) {
        this.background2Horizontal = horizontal;
        this.backgroundOffset = offset;
        return getThis();
    }

    /**
     * Use this instead of a textured background.
     * @param thickness use 0 to disable
     * @return
     */
    public P filledRectThickness(int thickness) {
        filledRectThickness = thickness;
        return getThis();
    }

    public int getFilledBackground() {
        return filledBackground;
    }

    public int getFilledBackground2() {
        return filledBackground2;
    }

    public P filledBackground(int filledBackground) {
        this.filledBackground = filledBackground;
        this.filledBackground2 = -1;
        return getThis();
    }

    public P filledBackground(int filledBackground, int filledBackground2) {
        this.filledBackground = filledBackground;
        this.filledBackground2 = filledBackground2;
        return getThis();
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    @Override
    public void bounds(int x, int y, int w, int h) {
        this.bounds = new Rectangle(x, y, w, h);
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public boolean in(double x, double y) {
        if (bounds == null) {
            return false;
        } else {
            return bounds.contains(x, y);
        }
    }

    protected void drawBackground(Screen gui, PoseStack matrixStack, int x, int y, int w, int h) {
        if (!visible) {
            return;
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int xx = x + bounds.x;
        int yy = y + bounds.y;
        if (background1 != null) {
            mc.getTextureManager().bindForSetup(background1);
            if (background2 == null) {
                gui.blit(matrixStack, xx, yy, 0, 0, w, h);
            } else {
                if (background2Horizontal) {
                    gui.blit(matrixStack, xx, yy, 0, 0, backgroundOffset, h);
                    mc.getTextureManager().bindForSetup(background2);
                    gui.blit(matrixStack, xx + backgroundOffset, yy, 0, 0, w - backgroundOffset, h);
                } else {
                    gui.blit(matrixStack, xx, yy, 0, 0, w, backgroundOffset);
                    mc.getTextureManager().bindForSetup(background2);
                    gui.blit(matrixStack, xx, yy + backgroundOffset, 0, 0, w, h - backgroundOffset);
                }
            }
        } else if (filledRectThickness > 0) {
            RenderHelper.drawThickBeveledBox(matrixStack, xx, yy, xx + w - 1, yy + h - 1, filledRectThickness, StyleConfig.colorBackgroundBevelBright, StyleConfig.colorBackgroundBevelDark, filledBackground == -1 ? StyleConfig.colorBackgroundFiller : filledBackground);
        } else if (filledRectThickness < 0) {
            RenderHelper.drawThickBeveledBox(matrixStack, xx, yy, xx + w - 1, yy + h - 1, -filledRectThickness, StyleConfig.colorBackgroundBevelDark, StyleConfig.colorBackgroundBevelBright, filledBackground == -1 ? StyleConfig.colorBackgroundFiller : filledBackground);
        } else if (filledBackground != -1) {
            RenderHelper.drawHorizontalGradientRect(matrixStack, xx, yy, xx + w - 1, yy + h - 1, filledBackground, filledBackground2 == -1 ? filledBackground : filledBackground2);
        }
    }

    protected void drawBackground(Screen gui, PoseStack matrixStack, int x, int y) {
        if (!visible) {
            return;
        }
        drawBackground(gui, matrixStack, x, y, bounds.width, bounds.height);
    }

    protected void drawStyledBoxNormal(Window window, PoseStack matrixStack, int x1, int y1, int x2, int y2) {
        drawStyledBox(window, matrixStack, x1, y1, x2, y2,
                StyleConfig.colorButtonBorderTopLeft, StyleConfig.colorButtonFiller, StyleConfig.colorButtonFillerGradient1, StyleConfig.colorButtonFillerGradient2, StyleConfig.colorButtonBorderBottomRight);
    }

    protected void drawStyledBoxNormal(Window window, PoseStack matrixStack, int x1, int y1, int x2, int y2, int averageOverride) {
        drawStyledBox(window, matrixStack, x1, y1, x2, y2,
                StyleConfig.colorButtonBorderTopLeft, averageOverride, averageOverride, averageOverride, StyleConfig.colorButtonBorderBottomRight);
    }

    protected void drawStyledBoxSelected(Window window, PoseStack matrixStack, int x1, int y1, int x2, int y2) {
        drawStyledBox(window, matrixStack, x1, y1, x2, y2,
                StyleConfig.colorButtonSelectedBorderTopLeft, StyleConfig.colorButtonSelectedFiller, StyleConfig.colorButtonSelectedFillerGradient1, StyleConfig.colorButtonSelectedFillerGradient2, StyleConfig.colorButtonSelectedBorderBottomRight);
    }

    protected void drawStyledBoxHovering(Window window, PoseStack matrixStack, int x1, int y1, int x2, int y2) {
        drawStyledBox(window, matrixStack, x1, y1, x2, y2,
                StyleConfig.colorButtonHoveringBorderTopLeft, StyleConfig.colorButtonHoveringFiller, StyleConfig.colorButtonHoveringFillerGradient1, StyleConfig.colorButtonHoveringFillerGradient2, StyleConfig.colorButtonHoveringBorderBottomRight);
    }

    protected void drawStyledBoxDisabled(Window window, PoseStack matrixStack, int x1, int y1, int x2, int y2) {
        drawStyledBox(window, matrixStack, x1, y1, x2, y2,
                StyleConfig.colorButtonDisabledBorderTopLeft, StyleConfig.colorButtonDisabledFiller, StyleConfig.colorButtonDisabledFillerGradient1, StyleConfig.colorButtonDisabledFillerGradient2, StyleConfig.colorButtonDisabledBorderBottomRight);
    }

    private void drawStyledBox(Window window, PoseStack matrixStack, int x1, int y1, int x2, int y2, int bright, int average, int average1, int average2, int dark) {
        switch (window.getCurrentStyle()) {
            case STYLE_BEVEL:
                RenderHelper.drawThinButtonBox(matrixStack, x1, y1, x2, y2, bright, average, dark);
                break;
            case STYLE_BEVEL_GRADIENT:
                RenderHelper.drawThinButtonBoxGradient(matrixStack, x1, y1, x2, y2, bright, average1, average2, dark);
                break;
            case STYLE_FLAT:
                RenderHelper.drawFlatButtonBox(matrixStack, x1, y1, x2, y2, bright, average, dark);
                break;
            case STYLE_FLAT_GRADIENT:
                RenderHelper.drawFlatButtonBoxGradient(matrixStack, x1, y1, x2, y2, bright, average1, average2, dark);
                break;
            case STYLE_THICK:
                RenderHelper.drawThickButtonBox(matrixStack, x1, y1, x2, y2, bright, average, dark);
                break;
        }
    }

    @Override
    public void draw(Screen gui, PoseStack matrixStack, int x, int y) {
        drawBackground(gui, matrixStack, x, y);
    }

    @Override
    public void drawPhase2(Screen gui, PoseStack matrixStack, int x, int y) {

    }

    @Override
    public Widget<?> mouseClick(double x, double y, int button) {
        return null;
    }

    @Override
    public void mouseRelease(double x, double y, int button) {
    }

    @Override
    public void mouseMove(double x, double y) {
    }

    @Override
    public boolean mouseScrolled(double x, double y, double amount) {
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
    @Override
    public void markLayoutDirty() {
        layoutDirty = true;
    }

    void markClean() {
        layoutDirty = false;
    }

    boolean isDirty() {
        return layoutDirty;
    }

    @Override
    public P hint(LayoutHint hint) {
        layoutHint = hint;
        layoutDirty = true;
        return getThis();
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
    public P userObject(Object obj) {
        userObject = obj;
        return getThis();
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
        command.findCommand("hint").ifPresent(cmd -> hint(
                cmd.getOptionalPar(0, 0),
                cmd.getOptionalPar(1, 0),
                cmd.getOptionalPar(2, 40),
                cmd.getOptionalPar(3, 40)
        ));
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
