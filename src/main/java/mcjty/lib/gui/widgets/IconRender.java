package mcjty.lib.gui.widgets;

import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.typed.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class IconRender extends AbstractWidget<IconRender> {

    public static final String TYPE_ICONRENDER = "iconrender";

    private TextureAtlasSprite icon = null;

    public IconRender(Minecraft mc, Gui gui) {
        super(mc, gui);
        setDesiredHeight(16);
        setDesiredWidth(16);
    }

    public TextureAtlasSprite getIcon() {
        return icon;
    }

    public IconRender setIcon(TextureAtlasSprite icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public void draw(int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(x, y);
        if (icon != null) {
            RenderHelper.renderObject(mc, x + bounds.x, y + bounds.y, icon, false);
        }
    }


    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_ICONRENDER);
    }

    @Override
    public <T> void setGenericValue(T value) {
    }

    @Override
    public Object getGenericValue(Type type) {
        return null;
    }
}
