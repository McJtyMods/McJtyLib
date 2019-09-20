package mcjty.lib.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import mcjty.lib.McJtyLib;
import mcjty.lib.base.ModBase;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.widgets.BlockRender;
import mcjty.lib.gui.widgets.Widget;
import mcjty.lib.network.PacketSendServerCommand;
import mcjty.lib.network.PacketServerCommandTyped;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.Tools;
import net.minecraft.block.Block;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class GenericGuiContainer<T extends GenericTileEntity, C extends Container> extends ContainerScreen<C> {

    protected ModBase modBase;
    protected SimpleChannel network;

    protected Window window;
    private WindowManager windowManager;
    protected final T tileEntity;

    private GuiSideWindow sideWindow;

    @Override
    public int getGuiLeft() {
        return guiLeft;
    }

    @Override
    public int getGuiTop() {
        return guiTop;
    }

    public void setWindowDimensions(int x, int y) {
        this.xSize = x;
        this.ySize = y;
    }

    public GenericGuiContainer(ModBase mod, SimpleChannel network, T tileEntity, C container, PlayerInventory inventory, int manual, String manualNode) {
        super(container, inventory, new StringTextComponent("test"));   // @todo
        this.modBase = mod;
        this.network = network;
        this.tileEntity = tileEntity;
        sideWindow = new GuiSideWindow(manual, manualNode);
        windowManager = null;
    }

    public List<Rectangle> getSideWindowBounds() {
        if (sideWindow.getWindow() == null || sideWindow.getWindow().getToplevel() == null) {
            Logging.getLogger().error(new RuntimeException("Internal error! getSideWindowBounds() called before initGui!"));
            return Collections.emptyList();
        }
        return Collections.singletonList(sideWindow.getWindow().getToplevel().getBounds());
    }


    @Override
    public void init() {
        windowManager = null;
        super.init();
        sideWindow.initGui(modBase, network, minecraft, this, guiLeft, guiTop, xSize, ySize);
    }

    /**
     * Override this method to register your own windows to the window manager.
     *
     * @param mgr
     */
    protected void registerWindows(WindowManager mgr) {
    }

    protected WindowManager getWindowManager() {
        if (windowManager == null) {
            if (sideWindow.getWindow() == null) {
                RuntimeException e = new RuntimeException("Internal error! getWindowManager() called before initGui!");
                Logging.getLogger().error(e); // Some mods (NEI) will swallow this exception without a trace unless we log it ourself.
                throw e;
            }
            windowManager = new WindowManager(this);
            windowManager.addWindow(sideWindow.getWindow());
            windowManager.addWindow(window);
            registerWindows(windowManager);
        }
        return windowManager;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int i, int i2) {
        getWindowManager().drawTooltips();
    }

    public static String escapeString(String s) {
        return s.replace("@", "@@");
    }

    private static List<Object> parseString(String s, List<ItemStack> items) {
        List<Object> l = new ArrayList<>();
        String current = "";
        for (int i = 0; i < s.length(); ++i) {
            String c = s.substring(i, i + 1);
            if ("@".equals(c)) {
                ++i;
                int itemIdx = s.charAt(i) - '0';
                if(itemIdx == '@' - '0') {
                    // @@ becomes a literal @
                    current += "@";
                } else if(itemIdx < 0 || itemIdx > 9) {
                    // probably forgot to escape something
                    throw new IllegalArgumentException(s);
                } else {
                    // replace it with the corresponding item
                    if (!current.isEmpty()) {
                        l.add(current);
                        current = "";
                    }
                    ItemStack e = items.get(itemIdx);
                    if (!e.isEmpty()) {
                        l.add(e);
                    }
                }
            } else {
                current += c;
            }
        }
        if (!current.isEmpty()) {
            l.add(current);
        }
        return l;
    }

    public void drawHoveringText(List<String> textLines, List<ItemStack> items, int x, int y, FontRenderer font) {
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            int i = 0;

            int linesWithItemStacks = 0;
            for (String s : textLines) {
                int j;
                if (s != null && items != null && s.contains("@") && !items.isEmpty()) {
                    List<Object> list = parseString(s, items);
                    boolean lineHasItemStacks = false;
                    j = 0;
                    for (Object o : list) {
                        if (o instanceof String) {
                            j += font.getStringWidth((String) o);
                        } else {
                            j += 20;    // ItemStack
                            lineHasItemStacks = true;
                        }
                    }
                    if(lineHasItemStacks) {
                        ++linesWithItemStacks;
                    }

                } else {
                    j = font.getStringWidth(s);
                }

                if (j > i) {
                    i = j;
                }
            }

            int xx = x + 12;
            int yy = y - 12;
            int k = 8;

            if (textLines.size() > 1) {
                k += 2 + (textLines.size() - 1) * 10 + linesWithItemStacks * 8;
            }

            if (xx > this.width - guiLeft - i - 5) {
                xx -= 28 + i;
            }

            if (xx < 4 - guiLeft) {
                xx = 4 - guiLeft;
            }

            if (yy > this.height - guiTop - k - 4) {
                yy = this.height - guiTop - k - 4;
            } else if (yy < 4 - guiTop) {
                yy = 4 - guiTop;
            }

//            this.zLevel = 300.0F;     // @todo 1.14
            this.itemRenderer.zLevel = 300.0F;
            int l = -267386864;
            this.fillGradient(xx - 3, yy - 4, xx + i + 3, yy - 3, l, l);
            this.fillGradient(xx - 3, yy + k + 3, xx + i + 3, yy + k + 4, l, l);
            this.fillGradient(xx - 3, yy - 3, xx + i + 3, yy + k + 3, l, l);
            this.fillGradient(xx - 4, yy - 3, xx - 3, yy + k + 3, l, l);
            this.fillGradient(xx + i + 3, yy - 3, xx + i + 4, yy + k + 3, l, l);
            int i1 = 1347420415;
            int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
            this.fillGradient(xx - 3, yy - 3 + 1, xx - 3 + 1, yy + k + 3 - 1, i1, j1);
            this.fillGradient(xx + i + 2, yy - 3 + 1, xx + i + 3, yy + k + 3 - 1, i1, j1);
            this.fillGradient(xx - 3, yy - 3, xx + i + 3, yy - 3 + 1, i1, i1);
            this.fillGradient(xx - 3, yy + k + 2, xx + i + 3, yy + k + 3, j1, j1);

            for (int k1 = 0; k1 < textLines.size(); ++k1) {
                String s1 = textLines.get(k1);
                if (s1 != null && items != null && s1.contains("@") && !items.isEmpty()) {
                    List<Object> list = parseString(s1, items);
                    int curx = xx;
                    boolean lineHasItemStacks = false;
                    for (Object o : list) {
                        if (o instanceof String) {
                            String s2 = (String)o;
                            font.drawStringWithShadow(s2, curx, yy, -1);
                            curx += font.getStringWidth(s2);
                        } else {
                            RenderHelper.renderObject(getMinecraft(), curx + 1, yy, o, false);
                            curx += 20;
                            lineHasItemStacks = true;
                        }
                    }
                    if(lineHasItemStacks) {
                        yy += 8;
                    }
                } else {
                    font.drawStringWithShadow(s1, xx, yy, -1);
                }

                if (k1 == 0) {
                    yy += 2;
                }

                yy += 10;
            }

//            this.zLevel = 0.0F;       // @todo 1.14
            this.itemRenderer.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
            net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawWindow();
    }

    protected void drawWindow() {
        if (window == null) {
            return;
        }
        renderBackground();
        getWindowManager().draw();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (window == null) {
            return;
        }
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        drawStackTooltips(mouseX, mouseY);
    }


    @Override
    public void drawSlot(Slot slotIn) {
        // Prevent slots from being rendered if they are (partially) covered by a modal window
        if (!isPartiallyCoveredByModalWindow(slotIn)) {
            super.drawSlot(slotIn);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        Slot slot = getSlotUnderMouse();
        // Prevent slots from being hoverable if they are (partially) covered by a modal window
        if (isPartiallyCoveredByModalWindow(slot)) {
            return false;
        } else {
            return super.isMouseOver(mouseX, mouseY);
        }
    }

    private boolean isPartiallyCoveredByModalWindow(Slot slotIn) {
        int xPos = slotIn.xPos + window.getToplevel().getBounds().x;
        int yPos = slotIn.yPos + window.getToplevel().getBounds().y;

        return getWindowManager().getModalWindows()
                .anyMatch(window -> window.getToplevel().getBounds().intersects(new Rectangle(xPos, yPos, 18, 18)));
    }

    /**
     * Draw tooltips for itemstacks that are in BlockRender widgets
     */
    protected void drawStackTooltips(int mouseX, int mouseY) {
        MouseHelper mouse = getMinecraft().mouseHelper;
        int x = (int)mouse.getMouseX() * width / getMinecraft().mainWindow.getWidth();
        int y = height - (int)mouse.getMouseY() * height / getMinecraft().mainWindow.getHeight() - 1;
        Widget<?> widget = window.getToplevel().getWidgetAtPosition(x, y);
        if (widget instanceof BlockRender) {
            BlockRender blockRender = (BlockRender) widget;
            Object renderItem = blockRender.getRenderItem();
            ItemStack itemStack;
            if (renderItem instanceof ItemStack) {
                itemStack = (ItemStack) renderItem;
            } else if (renderItem instanceof Block) {
                itemStack = new ItemStack((Block) renderItem);
            } else if (renderItem instanceof Item) {
                itemStack = new ItemStack((Item) renderItem);
            } else {
                itemStack = ItemStack.EMPTY;
            }
            if (!itemStack.isEmpty()) {
                customRenderToolTip(blockRender, itemStack, mouseX, mouseY);
            }
        }
    }

    protected List<String> addCustomLines(List<String> oldList, BlockRender blockRender, ItemStack stack) {
        return oldList;
    }

    protected void customRenderToolTip(BlockRender blockRender, ItemStack stack, int x, int y) {
        List<String> list;
        if (stack.getItem() == null) {
            // Protection for bad itemstacks
            list = new ArrayList<>();
        } else {
            ITooltipFlag flag = this.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
            list = stack.getTooltip(this.getMinecraft().player, flag).stream().map(s -> s.getFormattedText()).collect(Collectors.toList());
        }

        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, stack.getRarity().color + list.get(i));
            } else {
                list.set(i,  TextFormatting.GRAY + list.get(i));
            }
        }

        list = addCustomLines(list, blockRender, stack);

        FontRenderer font = null;
        if (stack.getItem() != null) {
            font = stack.getItem().getFontRenderer(stack);
        }
        this.renderTooltip(list, x, y, (font == null ? getMinecraft().fontRenderer : font));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        getMinecraft().keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean rc = super.mouseClicked(x, y, button);
        if (window != null) {
            getWindowManager().mouseClicked((int) x, (int) y, button);
        }
        return rc;
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double scaledX, double scaledY) {
        boolean rc = super.mouseDragged(x, y, button, scaledX, scaledY);
        if (window != null) {
            getWindowManager().handleMouseInput(button);
        }
        return rc;
    }

    /*
     * 99% sure this is the correct one
     */
    @Override
    public boolean mouseReleased(double x, double y, int state) {
        boolean rc = super.mouseReleased(x, y, state);
        if (window != null) {
            getWindowManager().mouseReleased((int) x, (int) y, state);
        }
        return rc;
    }

    public Window getWindow() {
        return window;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean b = window == null || getWindowManager().keyTyped(keyCode, scanCode);
        if (b) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            return false;
        }
    }

    public void keyTypedFromEvent(int keyCode, int scanCode) {
        if (window != null) {
            if (getWindowManager().keyTyped(keyCode, scanCode)) {
                super.keyPressed(keyCode, scanCode, 0); // @todo 1.14: modifiers?
            }
        }
    }

    public void charTypedFromEvent(char codePoint) {
        if (window != null) {
            if (getWindowManager().charTyped(codePoint)) {
                // @todo 1.14?
//                super.keyPressed(keyCode, scanCode, 0); // @todo 1.14: modifiers?
            }
        }
    }

    public void sendServerCommand(SimpleChannel network, String command, TypedMap params) {
        network.sendToServer(new PacketServerCommandTyped(tileEntity.getPos(), null, command, params));
    }

    public void sendServerCommand(SimpleChannel network, DimensionType dimensionId, String command, TypedMap params) {
        network.sendToServer(new PacketServerCommandTyped(tileEntity.getPos(), dimensionId, command, params));
    }

    public void sendServerCommand(String modid, String command, @Nonnull TypedMap arguments) {
        network.sendToServer(new PacketSendServerCommand(modid, command, arguments));
    }

    public void sendServerCommand(String modid, String command) {
        network.sendToServer(new PacketSendServerCommand(modid, command, TypedMap.EMPTY));
    }

    // Register a container/gui on the client side
    public static <C extends GenericContainer, S extends GenericGuiContainer<T,C>, T extends GenericTileEntity> void register(
            ContainerType<C> type,
            GuiSupplier<C, S, T> guiSupplier) {
        ScreenManager.IScreenFactory<C, S> factory = (container, inventory, title) -> {
            TileEntity te = McJtyLib.proxy.getClientWorld().getTileEntity(container.getPos());
            return Tools.safeMap(te, (T tile) -> guiSupplier.create(tile, container, inventory), "Invalid tile entity!");
        };
        ScreenManager.registerFactory(type, factory);
    }

    @FunctionalInterface
    public static interface GuiSupplier<C extends GenericContainer, S extends GenericGuiContainer, T extends GenericTileEntity> {
        S create(T tile, C container, PlayerInventory inventory);
    }
}
