package mcjty.lib.gui;

import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class GuiParserTools {

    public static void parseAndHandleServer(ResourceLocation guiDescription, Consumer<GuiParser.GuiCommand> consumer) {
        // @todo 1.14
//        try {
//            ModContainer mod = Loader.instance().getIndexedModList().get(guiDescription.getResourceDomain());
//
//            JsonContext ctx = new JsonContext(mod.getModId());
//            Path filePath = null;
//            if (mod.getSource().isFile()) {
//                FileSystem fs = FileSystems.newFileSystem(mod.getSource().toPath(), null);
//                filePath = fs.getPath("/assets/" + ctx.getModId() + "/" + guiDescription.getResourcePath());
//            } else if (mod.getSource().isDirectory()) {
//                filePath = mod.getSource().toPath().resolve("assets/" + ctx.getModId() + "/" + guiDescription.getResourcePath());
//            }
//
//            System.out.println("filePath = " + filePath);
//            try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(filePath), StandardCharsets.UTF_8))) {
//                GuiParser.parse(br).forEach(consumer);
//            } catch (GuiParser.ParserException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

}
