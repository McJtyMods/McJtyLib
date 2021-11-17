package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.TextEnterEvent;
import mcjty.lib.gui.events.TextEvent;
import mcjty.lib.gui.events.TextSpecialKeyEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextField extends AbstractWidget<TextField> {

    public static final String TYPE_TEXTFIELD = "textfield";
    public static final Key<String> PARAM_TEXT = new Key<>("text", Type.STRING);

    public static final boolean DEFAULT_EDITABLE = true;

    private String text = "";
    private int cursor = 0;
    private int startOffset = 0;        // Start character where we are displaying
    /**
     * One end of the selected region. If nothing is selected, is should be -1.
     */
    private int selection = -1;
    private List<TextEvent> textEvents = null;
    private List<TextEnterEvent> textEnterEvents = null;
    private List<TextSpecialKeyEvent> textSpecialKeyEvents = null;
    private boolean editable = DEFAULT_EDITABLE;

    public boolean isEditable() {
        return editable;
    }

    public TextField editable(boolean editable) {
        this.editable = editable;
        return this;
    }

    public String getText() {
        return text;
    }

    public TextField text(String text) {
        if (Objects.equals(this.text, text)) {
            return this;
        }
        this.text = text;
        cursor = text.length();
        if (startOffset >= cursor) {
            startOffset = cursor - 1;
            if (startOffset < 0) {
                startOffset = 0;
            }
        }
        return this;
    }

    @Override
    public Widget<?> mouseClick(double x, double y, int button) {
        if (isEnabledAndVisible() && editable) {
            window.setTextFocus(this);
            if (button == 1) {
                text("");
                fireTextEvents("");
            }
            return this;
        }
        return null;
    }

    private static boolean isControlDown() {
        long handle = Minecraft.getInstance().getWindow().getWindow();
        return InputMappings.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_CONTROL) || InputMappings.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    @Override
    public boolean keyTyped(int keyCode, int scanCode) {
        boolean rc = super.keyTyped(keyCode, scanCode);
        if (rc) {
            return true;
        }
        if (isEnabledAndVisible() && editable) {
            long handle = Minecraft.getInstance().getWindow().getWindow();
            if (isControlDown()) {
                if(keyCode == GLFW.GLFW_KEY_V) {
                    String data = GLFW.glfwGetClipboardString(handle);
                    if (data != null) {
                        if (isRegionSelected()) {
                            replaceSelectedRegion(data);
                        } else {
                            text = text.substring(0, cursor) + data + text.substring(cursor);
                        }
                        cursor += data.length();
                        fireTextEvents(text);
                    }
                } else if (keyCode == GLFW.GLFW_KEY_C) {
                    if (isRegionSelected()) {
                        GLFW.glfwSetClipboardString(handle, getSelectedText());
                    }
                } else if (keyCode == GLFW.GLFW_KEY_X) {
                    if (isRegionSelected()) {
                        GLFW.glfwSetClipboardString(handle, getSelectedText());
                        replaceSelectedRegion("");
                        fireTextEvents(text);
                    }
                } else if (keyCode == GLFW.GLFW_KEY_A) {
                    selectAll();
                } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
                    updateSelection();
                    if (cursor > 0) {
                        cursor = findNextWord(true);
                    }
                } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                    updateSelection();
                    if (cursor < text.length()) {
                        cursor = findNextWord(false);
                    }
                }
            } else if (keyCode == GLFW.GLFW_KEY_ENTER) {
                fireTextEnterEvents(text);
//                window.setTextFocus(null);
                return false;
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                return false;
            } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (isRegionSelected()) {
                    replaceSelectedRegion("");
                    fireTextEvents(text);
                } else if (!text.isEmpty() && cursor > 0) {
                    text = text.substring(0, cursor - 1) + text.substring(cursor);
                    cursor--;
                    fireTextEvents(text);
                }
            } else if (keyCode == GLFW.GLFW_KEY_DELETE) {
                if (isRegionSelected()) {
                    replaceSelectedRegion("");
                    fireTextEvents(text);
                } else if (cursor < text.length()) {
                    text = text.substring(0, cursor) + text.substring(cursor + 1);
                    fireTextEvents(text);
                }
            } else if (keyCode == GLFW.GLFW_KEY_HOME) {
                updateSelection();
                cursor = 0;
            } else if (keyCode == GLFW.GLFW_KEY_END) {
                updateSelection();
                cursor = text.length();
            } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                fireArrowDownEvents();
            } else if (keyCode == GLFW.GLFW_KEY_UP) {
                fireArrowUpEvents();
            } else if (keyCode == GLFW.GLFW_KEY_TAB) {
                fireTabEvents();
            } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
                updateSelection();
                if (cursor > 0) {
                    cursor--;
                }
            } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                updateSelection();
                if (cursor < text.length()) {
                    cursor++;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint) {
        if (isEnabledAndVisible() && editable) {
            if (isRegionSelected()) {
                replaceSelectedRegion(Character.toString(codePoint));
            } else {
                text = text.substring(0, cursor) + codePoint + text.substring(cursor);
            }
            cursor++;
            fireTextEvents(text);
            return true;
        }
        return false;
    }

    private int calculateVerticalOffset() {
        int h = mc.font.lineHeight;
        return (bounds.height - h) / 2;
    }

    private void ensureVisible() {
        if (cursor < startOffset) {
            startOffset = cursor;
        } else {
            int w = mc.font.width(text.substring(startOffset, cursor));
            while (w > bounds.width - 12) {
                startOffset++;
                w = mc.font.width(text.substring(startOffset, cursor));
            }
        }
    }

    public void selectAll() {
        setSelection(0, text.length());
    }

    public void setSelection(int start, int end) {
        selection = start;
        cursor = end;
    }

    public void clearSelection() {
        selection = -1;
    }

    public boolean isRegionSelected() {
        return selection != -1;
    }

    /**
     * Inclusive text index indicating start of the selected region. If nothing is selected, it will return -1.
     */
    public int getSelectionStart() {
        return Math.min(cursor, selection);
    }

    /**
     * Exclusive text index indicating end of the selecred region, If nothing is selected, it will return {@link #cursor}.
     */
    public int getSelectionEnd() {
        return Math.max(cursor, selection);
    }

    public String getSelectedText() {
        return text.substring(getSelectionStart(), getSelectionEnd());
    }

    public void replaceSelectedRegion(String replacement) {
        int selectionStart = getSelectionStart();
        text = text.substring(0, selectionStart) + replacement + text.substring(getSelectionEnd());
        cursor = selectionStart;
        clearSelection();
    }

    private void updateSelection() {
        if (Screen.hasShiftDown()) {
            // Don't clear selection as long as shift is pressed
            if (!isRegionSelected()) {
                selection = cursor;
            }
        } else {
            clearSelection();
        }
    }

    /**
     * Try to match a word by that is surrounded by either whitespace or ends of the text.
     *
     * @param reversed If {@code true}, when it will search towards left, otherwise towards right.
     * @return Index, either end or beginning of a word. When {@code reversed}, it will return the beginning and otherwise the end.
     */
    private int findNextWord(boolean reversed) {
        int change = reversed ? -1 : 1;
        int i = cursor;
        char last = ' ';
        while (true) {
            i += change;
            if (i < 0 || i >= text.length()) {
                break;
            }

            char c = text.charAt(i);
            if (c == ' ' && last != ' ') {
                break;
            }
            last = c;
        }

        if (reversed) {
            return i - change;
        }
        return i;
    }

    @Override
    public void draw(Screen gui, MatrixStack matrixStack, int x, int y) {
        super.draw(gui, matrixStack, x, y);

        int xx = x + bounds.x;
        int yy = y + bounds.y;

        ensureVisible();

        int col = StyleConfig.colorTextFieldFiller;
        if (window.getTextFocus() == this) {
            col = StyleConfig.colorTextFieldFocusedFiller;
        } else if (isHovering()) {
            col = StyleConfig.colorTextFieldHoveringFiller;
        }

        RenderHelper.drawThickBeveledBox(matrixStack, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 1, StyleConfig.colorTextFieldTopLeft, StyleConfig.colorTextFieldBottomRight, col);

        String renderedText = mc.font.plainSubstrByWidth(this.text.substring(startOffset), bounds.width - 10);
        int textX = x + 5 + bounds.x;
        int textY = y + calculateVerticalOffset() + bounds.y;
        if (isEnabled()) {
            if (isEditable()) {
                mc.font.draw(matrixStack, renderedText, textX, textY, 0xff000000);
            } else {
                mc.font.draw(matrixStack, renderedText, textX, textY, 0xff333333);
            }

            if (isRegionSelected()) {
                int selectionStart = getSelectionStart();
                int selectionEnd = getSelectionEnd();

                int renderedStart = MathHelper.clamp(selectionStart - startOffset, 0, renderedText.length());
                int renderedEnd = MathHelper.clamp(selectionEnd - startOffset, 0, renderedText.length());

                String renderedSelection = renderedText.substring(renderedStart, renderedEnd);
                String renderedPreSelection = renderedText.substring(0, renderedStart);
                int selectionX = textX + mc.font.width(renderedPreSelection);
                int selectionWidth = mc.font.width(renderedSelection);
                RenderHelper.drawColorLogic(selectionX - 1, textY, selectionWidth + 1, mc.font.lineHeight, 60, 147, 242, GlStateManager.LogicOp.OR_REVERSE);
            }
        } else {
            mc.font.draw(matrixStack, renderedText, textX, textY, 0xffa0a0a0);
        }

        if (window.getTextFocus() == this) {
            int w = mc.font.width(this.text.substring(startOffset, cursor));
            AbstractGui.fill(matrixStack, xx + 5 + w, yy + 2, xx + 5 + w + 1, yy + bounds.height - 3, StyleConfig.colorTextFieldCursor);
        }
    }

    public TextField event(TextEvent event) {
        if (textEvents == null) {
            textEvents = new ArrayList<>();
        }
        textEvents.add(event);
        return this;
    }

    public void removeTextEvent(TextEvent event) {
        if (textEvents != null) {
            textEvents.remove(event);
        }
    }

    private void fireTextEvents(String newText) {
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, "text")
                .put(PARAM_TEXT, newText)
                .build());
        if (textEvents != null) {
            for (TextEvent event : textEvents) {
                event.textChanged(newText);
            }
        }
    }

    public TextField specialKeyEvent(TextSpecialKeyEvent event) {
        if (textSpecialKeyEvents == null) {
            textSpecialKeyEvents = new ArrayList<>();
        }
        textSpecialKeyEvents.add(event);
        return this;
    }

    private void fireArrowUpEvents() {
        fireChannelEvents("arrowup");
        if (textSpecialKeyEvents != null) {
            for (TextSpecialKeyEvent event : textSpecialKeyEvents) {
                event.arrowUp();
            }
        }
    }

    private void fireArrowDownEvents() {
        fireChannelEvents("arrowdown");
        if (textSpecialKeyEvents != null) {
            for (TextSpecialKeyEvent event : textSpecialKeyEvents) {
                event.arrowDown();
            }
        }
    }

    private void fireTabEvents() {
        fireChannelEvents("tab");
        if (textSpecialKeyEvents != null) {
            for (TextSpecialKeyEvent event : textSpecialKeyEvents) {
                event.tab();
            }
        }
    }

    public TextField addTextEnterEvent(TextEnterEvent event) {
        if (textEnterEvents == null) {
            textEnterEvents = new ArrayList<>();
        }
        textEnterEvents.add(event);
        return this;
    }

    public void removeTextEnterEvent(TextEnterEvent event) {
        if (textEnterEvents != null) {
            textEnterEvents.remove(event);
        }
    }

    private void fireTextEnterEvents(String newText) {
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, "enter")
                .put(PARAM_TEXT, newText)
                .build());
        if (textEnterEvents != null) {
            for (TextEnterEvent event : textEnterEvents) {
                event.textEntered(newText);
            }
        }
    }

    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        editable = GuiParser.get(command, "editable", DEFAULT_EDITABLE);
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        GuiParser.put(command, "editable", editable, DEFAULT_EDITABLE);
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_TEXTFIELD);
    }

    @Override
    public <T> void setGenericValue(T value) {
        if (value == null) {
            text("");
        } else {
            text(value.toString());
        }
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        if (Type.INTEGER.equals(type)) {
            try {
                return Integer.parseInt(getText());
            } catch (NumberFormatException e) {
                return 0;
            }
        } else if (Type.DOUBLE.equals(type)) {
            try {
                return Double.parseDouble(getText());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return getText();
    }
}
