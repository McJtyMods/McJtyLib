package mcjty.lib.varia;

import com.mojang.authlib.GameProfile;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.Objects;
import java.util.UUID;

public class FakePlayerGetter {

    private final GenericTileEntity te;
    private final String fakeName;
    private ServerPlayer harvester = null;
//    private final LazyGetter<ServerPlayerEntity> harvester = LazyGetter.of(this::getFakeHarvester);

    public FakePlayerGetter(GenericTileEntity te, String fakeName) {
        this.te = te;
        this.fakeName = fakeName;
    }

    public ServerPlayer get() {
        ServerPlayer playerEntity = getFakeHarvester();

        UUID owner = te.getOwnerUUID();
        if (owner != null) {
            ServerPlayer player = te.getLevel().getServer().getPlayerList().getPlayer(owner);
            if (player != null) {
                // Check if the name matches
                if (!Objects.equals(playerEntity.getGameProfile().getName(), player.getGameProfile().getName())) {
                    harvester = null;
                    playerEntity = getFakeHarvester();
                }
            }
        }
        return playerEntity;
    }

    private ServerPlayer getFakeHarvester() {
        if (harvester == null) {
            UUID owner = te.getOwnerUUID();
            if (owner == null) {
                owner = UUID.nameUUIDFromBytes("rftools_builder".getBytes());
            }
            harvester = FakePlayerFactory.get((ServerLevel) te.getLevel(), new GameProfile(owner, getName()));
            harvester.setLevel((ServerLevel) te.getLevel());
            BlockPos worldPosition = te.getBlockPos();
            harvester.setPos(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        }
        return harvester;
    }

    private String getName() {
        UUID owner = te.getOwnerUUID();
        if (owner == null) {
            return fakeName;
        }
        ServerPlayer player = te.getLevel().getServer().getPlayerList().getPlayer(owner);
        if (player == null) {
            return fakeName;
        }
        return player.getGameProfile().getName();
    }


}
