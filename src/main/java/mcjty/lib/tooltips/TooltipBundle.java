package mcjty.lib.tooltips;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Matrix4f;

/** Retains the item's original tooltip image alongside McJtyLib's icons. */
public record TooltipBundle(List<TooltipComponent> components) implements TooltipComponent {

    public ClientTooltipComponent client() {

        var children = components.stream().map(ClientTooltipComponent::create).toList();

        return new ClientTooltipComponent() {

            public int getWidth(Font font) {
                return children.stream().mapToInt(c -> c.getWidth(font)).max().orElse(0);
            }

            public int getHeight() {
                return children.stream().mapToInt(c -> c.getHeight()).sum();
            }

            public void renderText(
                    Font font,
                    int x,
                    int y,
                    Matrix4f matrix,
                    MultiBufferSource.BufferSource buffers) {
                for (var c : children) {
                    c.renderText(font, x, y, matrix, buffers);
                    y += c.getHeight();
                }
            }

            public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
                for (var c : children) {
                    c.renderImage(font, x, y, graphics);
                    y += c.getHeight();
                }
            }
        };
    }
}
