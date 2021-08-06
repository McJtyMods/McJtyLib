package mcjty.lib.multipart;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
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

public class MultipartTE extends BlockEntity {

    public static final ModelProperty<Map<PartSlot, MultipartTE.Part>> PARTS = new ModelProperty<>();

    public static class Part {
        private final BlockState state;
        private final BlockEntity tileEntity;

        public Part(BlockState state, BlockEntity tileEntity) {
            this.state = state;
            this.tileEntity = tileEntity;
        }

        public BlockState getState() {
            return state;
        }

        public BlockEntity getTileEntity() {
            return tileEntity;
        }
    }

    private Map<PartSlot, Part> parts = new HashMap<>();
    private int version = 0;    // To update rendering client-side

    public MultipartTE(BlockPos pos, BlockState state) {
        super(TYPE_MULTIPART, pos, state);
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

    public void addPart(PartSlot slot, BlockState state, BlockEntity te) {
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
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        int oldVersion = version;
        load(packet.getTag());
        if (level.isClientSide && version != oldVersion) {
            ModelDataManager.requestModelDataRefresh(this);
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = super.getUpdateTag();
        save(updateTag);
        return updateTag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbtTag = new CompoundTag();
        save(nbtTag);
        return new ClientboundBlockEntityDataPacket(worldPosition, 1, nbtTag);
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
            level.blockEntityChanged(this.worldPosition);
        }
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);

        Map<PartSlot, MultipartTE.Part> newparts = new HashMap<>();
        version = compound.getInt("version");
        ListTag list = compound.getList("parts", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < list.size() ; i++) {
            CompoundTag tag = list.getCompound(i);

            PartSlot slot = PartSlot.byName(tag.getString("slot"));
            if (slot != null) {
                BlockState state = NbtUtils.readBlockState(tag);
                if (partExists(slot, state)) {
                    // Part is already there. Just update it
                    Part part = this.parts.get(slot);
                    if (tag.contains("te")) {
                        CompoundTag tc = tag.getCompound("te");
                        BlockEntity te = part.tileEntity;
                        if (te == null) {
                            if (state.getBlock() instanceof EntityBlock entityBlock) {
                                te = entityBlock.newBlockEntity(worldPosition, state);
                                if (te != null) {
                                    te.setLevel(level);
                                    te.load(tc);
                                    te.setLevel(level);
                                }
                            }
                        } else {
                            te.load(tc);
//                            te.setPosition(worldPosition);    // @todo 1.17
                        }
                    }
                    newparts.put(slot, part);
                } else {
                    BlockEntity te = null;
                    if (tag.contains("te")) {
                        CompoundTag tc = tag.getCompound("te");
                        if (state.getBlock() instanceof EntityBlock entityBlock) {
                            te = entityBlock.newBlockEntity(worldPosition, state);
                            if (te != null) {
                                te.setLevel(level);
                                te.load(tc);
                                te.setLevel(level);
                            }
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
    public void setLevel(Level level) {
        super.setLevel(level);
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            if (entry.getValue().getTileEntity() != null) {
                entry.getValue().getTileEntity().setLevel(level);
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        ListTag list = new ListTag();
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            PartSlot slot = entry.getKey();
            Part part = entry.getValue();

            BlockState state = part.getState();
            CompoundTag tag = NbtUtils.writeBlockState(state);

            tag.putString("slot", slot.name());

            BlockEntity te = part.getTileEntity();
            if (te != null) {
                CompoundTag tc = new CompoundTag();
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
