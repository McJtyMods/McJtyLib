package mcjty.lib.gui.widgets;

import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class IconRender extends AbstractWidget<IconRender> {

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
    public void draw(Window window, int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(window, x, y);
        if (icon != null) {
            RenderHelper.renderObject(mc, x + bounds.x, y + bounds.y, icon, false);
        }
    }

}
