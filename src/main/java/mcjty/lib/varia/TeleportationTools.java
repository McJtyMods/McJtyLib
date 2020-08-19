package mcjty.lib.varia;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.function.Function;

public class TeleportationTools {

    public static void teleport(PlayerEntity player, DimensionId dimension, double destX, double destY, double destZ, @Nullable Direction direction) {
        DimensionId oldId = DimensionId.fromWorld(player.getEntityWorld());

        float rotationYaw = player.rotationYaw;
        float rotationPitch = player.rotationPitch;

        if (!oldId.equals(dimension)) {
            teleportToDimension(player, dimension, destX, destY, destZ);
        }
        if (direction != null) {
            fixOrientation(player, destX, destY, destZ, direction);
        } else {
            player.rotationYaw = rotationYaw;
            player.rotationPitch = rotationPitch;
        }
        player.setPositionAndUpdate(destX, destY, destZ);
    }

    public static void teleportToDimension(PlayerEntity player, DimensionId dimension, double x, double y, double z) {
        player.changeDimension(dimension.getWorld(player.getEntityWorld()), new ITeleporter() {
            @Override
            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                entity = repositionEntity.apply(false);
                entity.setPositionAndUpdate(x, y, z);
                return entity;
            }
        });
    }

    private static void facePosition(Entity entity, double newX, double newY, double newZ, BlockPos dest) {
        double d0 = dest.getX() - newX;
        double d1 = dest.getY() - (newY + entity.getEyeHeight());
        double d2 = dest.getZ() - newZ;

        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
        entity.rotationPitch = updateRotation(entity.rotationPitch, f1);
        entity.rotationYaw = updateRotation(entity.rotationYaw, f);
    }

    private static float updateRotation(float angle, float targetAngle) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);
        return angle + f;
    }


    /**
     * Teleport an entity and return the new entity (as teleporting to other dimensions causes
     * entities to be killed and recreated)
     */
    public static Entity teleportEntity(Entity entity, World destWorld, double newX, double newY, double newZ, Direction facing) {
        World world = entity.getEntityWorld();
        if (DimensionId.fromWorld(world).equals(DimensionId.fromWorld(destWorld))) {
            if (facing != null) {
                fixOrientation(entity, newX, newY, newZ, facing);
            }
            entity.setLocationAndAngles(newX, newY, newZ, entity.rotationYaw, entity.rotationPitch);
            ((ServerWorld) destWorld).updateEntity(entity);
            return entity;
        } else {
            return entity.changeDimension((ServerWorld) destWorld, new ITeleporter() {
                @Override
                public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                    entity = repositionEntity.apply(false);
                    if (facing != null) {
                        fixOrientation(entity, newX, newY, newZ, facing);
                    }
                    entity.setPositionAndUpdate(newX, newY, newZ);
                    return entity;
                }
            });
        }
    }

    private static void fixOrientation(Entity entity, double newX, double newY, double newZ, Direction facing) {
        if (facing != Direction.DOWN && facing != Direction.UP) {
            facePosition(entity, newX, newY, newZ, new BlockPos(newX, newY, newZ).offset(facing, 4));
        }
    }

}
