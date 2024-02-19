package mcjty.lib.client;

import mcjty.lib.McJtyLib;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.network.PacketOpenManual;
import mcjty.lib.tooltips.TooltipRender;

public class ClientManualHelper {
    public static void openManualFromGui() {
        if (TooltipRender.lastUsedTooltipItem != null) {
            ManualEntry entry = TooltipRender.lastUsedTooltipItem.getManualEntry();
            if (entry.manual() != null) {
                McJtyLib.sendToServer(PacketOpenManual.create(entry.manual(), entry.entry(), entry.page()));
            }
        }
    }
}
