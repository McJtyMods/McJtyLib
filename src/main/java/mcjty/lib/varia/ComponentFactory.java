package mcjty.lib.varia;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringDecomposer;

public class ComponentFactory {

    public static MutableComponent translatable(String key) {
        return Component.translatable(key);
    }

    public static MutableComponent translatable(String key, Object... objects) {
        return Component.translatable(key, objects);
    }

    public static MutableComponent literal(String text) {
        return Component.literal(text);
    }

    /** Convert legacy section-sign codes to styled text before layout and rendering. */
    public static MutableComponent formatted(String text) {
        MutableComponent result = Component.empty();
        StringBuilder run = new StringBuilder();
        Style[] currentStyle = {Style.EMPTY};
        StringDecomposer.iterateFormatted(text, Style.EMPTY, (index, style, codePoint) -> {
            if (!style.equals(currentStyle[0])) {
                if (!run.isEmpty()) {
                    result.append(Component.literal(run.toString()).setStyle(currentStyle[0]));
                    run.setLength(0);
                }
                currentStyle[0] = style;
            }
            run.appendCodePoint(codePoint);
            return true;
        });
        if (!run.isEmpty()) {
            result.append(Component.literal(run.toString()).setStyle(currentStyle[0]));
        }
        return result;
    }

    public static MutableComponent empty() {
        return Component.empty();
    }

    public static MutableComponent keybind(String keybind) {
        return Component.keybind(keybind);
    }
}
