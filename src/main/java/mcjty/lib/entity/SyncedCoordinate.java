package mcjty.lib.entity;

import mcjty.lib.varia.BlockPosTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class SyncedCoordinate extends SyncedVersionedObject {

    private BlockPos coordinate;

    public SyncedCoordinate(BlockPos coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        coordinate = BlockPosTools.readFromNBT(tagCompound, "c");
    }

    public void readFromNBT(NBTTagCompound tagCompound, String tagName) {
        NBTTagCompound xCompound = tagCompound.getCompoundTag(tagName);
        readFromNBT(xCompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        BlockPosTools.writeToNBT(tagCompound, "c", coordinate);
    }

    public void writeToNBT(NBTTagCompound tagCompound, String tagName) {
        NBTTagCompound xCompound = new NBTTagCompound();
        writeToNBT(xCompound);
        tagCompound.setTag(tagName, xCompound);
    }

    public BlockPos getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(BlockPos c) {
        coordinate = c;
        serverVersion++;
    }
}
