package mcjty.lib.varia;

import mcjty.lib.McJtyLib;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class Logging {
    private static Logging instance = null;

    public static long prevTicks = -1;
    private final Logger logger;

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

    public static void log(net.minecraft.world.level.Level world, BlockEntity te, String message) {
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

    public static void message(@Nonnull Player player, String message) {
        player.displayClientMessage(ComponentFactory.literal(message), false);
    }

    public static void warn(@Nonnull Player player, String message) {
        player.displayClientMessage(ComponentFactory.literal(message).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
    }
}
