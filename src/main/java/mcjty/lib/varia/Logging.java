package mcjty.lib.varia;

import mcjty.lib.McJtyLib;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class Logging {
    private static Logging instance = null;

    public static long prevTicks = -1;
    private Logger logger;

    public static boolean debugMode = false;
    public static ForgeConfigSpec.BooleanValue doLogging;

    private Logging() {
        logger = LogManager.getLogger(McJtyLib.MODID);
        instance = this;
    }

    private static Logging getInstance() {
        if (instance == null) {
            instance = new Logging();
        }
        return instance;
    }

    public static void logError(String msg) {
        getLogger().log(Level.ERROR, msg);
    }

    public static void log(World world, TileEntity te, String message) {
        if (doLogging.get()) {
            long ticks = world.getGameTime();
            if (ticks != prevTicks) {
                prevTicks = ticks;
                getInstance().logger.log(Level.INFO, "=== Time " + ticks + " ===");
            }
            String id = te.getBlockPos().getX() + "," + te.getBlockPos().getY() + "," + te.getBlockPos().getZ() + ": ";
            getInstance().logger.log(Level.INFO, id + message);
        }
    }

    public static Logger getLogger() {
        return getInstance().logger;
    }

    public static void logError(String msg, Throwable e) {
        getLogger().error(msg, e);
    }

    public static void log(String message) {
        getInstance().logger.log(Level.INFO, message);
    }

    public static void logDebug(String message) {
        if (debugMode) {
            getInstance().logger.log(Level.INFO, message);
        }
    }

    public static void message(@Nonnull PlayerEntity player, String message) {
        player.displayClientMessage(new StringTextComponent(message), false);
    }

    public static void warn(@Nonnull PlayerEntity player, String message) {
        player.displayClientMessage(new StringTextComponent(message).setStyle(Style.EMPTY.withColor(TextFormatting.RED)), false);
    }
}
