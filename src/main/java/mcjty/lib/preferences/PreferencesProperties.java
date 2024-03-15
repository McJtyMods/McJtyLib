package mcjty.lib.preferences;

import mcjty.lib.gui.BuffStyle;
import mcjty.lib.gui.GuiStyle;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketSendPreferencesToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public class PreferencesProperties implements INBTSerializable<CompoundTag>  {

    private static final int DEFAULT_BUFFX = -20;
    private static final int DEFAULT_BUFFY = -20;
    private static final GuiStyle DEFAULT_STYLE = GuiStyle.STYLE_FLAT_GRADIENT;

    private int buffX = DEFAULT_BUFFX;
    private int buffY = DEFAULT_BUFFY;
    private BuffStyle buffStyle = BuffStyle.BOTRIGHT;
    private GuiStyle style = DEFAULT_STYLE;

    private boolean dirty = true;

    public PreferencesProperties() {
    }

    public void tick(ServerPlayer player) {
        if (dirty) {
            syncToClient(player);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        loadNBTData(compoundTag);
    }

    private void syncToClient(ServerPlayer player) {
        Networking.sendToPlayer(PacketSendPreferencesToClient.create(buffStyle, buffX, buffY, style), player);
        dirty = false;
    }

    public void saveNBTData(CompoundTag compound) {
        compound.putString("buffStyle", buffStyle.getName());
        compound.putInt("buffX", buffX);
        compound.putInt("buffY", buffY);
        compound.putString("style", style.getStyle());
    }

    public void loadNBTData(CompoundTag compound) {
        buffStyle = BuffStyle.getStyle(compound.getString("buffStyle"));
        if (buffStyle == null) {
            buffStyle = BuffStyle.BOTRIGHT;
            buffX = DEFAULT_BUFFX;
            buffY = DEFAULT_BUFFY;
        } else {
            buffX = compound.getInt("buffX");
            buffY = compound.getInt("buffY");
        }
        String s = compound.getString("style");
        style = GuiStyle.getStyle(s);
        if (style == null) {
            style = DEFAULT_STYLE;
        }
        dirty = true;
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