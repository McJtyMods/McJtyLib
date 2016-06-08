package mcjty.lib.container;

import cofh.api.item.IToolHammer;
import mcjty.lib.api.smartwrench.SmartWrench;
import mcjty.lib.api.smartwrench.SmartWrenchMode;
import mcjty.lib.base.ModBase;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.varia.Logging;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class GenericModBlock<T extends GenericTileEntity, C extends Container> extends GenericBlock {

    private final Class<? extends C> containerClass;

    public GenericModBlock(ModBase mod,
                           Material material,
                           Class<? extends T> tileEntityClass,
                           Class<? extends C> containerClass,
                           String name, boolean isContainer) {
        this(mod, material, tileEntityClass, containerClass, GenericItemBlock.class, name, isContainer);
    }

    public GenericModBlock(ModBase mod,
                           Material material,
                           Class<? extends T> tileEntityClass,
                           Class<? extends C> containerClass,
                           Class<? extends ItemBlock> itemBlockClass,
                           String name, boolean isContainer) {
        super(mod, material, tileEntityClass, isContainer);
        this.containerClass = containerClass;
        setUnlocalizedName(name);
        setRegistryName(name);
        GameRegistry.register(this);
        if (itemBlockClass != null) {
            GameRegistry.register(createItemBlock(itemBlockClass), getRegistryName());
        }
        GameRegistry.registerTileEntityWithAlternatives(tileEntityClass, mod.getModId() + "_" + name, name);
    }

    private ItemBlock createItemBlock(Class<? extends ItemBlock> itemBlockClass) {
        try {
            Class<?>[] ctorArgClasses = new Class<?>[1];
            ctorArgClasses[0] = Block.class;
            Constructor<? extends ItemBlock> itemCtor = itemBlockClass.getConstructor(ctorArgClasses);
            return itemCtor.newInstance(this);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        T inventory = (T) tileEntity;
        C container;
        GenericGuiContainer gui;
        try {
            Constructor<? extends C> constructor = containerClass.getConstructor(EntityPlayer.class, IInventory.class);
            container = constructor.newInstance(entityPlayer, inventory instanceof IInventory ? inventory : null);
            Constructor<? extends GenericGuiContainer> guiConstructor = getGuiClass().getConstructor(tileEntityClass, containerClass);
            gui = guiConstructor.newInstance(inventory, container);
            return gui;
        } catch (Exception e) {
            Logging.logError("Severe exception during creation of gui!");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        T inventory = (T) tileEntity;
        C container;
        try {
            Constructor<? extends C> constructor = containerClass.getConstructor(EntityPlayer.class, IInventory.class);
            container = constructor.newInstance(entityPlayer, inventory instanceof IInventory ? inventory : null);
            return container;
        } catch (Exception e) {
            Logging.logError("Severe exception during creation of gui!");
            throw new RuntimeException(e);
        }
    }

    @Override
    protected WrenchUsage getWrenchUsage(BlockPos pos, EntityPlayer player, ItemStack itemStack, WrenchUsage wrenchUsed, Item item) {
        WrenchUsage usage = super.getWrenchUsage(pos, player, itemStack, wrenchUsed, item);
        if (item instanceof IToolHammer && usage == WrenchUsage.DISABLED) {
            // It is still possible it is a smart wrench.
            if (item instanceof SmartWrench) {
                SmartWrench smartWrench = (SmartWrench) item;
                SmartWrenchMode mode = smartWrench.getMode(itemStack);
                if (mode.equals(SmartWrenchMode.MODE_SELECT)) {
                    if (player.isSneaking()) {
                        usage = WrenchUsage.SNEAK_SELECT;
                    } else {
                        usage = WrenchUsage.SELECT;
                    }
                }
            }
        }
        return usage;
    }

    @Override
    protected boolean checkAccess(World world, EntityPlayer player, TileEntity te) {
//        if (te instanceof GenericTileEntity) {
//            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
//            if ((!SecurityTools.isPrivileged(player, world)) && (!player.getPersistentID().equals(genericTileEntity.getOwnerUUID()))) {
//                int securityChannel = genericTileEntity.getSecurityChannel();
//                if (securityChannel != -1) {
//                    SecurityChannels securityChannels = SecurityChannels.getChannels(world);
//                    SecurityChannels.SecurityChannel channel = securityChannels.getChannel(securityChannel);
//                    boolean playerListed = channel.getPlayers().contains(player.getDisplayNameString());
//                    if (channel.isWhitelist() != playerListed) {
//                        Logging.message(player, TextFormatting.RED + "You have no permission to use this block!");
//                        return true;
//                    }
//                }
//            }
//        }
        // @todo, security system from RFTools should be accessed here
        return false;
    }


}
