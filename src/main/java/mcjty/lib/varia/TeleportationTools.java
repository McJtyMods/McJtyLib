package mcjty.lib.varia;

import mcjty.lib.McJtyLib;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

public class TeleportationTools {

    public static void teleport(PlayerEntity player, RegistryKey<World> dimension, double destX, double destY, double destZ, @Nullable Direction direction) {
        RegistryKey<World> oldId = player.getCommandSenderWorld().dimension();

        float rotationYaw = player.yRot;
        float rotationPitch = player.xRot;

        if (!oldId.equals(dimension)) {
            teleportToDimension(player, dimension, destX, destY, destZ);
        }
        if (direction != null) {
            fixOrientation(player, destX, destY, destZ, direction);
        } else {
            player.yRot = rotationYaw;
            player.xRot = rotationPitch;
        }
        player.teleportTo(destX, destY, destZ);
    }

    public static void teleportToDimension(PlayerEntity player, RegistryKey<World> dimension, double x, double y, double z) {
        ServerWorld world = WorldTools.getLevel(player.getCommandSenderWorld(), dimension);
        if (world == null) {
            McJtyLib.setup.getLogger().error("Something went wrong teleporting to dimension " + dimension.location().getPath());
            return;
        }
        player.changeDimension(world, new ITeleporter() {
            @Override
            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                entity.setLevel(world);
                world.addDuringPortalTeleport((ServerPlayerEntity) entity);
                entity.moveTo(x, y, z);
                entity.teleportTo(x, y, z);
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
        entity.xRot = updateRotation(entity.xRot, f1);
        entity.yRot = updateRotation(entity.yRot, f);
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
        World world = entity.getCommandSenderWorld();
        if (Objects.equals(world.dimension(), destWorld.dimension())) {
            if (facing != null) {
                fixOrientation(entity, newX, newY, newZ, facing);
            }
            entity.moveTo(newX, newY, newZ, entity.yRot, entity.xRot);
            ((ServerWorld) destWorld).tickNonPassenger(entity);
            return entity;
        } else {
            return entity.changeDimension((ServerWorld) destWorld, new ITeleporter() {
                @Override
                public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                    entity = repositionEntity.apply(false);
                    if (facing != null) {
                        fixOrientation(entity, newX, newY, newZ, facing);
                    }
                    entity.teleportTo(newX, newY, newZ);
                    return entity;
                }
            });
        }
    }

    private static void fixOrientation(Entity entity, double newX, double newY, double newZ, Direction facing) {
        if (facing != Direction.DOWN && facing != Direction.UP) {
            facePosition(entity, newX, newY, newZ, new BlockPos(newX, newY, newZ).relative(facing, 4));
        }
    }

}
