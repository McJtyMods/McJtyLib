package mcjty.lib.items;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class GenericArmorItem extends ArmorItem {

    public GenericArmorItem(Holder<ArmorMaterial> material, EquipmentSlot slot, Properties properties) {
        super(material, convert(slot), properties);
    }

    private static ArmorItem.Type convert(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> ArmorItem.Type.HELMET;
            case CHEST -> ArmorItem.Type.CHESTPLATE;
            case LEGS -> ArmorItem.Type.LEGGINGS;
            case FEET -> ArmorItem.Type.BOOTS;
            default -> throw new IllegalArgumentException("Unknown slot: " + slot);
        };
    }

    public static EquipmentSlot getSlotForItem(ItemStack stack) {
        return ((ArmorItem) stack.getItem()).getEquipmentSlot();
    }
}
