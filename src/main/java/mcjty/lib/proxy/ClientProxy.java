package mcjty.lib.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy {

    @Override
    public void initStandardItemModel(Block block) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }

    @Override
    public void initStateMapper(Block block, ModelResourceLocation model) {
        StateMapperBase ignoreState = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
                return model;
            }
        };
        ModelLoader.setCustomStateMapper(block, ignoreState);
    }

    @Override
    public void initItemModelMesher(Item item, int meta, ModelResourceLocation model) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, model);
    }
}
