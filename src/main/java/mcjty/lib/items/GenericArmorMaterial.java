package mcjty.lib.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

public interface GenericArmorMaterial extends ArmorMaterial {

    int getDurabilityForType(EquipmentSlot slot);

    int getDefenseForType(EquipmentSlot slot);

    @Override
    default int getDurabilityForType(ArmorItem.Type type) {
        return getDurabilityForType(type.getSlot());
    }

    @Override
    default int getDefenseForType(ArmorItem.Type type) {
        return getDefenseForType(type.getSlot());
    }
}
