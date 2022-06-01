package mcjty.lib.varia;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class WrenchChecker {

    public static final ResourceLocation WRENCH = new ResourceLocation("forge", "tools/wrench");

    private static Set<ResourceLocation> wrenches;

    public static boolean isAWrench(Item item) {
        if (wrenches == null) {
            wrenches = Arrays.<String>asList(
                    "rftoolsbase:smartwrench",
                    "rftoolsbase:smartwrench_select"
            ).stream().map(ResourceLocation::new).collect(Collectors.toSet());
        }
        if (wrenches.contains(item.getRegistryName())) {
            return true;
        }
        return item.getTags().contains(WRENCH);
    }
}
