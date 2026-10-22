package mcjty.lib.tooltips;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import java.util.List;

/** Retains the item's original tooltip image alongside McJtyLib's icons. */
public record TooltipBundle(List<TooltipComponent> components) implements TooltipComponent {

    public ClientTooltipComponent client() {

        var children=components.stream().map(ClientTooltipComponent::create).toList();

        return new ClientTooltipComponent() {

            public int getWidth(Font font) {
                return children.stream().mapToInt(c->c.getWidth(font)).max().orElse(0);
            }

            public int getHeight(Font font) {
                return children.stream().mapToInt(c->c.getHeight(font)).sum();
            }

            public void extractText(GuiGraphicsExtractor graphics,Font font,int x,int y) {
                for(var c:children) {
                    c.extractText(graphics,font,x,y);
                    y+=c.getHeight(font);
                }
            }

            public void extractImage(Font font,int x,int y,int width,int height,GuiGraphicsExtractor graphics) {
                for(var c:children) {
                    c.extractImage(font,x,y,width,c.getHeight(font),graphics);
                    y+=c.getHeight(font);
                }
            }

        };

    }

}
