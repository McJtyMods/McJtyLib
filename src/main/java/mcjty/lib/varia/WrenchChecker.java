package mcjty.lib.varia;

import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WrenchChecker {

    public static final ResourceLocation WRENCH = new ResourceLocation("forge", "wrench");

    private static Set<ResourceLocation> wrenches;

    public static boolean isAWrench(Item item) {
        if (wrenches == null) {
            wrenches = Stream.of(
                    "rftoolsbase:smartwrench",
                    "rftoolsbase:smartwrench_select"
            ).map(ResourceLocation::new).collect(Collectors.toSet());
        }
        if (wrenches.contains(item.getRegistryName())) {
            return true;
        }
        return item.getTags().contains(WRENCH);
    }
}
