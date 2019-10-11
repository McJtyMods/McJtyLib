package mcjty.lib.varia;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.server.ServerWorld;

public class McJtyLibTeleporter extends Teleporter {
    private final ServerWorld worldServerInstance;

    private double x;
    private double y;
    private double z;


    public McJtyLibTeleporter(ServerWorld world, double x, double y, double z) {
        super(world);
        this.worldServerInstance = world;
        this.x = x;
        this.y = y;
        this.z = z;

    }


    @Override
    public boolean placeInPortal(Entity pEntity, float rotationYaw) {
        this.worldServerInstance.getBlockState(new BlockPos((int) this.x, (int) this.y, (int) this.z));   //dummy load to maybe gen chunk

        pEntity.setPosition(this.x, this.y, this.z);
        pEntity.setMotion(0, 0, 0);
        return true;
    }

}
