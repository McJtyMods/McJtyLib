package mcjty.lib.container;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.TriConsumer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CraftingSlot extends SlotItemHandler {
    private final GenericTileEntity te;
    private final PlayerEntity player;
    private TriConsumer<TileEntity, PlayerEntity, ItemStack> onCraft = (container, playerEntity, stack) -> {};
    private int removeCount;

    public CraftingSlot(PlayerEntity playerEntity, IItemHandler inventory, GenericTileEntity te, int index, int x, int y) {
        super(inventory, index, x, y);
        this.te = te;
        this.player = playerEntity;
    }

    public CraftingSlot onCraft(TriConsumer<TileEntity, PlayerEntity, ItemStack> onCraft) {
        this.onCraft = onCraft;
        return this;
    }

    public TriConsumer<TileEntity, PlayerEntity, ItemStack> getOnCraft() {
        return onCraft;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        if (this.getHasStack()) {
            this.removeCount += Math.min(amount, this.getStack().getCount());
        }
        return super.decrStackSize(amount);
    }

    @Override
    public void putStack(ItemStack stack) {
        if (te != null) {
            te.onSlotChanged(getSlotIndex(), stack);
        }
        super.putStack(stack);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
        this.onCrafting(stack);
        super.onTake(thePlayer, stack);
        return stack;
    }

    @Override
    protected void onCrafting(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.onCrafting(stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    @Override
    protected void onCrafting(ItemStack stack) {
        stack.onCrafting(player.world, player, this.removeCount);
        onCraft.accept(te, player, stack);
        // @todo
        if (!this.player.world.isRemote && this.inventory instanceof AbstractFurnaceTileEntity) {
            ((AbstractFurnaceTileEntity)this.inventory).func_213995_d(this.player);
        }

        this.removeCount = 0;
        net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerSmeltedEvent(this.player, stack);
    }
}
