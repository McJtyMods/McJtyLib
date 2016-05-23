package mcjty.lib.entity;

import mcjty.lib.base.GeneralConfig;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.network.Argument;
import mcjty.lib.network.ClientCommandHandler;
import mcjty.lib.network.CommandHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GenericTileEntity extends TileEntity implements CommandHandler, ClientCommandHandler {

    private int infused = 0;

    private String ownerName = "";
    private UUID ownerUUID = null;
    private int securityChannel = -1;

    public void markDirtyClient() {
        markDirty();
        if (worldObj != null) {
            IBlockState state = worldObj.getBlockState(getPos());
            worldObj.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    /// Called by GenericBlock.checkRedstoneWithTE() to set the redstone/powered state of this TE.
    public void setPowered(int powered) {
    }

    // Called when a slot is changed.
    public void onSlotChanged(int index, ItemStack stack) {
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeClientDataToNBT(nbtTag);
        return new SPacketUpdateTileEntity(pos, 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readClientDataFromNBT(packet.getNbtCompound());
    }

    public void setInfused(int infused) {
        this.infused = infused;
        markDirtyClient();
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

    /**
     * For compatibility reasons this calls writeToNBT() but for
     * efficiency reasons you should override this in your tile
     * entity to only write what you need on the client.
     * @param tagCompound
     */
    public void writeClientDataToNBT(NBTTagCompound tagCompound) {
        writeToNBT(tagCompound);
    }

    /**
     * For compatibility reasons this calls readFromNBT() but for
     * efficiency reasons you should override this in your tile
     * entity to only read what you need on the client.
     * @param tagCompound
     */
    public void readClientDataFromNBT(NBTTagCompound tagCompound) {
        readFromNBT(tagCompound);
    }

    /**
     * GUI Data sync
     */
    public void syncDataForGUI(Object[] data) {
    }

    /**
     * GUI Data sync. Supported types: Integer, Long, Float, String, Byte, Boolean
     */
    public Object[] getDataForGUI() {
        return new Object[0];
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
        markDirtyClient();

        return true;
    }

    public void clearOwner() {
        if (!GeneralConfig.manageOwnership) {
            return;
        }
        ownerUUID = null;
        ownerName = "";
        securityChannel = -1;
        markDirtyClient();
    }

    public void setSecurityChannel(int id) {
        securityChannel = id;
        markDirtyClient();
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
}
