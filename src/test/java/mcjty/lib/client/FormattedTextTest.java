package mcjty.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class FormattedTextTest {
    private record Glyph(int codePoint, Style style) {}

    private static List<Glyph> glyphs(FormattedCharSequence text) {
        List<Glyph> result = new ArrayList<>();
        text.accept((index, style, codePoint) -> {
            result.add(new Glyph(codePoint, style));
            return true;
        });
        return result;
    }

    private static String plain(List<Glyph> glyphs) {
        StringBuilder result = new StringBuilder();
        glyphs.forEach(g -> result.appendCodePoint(g.codePoint()));
        return result.toString();
    }

    @Test
    void tooltipColorsAndResetsBecomeStyles() {
        Component text = ComponentFactory.formatted(ChatFormatting.YELLOW + "Press" + ChatFormatting.BOLD + "!" + ChatFormatting.RESET + " ready");
        var glyphs = glyphs(text.getVisualOrderText());
        assertEquals("Press! ready", plain(glyphs));
        assertEquals(TextColor.fromLegacyFormat(ChatFormatting.YELLOW).getValue(), glyphs.getFirst().style().getColor().getValue());
        assertFalse(glyphs.getFirst().style().isBold());
        assertTrue(glyphs.get(5).style().isBold());
        assertEquals(TextColor.fromLegacyFormat(ChatFormatting.YELLOW).getValue(), glyphs.get(5).style().getColor().getValue());
        assertNull(glyphs.getLast().style().getColor());
        assertFalse(glyphs.getLast().style().isBold());
    }

    @Test
    void hudClippingCountsVisibleGlyphsAndRetainsTheirStyles() {
        StringSplitter splitter = new StringSplitter((codePoint, style) -> style.isBold() ? 2 : 1);
        var clipped = splitter.headByWidth(ComponentFactory.formatted(ChatFormatting.BLUE + "Progress: 42"), 9, Style.EMPTY);
        var glyphs = glyphs(net.minecraft.locale.Language.getInstance().getVisualOrder(clipped));
        assertEquals("Progress:", plain(glyphs));
        assertTrue(glyphs.stream().allMatch(g -> g.style().getColor().getValue() == TextColor.fromLegacyFormat(ChatFormatting.BLUE).getValue()));
        assertEquals(9, splitter.stringWidth(clipped));
    }

    @Test
    void worldTextSubmissionConsumesCodesAndPreservesColors() {
        AtomicReference<FormattedCharSequence> submitted = new AtomicReference<>();
        var collector = (SubmitNodeCollector) Proxy.newProxyInstance(SubmitNodeCollector.class.getClassLoader(), new Class<?>[]{SubmitNodeCollector.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("submitText")) submitted.set((FormattedCharSequence) args[3]);
                    return null;
                });
        RenderHelper.renderText(null, ChatFormatting.BLUE + "Mode:", 7, 7, 0xffffffff, new PoseStack(), collector, 0);
        var glyphs = glyphs(submitted.get());
        assertEquals("Mode:", plain(glyphs));
        assertTrue(glyphs.stream().allMatch(g -> g.style().getColor().getValue() == TextColor.fromLegacyFormat(ChatFormatting.BLUE).getValue()));
    }

    @Test
    void plainAndUnicodeTextSurviveFormatting() {
        assertEquals("", ComponentFactory.formatted("").getString());
        assertEquals("Plain text", ComponentFactory.formatted("Plain text").getString());
        var glyphs = glyphs(ComponentFactory.formatted(ChatFormatting.RED + "Warning \uD83D\uDE80").getVisualOrderText());
        assertEquals("Warning \uD83D\uDE80", plain(glyphs));
        assertEquals(9, glyphs.size());
        assertEquals(TextColor.fromLegacyFormat(ChatFormatting.RED).getValue(), glyphs.getLast().style().getColor().getValue());
        assertEquals("\u00a7eLiteral", ComponentFactory.literal("\u00a7eLiteral").getString());
    }
}
