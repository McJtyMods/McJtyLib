package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ChoiceEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ImageChoiceLabel extends AbstractImageLabel<ImageChoiceLabel> {

    public static final String TYPE_IMAGECHOICELABEL = "imagechoicelabel";
    public static final Key<String> PARAM_CHOICE = new Key<>("choice", Type.STRING);
    public static final Key<Integer> PARAM_CHOICE_IDX = new Key<>("choiceIdx", Type.INTEGER);

    public static final boolean DEFAULT_WITH_BORDER = false;

    private static class ChoiceEntry {
        private final String choice;
        private final List<String> tooltips;
        private final ResourceLocation image;
        private final int u;
        private final int v;

        public ChoiceEntry(String choice, List<String> tooltips, ResourceLocation image, int u, int v) {
            this.choice = choice;
            this.tooltips = tooltips;
            this.image = image;
            this.u = u;
            this.v = v;
        }

        public String getChoice() {
            return choice;
        }

        public List<String> getTooltips() {
            return tooltips;
        }

        public ResourceLocation getImage() {
            return image;
        }

        public int getU() {
            return u;
        }

        public int getV() {
            return v;
        }
    }

    private List<ChoiceEntry> choiceList = new ArrayList<>();
    private boolean withBorder = DEFAULT_WITH_BORDER;
    private int highlightedChoice = -1;     // If this choice is selected we 'highlight' it (only in combination with withBorder=true)

    private int currentChoice = -1;
    private List<ChoiceEvent> choiceEvents = null;

    public boolean isWithBorder() {
        return withBorder;
    }

    public ImageChoiceLabel withBorder(boolean withBorder) {
        this.withBorder = withBorder;
        return this;
    }

    public int getHighlightedChoice() {
        return highlightedChoice;
    }

    public ImageChoiceLabel highlightedChoice(int highlightedChoice) {
        this.highlightedChoice = highlightedChoice;
        return this;
    }

    @Override
    public List<String> getTooltips() {
        if (currentChoice == -1) {
            return super.getTooltips();
        } else {
            return choiceList.get(currentChoice).getTooltips();
        }
    }

    public ImageChoiceLabel choice(String choice, String tooltips, ResourceLocation image, int u, int v) {
        choiceList.add(new ChoiceEntry(choice, Arrays.asList(StringUtils.split(tooltips, "\n")), image, u, v));
        if (currentChoice == -1) {
            setCurrentChoice(0);
        }
        return this;
    }

    public void clear() {
        choiceList.clear();
        currentChoice = -1;
    }

    @Override
    public Widget<?> mouseClick(double x, double y, int button) {
        if (isEnabledAndVisible()) {
            int c = currentChoice;
            if (button == 1) { // @todo 1.14 || McJtyLib.proxy.isSneaking()) {
                c--;
                if (c < 0) {
                    c = choiceList.size() - 1;
                }
            } else {
                c++;
                if (c >= choiceList.size()) {
                    c = 0;
                }
            }
            setCurrentChoice(c);
            fireChoiceEvents(choiceList.get(c).getChoice());
        }
        return null;
    }


    public void setCurrentChoice(int currentChoice) {
        if (this.currentChoice == currentChoice) {
            return;
        }
        this.currentChoice = currentChoice;
        image(choiceList.get(currentChoice).getImage(), choiceList.get(currentChoice).getU(), choiceList.get(currentChoice).getV());
    }

    public int findChoice(String choice) {
        if (choice == null) {
            return -1;
        }
        int i = 0;
        for (ChoiceEntry s : choiceList) {
            if (choice.equals(s.getChoice())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public void setCurrentChoice(String choice) {
        int idx = findChoice(choice);
        if (idx != -1) {
            setCurrentChoice(idx);
        }
    }

    public int getCurrentChoiceIndex() {
        return currentChoice;
    }

    public String getCurrentChoice() {
        if (currentChoice == -1) {
            return null;
        }
        return choiceList.get(currentChoice).getChoice();
    }

    public ImageChoiceLabel event(ChoiceEvent event) {
        if (choiceEvents == null) {
            choiceEvents = new ArrayList<>();
        }
        choiceEvents.add(event);
        return this;
    }

    public void removeChoiceEvent(ChoiceEvent event) {
        if (choiceEvents != null) {
            choiceEvents.remove(event);
        }
    }

    private void fireChoiceEvents(String choice) {
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, "choice")
                .put(PARAM_CHOICE, choice)
                .put(PARAM_CHOICE_IDX, currentChoice)
                .build());
        if (choiceEvents != null) {
            for (ChoiceEvent event : choiceEvents) {
                event.choiceChanged(choice);
            }
        }
    }

    @Override
    public void draw(Screen gui, MatrixStack matrixStack, int x, int y) {
        if (withBorder) {
            int xx = x + bounds.x;
            int yy = y + bounds.y;

            if (isEnabled()) {
                if (currentChoice == highlightedChoice) {
                    drawStyledBoxSelected(window, matrixStack, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
                } else if (isHovering()) {
                    drawStyledBoxHovering(window, matrixStack, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
                } else {
                    drawStyledBoxNormal(window, matrixStack, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
                }
            } else {
                drawStyledBoxDisabled(window, matrixStack, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            }
        }

        super.draw(gui, matrixStack, x, y);
    }


    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        command.findCommand("choices").ifPresent(cmd ->
                cmd.commands().forEach(choiceCmd -> {
                    String choice = choiceCmd.getOptionalPar(0, "");
                    List<String> tooltips = choiceCmd.findCommand("tooltips")
                            .map(tooltipsCmd -> tooltipsCmd.parameters()
                                    .map(Object::toString)
                                    .collect(Collectors.toList()))
                            .orElse(Collections.emptyList());
                    ResourceLocation image = new ResourceLocation(GuiParser.get(choiceCmd, "image", ""));
                    int u = GuiParser.getIndexed(choiceCmd, "uv", 0, 0);
                    int v = GuiParser.getIndexed(choiceCmd, "uv", 1, 0);
                    ChoiceEntry entry = new ChoiceEntry(choice, tooltips, image, u, v);
                    choiceList.add(entry);
                }));
        withBorder = GuiParser.get(command, "border", DEFAULT_WITH_BORDER);
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        GuiParser.GuiCommand choicesCmd = new GuiParser.GuiCommand("choices");
        for (ChoiceEntry entry : choiceList) {
            GuiParser.GuiCommand choiceCmd = new GuiParser.GuiCommand("choice").parameter(entry.getChoice());
            choicesCmd.command(choiceCmd);
            choiceCmd.command(new GuiParser.GuiCommand("uv").parameter(entry.getU()).parameter(entry.getV()));
            if (entry.getTooltips() != null && !entry.getTooltips().isEmpty()) {
                GuiParser.GuiCommand tooltipsCmd = new GuiParser.GuiCommand("tooltips");
                choiceCmd.command(tooltipsCmd);
                for (String tt : entry.getTooltips()) {
                    tooltipsCmd.parameter(tt);
                }
            }
            choiceCmd.command(new GuiParser.GuiCommand("image").parameter(entry.getImage().toString()));
        }
        command.command(choicesCmd);
        GuiParser.put(command, "border", withBorder, DEFAULT_WITH_BORDER);
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_IMAGECHOICELABEL);
    }

    @Override
    public <T> void setGenericValue(T value) {
        if (value instanceof Integer) {
            setCurrentChoice((Integer) value);
        } else if (value instanceof Boolean) {
            setCurrentChoice(((Boolean) value) ? 1 : 0);
        } else {
            setCurrentChoice(value.toString());
        }
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        if (Type.INTEGER.equals(type)) {
            return getCurrentChoiceIndex();
        } else if (Type.BOOLEAN.equals(type)) {
            return getCurrentChoiceIndex() != 0;
        }
        return getCurrentChoice();
    }
}
