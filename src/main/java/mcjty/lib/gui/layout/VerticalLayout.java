package mcjty.lib.gui.layout;

import mcjty.lib.gui.widgets.AbstractWidget;
import mcjty.lib.gui.widgets.Widget;

import java.util.Collection;

public class VerticalLayout extends AbstractLayout<VerticalLayout> {
    @Override
    public void doLayout(Collection<Widget<?>> children, int width, int height) {
        int otherHeight = calculateDynamicSize(children, height, Widget.Dimension.DIMENSION_HEIGHT);

        int top = getVerticalMargin();
        for (Widget<?> child : children) {
            int h = child.getDesiredHeight();
            if (h == Widget.SIZE_UNKNOWN) {
                h = otherHeight;
            }
            ((AbstractWidget)child).setBounds(align(getHorizontalMargin(), top, width-getHorizontalMargin()*2, h, child));
            top += h;
            top += getSpacing();
        }
    }
}
