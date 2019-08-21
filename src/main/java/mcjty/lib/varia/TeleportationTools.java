package mcjty.lib.varia;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

public class TeleportationTools {

    public static void performTeleport(PlayerEntity player, int dimension, BlockPos dest, @Nullable Direction direction) {
        performTeleport(player, dimension, dest.getX() + 0.5, dest.getY() + 1.5, dest.getZ() + 0.5, direction);
    }

    public static void performTeleport(PlayerEntity player, int dimension, double destX, double destY, double destZ, @Nullable Direction direction) {
        int oldId = player.getEntityWorld().getDimension().getType().getId();

        float rotationYaw = player.rotationYaw;
        float rotationPitch = player.rotationPitch;

        if (oldId != dimension) {
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

    /**
     * Get a world for a dimension, possibly loading it from the configuration manager.
     */
    public static World getWorldForDimension(int id) {
        World w = WorldTools.getWorld(id);
        if (w == null) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            w = server.getWorld(DimensionType.getById(id));
        }
        return w;
    }


    public static void teleportToDimension(PlayerEntity player, int dimension, double x, double y, double z) {
        int oldDimension = player.getEntityWorld().getDimension().getType().getId();
        ServerPlayerEntity PlayerEntityMP = (ServerPlayerEntity) player;
        MinecraftServer server = player.getEntityWorld().getServer();
        ServerWorld worldServer = server.getWorld(DimensionType.getById(dimension));
        if (worldServer == null) {
            return;
        }
        player.addExperienceLevel(0);


        // @todo 1.14
//        worldServer.getServer().getPlayerList().transferPlayerToDimension(PlayerEntityMP, dimension, new McJtyLibTeleporter(worldServer, x, y, z));
        player.setPositionAndUpdate(x, y, z);
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
        if (entity instanceof PlayerEntity) {
            performTeleport((PlayerEntity) entity, destWorld.getDimension().getType().getId(), newX, newY, newZ, facing);
            return entity;
        } else {
            float rotationYaw = entity.rotationYaw;
            float rotationPitch = entity.rotationPitch;

            if (world.getDimension().getType() != destWorld.getDimension().getType()) {
                CompoundNBT tagCompound = new CompoundNBT();
                entity.writeUnlessRemoved(tagCompound);
                tagCompound.remove("Dimension");
                EntityType<?> type = entity.getType();
                ((ServerWorld) world).removeEntity(entity);
                entity.revive();
                // @todo 1.14 check?
                ((ServerWorld) world).updateEntity(entity);
//                world.updateEntityWithOptionalForce(entity, false);


                Entity newEntity = type.create(destWorld);
                newEntity.read(tagCompound);
                if (facing != null) {
                    fixOrientation(newEntity, newX, newY, newZ, facing);
                } else {
                    newEntity.rotationYaw = rotationYaw;
                    newEntity.rotationPitch = rotationPitch;
                }
                newEntity.setLocationAndAngles(newX, newY, newZ, newEntity.rotationYaw, newEntity.rotationPitch);
                boolean flag = newEntity.forceSpawn;
                newEntity.forceSpawn = true;
                destWorld.addEntity(newEntity);
                newEntity.forceSpawn = flag;
                ((ServerWorld) world).updateEntity(newEntity);

                entity.remove();

                ((ServerWorld)world).resetUpdateEntityTick();
                ((ServerWorld)destWorld).resetUpdateEntityTick();
                return newEntity;
            } else {
                if (facing != null) {
                    fixOrientation(entity, newX, newY, newZ, facing);
                } else {
                    entity.rotationYaw = rotationYaw;
                    entity.rotationPitch = rotationPitch;
                }
                entity.setLocationAndAngles(newX, newY, newZ, entity.rotationYaw, entity.rotationPitch);
                ((ServerWorld) destWorld).updateEntity(entity);
                return entity;
            }
        }
    }

    private static void fixOrientation(Entity entity, double newX, double newY, double newZ, Direction facing) {
        if (facing != Direction.DOWN && facing != Direction.UP) {
            facePosition(entity, newX, newY, newZ, new BlockPos(newX, newY, newZ).offset(facing, 4));
        }
    }

}
