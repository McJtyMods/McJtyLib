package mcjty.lib.gui;

import mcjty.lib.base.ModBase;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.widgets.BlockRender;
import mcjty.lib.gui.widgets.Widget;
import mcjty.lib.network.PacketSendServerCommand;
import mcjty.lib.network.PacketServerCommandTyped;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class GenericGuiContainer<T extends GenericTileEntity> extends ContainerScreen {

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

    public GenericGuiContainer(ModBase mod, SimpleChannel network, T tileEntity, Container container, int manual, String manualNode) {
        super(container);
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
    public void initGui() {
        windowManager = null;
        super.initGui();
        sideWindow.initGui(modBase, network, mc, this, guiLeft, guiTop, xSize, ySize);
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
            GlStateManager.disableDepth();
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

            this.zLevel = 300.0F;
            this.itemRender.zLevel = 300.0F;
            int l = -267386864;
            this.drawGradientRect(xx - 3, yy - 4, xx + i + 3, yy - 3, l, l);
            this.drawGradientRect(xx - 3, yy + k + 3, xx + i + 3, yy + k + 4, l, l);
            this.drawGradientRect(xx - 3, yy - 3, xx + i + 3, yy + k + 3, l, l);
            this.drawGradientRect(xx - 4, yy - 3, xx - 3, yy + k + 3, l, l);
            this.drawGradientRect(xx + i + 3, yy - 3, xx + i + 4, yy + k + 3, l, l);
            int i1 = 1347420415;
            int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
            this.drawGradientRect(xx - 3, yy - 3 + 1, xx - 3 + 1, yy + k + 3 - 1, i1, j1);
            this.drawGradientRect(xx + i + 2, yy - 3 + 1, xx + i + 3, yy + k + 3 - 1, i1, j1);
            this.drawGradientRect(xx - 3, yy - 3, xx + i + 3, yy - 3 + 1, i1, i1);
            this.drawGradientRect(xx - 3, yy + k + 2, xx + i + 3, yy + k + 3, j1, j1);

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
                            RenderHelper.renderObject(mc, curx + 1, yy, o, false);
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

            this.zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawWindow();
    }

    protected void drawWindow() {
        drawDefaultBackground();
        getWindowManager().draw();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
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
    public boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
        // Prevent slots from being hoverable if they are (partially) covered by a modal window
        if (isPartiallyCoveredByModalWindow(slotIn)) {
            return false;
        } else {
            return super.isMouseOverSlot(slotIn, mouseX, mouseY);
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
        int x = Mouse.getEventX() * width / mc.displayWidth;
        int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
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
            ITooltipFlag flag = this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
            list = stack.getTooltip(this.mc.player, flag);
        }

        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, stack.getRarity().rarityColor + list.get(i));
            } else {
                list.set(i, TextFormatting.GRAY + list.get(i));
            }
        }

        list = addCustomLines(list, blockRender, stack);

        FontRenderer font = null;
        if (stack.getItem() != null) {
            font = stack.getItem().getFontRenderer(stack);
        }
        this.drawHoveringText(list, x, y, (font == null ? fontRenderer : font));
    }


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);
        getWindowManager().mouseClicked(x, y, button);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        getWindowManager().handleMouseInput();
    }

    /*
     * 99% sure this is the correct one
     */
    @Override
    protected void mouseReleased(int x, int y, int state) {
        super.mouseReleased(x, y, state);
        getWindowManager().mouseReleased(x, y, state);
    }

    public Window getWindow() {
        return window;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        boolean b = getWindowManager().keyTyped(typedChar, keyCode);
        if (b) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    public void keyTypedFromEvent(char typedChar, int keyCode) {
        if (getWindowManager().keyTyped(typedChar, keyCode)) {
            try {
                super.keyTyped(typedChar, keyCode);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public void sendServerCommand(SimpleNetworkWrapper network, String command, TypedMap params) {
        network.sendToServer(new PacketServerCommandTyped(tileEntity.getPos(), null, command, params));
    }

    public void sendServerCommand(SimpleNetworkWrapper network, int dimensionId, String command, TypedMap params) {
        network.sendToServer(new PacketServerCommandTyped(tileEntity.getPos(), dimensionId, command, params));
    }

    public void sendServerCommand(String modid, String command, @Nonnull TypedMap arguments) {
        network.sendToServer(new PacketSendServerCommand(modid, command, arguments));
    }

    public void sendServerCommand(String modid, String command) {
        network.sendToServer(new PacketSendServerCommand(modid, command, TypedMap.EMPTY));
    }

}
