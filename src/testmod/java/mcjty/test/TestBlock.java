package mcjty.test;

import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericBlock;
import net.minecraft.block.material.Material;

public class TestBlock extends GenericBlock<TestTileEntity, EmptyContainer> {

    public TestBlock() {
        super(TestMod.instance, Material.IRON, TestTileEntity.class, EmptyContainer.class, "testblock", false);
    }

    @Override
    public int getGuiID() {
        return 0;
    }
}
