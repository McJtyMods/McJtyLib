package mcjty.lib.multipart;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.Property;
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
import java.util.Collection;
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
        if (level.isClientSide) {
            System.out.println("====== CLIENT(" + prefix + ") " + worldPosition.toString() + " ======");
        } else {
            System.out.println("====== SERVER(" + prefix + ") " + worldPosition.toString() + " ======");
        }
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            BlockState state = entry.getValue().state;
            System.out.println("    SLOT: " + entry.getKey().name() +
                    "    " + state.getBlock().getRegistryName().toString());
            for (Property<?> property : state.getProperties()) {
                System.out.println("        PROP: " + property + " = " + state.getValue(property));
            }

        }
    }

    public void addPart(PartSlot slot, BlockState state, TileEntity te) {
        System.out.println("MultipartTE.addPart: " + level.isClientSide);
        parts.put(slot, new Part(state, te));

        if (te instanceof GenericTileEntity) {
            ((GenericTileEntity) te).onPartAdded(slot, state, this);
        }

        if (!level.isClientSide) {
            version++;
            markDirtyClient();
        } else {
            // @todo may not be needed. It doesn't seem to help anyway
//            ModelDataManager.requestModelDataRefresh(this);
//            world.markBlockRangeForRenderUpdate(pos, null, null);
        }
//        dumpParts("add");
    }

    public void removePart(BlockState partState) {
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            if (entry.getValue().getState() == partState) {
                parts.remove(entry.getKey());
                if (!level.isClientSide) {
                    version++;
                    markDirtyClient();
                }
//                dumpParts("remove");
                return;
            }
        }
    }

    public void dump() {
        if (level.isClientSide) {
            System.out.println("CLIENT: " + worldPosition);
        } else {
            System.out.println("SERVER: " + worldPosition);
        }
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            System.out.println("    SLOT: " + entry.getKey());
            Part part = entry.getValue();
            BlockState state = part.getState();
            Block block = state.getBlock();
            Collection<Property<?>> properties = state.getProperties();
            System.out.println("        block: " + block.getRegistryName().toString());
            for (Property<?> property : properties) {
                System.out.println("        property: " + property.getName() + " = " + state.getValue(property).toString());
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
        load(getBlockState(), packet.getTag());
        if (level.isClientSide && version != oldVersion) {
            ModelDataManager.requestModelDataRefresh(this);
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT updateTag = super.getUpdateTag();
        save(updateTag);
        return updateTag;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbtTag = new CompoundNBT();
        save(nbtTag);
        return new SUpdateTileEntityPacket(worldPosition, 1, nbtTag);
    }


    public void markDirtyClient() {
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    private boolean partExists(PartSlot slot, BlockState state) {
        if (parts.containsKey(slot)) {
            return parts.get(slot).getState().equals(state);
        }
        return false;
    }

    public void markDirtyQuick() {
        if (level != null) {
            level.blockEntityChanged(this.worldPosition, this);
        }
    }

    @Override
    public void load(BlockState st, CompoundNBT compound) {
        super.load(st, compound);

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
                            te = state.getBlock().createTileEntity(state, level);// @todo
                            if (te != null) {
                                te.setLevelAndPosition(level, worldPosition);
                                te.load(state, tc);
                                te.setLevelAndPosition(level, worldPosition);
                            }
                        } else {
                            te.load(state, tc);
                            te.setPosition(worldPosition);
                        }
                    }
                    newparts.put(slot, part);
                } else {
                    TileEntity te = null;
                    if (tag.contains("te")) {
                        CompoundNBT tc = tag.getCompound("te");
                        te = state.getBlock().createTileEntity(state, level);// @todo
                        if (te != null) {
                            te.setLevelAndPosition(level, worldPosition);
                            te.load(state, tc);
                            te.setLevelAndPosition(level, worldPosition);
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
    public void setLevelAndPosition(World worldIn, BlockPos pos) {
        super.setLevelAndPosition(worldIn, pos);
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            if (entry.getValue().getTileEntity() != null) {
                entry.getValue().getTileEntity().setLevelAndPosition(worldIn, pos);
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
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
                tc = te.save(tc);
                tag.put("te", tc);
            }

            list.add(tag);
        }
        compound.put("parts", list);
        compound.putInt("version", version);

        return super.save(compound);
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
