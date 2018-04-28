package mcjty.lib.gui;

import net.minecraft.util.ResourceLocation;
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
            ModContainer modContainer = Loader.instance().getIndexedModList().get(guiDescription.getResourceDomain());
            FileSystem fs = FileSystems.newFileSystem(modContainer.getSource().toPath(), null);
            System.out.println("directory = " + fs);
            Path filePath = fs.getPath("/assets/" + guiDescription.getResourceDomain() + "/" + guiDescription.getResourcePath());
            System.out.println("filePath = " + filePath);
//            File directory = DimensionManager.getWorld(0).getSaveHandler().getWorldDirectory();
//            File file = new File(new File(directory, guiDescription.getResourceDomain()), guiDescription.getResourcePath());
            try(BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(filePath),StandardCharsets.UTF_8))) {
                GuiParser.parse(br).forEach(consumer);
            } catch (GuiParser.ParserException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
