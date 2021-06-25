package mcjty.lib.preferences;

import mcjty.lib.McJtyLib;
import mcjty.lib.gui.GuiStyle;
import mcjty.lib.network.PacketSendPreferencesToClient;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.network.NetworkDirection;

import javax.annotation.Nonnull;

public class PreferencesProperties {

    private static final int DEFAULT_BUFFX = 2;
    private static final int DEFAULT_BUFFY = 2;
    private static final GuiStyle DEFAULT_STYLE = GuiStyle.STYLE_FLAT_GRADIENT;

    private int buffX = DEFAULT_BUFFX;
    private int buffY = DEFAULT_BUFFY;
    private GuiStyle style = DEFAULT_STYLE;

    private boolean dirty = true;

    public PreferencesProperties() {
    }

    public void tick(ServerPlayerEntity player) {
        if (dirty) {
            syncToClient(player);
        }
    }

    private void syncToClient(ServerPlayerEntity player) {
        McJtyLib.networkHandler.sendTo(new PacketSendPreferencesToClient(buffX, buffY, style), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        dirty = false;
    }

    public void saveNBTData(CompoundNBT compound) {
        compound.putInt("buffX", buffX);
        compound.putInt("buffY", buffY);
        compound.putString("style", style.getStyle());
    }

    public void loadNBTData(CompoundNBT compound) {
        buffX = compound.getInt("buffX");
        buffY = compound.getInt("buffY");
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

    @Nonnull
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

    public static void register() {
        CapabilityManager.INSTANCE.register(PreferencesProperties.class, new Capability.IStorage<PreferencesProperties>() {

            @Override
            public INBT writeNBT(Capability<PreferencesProperties> capability, PreferencesProperties instance, Direction side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(Capability<PreferencesProperties> capability, PreferencesProperties instance, Direction side, INBT nbt) {
                throw new UnsupportedOperationException();
            }

        }, () -> {
            throw new UnsupportedOperationException();
        });
    }
}
