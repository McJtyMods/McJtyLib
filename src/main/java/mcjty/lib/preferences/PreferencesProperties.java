package mcjty.lib.preferences;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.gui.BuffStyle;
import mcjty.lib.gui.GuiStyle;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketSendPreferencesToClient;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;

public class PreferencesProperties {

    private static final int DEFAULT_BUFFX = -20;
    private static final int DEFAULT_BUFFY = -20;
    private static final GuiStyle DEFAULT_STYLE = GuiStyle.STYLE_FLAT_GRADIENT;

    private int buffX = DEFAULT_BUFFX;
    private int buffY = DEFAULT_BUFFY;
    private BuffStyle buffStyle = BuffStyle.BOTRIGHT;
    private GuiStyle style = DEFAULT_STYLE;

    public static final Codec<PreferencesProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuffStyle.CODEC.fieldOf("buffStyle").forGetter(PreferencesProperties::getBuffStyle),
            Codec.INT.fieldOf("buffX").forGetter(PreferencesProperties::getBuffX),
            Codec.INT.fieldOf("buffY").forGetter(PreferencesProperties::getBuffY),
            GuiStyle.CODEC.fieldOf("style").forGetter(PreferencesProperties::getStyle)
    ).apply(instance, PreferencesProperties::new));

    private boolean dirty = true;

    public PreferencesProperties(BuffStyle buffStyle, int buffX, int buffY, GuiStyle style) {
        this.buffStyle = buffStyle;
        this.buffX = buffX;
        this.buffY = buffY;
        this.style = style;
    }

    public PreferencesProperties() {
    }

    public void tick(ServerPlayer player) {
        if (dirty) {
            syncToClient(player);
        }
    }

    private void syncToClient(ServerPlayer player) {
        Networking.sendToPlayer(PacketSendPreferencesToClient.create(buffStyle, buffX, buffY, style), player);
        dirty = false;
    }

    public void reset() {
        buffStyle = BuffStyle.TOPLEFT;
        buffX = DEFAULT_BUFFX;
        buffY = DEFAULT_BUFFY;
        style = DEFAULT_STYLE;
        dirty = true;
    }

    public boolean setStyle(String s) {
        GuiStyle st = GuiStyle.getStyle(s);
        if (st == null) {
            return false;
        }
        style = st;
        dirty = true;
        return true;
    }

    public boolean setStyle(GuiStyle st) {
        if (st == null) {
            return false;
        }
        style = st;
        dirty = true;
        return true;
    }

    @Nonnull
    public GuiStyle getStyle() {
        return style;
    }

    public void setBuffXY(BuffStyle buffStyle, int x, int y) {
        this.buffStyle = buffStyle;
        this.buffX = x;
        this.buffY = y;
        dirty = true;
    }

    public BuffStyle getBuffStyle() {
        return buffStyle;
    }

    public int getBuffX() {
        return buffX;
    }

    public int getBuffY() {
        return buffY;
    }
}