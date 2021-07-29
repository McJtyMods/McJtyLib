package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.LayoutHint;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.typed.Type;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * A widget is a rectangular object in a GUI. Can be anything from a simple button to a
 * more complicated table with images and descriptions.
 */
public interface Widget<P extends Widget<P>> {
    static final int SIZE_UNKNOWN = -1;

    static enum Dimension {
        DIMENSION_WIDTH,
        DIMENSION_HEIGHT
    }

    String getName();

    Window getWindow();

    void setWindow(Window window);

    P name(String name);

    /**
     * When this is set this widget will broadcast events on the given channel
     */
    P channel(String channel);

    /**
     * Return true if this widget is equal to the parameter widget or if the parameter
     * widget is contained in this widget.
     */
    boolean containsWidget(Widget<?> w);

    /**
     * Set the actual bounds for this widget. These coordinates are relative to the parents
     * coordinate system. This function is typically called by the parent of this widget
     * and should only be used by the application for the toplevel widget.
     */
    void bounds(int x, int y, int w, int h);

    /**
     * Version of getDesiredWidth/getDesiredHeight that accepts a dimension parameter.
     * @param dimension
     * @return
     */
    int getDesiredSize(Dimension dimension);

    /**
     * Set the desired width for this widget. This property is used by layout instances
     * which may honor or ignore this. You can also use one of the SIZE_... constants.
     */
    P desiredWidth(int width);

    int getDesiredWidth();

    /**
     * Set the desired height for this widget. This property is used by layout instances
     * which may honor or ignore this. You can also use one of the SIZE_... constants.
     */
    P desiredHeight(int height);

    int getDesiredHeight();

    /**
     * Set the tooltip for this widget.
     */
    P tooltips(String... tooltips);

    /**
     * Set itemstacks that the tooltips can use with @<index> notation
     * @return
     */
    P tooltipItems(ItemStack... items);

    List<String> getTooltips();

    List<ItemStack> getTooltipItems();

    /**
     * Enable or disable mouse interaction with this widget. This is true by default.
     */
    P enabled(boolean enabled);

    /**
     * Enable or disable based on a combination of 'flags'
     */
    P enabledFlags(String... flags);

    @Nonnull
    Set<Integer> getEnabledFlags();

    boolean isEnabled();

    boolean isEnabledAndVisible();

    P hovering(boolean hovering);

    boolean isHovering();

    /**
     * Make this widget visible/invisible.
     */
    P visible(boolean visible);

    boolean isVisible();

    /**
     * Get the bounds for this widget relative to the parents coordinate system.
     * Can be null in case the layout for this widget hasn't been set yet.
     * @return
     */
    Rectangle getBounds();

    /**
     * Check if a coordinate is in the bounds of this widget.
     */
    boolean in(double x, double y);

    /**
     * Find the widget at the given position. It can be assumed in this function that it
     * is only called for valid coordaintes that are guaranteed to be in this widget. So widgets
     * that don't have children should just return themselves.
     * @param x
     * @param y
     */
    Widget<?> getWidgetAtPosition(double x, double y);

    /**
     * Draw this widget on the GUI at the specific position. This position is usually supplied by the
     * parent widget or for the top level widget it is the top-left corner on screen.
     * The given coordinates are the absolute coordinates of the parent. This does *not* include
     * the top/left x,y of this widget itself.
     */
    void draw(Screen gui, PoseStack matrixStack, int x, int y);

    /**
     * After the window has been drawn this is called again to give widgets a chance
     * to render additional stuff that has to be rendered on top of the rest
     */
    void drawPhase2(Screen gui, PoseStack matrixStack, int x, int y);

    /**
     * Handle a mouse click for this widget. This widget does not have to check if the coordinate is
     * in the bounds. The given coordinates are relative to the parent of this widget.
     *
     *
     * @param x
     * @param y
     * @param button
     * @return a reference to the widget that wants focus (or null if not)
     */
    Widget<?> mouseClick(double x, double y, int button);

    /**
     * Handle a mouse release for this widget.
     * @param x
     * @param y
     * @param button
     */
    void mouseRelease(double x, double y, int button);

    /**
     * Handle a mouse move event.
     * @param x
     * @param y
     */
    void mouseMove(double x, double y);

    /**
     * Handle mousewheel.
     *
     *
     * @param x
     * @param y
     * @param amount
     * @return true if handled
     */
    boolean mouseScrolled(double x, double y, double amount);

    /**
     * Handle a keyboard event.
     *
     * @param keyCode
     * @param scanCode
     * @return true if key was handled
     */
    boolean keyTyped(int keyCode, int scanCode);

    /**
     * Handle a keyboard event.
     *
     * @param codePoint
     * @return true if key was handled
     */
    boolean charTyped(char codePoint);

    /**
     * Some layout managers need a layout hint.
     *
     * @param hint
     * @return this widget
     */
    P hint(LayoutHint hint);

    /**
     * Conveniance for the positional layout
     */
    default P hint(int x, int y, int width, int height) {
        return hint(new PositionalLayout.PositionalHint(x, y, width, height));
    }

    LayoutHint getLayoutHint();

    /**
     * Associate a user object with this widget.
     */
    P userObject(Object obj);

    Object getUserObject();

    void readFromGuiCommand(GuiParser.GuiCommand command);

    GuiParser.GuiCommand createGuiCommand();

    void fillGuiCommand(GuiParser.GuiCommand command);

    <T> void setGenericValue(T value);

    Object getGenericValue(Type<?> type);

    // Mark the layout as dirty (mostly if it is a container)
    void markLayoutDirty();
}
