package mcjty.test;

import mcjty.lib.container.GenericBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, PlayerEntity player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof GenericBlock) {
            GenericBlock genericBlock = (GenericBlock) block;
            TileEntity te = world.getTileEntity(pos);
            return genericBlock.createServerContainer(player, te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, PlayerEntity player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof GenericBlock) {
            GenericBlock genericBlock = (GenericBlock) block;
            TileEntity te = world.getTileEntity(pos);
            return genericBlock.createClientGui(player, te);
        }
        return null;
    }
}
