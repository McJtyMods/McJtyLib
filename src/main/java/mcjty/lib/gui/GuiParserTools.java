package mcjty.lib.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class GuiParserTools {

    public static void parseAndHandleServer(ResourceLocation guiDescription, Consumer<GuiParser.GuiCommand> consumer) {
        try {
            ModContainer mod = Loader.instance().getIndexedModList().get(guiDescription.getResourceDomain());

            JsonContext ctx = new JsonContext(mod.getModId());
            Path filePath = null;
            if (mod.getSource().isFile()) {
                FileSystem fs = FileSystems.newFileSystem(mod.getSource().toPath(), null);
                filePath = fs.getPath("/assets/" + ctx.getModId() + "/" + guiDescription.getResourcePath());
            } else if (mod.getSource().isDirectory()) {
                filePath = mod.getSource().toPath().resolve("assets/" + ctx.getModId() + "/" + guiDescription.getResourcePath());
            }

            System.out.println("filePath = " + filePath);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(filePath), StandardCharsets.UTF_8))) {
                GuiParser.parse(br).forEach(consumer);
            } catch (GuiParser.ParserException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
