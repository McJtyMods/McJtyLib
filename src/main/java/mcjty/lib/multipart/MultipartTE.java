package mcjty.lib.multipart;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import java.util.HashMap;
import java.util.Map;

public class MultipartTE extends TileEntity {

    public static class Part {
        private final IBlockState state;
        private final TileEntity tileEntity;

        public Part(IBlockState state, TileEntity tileEntity) {
            this.state = state;
            this.tileEntity = tileEntity;
        }

        public IBlockState getState() {
            return state;
        }

        public TileEntity getTileEntity() {
            return tileEntity;
        }
    }

    private Map<PartSlot, Part> parts = new HashMap<>();
//    private List<PartBlockId> parts = new ArrayList<>();
//    private List<TileEntity> tileEntities = new ArrayList<>();
    private int version = 0;    // To update rendering client-side

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public void addPart(PartSlot slot, IBlockState state, TileEntity te) {
        parts.put(slot, new Part(state, te));
        version++;
        markDirtyClient();
    }

    public Map<PartSlot, Part> getParts() {
        return parts;
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

            PartSlot slot = PartSlot.byName(tag.getString("slot"));
            if (slot != null) {
                ResourceLocation id = new ResourceLocation(tag.getString("id"));
                int meta = tag.getInteger("meta");
                Block block = ForgeRegistries.BLOCKS.getValue(id);
                if (block != null) {
                    IBlockState state = block.getStateFromMeta(meta);
                    TileEntity te = null;
                    if (tag.hasKey("te")) {
                        NBTTagCompound tc = tag.getCompoundTag("te");
                        te = block.createTileEntity(null, state);// @todo
                        if (te != null) {
                            te.readFromNBT(tc);
                        }
                    }
                    Part part = new Part(state, te);
                    parts.put(slot, part);
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            PartSlot slot = entry.getKey();
            Part part = entry.getValue();

            tag.setString("slot", slot.name());

            IBlockState state = part.getState();
            tag.setString("id", state.getBlock().getRegistryName().toString());
            tag.setInteger("meta", state.getBlock().getMetaFromState(state));

            TileEntity te = part.getTileEntity();
            if (te != null) {
                NBTTagCompound tc = new NBTTagCompound();
                tc = te.writeToNBT(tc);
                tag.setTag("te", tc);
            }

            list.appendTag(tag);
        }
        compound.setTag("parts", list);
        compound.setInteger("version", version);

        return super.writeToNBT(compound);
    }

    public boolean testIntersect(IBlockState blockState) {
        AxisAlignedBB box = blockState.getBoundingBox(world, pos);
        for (Map.Entry<PartSlot, Part> entry : getParts().entrySet()) {
            // @todo just check on slot?
            Part part = entry.getValue();
            AxisAlignedBB partBox = part.getState().getBoundingBox(world, pos);
            if (box.intersects(partBox)) {
                return true;
            }
        }
        return false;
    }


}
