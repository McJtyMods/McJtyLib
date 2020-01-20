package mcjty.test;

import mcjty.lib.container.GenericBlock;
import mcjty.lib.container.GenericGuiContainer;
import net.minecraft.block.material.Material;



public class TestBlock extends GenericBlock<TestTileEntity, TestContainer> {

    public TestBlock() {
        super(TestMod.instance, Material.IRON, TestTileEntity.class, TestContainer.class, "testblock", true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return TestGui.class;
    }

    @Override
    public int getGuiID() {
        return TestMod.TESTGUI;
    }
}
