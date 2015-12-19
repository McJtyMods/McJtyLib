package mcjty.lib.entity;

import mcjty.lib.base.GeneralConfig;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.network.Argument;
import mcjty.lib.network.ClientCommandHandler;
import mcjty.lib.network.CommandHandler;
import mcjty.lib.varia.Coordinate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GenericTileEntity extends TileEntity implements CommandHandler, ClientCommandHandler {

    private List<SyncedObject> syncedObjects = new ArrayList<SyncedObject>();
    private int infused = 0;

    private String ownerName = "";
    private UUID ownerUUID = null;
    private int securityChannel = -1;

    public void markDirtyClient() {
        markDirty();
        worldObj.markBlockForUpdate(getPos());
    }

    public void setInvalid() {
        for (SyncedObject value : syncedObjects) {
            value.setInvalid();
        }
        notifyBlockUpdate();
    }

    /// Called by GenericBlock.checkRedstoneWithTE() to set the redstone/powered state of this TE.
    public void setPowered(int powered) {
    }

    protected void checkStateClient() {
        // @todo obsolete system?
        // Sync all values from the server.
        boolean syncNeeded = false;
        for (SyncedObject value : syncedObjects) {
            if (!value.isClientValueUptodate()) {
                value.updateClientValue();
                syncNeeded = true;
            }
        }
        if (syncNeeded) {
            notifyBlockUpdate();
        }
    }

    protected void notifyBlockUpdate() {
        IBlockState ibs = worldObj.getBlockState(pos);
        int oldMeta = ibs.getBlock().getMetaFromState(ibs);
        int newMeta = updateMetaData(oldMeta);
        if (oldMeta != newMeta) {
            worldObj.setBlockState(pos, ibs.getBlock().getStateFromMeta(newMeta), 2);
        }
        worldObj.notifyNeighborsOfStateChange(pos, getBlockType());
        worldObj.markBlockForUpdate(pos);
    }

    protected int updateMetaData(int meta) {
        return meta;
    }

    protected void registerSyncedObject(SyncedObject value) {
        syncedObjects.add(value);
    }

    // Called when a slot is changed.
    public void onSlotChanged(int index, ItemStack stack) {
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(pos, 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    public void setInfused(int infused) {
        this.infused = infused;
        markDirty();
        worldObj.markBlockForUpdate(pos);
    }

    public int getInfused() {
        return infused;
    }

    public float getInfusedFactor() {
        return ((float) infused) / GeneralConfig.maxInfuse;
    }

    public boolean canPlayerAccess(EntityPlayer player) {
        return !isInvalid() && player.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }


    protected void readBufferFromNBT(NBTTagCompound tagCompound, InventoryHelper inventoryHelper) {
        NBTTagList bufferTagList = tagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < bufferTagList.tagCount() ; i++) {
            NBTTagCompound nbtTagCompound = bufferTagList.getCompoundTagAt(i);
            inventoryHelper.setStackInSlot(i, ItemStack.loadItemStackFromNBT(nbtTagCompound));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        readRestorableFromNBT(tagCompound);
    }

    /**
     * Override this method to recover all information that you want
     * to recover from an ItemBlock in the player's inventory.
     * @param tagCompound
     */
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        infused = tagCompound.getInteger("infused");
        ownerName = tagCompound.getString("owner");
        if (tagCompound.hasKey("idM")) {
            ownerUUID = new UUID(tagCompound.getLong("idM"), tagCompound.getLong("idL"));
        } else {
            ownerUUID = null;
        }
        if (tagCompound.hasKey("secChannel")) {
            securityChannel = tagCompound.getInteger("secChannel");
        } else {
            securityChannel = -1;
        }
    }

    protected void writeBufferToNBT(NBTTagCompound tagCompound, InventoryHelper inventoryHelper) {
        NBTTagList bufferTagList = new NBTTagList();
        for (int i = 0 ; i < inventoryHelper.getCount() ; i++) {
            ItemStack stack = inventoryHelper.getStackInSlot(i);
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            if (stack != null) {
                stack.writeToNBT(nbtTagCompound);
            }
            bufferTagList.appendTag(nbtTagCompound);
        }
        tagCompound.setTag("Items", bufferTagList);
    }


    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        writeRestorableToNBT(tagCompound);
    }

    /**
     * Override this method to store all information that you want
     * to store in an ItemBlock in the player's inventory (when the block
     * is picked up with a wrench).
     * @param tagCompound
     */
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("infused", infused);
        tagCompound.setString("owner", ownerName);
        if (ownerUUID != null) {
            tagCompound.setLong("idM", ownerUUID.getMostSignificantBits());
            tagCompound.setLong("idL", ownerUUID.getLeastSignificantBits());
        }
        if (securityChannel != -1) {
            tagCompound.setInteger("secChannel", securityChannel);
        }
    }

    public boolean setOwner(EntityPlayer player) {
        if (!GeneralConfig.manageOwnership) {
            return false;
        }

        if (ownerUUID != null) {
            // Already has an owner.
            return false;
        }
        ownerUUID = player.getGameProfile().getId();
        ownerName = player.getName();
        markDirty();
        worldObj.markBlockForUpdate(pos);

        return true;
    }

    public void clearOwner() {
        if (!GeneralConfig.manageOwnership) {
            return;
        }
        ownerUUID = null;
        ownerName = "";
        securityChannel = -1;
        markDirty();
        worldObj.markBlockForUpdate(pos);
    }

    public void setSecurityChannel(int id) {
        securityChannel = id;
        markDirty();
        worldObj.markBlockForUpdate(pos);
    }

    public int getSecurityChannel() {
        return securityChannel;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        return false;
    }

    @Override
    public List executeWithResultList(String command, Map<String, Argument> args) {
        return null;
    }

    @Override
    public Integer executeWithResultInteger(String command, Map<String, Argument> args) {
        return null;
    }

    @Override
    public boolean execute(String command, List list) {
        return false;
    }

    @Override
    public boolean execute(String command, Integer result) {
        return false;
    }

    @Deprecated
    public Coordinate getCoordinate() {
        return new Coordinate(pos);
    }

}
