package mcjty.lib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class GuiParserClientTools {

    public static void parseAndHandleClient(ResourceLocation guiDescription, Consumer<GuiParser.GuiCommand> consumer) {
        try {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(guiDescription);
            try(BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                GuiParser.parse(br).forEach(consumer);
            } catch (GuiParser.ParserException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
