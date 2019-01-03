package mcjty.lib.proxy;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

public interface IProxy {

    void initStandardItemModel(Block block);

    void initCustomItemModel(Item item, int meta, ModelResourceLocation model);

    void initStateMapper(Block block, ModelResourceLocation model);

    void initItemModelMesher(Item item, int meta, ModelResourceLocation model);

    void initTESRItemStack(Item item, int meta, Class<? extends TileEntity> clazz);

    void initCustomMeshDefinition(Item item, ItemMeshDefinition meshDefinition);
}
