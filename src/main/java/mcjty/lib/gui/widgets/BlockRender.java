package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.events.BlockRenderEvent;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class BlockRender extends AbstractWidget<BlockRender> {

    public static final String TYPE_BLOCKRENDER = "blockrender";

    public static final int DEFAULT_OFFSET = 0;
    public static final boolean DEFAULT_HILIGHT_ON_HOVER = false;
    public static final boolean DEFAULT_SHOW_LABEL = false;

    private Object renderItem = null;
    private int offsetX = DEFAULT_OFFSET;
    private int offsetY = DEFAULT_OFFSET;
    private long prevTime = -1;
    private boolean hilightOnHover = DEFAULT_HILIGHT_ON_HOVER;
    private boolean showLabel = DEFAULT_SHOW_LABEL;
    private Integer labelColor = null;
    private List<BlockRenderEvent> selectionEvents = null;

    public Object getRenderItem() {
        return renderItem;
    }

    public BlockRender renderItem(Object renderItem) {
        this.renderItem = renderItem;
        return this;
    }

    public BlockRender() {
        desiredHeight(16);
        desiredWidth(16);
    }

    public boolean isShowLabel() {
        return showLabel;
    }

    public BlockRender showLabel(boolean showLabel) {
        this.showLabel = showLabel;
        return this;
    }

    public int getLabelColor() {
        return labelColor == null ? StyleConfig.colorTextNormal : labelColor;
    }

    public BlockRender labelColor(int labelColor) {
        this.labelColor = labelColor;
        return this;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public BlockRender offsetX(int offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public BlockRender offsetY(int offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    public boolean isHilightOnHover() {
        return hilightOnHover;
    }

    public BlockRender hilightOnHover(boolean hilightOnHover) {
        this.hilightOnHover = hilightOnHover;
        return this;
    }

    @Override
    public void draw(Screen gui, MatrixStack matrixStack, int x, int y) {
        if (!visible) {
            return;
        }
        if (showLabel) {
            drawBackground(gui, matrixStack, x, y, bounds.height, bounds.height);
        } else {
            super.draw(gui, matrixStack, x, y);
        }
        if (renderItem != null) {
            int xx = x + bounds.x + offsetX;
            int yy = y + bounds.y + offsetY;
            mc.getItemRenderer().blitOffset = 100;
            this.window.getGui().setBlitOffset(100);
            RenderHelper.renderObject(matrixStack, xx, yy, renderItem, false);
            mc.getItemRenderer().blitOffset = 0;
            this.window.getGui().setBlitOffset(0);
            if (hilightOnHover && isHovering()) {
                RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                RenderHelper.drawVerticalGradientRect(xx, yy, xx + 16, yy + 16, -2130706433, -2130706433);
                RenderSystem.colorMask(true, true, true, true);
//                RenderSystem.enableLighting();
                RenderSystem.enableDepthTest();
            }

            if (showLabel) {
                String name;
                if (renderItem instanceof ItemStack) {
                    name = ((ItemStack) renderItem).getHoverName().getString() /* was getFormattedText() */;
                } else if (renderItem instanceof FluidStack) {
                    name = ((FluidStack) renderItem).getDisplayName().getString() /* was getFormattedText() */;   // @todo 1.14 better lang support
                } else if (renderItem instanceof Item) {
                    name = new ItemStack((Item) renderItem).getHoverName().getString() /* was getFormattedText() */;
                } else if (renderItem instanceof Block) {
                    name = new ItemStack((Block) renderItem).getHoverName().getString() /* was getFormattedText() */;
                } else {
                    name = "";
                }
                int h = mc.font.lineHeight;
                int dy = (bounds.height - h)/2;
                mc.font.draw(matrixStack, name, xx+20, yy + dy, getLabelColor());
            }
        }
    }

    @Override
    public Widget<?> mouseClick(double x, double y, int button) {
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

    public BlockRender event(BlockRenderEvent event) {
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
        fireChannelEvents("select");
        if (selectionEvents != null) {
            for (BlockRenderEvent event : selectionEvents) {
                event.select();
            }
        }
    }

    private void fireDoubleClickEvent() {
        fireChannelEvents("doubleclick");
        if (selectionEvents != null) {
            for (BlockRenderEvent event : selectionEvents) {
                event.doubleClick();
            }
        }
    }

    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        command.findCommand("offset").ifPresent(cmd -> {
            offsetX = cmd.getOptionalPar(0, DEFAULT_OFFSET);
            offsetY = cmd.getOptionalPar(1, DEFAULT_OFFSET);
        });
        hilightOnHover = GuiParser.get(command, "highlighthover", DEFAULT_HILIGHT_ON_HOVER);
        showLabel = GuiParser.get(command, "showlabel", DEFAULT_SHOW_LABEL);
        labelColor = GuiParser.get(command, "labelColor", null);
        command.findCommand("render").ifPresent(cmd -> renderItem = ItemStackTools.guiCommandToItemStack(cmd));
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        if (offsetX != DEFAULT_OFFSET || offsetY != DEFAULT_OFFSET) {
            command.command(new GuiParser.GuiCommand("offset").parameter(offsetX).parameter(offsetY));
        }
        GuiParser.put(command, "highlighthover", hilightOnHover, DEFAULT_HILIGHT_ON_HOVER);
        GuiParser.put(command, "showlabel", showLabel, DEFAULT_SHOW_LABEL);
        GuiParser.put(command, "labelColor", labelColor, null);
        if (renderItem != null) {
            if (renderItem instanceof ItemStack) {
                command.command(ItemStackTools.itemStackToGuiCommand("render", (ItemStack) renderItem));
            }
            // @todo other types
        }
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_BLOCKRENDER);
    }

    @Override
    public <T> void setGenericValue(T value) {
        if (value == null) {
            renderItem(null);
        } else {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(value.toString()));
            if (item != null) {
                renderItem(new ItemStack(item));
            } else {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(value.toString()));
                if (block != null) {
                    renderItem(new ItemStack(block));
                } else {
                    renderItem(null);
                }
            }
        }
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        if (renderItem instanceof ItemStack) {
            return ((ItemStack) renderItem).getItem().getRegistryName().toString();
        } else {
            return null;
        }
    }
}
