package mcjty.lib.proxy;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

public interface IProxy {

    void initStandardItemModel(Block block);

    void initStateMapper(Block block, ModelResourceLocation model);

    void initItemModelMesher(Item item, int meta, ModelResourceLocation model);
}
