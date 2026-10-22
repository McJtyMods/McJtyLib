package mcjty.lib.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

public class GenericArmorItem extends Item {
    public GenericArmorItem(ArmorMaterial material, EquipmentSlot slot, Properties properties) {
        super(properties.humanoidArmor(material, switch (slot) {
            case HEAD -> ArmorType.HELMET;
            case CHEST -> ArmorType.CHESTPLATE;
            case LEGS -> ArmorType.LEGGINGS;
            case FEET -> ArmorType.BOOTS;
            default -> throw new IllegalArgumentException("Not an armor slot: " + slot);
        }));
    }

    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        var equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable == null ? EquipmentSlot.MAINHAND : equippable.slot();
    }
}
