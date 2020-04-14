package mcjty.lib.gui.layout;

import mcjty.lib.gui.widgets.AbstractWidget;
import mcjty.lib.gui.widgets.Widget;

import java.util.Collection;

public class HorizontalLayout extends AbstractLayout<HorizontalLayout> {

    @Override
    public void doLayout(Collection<Widget<?>> children, int width, int height) {
        int otherWidth = calculateDynamicSize(children, width, Widget.Dimension.DIMENSION_WIDTH);

        int left = getHorizontalMargin();
        for (Widget<?> child : children) {
            int w = child.getDesiredWidth();
            if (w == Widget.SIZE_UNKNOWN) {
                w = otherWidth;
            }
            ((AbstractWidget)child).setBounds(align(left, getVerticalMargin(), w, height-getVerticalMargin()*2, child));
            left += w;
            left += getSpacing();
        }
    }

}
