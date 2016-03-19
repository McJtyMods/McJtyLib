package mcjty.lib.preferences;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PreferencesHandler {
    @SubscribeEvent
    public void onEntityConstructing(AttachCapabilitiesEvent.Entity event){
        if (event.getEntity() instanceof EntityLivingBase) {
            event.addCapability(new ResourceLocation("McJtyLib", "Preferences"), new PreferencesDispatcher());
        }
    }
}
