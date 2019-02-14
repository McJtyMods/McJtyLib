package mcjty.lib.multipart;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

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
    private int version = 0;    // To update rendering client-side

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    private void dumpParts(String prefix) {
        if (world.isRemote) {
            System.out.println("====== CLIENT(" + prefix + ") " + pos.toString() + " ======");
        } else {
            System.out.println("====== SERVER(" + prefix + ") " + pos.toString() + " ======");
        }
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            IBlockState state = entry.getValue().state;
            System.out.println("    SLOT: " + entry.getKey().name() +
                    "    " + state.getBlock().getRegistryName().toString());
            for (Map.Entry<IProperty<?>, Comparable<?>> e : state.getProperties().entrySet()) {
                System.out.println("        PROP: " + e.getKey() + " = " + e.getValue());
            }

        }
    }

    public void addPart(PartSlot slot, IBlockState state, TileEntity te) {
        parts.put(slot, new Part(state, te));

        if (te instanceof GenericTileEntity) {
            ((GenericTileEntity) te).onPartAdded(slot, state, this);
        }

        if (!world.isRemote) {
            version++;
            markDirtyClient();
        }
//        dumpParts("add");
    }

    public void removePart(IBlockState partState) {
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            if (entry.getValue().getState() == partState) {
                parts.remove(entry.getKey());
                if (!world.isRemote) {
                    version++;
                    markDirtyClient();
                }
//                dumpParts("remove");
                return;
            }
        }
    }

    public Map<PartSlot, Part> getParts() {
        return parts;
    }


    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        int oldVersion = version;
        readFromNBT(packet.getNbtCompound());
        if (world.isRemote && version != oldVersion) {
//            dumpParts("onData");
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound updateTag = super.getUpdateTag();
        writeToNBT(updateTag);
        return updateTag;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(pos, 1, nbtTag);
    }

    public void markDirtyClient() {
        markDirty();
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    private boolean partExists(PartSlot slot, IBlockState state) {
        if (parts.containsKey(slot)) {
            return parts.get(slot).getState().equals(state);
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        Map<PartSlot, MultipartTE.Part> newparts = new HashMap<>();
        version = compound.getInteger("version");
        NBTTagList list = compound.getTagList("parts", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < list.tagCount() ; i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);

            PartSlot slot = PartSlot.byName(tag.getString("slot"));
            if (slot != null) {
                IBlockState state = NBTUtil.readBlockState(tag);
                if (partExists(slot, state)) {
                    // Part is already there. Just update it
                    Part part = parts.get(slot);
                    if (tag.hasKey("te")) {
                        NBTTagCompound tc = tag.getCompoundTag("te");
                        TileEntity te = part.tileEntity;
                        if (te == null) {
                            te = state.getBlock().createTileEntity(world, state);// @todo
                            if (te != null) {
                                te.setWorld(world);
                                te.readFromNBT(tc);
                                te.setPos(pos);
                            }
                        } else {
                            te.readFromNBT(tc);
                            te.setPos(pos);
                        }
                    }
                    newparts.put(slot, part);
                } else {
                    TileEntity te = null;
                    if (tag.hasKey("te")) {
                        NBTTagCompound tc = tag.getCompoundTag("te");
                        te = state.getBlock().createTileEntity(world, state);// @todo
                        if (te != null) {
                            te.setWorld(world);
                            te.readFromNBT(tc);
                            te.setPos(pos);
                        }
                    }
                    Part part = new Part(state, te);
                    newparts.put(slot, part);
                }
            }
        }
        parts = newparts;
    }

    @Override
    protected void setWorldCreate(World worldIn) {
        setWorld(worldIn);
    }

    @Override
    public void setWorld(World worldIn) {
        super.setWorld(worldIn);
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            if (entry.getValue().getTileEntity() != null) {
                entry.getValue().getTileEntity().setWorld(world);
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
            NBTUtil.writeBlockState(tag, state);

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
