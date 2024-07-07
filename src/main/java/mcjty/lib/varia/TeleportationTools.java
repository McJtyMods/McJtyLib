package mcjty.lib.varia;

import mcjty.lib.McJtyLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Objects;

public class TeleportationTools {

    public static void teleport(Player player, ResourceKey<Level> dimension, double destX, double destY, double destZ, @Nullable Direction direction) {
        ResourceKey<Level> oldId = player.getCommandSenderWorld().dimension();

        float rotationYaw = player.getYRot();
        float rotationPitch = player.getXRot();

        if (!oldId.equals(dimension)) {
            teleportToDimension(player, dimension, destX, destY, destZ);
        }
        if (direction != null) {
            fixOrientation(player, destX, destY, destZ, direction);
        } else {
            player.setYRot(rotationYaw);
            player.setXRot(rotationPitch);
        }
        player.teleportTo(destX, destY, destZ);
    }

    public static void teleportToDimension(Player player, ResourceKey<Level> dimension, double x, double y, double z) {
        ServerLevel world = LevelTools.getLevel(player.getCommandSenderWorld(), dimension);
        if (world == null) {
            McJtyLib.setup.getLogger().error("Something went wrong teleporting to dimension " + dimension.location().getPath());
            return;
        }
        DimensionTransition transition = new DimensionTransition(world, new Vec3(x, y, z), new Vec3(0, 0, 0), 0, 0, DimensionTransition.DO_NOTHING);
        player.changeDimension(transition);
    }

    private record Rot(float yaw, float pitch) {}

    private static Rot facePosition(Entity entity, double newX, double newY, double newZ, BlockPos dest) {
        double d0 = dest.getX() - newX;
        double d1 = dest.getY() - (newY + entity.getEyeHeight());
        double d2 = dest.getZ() - newZ;

        double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
        float f = (float) (Mth.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
        float f1 = (float) (-(Mth.atan2(d1, d3) * (180D / Math.PI)));
        f1 = updateRotation(entity.getXRot(), f1);
        f = updateRotation(entity.getYRot(), f);
        return new Rot(f, f1);

    }

    private static float updateRotation(float angle, float targetAngle) {
        float f = Mth.wrapDegrees(targetAngle - angle);
        return angle + f;
    }


    /**
     * Teleport an entity and return the new entity (as teleporting to other dimensions causes
     * entities to be killed and recreated)
     */
    public static Entity teleportEntity(Entity entity, Level destWorld, double newX, double newY, double newZ, Direction facing) {
        Level world = entity.getCommandSenderWorld();
        if (Objects.equals(world.dimension(), destWorld.dimension())) {
            if (facing != null) {
                fixOrientation(entity, newX, newY, newZ, facing);
            }
            entity.moveTo(newX, newY, newZ, entity.getYRot(), entity.getXRot());
            ((ServerLevel) destWorld).tickNonPassenger(entity);
            return entity;
        } else {
            var rot = fixOrientation(entity, newX, newY, newZ, facing);
            DimensionTransition transition = new DimensionTransition((ServerLevel) destWorld, new Vec3(newX, newY, newZ), Vec3.ZERO,
                    rot.yaw(), rot.pitch(), DimensionTransition.DO_NOTHING);    // @todo 1.21 check?
            return entity.changeDimension(transition);
        }
    }

    private static Rot fixOrientation(Entity entity, double newX, double newY, double newZ, Direction facing) {
        if (facing != Direction.DOWN && facing != Direction.UP) {
            return facePosition(entity, newX, newY, newZ, new BlockPos((int) newX, (int) newY, (int) newZ).relative(facing, 4));
        } else {
            return new Rot(entity.getYRot(), entity.getXRot());
        }
    }

}
