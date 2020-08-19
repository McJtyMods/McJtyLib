package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.typed.Type;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class IconRender extends AbstractWidget<IconRender> {

    public static final String TYPE_ICONRENDER = "iconrender";

    private TextureAtlasSprite icon = null;

    public IconRender() {
        desiredHeight(16);
        desiredWidth(16);
    }

    public TextureAtlasSprite getIcon() {
        return icon;
    }

    public IconRender icon(TextureAtlasSprite icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public void draw(Screen gui, MatrixStack matrixStack, int x, int y) {
        if (!visible) {
            return;
        }
        super.draw(gui, matrixStack, x, y);
        if (icon != null) {
            RenderHelper.renderObject(matrixStack, x + bounds.x, y + bounds.y, icon, false);
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
    public Object getGenericValue(Type<?> type) {
        return null;
    }
}
