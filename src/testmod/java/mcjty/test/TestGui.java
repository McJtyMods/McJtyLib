package mcjty.test;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.WindowManager;
import mcjty.lib.gui.icons.IconManager;
import mcjty.lib.gui.icons.ImageIcon;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.layout.VerticalLayout;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.gui.widgets.TextField;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class TestGui extends GenericGuiContainer<TestTileEntity> {
    public static final int SIDEWIDTH = 80;
    public static final int WIDTH = 256;
    public static final int HEIGHT = 236;

    private static final ResourceLocation mainBackground = new ResourceLocation(TestMod.MODID, "textures/gui/testgui.png");
    private static final ResourceLocation sideBackground = new ResourceLocation(TestMod.MODID, "textures/gui/sidegui.png");
    private static final ResourceLocation icons = new ResourceLocation(TestMod.MODID, "textures/gui/icons.png");

    private Window sideWindow;
    private IconManager iconManager;
    private IconHolder iconHolder1;
    private IconHolder iconHolder2;
    private IconHolder iconHolder3;

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
    }

    private Panel setupGridPanel() {

        int leftx = 40;
        int topy = 40;
        iconHolder1 = new IconHolder(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(leftx, topy, 21, 21));
        iconHolder1.setIcon(new ImageIcon().setDimensions(19, 19).setImage(icons, 0, 0))
                .setBorder(1);

        iconHolder2 = new IconHolder(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(leftx +22, topy, 21, 21))
                .setBorder(1);

        iconHolder3 = new IconHolder(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(leftx +22+22, topy, 21, 21))
                .setBorder(1);

        return new Panel(mc, this).setLayout(new PositionalLayout()).setLayoutHint(new PositionalLayout.PositionalHint(5, 5, 246, 113))
                .addChild(iconHolder1)
                .addChild(iconHolder2)
                .addChild(iconHolder3);
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
                .setRowheight(23);
        Slider slider = new Slider(mc, this)
                .setVertical()
                .setScrollable(list)
                .setLayoutHint(new PositionalLayout.PositionalHint(62, 0, 9, 220));

        int x = 0;
        for (int i = 0 ; i < 13 ; i++) {
            Panel childPanel = new Panel(mc, this).setLayout(new HorizontalLayout().setVerticalMargin(0)).setDesiredHeight(21);
            IconHolder holder = new IconHolder(mc, this).setDesiredWidth(21).setDesiredHeight(21);
            holder.setIcon(new ImageIcon().setDimensions(19, 19).setImage(icons, i*19, x*2*19))
                    .setMakeCopy(true)
                    .setBorder(1);
            childPanel.addChild(holder);
            holder = new IconHolder(mc, this).setDesiredWidth(21).setDesiredHeight(21);
            holder.setIcon(new ImageIcon().setDimensions(19, 19).setImage(icons, i*19, x*2*19+19))
                    .setMakeCopy(true)
                    .setBorder(1);
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
                .setLayoutHint(new PositionalLayout.PositionalHint(50, 12, 11, 13));

        return new Panel(mc, this).setLayout(new PositionalLayout())
                .addChild(label)
                .addChild(field)
                .addChild(button)
                .setDesiredWidth(62);
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
