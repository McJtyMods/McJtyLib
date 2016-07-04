package mcjty.lib.gui.widgets;

import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.BlockRenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;

public class BlockRender extends AbstractWidget<BlockRender> {
    private Object renderItem = null;
    private int offsetX = 0;
    private int offsetY = 0;
    private long prevTime = -1;
    private boolean hilightOnHover = false;
    private List<BlockRenderEvent> selectionEvents = null;

    public Object getRenderItem() {
        return renderItem;
    }

    public BlockRender setRenderItem(Object renderItem) {
        this.renderItem = renderItem;
        return this;
    }

    public BlockRender(Minecraft mc, Gui gui) {
        super(mc, gui);
        setDesiredHeight(16);
        setDesiredWidth(16);
    }

    public int getOffsetX() {
        return offsetX;
    }

    public BlockRender setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public BlockRender setOffsetY(int offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    public boolean isHilightOnHover() {
        return hilightOnHover;
    }

    public BlockRender setHilightOnHover(boolean hilightOnHover) {
        this.hilightOnHover = hilightOnHover;
        return this;
    }

    @Override
    public void draw(Window window, int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(window, x, y);
        if (renderItem != null) {
            int xx = x + bounds.x + offsetX;
            int yy = y + bounds.y + offsetY;
            RenderHelper.renderObject(mc, xx, yy, renderItem, false);
            if (hilightOnHover && isHovering()) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.colorMask(true, true, true, false);
                RenderHelper.drawVerticalGradientRect(xx, yy, xx + 16, yy + 16, -2130706433, -2130706433);
                GlStateManager.colorMask(true, true, true, true);
//                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
    }

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        if (isEnabledAndVisible()) {
            fireSelectionEvents();
            long t = System.currentTimeMillis();
            if (prevTime != -1 && (t - prevTime) < 250) {
                fireDoubleClickEvent();
            }
            prevTime = t;
            return this;
        }
        return null;
    }

    public BlockRender addSelectionEvent(BlockRenderEvent event) {
        if (selectionEvents == null) {
            selectionEvents = new ArrayList<>();
        }
        selectionEvents.add(event);
        return this;
    }

    public void removeSelectionEvent(BlockRenderEvent event) {
        if (selectionEvents != null) {
            selectionEvents.remove(event);
        }
    }

    private void fireSelectionEvents() {
        if (selectionEvents != null) {
            for (BlockRenderEvent event : selectionEvents) {
                event.select(this);
            }
        }
    }

    private void fireDoubleClickEvent() {
        if (selectionEvents != null) {
            for (BlockRenderEvent event : selectionEvents) {
                event.doubleClick(this);
            }
        }
    }
}
