package mcjty.lib.multipart;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MultipartTE extends TileEntity {

    private List<PartBlockId> parts = new ArrayList<>();
    private int version = 0;    // To update rendering client-side

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public void addPart(IBlockState part) {
        parts.add(new PartBlockId(part));
        version++;
        markDirtyClient();
    }

    public List<PartBlockId> getParts() {
        List<PartBlockId> newParts = new ArrayList<>(parts);
//        Block value = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("ariente", "signal_receiver"));
//        newParts.add(new PartBlockId(value.getDefaultState()));
        return newParts;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        int oldVersion = version;
        super.onDataPacket(net, packet);
        if (world.isRemote && version != oldVersion) {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound updateTag = super.getUpdateTag();
        readFromNBT(updateTag);
        return updateTag;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = getUpdateTag();
        return new SPacketUpdateTileEntity(pos, 1, nbtTag);
    }

    public void markDirtyClient() {
        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        version = compound.getInteger("version");
        NBTTagList list = compound.getTagList("parts", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < list.tagCount() ; i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            ResourceLocation id = new ResourceLocation(tag.getString("id"));
            int meta = tag.getInteger("meta");
            Block block = ForgeRegistries.BLOCKS.getValue(id);
            if (block != null) {
                parts.add(new PartBlockId(block.getStateFromMeta(meta)));
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (PartBlockId part : parts) {
            NBTTagCompound tag = new NBTTagCompound();
            IBlockState state = part.getBlockState();
            tag.setString("id", state.getBlock().getRegistryName().toString());
            tag.setInteger("meta", state.getBlock().getMetaFromState(state));
            list.appendTag(tag);
        }
        compound.setTag("parts", list);
        compound.setInteger("version", version);

        return super.writeToNBT(compound);
    }

    public boolean testIntersect(IBlockState blockState) {
        AxisAlignedBB box = blockState.getBoundingBox(world, pos);
        for (PartBlockId part : getParts()) {
            AxisAlignedBB partBox = part.getBlockState().getBoundingBox(world, pos);
            if (box.intersects(partBox)) {
                return true;
            }
        }
        return false;
    }


}
