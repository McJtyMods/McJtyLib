package mcjty.lib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WindowTools {

    public static void parseAndHandleClient(ResourceLocation guiDescription, Consumer<GuiParser.GuiCommand> consumer) {
        try {
            IResource resource = Minecraft.getInstance().getResourceManager().getResource(guiDescription);
            try(BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                GuiParser.parse(br).forEach(consumer);
            } catch (GuiParser.ParserException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Used by GenericGuiContainer for tooltips
    public static List<Object> parseString(String s, List<ItemStack> items) {
        List<Object> l = new ArrayList<>();
        String current = "";
        int i = 0;
        while (i < s.length()) {
            String c = s.substring(i, i + 1);
            if ("@".equals(c)) {
                ++i;
                int itemIdx = s.charAt(i) - '0';
                if(itemIdx == '@' - '0') {
                    // @@ becomes a literal @
                    current += "@";
                } else if(itemIdx < 0 || itemIdx > 9) {
                    // probably forgot to escape something
                    throw new IllegalArgumentException(s);
                } else {
                    // replace it with the corresponding item
                    if (!current.isEmpty()) {
                        l.add(current);
                        current = "";
                    }
                    ItemStack e = items.get(itemIdx);
                    if (!e.isEmpty()) {
                        l.add(e);
                    }
                }
            } else {
                current += c;
            }
            i++;
        }
        if (!current.isEmpty()) {
            l.add(current);
        }
        return l;
    }
}
