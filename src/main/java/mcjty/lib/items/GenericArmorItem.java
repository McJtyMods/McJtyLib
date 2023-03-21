package mcjty.lib.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class GenericArmorItem extends ArmorItem {

    public GenericArmorItem(ArmorMaterial material, EquipmentSlot slot, Properties properties) {
        super(material, slot, properties);
    }

    public static EquipmentSlot getSlotForItem(ItemStack stack) {
        return ((ArmorItem) stack.getItem()).getSlot();
    }
}
