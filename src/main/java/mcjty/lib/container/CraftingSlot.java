package mcjty.lib.container;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.TriConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class CraftingSlot extends SlotItemHandler {
    private final GenericTileEntity te;
    private final Player player;
    private TriConsumer<BlockEntity, Player, ItemStack> onCraft = (container, playerEntity, stack) -> {};
    private int removeCount;

    public CraftingSlot(Player playerEntity, IItemHandler inventory, GenericTileEntity te, int index, int x, int y) {
        super(inventory, index, x, y);
        this.te = te;
        this.player = playerEntity;
    }

    public CraftingSlot onCraft(TriConsumer<BlockEntity, Player, ItemStack> onCraft) {
        this.onCraft = onCraft;
        return this;
    }

    public TriConsumer<BlockEntity, Player, ItemStack> getOnCraft() {
        return onCraft;
    }

    @Override
    @Nonnull
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }
        return super.remove(amount);
    }

    @Override
    public void set(@Nonnull ItemStack stack) {
        if (te != null) {
            te.onSlotChanged(getSlotIndex(), stack);
        }
        super.set(stack);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(@Nonnull Player thePlayer, @Nonnull ItemStack stack) {
        this.checkTakeAchievements(stack);
        super.onTake(thePlayer, stack);
    }

    @Override
    protected void onQuickCraft(@Nonnull ItemStack stack, int amount) {
        this.removeCount += amount;
        this.checkTakeAchievements(stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(player.level, player, this.removeCount);
        onCraft.accept(te, player, stack);

        this.removeCount = 0;
        net.minecraftforge.event.ForgeEventFactory.firePlayerSmeltedEvent(this.player, stack);
    }
}
