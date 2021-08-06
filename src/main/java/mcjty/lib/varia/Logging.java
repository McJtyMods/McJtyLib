package mcjty.lib.varia;

import mcjty.lib.McJtyLib;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeConfigSpec;
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
        getLogger().log(org.apache.logging.log4j.Level.ERROR, msg);
    }

    public static void log(Level world, BlockEntity te, String message) {
        if (doLogging.get()) {
            long ticks = world.getGameTime();
            if (ticks != prevTicks) {
                prevTicks = ticks;
                getInstance().logger.log(org.apache.logging.log4j.Level.INFO, "=== Time " + ticks + " ===");
            }
            String id = te.getBlockPos().getX() + "," + te.getBlockPos().getY() + "," + te.getBlockPos().getZ() + ": ";
            getInstance().logger.log(org.apache.logging.log4j.Level.INFO, id + message);
        }
    }

    public static Logger getLogger() {
        return getInstance().logger;
    }

    public static void logError(String msg, Throwable e) {
        getLogger().error(msg, e);
    }

    public static void log(String message) {
        getInstance().logger.log(org.apache.logging.log4j.Level.INFO, message);
    }

    public static void logDebug(String message) {
        if (debugMode) {
            getInstance().logger.log(org.apache.logging.log4j.Level.INFO, message);
        }
    }

    public static void message(@Nonnull Player player, String message) {
        player.displayClientMessage(new TextComponent(message), false);
    }

    public static void warn(@Nonnull Player player, String message) {
        player.displayClientMessage(new TextComponent(message).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
    }
}
