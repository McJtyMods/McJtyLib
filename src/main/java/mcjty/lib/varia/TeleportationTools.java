package mcjty.lib.varia;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntityMP;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;

public class TeleportationTools {

    public static void performTeleport(PlayerEntity player, int dimension, BlockPos dest, @Nullable Direction direction) {
        performTeleport(player, dimension, dest.getX() + 0.5, dest.getY() + 1.5, dest.getZ() + 0.5, direction);
    }

    public static void performTeleport(PlayerEntity player, int dimension, double destX, double destY, double destZ, @Nullable Direction direction) {
        int oldId = player.getEntityWorld().provider.getDimension();

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
        World w = DimensionManager.getWorld(id);
        if (w == null) {
            w = DimensionManager.getWorld(0).getMinecraftServer().getWorld(id);
        }
        return w;
    }


    public static void teleportToDimension(PlayerEntity player, int dimension, double x, double y, double z) {
        int oldDimension = player.getEntityWorld().provider.getDimension();
        PlayerEntityMP PlayerEntityMP = (PlayerEntityMP) player;
        MinecraftServer server = player.getEntityWorld().getMinecraftServer();
        WorldServer worldServer = server.getWorld(dimension);
        if (worldServer == null) {
            return;
        }
        player.addExperienceLevel(0);


        worldServer.getMinecraftServer().getPlayerList().transferPlayerToDimension(PlayerEntityMP, dimension, new McJtyLibTeleporter(worldServer, x, y, z));
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
            performTeleport((PlayerEntity) entity, destWorld.provider.getDimension(), newX, newY, newZ, facing);
            return entity;
        } else {
            float rotationYaw = entity.rotationYaw;
            float rotationPitch = entity.rotationPitch;

            if (world.provider.getDimension() != destWorld.provider.getDimension()) {
                CompoundNBT tagCompound = new CompoundNBT();
                entity.writeToNBT(tagCompound);
                tagCompound.removeTag("Dimension");
                Class<? extends Entity> entityClass = entity.getClass();
                world.removeEntity(entity);
                entity.isDead = false;
                world.updateEntityWithOptionalForce(entity, false);

                Entity newEntity = EntityList.newEntity(entityClass, destWorld);
                newEntity.readFromNBT(tagCompound);
                if (facing != null) {
                    fixOrientation(newEntity, newX, newY, newZ, facing);
                } else {
                    newEntity.rotationYaw = rotationYaw;
                    newEntity.rotationPitch = rotationPitch;
                }
                newEntity.setLocationAndAngles(newX, newY, newZ, newEntity.rotationYaw, newEntity.rotationPitch);
                boolean flag = newEntity.forceSpawn;
                newEntity.forceSpawn = true;
                destWorld.spawnEntity(newEntity);
                newEntity.forceSpawn = flag;
                destWorld.updateEntityWithOptionalForce(newEntity, false);

                entity.isDead = true;

                ((WorldServer)world).resetUpdateEntityTick();
                ((WorldServer)destWorld).resetUpdateEntityTick();
                return newEntity;
            } else {
                if (facing != null) {
                    fixOrientation(entity, newX, newY, newZ, facing);
                } else {
                    entity.rotationYaw = rotationYaw;
                    entity.rotationPitch = rotationPitch;
                }
                entity.setLocationAndAngles(newX, newY, newZ, entity.rotationYaw, entity.rotationPitch);
                destWorld.updateEntityWithOptionalForce(entity, false);
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
