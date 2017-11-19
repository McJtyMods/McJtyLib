package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * This is a packet that can be used to update the NBT of an item in an inventory.
 */
public abstract class PacketUpdateNBTItemInventory implements IMessage {
    public BlockPos pos;
    public int slotIndex;
    public NBTTagCompound tagCompound;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        slotIndex = buf.readInt();
        tagCompound = NetworkTools.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
        buf.writeInt(slotIndex);
        NetworkTools.writeTag(buf, tagCompound);
    }

    protected abstract boolean isValidBlock(World world, BlockPos pos, TileEntity tileEntity);

    public PacketUpdateNBTItemInventory() {
    }

    public PacketUpdateNBTItemInventory(BlockPos pos, int slotIndex, NBTTagCompound tagCompound) {
        this.pos = pos;
        this.slotIndex = slotIndex;
        this.tagCompound = tagCompound;
    }

}
