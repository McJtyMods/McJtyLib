package mcjty.test;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.layout.VerticalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class TestGui extends GenericGuiContainer<TestTileEntity> {
    public static final int WIDTH = 256;
    public static final int HEIGHT = 234;

    private static final ResourceLocation iconLocation = new ResourceLocation(TestMod.MODID, "textures/gui/testgui.png");

    public TestGui(TestTileEntity tileEntity, TestContainer container) {
        super(TestMod.instance, TestMod.network, tileEntity, container, 0, "testblock");

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        Panel editorPanel = setupEditorPanel();
        Panel listPanel = setupListPanel();
        Panel controlPanel = setupControlPanel();

        Panel toplevel = new Panel(mc, this).setLayout(new PositionalLayout()).setBackground(iconLocation)
                .addChild(editorPanel)
                .addChild(listPanel)
                .addChild(controlPanel);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));
        window = new Window(this, toplevel);
    }

    private Panel setupControlPanel() {
        return new Panel(mc, this).setLayout(new VerticalLayout()).setLayoutHint(new PositionalLayout.PositionalHint(26, 155, 58, 50))
                .addChild(new Button(mc, this).setText("Load").setDesiredHeight(15))
                .addChild(new Button(mc, this).setText("Save").setDesiredHeight(15))
                .addChild(new Button(mc, this).setText("Clear").setDesiredHeight(15));
    }

    private Panel setupListPanel() {
        WidgetList list = new WidgetList(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(0, 0, 43, 116));
        Slider slider = new Slider(mc, this)
                .setVertical()
                .setScrollable(list)
                .setLayoutHint(new PositionalLayout.PositionalHint(43, 0, 9, 116));

        return new Panel(mc, this).setLayout(new PositionalLayout()).setLayoutHint(new PositionalLayout.PositionalHint(200, 5, 50, 116))
                .addChild(list)
                .addChild(slider);
//                .setFilledRectThickness(-2)
//                .setFilledBackground(StyleConfig.colorListBackground);
    }

    private Panel setupEditorPanel() {
        Label slotLabel = (Label) new Label(mc, this).setText("Slot:").setDesiredHeight(14);
        TextField slotInfo = new TextField(mc, this).setText("<var 3>").setDesiredHeight(14);
        Button slotButton = new Button(mc, this).setText("...").setDesiredHeight(14);

        Label amountLabel = (Label)new Label(mc, this).setText("Amount:").setDesiredHeight(14);
        TextField amountInfo = new TextField(mc, this).setText("<64>").setDesiredHeight(14);
        Button amountButton = new Button(mc, this).setText("...").setDesiredHeight(14);

        return new Panel(mc, this).setLayout(new HorizontalLayout()).setLayoutHint(new PositionalLayout.PositionalHint(4, 125, 249, 26))
                .addChild(slotLabel)
                .addChild(slotInfo)
                .addChild(slotButton)
                .addChild(amountLabel)
                .addChild(amountInfo)
                .addChild(amountButton)
                .setFilledRectThickness(-2)
                .setFilledBackground(StyleConfig.colorListBackground);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawWindow();
    }
}
