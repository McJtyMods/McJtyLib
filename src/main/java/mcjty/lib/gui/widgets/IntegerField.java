package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.IntegerEnterEvent;
import mcjty.lib.gui.events.IntegerEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class IntegerField extends AbstractWidget<IntegerField> {

    public static final String TYPE_INTEGERFIELD = "integerfield";
    public static final Key<Integer> PARAM_INTEGER = new Key<>("integer", Type.INTEGER);

    public static final boolean DEFAULT_EDITABLE = true;

    private String text = "";
    private int cursor = 0;
    private int startOffset = 0;        // Start character where we are displaying
    private int minimum = Integer.MIN_VALUE;
    private int maximum = Integer.MAX_VALUE;

    /**
     * One end of the selected region. If nothing is selected, is should be -1.
     */
    private int selection = -1;
    private List<IntegerEvent> integerEvents = null;
    private List<IntegerEnterEvent> integerEnterEvents = null;
    private boolean editable = DEFAULT_EDITABLE;

    public boolean isEditable() {
        return editable;
    }

    public IntegerField editable(boolean editable) {
        this.editable = editable;
        return this;
    }

    public int getInt() {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public IntegerField minimum(int minimum) {
        this.minimum = minimum;
        return this;
    }

    public IntegerField maximum(int maximum) {
        this.maximum = maximum;
        return this;
    }

    private void validate() {
        int v = getInt();
        if (v < minimum) {
            text = Integer.toString(minimum);
        } else if (v > maximum) {
            text = Integer.toString(maximum);
        }
        if (cursor > text.length()) {
            cursor = text.length();
        }
    }

    public IntegerField integer(int value) {
        if (getInt() == value) {
            return this;
        }
        this.text = Integer.toString(value);
        if (cursor > text.length()) {
            cursor = text.length();
        }
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
                integer(0);
                fireIntegerEvents(0);
            }
            return this;
        }
        return null;
    }

    private static boolean isControlDown() {
        long handle = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_CONTROL) || InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_CONTROL);
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
                        validate();
                        fireIntegerEvents(getInt());
                    }
                } else if (keyCode == GLFW.GLFW_KEY_C) {
                    if (isRegionSelected()) {
                        GLFW.glfwSetClipboardString(handle, getSelectedText());
                    }
                } else if (keyCode == GLFW.GLFW_KEY_X) {
                    if (isRegionSelected()) {
                        GLFW.glfwSetClipboardString(handle, getSelectedText());
                        replaceSelectedRegion("");
                        fireIntegerEvents(getInt());
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
                fireIntegerEnterEvents(getInt());
//                window.setTextFocus(null);
                return false;
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                return false;
            } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (isRegionSelected()) {
                    replaceSelectedRegion("");
                    fireIntegerEvents(getInt());
                } else if (!text.isEmpty() && cursor > 0) {
                    text = text.substring(0, cursor - 1) + text.substring(cursor);
                    cursor--;
                    validate();
                    fireIntegerEvents(getInt());
                }
            } else if (keyCode == GLFW.GLFW_KEY_DELETE) {
                if (isRegionSelected()) {
                    replaceSelectedRegion("");
                    fireIntegerEvents(getInt());
                } else if (cursor < text.length()) {
                    text = text.substring(0, cursor) + text.substring(cursor + 1);
                    validate();
                    fireIntegerEvents(getInt());
                }
            } else if (keyCode == GLFW.GLFW_KEY_HOME) {
                updateSelection();
                cursor = 0;
            } else if (keyCode == GLFW.GLFW_KEY_END) {
                updateSelection();
                cursor = text.length();
//            } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
//                fireArrowDownEvents();
//            } else if (keyCode == GLFW.GLFW_KEY_UP) {
//                fireArrowUpEvents();
//            } else if (keyCode == GLFW.GLFW_KEY_TAB) {
//                fireTabEvents();
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
                cursor++;
                fireIntegerEvents(getInt());
                return true;
            } else {
                if (Character.isDigit(codePoint) || codePoint == '-') {
                    text = text.substring(0, cursor) + codePoint + text.substring(cursor);
                    cursor++;
                    validate();
                    fireIntegerEvents(getInt());
                    return true;
                }
            }
        }
        return false;
    }

    private int calculateVerticalOffset() {
        int h = mc.font.lineHeight;
        return (bounds.height - h) / 2;
    }

    private void ensureVisible() {
        if (cursor > text.length()) {
            cursor = text.length();
        }
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
        validate();
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
    public void draw(Screen gui, GuiGraphics graphics, int x, int y) {
        super.draw(gui, graphics, x, y);

        int xx = x + bounds.x;
        int yy = y + bounds.y;

        ensureVisible();

        int col = StyleConfig.colorTextFieldFiller;
        if (window.getTextFocus() == this) {
            col = StyleConfig.colorTextFieldFocusedFiller;
        } else if (isHovering()) {
            col = StyleConfig.colorTextFieldHoveringFiller;
        }

        RenderHelper.drawThickBeveledBox(graphics, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 1, StyleConfig.colorTextFieldTopLeft, StyleConfig.colorTextFieldBottomRight, col);

        String renderedText = mc.font.plainSubstrByWidth(this.text.substring(startOffset), bounds.width - 10);
        int textX = x + 5 + bounds.x;
        int textY = y + calculateVerticalOffset() + bounds.y;
        if (isEnabled()) {
            if (isEditable()) {
                graphics.drawString(mc.font, renderedText, textX, textY, 0xff000000);
            } else {
                graphics.drawString(mc.font, renderedText, textX, textY, 0xff333333);
            }

            if (isRegionSelected()) {
                int selectionStart = getSelectionStart();
                int selectionEnd = getSelectionEnd();

                int renderedStart = Mth.clamp(selectionStart - startOffset, 0, renderedText.length());
                int renderedEnd = Mth.clamp(selectionEnd - startOffset, 0, renderedText.length());

                String renderedSelection = renderedText.substring(renderedStart, renderedEnd);
                String renderedPreSelection = renderedText.substring(0, renderedStart);
                int selectionX = textX + mc.font.width(renderedPreSelection);
                int selectionWidth = mc.font.width(renderedSelection);
                RenderHelper.drawColorLogic(selectionX - 1, textY, selectionWidth + 1, mc.font.lineHeight, 60, 147, 242, GlStateManager.LogicOp.OR_REVERSE);
            }
        } else {
            graphics.drawString(mc.font, renderedText, textX, textY, 0xffa0a0a0);
        }

        if (window.getTextFocus() == this) {
            int w = mc.font.width(this.text.substring(startOffset, cursor));
            graphics.fill(xx + 5 + w, yy + 2, xx + 5 + w + 1, yy + bounds.height - 3, StyleConfig.colorTextFieldCursor);
        }
    }

    public IntegerField event(IntegerEvent event) {
        if (integerEvents == null) {
            integerEvents = new ArrayList<>();
        }
        integerEvents.add(event);
        return this;
    }

    public void removeIntegerEvent(IntegerEvent event) {
        if (integerEvents != null) {
            integerEvents.remove(event);
        }
    }

    private void fireIntegerEvents(int newValue) {
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, "integer")
                .put(PARAM_INTEGER, newValue)
                .build());
        if (integerEvents != null) {
            for (IntegerEvent event : integerEvents) {
                event.intChanged(newValue);
            }
        }
    }


    public IntegerField addIntegerEnterEvent(IntegerEnterEvent event) {
        if (integerEnterEvents == null) {
            integerEnterEvents = new ArrayList<>();
        }
        integerEnterEvents.add(event);
        return this;
    }

    public void removeIntegerEnterEvent(IntegerEnterEvent event) {
        if (integerEnterEvents != null) {
            integerEnterEvents.remove(event);
        }
    }

    private void fireIntegerEnterEvents(int newValue) {
        fireChannelEvents(TypedMap.builder()
                .put(Window.PARAM_ID, "enter")
                .put(PARAM_INTEGER, newValue)
                .build());
        if (integerEnterEvents != null) {
            for (IntegerEnterEvent event : integerEnterEvents) {
                event.integerEntered(newValue);
            }
        }
    }

    @Override
    public void readFromGuiCommand(GuiParser.GuiCommand command) {
        super.readFromGuiCommand(command);
        editable = GuiParser.get(command, "editable", DEFAULT_EDITABLE);
        minimum = GuiParser.get(command, "minimum", Integer.MIN_VALUE);
        maximum = GuiParser.get(command, "maximum", Integer.MAX_VALUE);
    }

    @Override
    public void fillGuiCommand(GuiParser.GuiCommand command) {
        super.fillGuiCommand(command);
        GuiParser.put(command, "editable", editable, DEFAULT_EDITABLE);
    }

    @Override
    public GuiParser.GuiCommand createGuiCommand() {
        return new GuiParser.GuiCommand(TYPE_INTEGERFIELD);
    }

    @Override
    public <T> void setGenericValue(T value) {
        if (value == null) {
            integer(0);
        } else {
            try {
                integer(Integer.parseInt(value.toString()));
            } catch (NumberFormatException e) {
                integer(0);
            }
        }
    }

    @Override
    public Object getGenericValue(Type<?> type) {
        if (Type.INTEGER.equals(type)) {
            return getInt();
        } else if (Type.STRING.equals(type)) {
            return text;
        } else if (Type.DOUBLE.equals(type)) {
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        } else if (Type.FLOAT.equals(type)) {
            try {
                return Float.parseFloat(text);
            } catch (NumberFormatException e) {
                return 0.0f;
            }
        }
        return getInt();
    }
}
