package mcjty.lib.font;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** AWT font metrics and lazily uploaded glyph textures for GUI extraction. */
public class TrueTypeFont implements AutoCloseable {
    public static final int ALIGN_LEFT = 0, ALIGN_RIGHT = 1, ALIGN_CENTER = 2;
    private static final AtomicInteger IDS = new AtomicInteger();
    private final Font font;
    private final FontMetrics metrics;
    private final boolean antiAlias;
    private final Map<Character, Identifier> glyphs = new LinkedHashMap<>();
    private final int id = IDS.incrementAndGet();

    public TrueTypeFont(Font font, boolean antiAlias, char[] additionalChars) {
        this.font = font;
        this.antiAlias = antiAlias;
        var image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        var graphics = image.createGraphics();
        graphics.setFont(font);
        metrics = graphics.getFontMetrics();
        graphics.dispose();
    }
    public TrueTypeFont(Font font, boolean antiAlias) { this(font, antiAlias, null); }
    public void setCorrection(boolean on) { }
    public float getWidth(String text) {
        return text.lines().mapToInt(metrics::stringWidth).max().orElse(0);
    }
    public float getHeight() { return metrics.getHeight(); }
    public float getHeight(String text) { return Math.max(1, text.lines().count()) * getHeight(); }
    public float getLineHeight() { return getHeight(); }
    public String trimStringToWidth(String text, int width) {
        int end = 0;
        while (end < text.length() && metrics.stringWidth(text.substring(0, end + 1)) <= width) end++;
        return text.substring(0, end);
    }

    public void drawString(GuiGraphicsExtractor graphics, float x, float y, String text, float scaleX, float scaleY, float yoffset, float... rgba) {
        drawString(graphics, x, y, text, scaleX, scaleY, ALIGN_LEFT, yoffset, rgba);
    }
    public void drawString(GuiGraphicsExtractor graphics, float x, float y, String text, float scaleX, float scaleY, int format, float yoffset, float... rgba) {
        int color = rgba.length < 4 ? 0xffffffff : ((int)(rgba[3]*255)<<24) | ((int)(rgba[0]*255)<<16) | ((int)(rgba[1]*255)<<8) | (int)(rgba[2]*255);
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y + yoffset);
        graphics.pose().scale(scaleX, scaleY);
        int lineY = 0;
        for (String line : text.split("\\n", -1)) {
            int lineX = format == ALIGN_RIGHT ? -metrics.stringWidth(line) : format == ALIGN_CENTER ? -metrics.stringWidth(line) / 2 : 0;
            for (char ch : line.toCharArray()) {
                int width = Math.max(1, metrics.charWidth(ch));
                Identifier texture = glyphs.computeIfAbsent(ch, this::uploadGlyph);
                graphics.blit(RenderPipelines.GUI_TEXTURED, texture, lineX, lineY, 0, 0, width, metrics.getHeight(), width, metrics.getHeight(), color);
                lineX += metrics.charWidth(ch);
            }
            lineY += metrics.getHeight();
        }
        graphics.pose().popMatrix();
    }
    private Identifier uploadGlyph(char character) {
        int width = Math.max(1, metrics.charWidth(character)), height = metrics.getHeight();
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var graphics = image.createGraphics();
        graphics.setFont(font);
        graphics.setColor(Color.WHITE);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics.drawString(String.valueOf(character), 0, metrics.getAscent());
        graphics.dispose();
        var nativeImage = new NativeImage(width, height, false);
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) nativeImage.setPixel(x, y, image.getRGB(x, y));
        var location = Identifier.fromNamespaceAndPath("mcjtylib", "font/" + id + "/" + (int)character);
        Minecraft.getInstance().getTextureManager().register(location, new DynamicTexture(() -> location.toString(), nativeImage));
        return location;
    }
    public static boolean isSupported(String name) {
        return java.util.Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()).contains(name);
    }
    public static Font[] getFonts() { return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts(); }
    public static byte[] intToByteArray(int value) { return new byte[]{(byte)(value>>>24),(byte)(value>>>16),(byte)(value>>>8),(byte)value}; }
    public void destroy() { close(); }
    @Override public void close() {
        glyphs.values().forEach(Minecraft.getInstance().getTextureManager()::release);
        glyphs.clear();
    }
}
