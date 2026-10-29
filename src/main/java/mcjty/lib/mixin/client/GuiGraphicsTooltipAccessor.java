package mcjty.lib.mixin.client;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiGraphicsExtractor.class)
public interface GuiGraphicsTooltipAccessor {

    @Invoker("setTooltipForNextFrameInternal")
    void mcjtylib$queueTooltip(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY,
                              ClientTooltipPositioner positioner, Identifier texture, boolean override);
}
