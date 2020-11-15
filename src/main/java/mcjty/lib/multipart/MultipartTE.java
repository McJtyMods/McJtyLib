package mcjty.lib.multipart;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static mcjty.lib.setup.Registration.TYPE_MULTIPART;

public class MultipartTE extends TileEntity {

    public static final ModelProperty<Map<PartSlot, MultipartTE.Part>> PARTS = new ModelProperty<>();

    public static class Part {
        private final BlockState state;
        private final TileEntity tileEntity;

        public Part(BlockState state, TileEntity tileEntity) {
            this.state = state;
            this.tileEntity = tileEntity;
        }

        public BlockState getState() {
            return state;
        }

        public TileEntity getTileEntity() {
            return tileEntity;
        }
    }

    private Map<PartSlot, Part> parts = new HashMap<>();
    private int version = 0;    // To update rendering client-side

    public MultipartTE() {
        super(TYPE_MULTIPART);
    }

    private void dumpParts(String prefix) {
        if (world.isRemote) {
            System.out.println("====== CLIENT(" + prefix + ") " + pos.toString() + " ======");
        } else {
            System.out.println("====== SERVER(" + prefix + ") " + pos.toString() + " ======");
        }
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            BlockState state = entry.getValue().state;
            System.out.println("    SLOT: " + entry.getKey().name() +
                    "    " + state.getBlock().getRegistryName().toString());
            for (IProperty<?> property : state.getProperties()) {
                System.out.println("        PROP: " + property + " = " + state.get(property));
            }

        }
    }

    public void addPart(PartSlot slot, BlockState state, TileEntity te) {
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

    public void removePart(BlockState partState) {
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

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder()
                .withInitial(PARTS, parts)
                .build();
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        int oldVersion = version;
        read(packet.getNbtCompound());
        if (world.isRemote && version != oldVersion) {
//            dumpParts("onData");
            ModelDataManager.requestModelDataRefresh(this);
            world.markBlockRangeForRenderUpdate(pos, null, null);
//            world.markForRerender(pos);
        }
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT updateTag = super.getUpdateTag();
        write(updateTag);
        return updateTag;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbtTag = new CompoundNBT();
        write(nbtTag);
        return new SUpdateTileEntityPacket(pos, 1, nbtTag);
    }

    public void markDirtyClient() {
        markDirty();
        if (world != null) {
            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    private boolean partExists(PartSlot slot, BlockState state) {
        if (parts.containsKey(slot)) {
            return parts.get(slot).getState().equals(state);
        }
        return false;
    }

    public void markDirtyQuick() {
        if (world != null) {
            world.markChunkDirty(this.pos, this);
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        Map<PartSlot, MultipartTE.Part> newparts = new HashMap<>();
        version = compound.getInt("version");
        ListNBT list = compound.getList("parts", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < list.size() ; i++) {
            CompoundNBT tag = list.getCompound(i);

            PartSlot slot = PartSlot.byName(tag.getString("slot"));
            if (slot != null) {
                BlockState state = NBTUtil.readBlockState(tag);
                if (partExists(slot, state)) {
                    // Part is already there. Just update it
                    Part part = this.parts.get(slot);
                    if (tag.contains("te")) {
                        CompoundNBT tc = tag.getCompound("te");
                        TileEntity te = part.tileEntity;
                        if (te == null) {
                            te = state.getBlock().createTileEntity(state, world);// @todo
                            if (te != null) {
                                te.setWorldAndPos(world, pos);
                                te.read(tc);
                                te.setWorldAndPos(world, pos);
                            }
                        } else {
                            te.read(tc);
                            te.setPos(pos);
                        }
                    }
                    newparts.put(slot, part);
                } else {
                    TileEntity te = null;
                    if (tag.contains("te")) {
                        CompoundNBT tc = tag.getCompound("te");
                        te = state.getBlock().createTileEntity(state, world);// @todo
                        if (te != null) {
                            te.setWorldAndPos(world, pos);
                            te.read(tc);
                            te.setWorldAndPos(world, pos);
                        }
                    }
                    Part part = new Part(state, te);
                    newparts.put(slot, part);
                }
            }
        }
        this.parts = newparts;
    }


    // @todo 1.14
//    @Override
//    protected void setWorldCreate(World worldIn) {
//        setWorld(worldIn);
//    }


    @Override
    public void setWorldAndPos(World worldIn, BlockPos pos) {
        super.setWorldAndPos(worldIn, pos);
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            if (entry.getValue().getTileEntity() != null) {
                entry.getValue().getTileEntity().setWorldAndPos(worldIn, pos);
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT list = new ListNBT();
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            PartSlot slot = entry.getKey();
            Part part = entry.getValue();

            BlockState state = part.getState();
            CompoundNBT tag = NBTUtil.writeBlockState(state);

            tag.putString("slot", slot.name());

            TileEntity te = part.getTileEntity();
            if (te != null) {
                CompoundNBT tc = new CompoundNBT();
                tc = te.write(tc);
                tag.put("te", tc);
            }

            list.add(tag);
        }
        compound.put("parts", list);
        compound.putInt("version", version);

        return super.write(compound);
    }

    // @todo 1.14
//    public boolean testIntersect(BlockState blockState) {
//        AxisAlignedBB box = blockState.getBoundingBox(world, pos);
//        for (Map.Entry<PartSlot, Part> entry : getParts().entrySet()) {
//            // @todo just check on slot?
//            Part part = entry.getValue();
//            AxisAlignedBB partBox = part.getState().getBoundingBox(world, pos);
//            if (box.intersects(partBox)) {
//                return true;
//            }
//        }
//        return false;
//    }
//

}
