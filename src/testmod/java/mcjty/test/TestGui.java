package mcjty.test;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.WindowManager;
import mcjty.lib.gui.icons.IIcon;
import mcjty.lib.gui.icons.IconManager;
import mcjty.lib.gui.icons.ImageIcon;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.layout.VerticalLayout;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class TestGui extends GenericGuiContainer<TestTileEntity> {
    public static final int SIDEWIDTH = 80;
    public static final int WIDTH = 256;
    public static final int HEIGHT = 236;

    public static int ICONSIZE = 20;

    private static final ResourceLocation mainBackground = new ResourceLocation(TestMod.MODID, "textures/gui/testgui.png");
    private static final ResourceLocation sideBackground = new ResourceLocation(TestMod.MODID, "textures/gui/sidegui.png");
    private static final ResourceLocation icons = new ResourceLocation(TestMod.MODID, "textures/gui/icons.png");

    private Window sideWindow;
    private IconManager iconManager;

    public TestGui(TestTileEntity tileEntity, TestContainer container) {
        super(TestMod.instance, TestMod.network, tileEntity, container, 0, "testblock");

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        // --- Main window ---
        Panel editorPanel = setupEditorPanel();
        Panel controlPanel = setupControlPanel();
        Panel gridPanel = setupGridPanel();
        Panel toplevel = new Panel(mc, this).setLayout(new PositionalLayout()).setBackground(mainBackground)
                .addChild(editorPanel)
                .addChild(controlPanel)
                .addChild(gridPanel);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));
        window = new Window(this, toplevel);

        // --- Side window ---
        Panel listPanel = setupListPanel();
        Panel sidePanel = new Panel(mc, this).setLayout(new PositionalLayout()).setBackground(sideBackground)
                .addChild(listPanel);
        sidePanel.setBounds(new Rectangle(guiLeft-SIDEWIDTH, guiTop, SIDEWIDTH, ySize));
        sideWindow = new Window(this, sidePanel);
    }

    @Override
    protected void registerWindows(WindowManager mgr) {
        super.registerWindows(mgr);
        mgr.addWindow(sideWindow);
        mgr.getIconManager().setClickHoldToDrag(true);
    }

    private Panel setupGridPanel() {

        Panel panel = new Panel(mc, this).setLayout(new PositionalLayout())
                .setLayoutHint(new PositionalLayout.PositionalHint(5, 5, 246, 113));

        WidgetList list = new WidgetList(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(0, 0, 236, 113))
                .setPropagateEventsToChildren(true)
                .setInvisibleSelection(true)
                .setDrawHorizontalLines(false)
                .setRowheight(ICONSIZE+1);
        Slider slider = new Slider(mc, this)
                .setVertical()
                .setScrollable(list)
                .setLayoutHint(new PositionalLayout.PositionalHint(237, 0, 9, 113));

        for (int y = 0 ; y < 10 ; y++) {
            Panel rowPanel = new Panel(mc, this).setLayout(new HorizontalLayout().setSpacing(-1).setHorizontalMargin(0).setVerticalMargin(0));
            for (int x = 0 ; x < 11 ; x++) {
                IconHolder holder = new IconHolder(mc, this)
                        .setDesiredWidth(ICONSIZE+2)
                        .setDesiredHeight(ICONSIZE+2)
                        .setBorder(1)
                        .setBorderColor(0xff777777)
                        .setSelectable(true)
                        .addIconClickedEvent((parent, icon, dx, dy) -> {
                            if (dy <= 3 && dx >= 10 && dx <= 14) {
                                handleIconOverlay(icon, "top", 0, 5);
                            } else if (dy >= ICONSIZE-3 && dx >= 10 && dx <= 14) {
                                handleIconOverlay(icon, "bot", 2, 5);
                            } else if (dx <= 3 && dy >= 10 && dy <= 14) {
                                handleIconOverlay(icon, "lef", 3, 5);
                            } else if (dx >= ICONSIZE-3 && dy >= 10 && dy <= 14) {
                                handleIconOverlay(icon, "rig", 1, 5);
                            }
                            return true;
                        });
                rowPanel.addChild(holder);
            }
            list.addChild(rowPanel);
        }

//        int leftx = 0;
//        int topy = 0;
//        for (int x = 0 ; x < 13 ; x++) {
//            for (int y = 0 ; y < 6 ; y++) {
//                IconHolder holder = new IconHolder(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(leftx + x*19, topy + y*19, 18, 18)).setBorder(1);
//                panel.addChild(holder);
//            }
//        }

        panel.addChild(list).addChild(slider);

        return panel;
    }

    private void handleIconOverlay(IIcon icon, String prefix, int u, int v) {
        if (icon.hasOverlay(prefix+"_red")) {
            icon.removeOverlay(prefix+"_red");
            icon.addOverlay(new ImageIcon(prefix+"_green").setDimensions(ICONSIZE, ICONSIZE).setImage(icons, u*ICONSIZE, (v+1)*ICONSIZE));
        } else if (icon.hasOverlay(prefix+"_green")) {
            icon.removeOverlay(prefix+"_green");
        } else {
            icon.addOverlay(new ImageIcon(prefix+"_red").setDimensions(ICONSIZE, ICONSIZE).setImage(icons, u*ICONSIZE, v*ICONSIZE));
        }
    }

    private Panel setupControlPanel() {
        return new Panel(mc, this).setLayout(new VerticalLayout()).setLayoutHint(new PositionalLayout.PositionalHint(26, 157, 58, 50))
                .addChild(new Button(mc, this).setText("Load").setDesiredHeight(15))
                .addChild(new Button(mc, this).setText("Save").setDesiredHeight(15))
                .addChild(new Button(mc, this).setText("Clear").setDesiredHeight(15));
    }

    private Panel setupListPanel() {
        WidgetList list = new WidgetList(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(0, 0, 62, 220))
                .setPropagateEventsToChildren(true)
                .setInvisibleSelection(true)
                .setDrawHorizontalLines(false)
                .setRowheight(ICONSIZE+2);
        Slider slider = new Slider(mc, this)
                .setVertical()
                .setScrollable(list)
                .setLayoutHint(new PositionalLayout.PositionalHint(62, 0, 9, 220));

        int x = 0;
        for (int i = 0 ; i < 16 ; i++) {
            Panel childPanel = new Panel(mc, this).setLayout(new HorizontalLayout().setVerticalMargin(1).setSpacing(1).setHorizontalMargin(1)).setDesiredHeight(ICONSIZE+1);
            IconHolder holder = new IconHolder(mc, this).setDesiredWidth(ICONSIZE).setDesiredHeight(ICONSIZE)
                    .setMakeCopy(true);
            holder.setIcon(new ImageIcon(String.valueOf(i)).setDimensions(ICONSIZE, ICONSIZE).setImage(icons, i*ICONSIZE, x*2*ICONSIZE));
            childPanel.addChild(holder);

            holder = new IconHolder(mc, this).setDesiredWidth(ICONSIZE).setDesiredHeight(ICONSIZE)
                    .setMakeCopy(true);
            holder.setIcon(new ImageIcon(String.valueOf(i)).setDimensions(ICONSIZE, ICONSIZE).setImage(icons, i*ICONSIZE, x*2*ICONSIZE+ICONSIZE));
            childPanel.addChild(holder);

            holder = new IconHolder(mc, this).setDesiredWidth(ICONSIZE).setDesiredHeight(ICONSIZE)
                    .setMakeCopy(true);
            holder.setIcon(new ImageIcon(String.valueOf(i)).setDimensions(ICONSIZE, ICONSIZE).setImage(icons, i*ICONSIZE, x*2*ICONSIZE+ICONSIZE));
            childPanel.addChild(holder);

            list.addChild(childPanel);
        }

        return new Panel(mc, this).setLayout(new PositionalLayout()).setLayoutHint(new PositionalLayout.PositionalHint(5, 5, 72, 220))
                .addChild(list)
                .addChild(slider);
//                .setFilledRectThickness(-2)
//                .setFilledBackground(StyleConfig.colorListBackground);
    }

    private Panel createValuePanel(String labelName, String tempDefault) {
        Label label = (Label) new Label(mc, this)
                .setText(labelName)
                .setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT)
                .setDesiredHeight(13)
                .setLayoutHint(new PositionalLayout.PositionalHint(0, 0, 55, 13));
        TextField field = new TextField(mc, this)
                .setText(tempDefault)
                .setDesiredHeight(13)
                .setLayoutHint(new PositionalLayout.PositionalHint(0, 12, 49, 13));
        Button button = new Button(mc, this)
                .setText("...")
                .setDesiredHeight(13)
                .addButtonEvent(w -> openValueEditor())
                .setLayoutHint(new PositionalLayout.PositionalHint(50, 12, 11, 13));

        return new Panel(mc, this).setLayout(new PositionalLayout())
                .addChild(label)
                .addChild(field)
                .addChild(button)
                .setDesiredWidth(62);
    }

    private void openValueEditor() {
        Panel panel = new Panel(mc, this)
                .setLayout(new VerticalLayout())
                .setFilledBackground(0xff666666, 0xffaaaaaa);
        panel.setBounds(new Rectangle(50, 50, 200, 100));
        Window modalWindow = getWindowManager().createModalWindow(panel);
        panel.addChild(new Label(mc, this).setText("Label"));
        panel.addChild(new Button(mc, this)
                .addButtonEvent(w -> getWindowManager().closeWindow(modalWindow))
                .setText("Close"));
    }

    private Panel setupEditorPanel() {
        Panel slotPanel = createValuePanel("Slot:", "<var 3>");
        Panel amountPanel = createValuePanel("Amount:", "<64>");

        return new Panel(mc, this).setLayout(new HorizontalLayout()).setLayoutHint(new PositionalLayout.PositionalHint(4, 123, 249, 30))
                .addChild(slotPanel)
                .addChild(amountPanel)
                .setFilledRectThickness(-1)
                .setFilledBackground(StyleConfig.colorListBackground);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawWindow();
    }
}
