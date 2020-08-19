package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mcjty.lib.base.ModBase;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.typed.Type;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextPage extends AbstractWidget<TextPage> {

    public static final String TYPE_TEXTPAGE = "textpage";

    private ModBase modBase;
    private final List<Page> pages = new ArrayList<>();
    private final Map<String,Integer> nodes = new HashMap<>();

    private int pageIndex = 0;
    private final List<Line> lines = new ArrayList<>();
    private final List<Link> links = new ArrayList<>();

    private ResourceLocation arrowImage = null;
    private int arrowU;
    private int arrowV;

    private ResourceLocation craftingGridImage = null;
    private int craftU;
    private int craftV;

    private int tabCounter = 0;

    public TextPage(ModBase modBase) {
        super();
        this.modBase = modBase;
    }

    public TextPage arrowImage(ResourceLocation image, int u, int v) {
        this.arrowImage = image;
        this.arrowU = u;
        this.arrowV = v;
        return this;
    }

    public TextPage craftingGridImage(ResourceLocation image, int u, int v) {
        this.craftingGridImage = image;
        this.craftU = u;
        this.craftV = v;
        return this;
    }

    private void setPage(Page page) {
        lines.clear();
        links.clear();
        if (!pages.isEmpty()) {
            int y = 3;
            int tab = 0;

            for (Line line : page.lines) {
                lines.add(line);
                if (line.isNexttab()) {
                    y = 3;
                    tab++;
                } else if (line.isLink()) {
                    links.add(new Link(tab, y, y+13, line.node));
                }
                y += line.height;
            }
        }
    }

    private void newPage(TextPage.Page page) {
        if (!page.isEmpty()) {
            pages.add(page);
        }
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageCount() {
        return pages.size();
    }

    private IResource getBestLanguageManual(ResourceLocation manualResource) throws IOException {
        IResourceManager resourceManager = mc.getResourceManager();
        try {
            return resourceManager.getResource(manualResource);
        } catch (FileNotFoundException e) {
            String fallBackPath = manualResource.getPath().replaceAll("-([a-z\\-]{2,6})_?([a-z]{0,3})", "");
            return resourceManager.getResource(new ResourceLocation(manualResource.getNamespace(), fallBackPath));
        }
    }

    public TextPage setText(ResourceLocation manualResource) {
        TextPage.Page page = new TextPage.Page();
        try (
                IResource iresource = getBestLanguageManual(manualResource);
                BufferedReader br = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("{------")) {
                    newPage(page);
                    page = new TextPage.Page();
                } else {
                    Line l = page.addLine(modBase, line);
                    if (l.isNode()) {
                        nodes.put(l.node, pages.size());
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading manual text", e);
        }
        newPage(page);
        showCurrentPage();
        return this;
    }

    public void prevPage() {
        pageIndex--;
        if (pageIndex < 0) {
            pageIndex = 0;
        }
        showCurrentPage();
    }

    public void nextPage() {
        pageIndex++;
        if (pageIndex >= pages.size()) {
            pageIndex = pages.size()-1;
        }
        showCurrentPage();
    }

    private void showCurrentPage() {
        setPage(pages.get(pageIndex));
    }

    @Override
    public TextPage mouseClick(double x, double y, int button) {
        if (isEnabledAndVisible()) {
            window.setTextFocus(this);
            for (Link link : links) {
                if (tabCounter == 0) {
                    if (link.y1 <= y && y <= link.y2) {
                        if (gotoLink(link)) {
                            return this;
                        }
                    }
                } else {
                    int t = x < getBounds().width / 2 ? 0 : 1;
                    if (link.y1 <= y && y <= link.y2 && link.tab == t) {
                        if (gotoLink(link)) {
                            return this;
                        }
                    }
                }
            }
            return this;
        }
        return null;
    }

    private boolean gotoLink(Link link) {
        String node = link.node;
        return gotoNode(node);
    }

    public boolean gotoNode(String node) {
        Integer page = nodes.get(node);
        if (page != null) {
            pageIndex = page;
            showCurrentPage();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(int keyCode, int scanCode) {
        boolean rc = super.keyTyped(keyCode, scanCode);
        if (rc) {
            return true;
        }
        if (isEnabledAndVisible()) {
            // @todo 1.14
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_LEFT) {
                prevPage();
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_RIGHT) {
                nextPage();
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_HOME) {
                pageIndex = 0;
                showCurrentPage();
            } else if (keyCode == GLFW.GLFW_KEY_END) {
                if (!pages.isEmpty()) {
                    pageIndex = pages.size()-1;
                    showCurrentPage();
                }
            }
        }
        return false;
    }

    @Override
    public void draw(Screen gui, MatrixStack matrixStack, int x, int y) {
        super.draw(gui, matrixStack, x, y);

        tabCounter = 0;
        y += 3;
        int starty = y;
        for (Line line : lines) {
            if (line.isNexttab()) {
                y = starty;
                x += getBounds().width /2;
                tabCounter++;
            }
            else if (line.recipe != null) {
                y = renderRecipe(gui, matrixStack, x, y, line);
            } else if (line.resourceLocation != null) {
                renderImage(gui, matrixStack, x, y, line);
            } else if (line.line != null) {
                renderLine(matrixStack, x, y, line);
            }
            y += line.height;
        }
    }

    private void renderImage(Screen gui, MatrixStack matrixStack, int x, int y, Line line) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(line.resourceLocation);
        gui.blit(matrixStack, x+4, y+1, line.u, line.v, 16, 16);

        int dx = 22;
        String s = "";
        int col = 0xFF000000;
        if (line.isBold()) {
            char c = 167;
            s = Character.toString(c) + "l";
        }
        if (line.isLink()) {
            char c = 167;
            s = Character.toString(c) + "n";
            col = 0xFF0040AA;
            dx = 25;
        }
        s += line.line;
        mc.fontRenderer.drawString(matrixStack, mc.fontRenderer.func_238412_a_(s, bounds.width-dx), x + dx + bounds.x, y + bounds.y + 3, col);    // @todo 1.16
    }

    private void renderLine(MatrixStack matrixStack, int x, int y, Line line) {
        int dx;
        String s = "";
        int col = 0xFF000000;
        dx = 0;
        if (line.isBold()) {
            char c = 167;
            s = Character.toString(c) + "l";
        }
        if (line.isLink()) {
            char c = 167;
            s = Character.toString(c) + "n";
            col = 0xFF0040AA;
            dx = 25;
        }
        s += line.line;
        mc.fontRenderer.drawString(matrixStack, mc.fontRenderer.func_238412_a_(s, bounds.width-dx), x + dx + bounds.x, y + bounds.y, col);    // @todo 1.16
    }

    private int renderRecipe(Screen gui, MatrixStack matrixStack, int x, int y, Line line) {
        y += 4;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        // @TODO: need support for shapeless and better error checking

        if (craftingGridImage != null) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(craftingGridImage);
            gui.blit(matrixStack, 25+x, y, craftU, craftV, 19*3, 19*3);
        }
        int w;
        int h;
        NonNullList<Ingredient> ingredients;
        // @todo 1.14
//        if (line.recipe instanceof ShapedRecipes) {
//            w = ((ShapedRecipes) line.recipe).getWidth();
//            h = ((ShapedRecipes) line.recipe).getHeight();
//            ingredients = line.recipe.getIngredients();
//        } else if (line.recipe instanceof ShapedOreRecipe) {
//            w = ((ShapedOreRecipe) line.recipe).getRecipeWidth();
//            h = ((ShapedOreRecipe) line.recipe).getRecipeHeight();
//            ingredients = line.recipe.getIngredients();
//        } else {
            w = 0;
            h = 0;
            ingredients = null;
//        }

        for (int i = 0 ; i < 3 ; i++) {
            for (int j = 0 ; j < 3 ; j++) {
                if (i < w && j < h) {
                    Ingredient ingredient = ingredients.get(i + j * w);
                    if (ingredient.getMatchingStacks().length > 0) {
                        ItemStack stack = ingredient.getMatchingStacks()[0];
                        if (stack != null && stack.getDamage() == 32767) {  // @todo 1.14 (still needed?)
                            // Just pick 0 here.
                            CompoundNBT tc = stack.getTag();
                            stack = new ItemStack(stack.getItem(), stack.getCount());
                            if (tc != null) {
                                stack.setTag(tc.copy());
                            }
                        }
                        RenderHelper.renderObject(matrixStack, 26 + x + i * 18, 1 + y + j * 18, stack, false);
                    }
                }
            }
        }
        if (arrowImage != null) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(arrowImage);
            gui.blit(matrixStack, x+25+67, y+18, arrowU, arrowV, 16, 16);
        }
        if (craftingGridImage != null) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(craftingGridImage);
            gui.blit(matrixStack, x+25+92, y + 16, craftU, craftV, 18, 18);
        }
        RenderHelper.renderObject(matrixStack, x+25+93, y + 17, line.recipe.getRecipeOutput(), false);
        y -= 4;
        return y;
    }

    private static class Line {
        private boolean bold;
        private boolean islink;
        private boolean isnode;
        private boolean nexttab;
        String node;
        String line;
        IRecipe recipe;
        ResourceLocation resourceLocation;
        int u;
        int v;
        int height;

        public boolean isBold() {
            return bold;
        }

        public boolean isLink() {
            return islink;
        }

        public boolean isNode() {
            return isnode;
        }

        public boolean isNexttab() {
            return nexttab;
        }

        Line(ModBase modBase, String line) {
            bold = false;
            islink = false;
            nexttab = false;
            node = null;
            this.line = null;
            recipe = null;
            height = 14;
            resourceLocation = null;

            if (line.startsWith("{b}")) {
                bold = true;
                this.line = line.substring(3);
            } else if (line.startsWith("{/}")) {
                nexttab = true;
                height = 0;
            } else if (line.startsWith("{n:")) {
                parseNode(line);
            } else if (line.startsWith("{l:")) {
                parseLink(line);
            } else if (line.startsWith("{i:")) {
                parseImage(modBase, line);
            } else if (line.startsWith("{ri:")) {
                parseItemRecipe(modBase, line);
            } else if (line.startsWith("{rb:")) {
                parseBlockRecipe(modBase, line);
            } else {
                this.line = line;
            }
        }

        private void parseNode(String line) {
            int end = line.indexOf('}');
            if (end == -1) {
                // Error, just put in the entire line
                this.line = line;
            } else {
                node = line.substring(3, end);
                isnode = true;
                this.line = null;
            }
            height = 0;
        }

        private void parseLink(String line) {
            int end = line.indexOf('}');
            if (end == -1) {
                // Error, just put in the entire line
                this.line = line;
            } else {
                node = line.substring(3, end);
                islink = true;
                this.line = line.substring(end + 1);
            }
        }

        private void parseBlockRecipe(ModBase modBase, String line) {
            int end = line.indexOf('}');
            if (end == -1) {
                // Error, just put in the entire line
                this.line = line;
            } else {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(modBase.getModId(), line.substring(4, end)));
                recipe = block == null ? null : findRecipe(new ItemStack(block));
                if (recipe == null) {
                    // Error,
                    this.line = line;
                } else {
                    height = 18*3+8;
                }
            }
        }

        private void parseItemRecipe(ModBase modBase, String line) {
            int end = line.indexOf('}');
            if (end == -1) {
                // Error, just put in the entire line
                this.line = line;
            } else {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(modBase.getModId(), line.substring(4, end)));
                recipe = item == null ? null : findRecipe(new ItemStack(item));
                if (recipe == null) {
                    // Error,
                    this.line = line;
                    // @todo 1.14
//                } else if (!(recipe instanceof ShapedRecipes) && !(recipe instanceof ShapedOreRecipe)) {
//                    recipe = null;
//                    // Error,
//                    this.line = line;
                } else {
                    height = 18*3+8;
                }
            }
        }

        private void parseImage(ModBase modBase, String line) {
            int end = line.indexOf('}');
            if (end == -1) {
                // Error, just put in the entire line
                this.line = line;
            } else {
                String substring = line.substring(3, end);
                String[] split = StringUtils.split(substring, ',');
                u = 0;
                v = 0;
                if(split.length > 2) {
                    try {
                        u = Integer.parseInt(split[1]);
                        v = Integer.parseInt(split[2]);
                    } catch (NumberFormatException e) {
                    }
                }
                resourceLocation = new ResourceLocation(modBase.getModId(), split[0]);
                this.line = split.length > 3 ? split[3] : "<error>";
                height = 16+2;
            }
        }
    }

    private static IRecipe findRecipe(ItemStack item) {
        if (item == null) {
            return null;
        }
        // @todo 1.14
//        for (Map.Entry<ResourceLocation, IRecipe> entry : ForgeRegistries.RECIPES.getEntries()) {
//            IRecipe recipe = entry.getValue();
//            if (recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe) {
//                ItemStack recipeOutput = recipe.getRecipeOutput();
//                if (!recipeOutput.isEmpty() && recipeOutput.isItemEqual(item)) {
//                    return recipe;
//                }
//            }
//        }
        return null;
    }

    public static class Page {
        final List<Line> lines = new ArrayList<>();

        public boolean isEmpty() {
            return lines.isEmpty();
        }

        public Line addLine(ModBase modBase, String line) {
            Line l = new Line(modBase, line);
            lines.add(l);
            return l;
        }
    }

    public static class Link {
        final int tab;
        final int y1;
        final int y2;
        final String node;

        public Link(int tab, int y1, int y2, String node) {
            this.tab = tab;
            this.y1 = y1;
            this.y2 = y2;
            this.node = node;
        }
    }

    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        // @todo
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        // @todo
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_TEXTPAGE);
    }

    @Override
    public <T> void setGenericValue(T value) {
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        return null;
    }
}
