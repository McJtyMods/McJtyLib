package mcjty.lib.varia;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WrenchChecker {

    private static Set<ResourceLocation> wrenches;

    public static final ResourceLocation WRENCH = ResourceLocation.fromNamespaceAndPath("forge", "tools/wrench");
    public static final TagKey<Item> WRENCH_TAG = TagKey.create(Registries.ITEM, WRENCH);


    public static boolean isAWrench(Item item) {
        if (wrenches == null) {
            wrenches = Stream.of(
                    "rftoolsbase:smartwrench",
                    "rftoolsbase:smartwrench_select"
            ).map(ResourceLocation::parse).collect(Collectors.toSet());
        }
        if (wrenches.contains(Tools.getId(item))) {
            return true;
        }
        return TagTools.hasTag(item, WRENCH_TAG);
    }
}
