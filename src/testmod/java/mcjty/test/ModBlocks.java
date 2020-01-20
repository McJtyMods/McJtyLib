package mcjty.test;




public class ModBlocks {

    public static TestBlock testBlock;

    public static void init() {
        testBlock = new TestBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        testBlock.initModel();
    }
}
