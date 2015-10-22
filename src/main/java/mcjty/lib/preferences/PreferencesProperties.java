package mcjty.lib.preferences;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import mcjty.lib.gui.GuiStyle;
import mcjty.lib.network.PacketSendPreferencesToClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class PreferencesProperties {

    private static final int DEFAULT_BUFFX = 2;
    private static final int DEFAULT_BUFFY = 2;
    private static final GuiStyle DEFAULT_STYLE = GuiStyle.STYLE_FLAT_GRADIENT;

    private Entity entity = null;

    private int buffX = DEFAULT_BUFFX;
    private int buffY = DEFAULT_BUFFY;
    private GuiStyle style = DEFAULT_STYLE;

    private boolean dirty = true;

    public PreferencesProperties() {
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void tick(SimpleNetworkWrapper network) {
        if (dirty) {
            syncToClient(network);
        }
    }

    private void syncToClient(SimpleNetworkWrapper network) {
        System.out.println("syncToClient: style = " + style);
        network.sendTo(new PacketSendPreferencesToClient(buffX, buffY, style), (EntityPlayerMP) entity);
        dirty = false;
    }

    public void saveNBTData(NBTTagCompound compound) {
        compound.setInteger("buffX", buffX);
        compound.setInteger("buffY", buffY);
        compound.setString("style", style.getStyle());
    }

    public void loadNBTData(NBTTagCompound compound) {
        buffX = compound.getInteger("buffX");
        buffY = compound.getInteger("buffY");
        String s = compound.getString("style");
        style = GuiStyle.getStyle(s);
        if (style == null) {
            style = DEFAULT_STYLE;
        }
        dirty = true;
    }

    public void reset() {
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

    public GuiStyle getStyle() {
        return style;
    }

    public void setBuffXY(int x, int y) {
        this.buffX = x;
        this.buffY = y;
        dirty = true;
    }

    public int getBuffX() {
        return buffX;
    }

    public int getBuffY() {
        return buffY;
    }
}
