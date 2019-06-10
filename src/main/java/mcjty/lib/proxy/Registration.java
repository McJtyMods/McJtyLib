package mcjty.lib.proxy;

import mcjty.lib.McJtyLib;
import mcjty.lib.container.GenericContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = McJtyLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registration {

    @ObjectHolder("mcjtylib:generic")
    public static ContainerType GENERIC_CONTAINER_TYPE;

    public static void onContainerTypeRegistry(final RegistryEvent.Register<ContainerType<?>> e) {
        e.getRegistry().register(
                new ContainerType<>((id, inv) -> new GenericContainer(id)).setRegistryName(McJtyLib.MODID, "generic"));
    }
}
