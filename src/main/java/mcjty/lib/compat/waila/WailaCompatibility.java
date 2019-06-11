package mcjty.lib.compat.waila;

//import mcp.mobius.waila.api.IWailaConfigHandler;
//import mcp.mobius.waila.api.IWailaDataAccessor;
//import mcp.mobius.waila.api.IWailaDataProvider;
//import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaCompatibility { } /* @todo 1.14 implements IWailaDataProvider {

    public static final WailaCompatibility INSTANCE = new WailaCompatibility();

    private WailaCompatibility() {}

    private static boolean registered;
    private static boolean loaded;

    public static IWailaRegistrar registrar;

    public static void load(IWailaRegistrar registrar) {
        if (!registered){
            throw new RuntimeException("Please register this handler using the provided method");
        }
        if (!loaded) {
            WailaCompatibility.registrar = registrar;

            registrar.registerHeadProvider(INSTANCE, BaseBlock.class);
            registrar.registerBodyProvider(INSTANCE, BaseBlock.class);
            registrar.registerTailProvider(INSTANCE, BaseBlock.class);

            McJtyLib.forEachMod(ModBase::handleWailaExtras);

            loaded = true;
        }
    }

    public static void register(){
        if (registered) {
            return;
        }
        registered = true;
        FMLInterModComms.sendMessage("waila", "register", "mcjty.lib.compat.waila.WailaCompatibility.load");
    }

    @Override
    public CompoundNBT getNBTData(PlayerEntityMP player, TileEntity te, CompoundNBT tag, World world, BlockPos pos) {
        return tag;
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return ItemStack.EMPTY;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        if (block instanceof WailaInfoProvider) {
            return ((WailaInfoProvider) block).getWailaBody(itemStack, currenttip, accessor, config);
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

}*/