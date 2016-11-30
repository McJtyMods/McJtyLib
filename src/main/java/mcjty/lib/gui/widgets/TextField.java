package mcjty.lib.gui.widgets;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.TextSpecialKeyEvent;
import mcjty.lib.gui.events.TextEnterEvent;
import mcjty.lib.gui.events.TextEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class TextField extends AbstractWidget<TextField> {
    private String text = "";
    private int cursor = 0;
    private int startOffset = 0;        // Start character where we are displaying
    private List<TextEvent> textEvents = null;
    private List<TextEnterEvent> textEnterEvents = null;
    private List<TextSpecialKeyEvent> textSpecialKeyEvents = null;
    private boolean editable = true;

    public TextField(Minecraft mc, Gui gui) {
        super(mc, gui);
    }

    public boolean isEditable() {
        return editable;
    }

    public TextField setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    public String getText() {
        return text;
    }

    public TextField setText(String text) {
        this.text = text;
        cursor = text.length();
        if (startOffset >= cursor) {
            startOffset = cursor-1;
            if (startOffset < 0) {
                startOffset = 0;
            }
        }
        return this;
    }

    @Override
    public Widget mouseClick(Window window, int x, int y, int button) {
        if (isEnabledAndVisible() && editable) {
            window.setTextFocus(this);
            if (button == 1) {
                setText("");
                fireTextEvents("");
            }
            return this;
        }
        return null;
    }

    @Override
    public boolean keyTyped(Window window, char typedChar, int keyCode) {
        boolean rc = super.keyTyped(window, typedChar, keyCode);
        if (rc) {
            return true;
        }
        if (isEnabledAndVisible() && editable) {
            if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_ESCAPE) {
                fireTextEnterEvents(text);
//                window.setTextFocus(null);
                return false;
            } else if (keyCode == Keyboard.KEY_BACK) {
                if (!text.isEmpty() && cursor > 0) {
                    text = text.substring(0, cursor-1) + text.substring(cursor);
                    cursor--;
                    fireTextEvents(text);
                }
            } else if (keyCode == Keyboard.KEY_DELETE) {
                if (cursor < text.length()) {
                    text = text.substring(0, cursor) + text.substring(cursor+1);
                    fireTextEvents(text);
                }
            } else if (keyCode == Keyboard.KEY_HOME) {
                cursor = 0;
            } else if (keyCode == Keyboard.KEY_END) {
                cursor = text.length();
            } else if (keyCode == Keyboard.KEY_DOWN) {
                fireArrowDownEvents();
            } else if (keyCode == Keyboard.KEY_UP) {
                fireArrowUpEvents();
            } else if (keyCode == Keyboard.KEY_TAB) {
                fireTabEvents();
            } else if (keyCode == Keyboard.KEY_LEFT) {
                if (cursor > 0) {
                    cursor--;
                }
            } else if (keyCode == Keyboard.KEY_RIGHT) {
                if (cursor < text.length()) {
                    cursor++;
                }
            } else if (new Integer(typedChar).intValue() == 0) {
                // Do nothing
            } else {
                text = text.substring(0, cursor) + typedChar + text.substring(cursor);
                cursor++;
                fireTextEvents(text);
            }
            return true;
        }
        return false;
    }

    private int calculateVerticalOffset() {
        int h = mc.fontRendererObj.FONT_HEIGHT;
        return (bounds.height - h)/2;
    }

    private void ensureVisible() {
        if (cursor < startOffset) {
            startOffset = cursor;
        } else {
            int w = mc.fontRendererObj.getStringWidth(text.substring(startOffset, cursor));
            while (w > bounds.width-12) {
                startOffset++;
                w = mc.fontRendererObj.getStringWidth(text.substring(startOffset, cursor));
            }
        }
    }


    @Override
    public void draw(Window window, int x, int y) {
        super.draw(window, x, y);

        int xx = x + bounds.x;
        int yy = y + bounds.y;

        ensureVisible();

        int col = StyleConfig.colorTextFieldFiller;
        if (window.getTextFocus() == this) {
            col = StyleConfig.colorTextFieldFocusedFiller;
        } else if (isHovering()) {
            col = StyleConfig.colorTextFieldHoveringFiller;
        }

        RenderHelper.drawThickBeveledBox(xx, yy, xx + bounds.width - 1, yy + bounds.height - 1, 1, StyleConfig.colorTextFieldTopLeft, StyleConfig.colorTextFieldBottomRight, col);

        if (isEnabled()) {
            if (isEditable()) {
                mc.fontRendererObj.drawString(mc.fontRendererObj.trimStringToWidth(text.substring(startOffset), bounds.width - 10), x + 5 + bounds.x, y + calculateVerticalOffset() + bounds.y, 0xff000000);
            } else {
                mc.fontRendererObj.drawString(mc.fontRendererObj.trimStringToWidth(text.substring(startOffset), bounds.width - 10), x + 5 + bounds.x, y + calculateVerticalOffset() + bounds.y, 0xff333333);
            }
        } else {
            mc.fontRendererObj.drawString(mc.fontRendererObj.trimStringToWidth(text.substring(startOffset), bounds.width - 10), x + 5 + bounds.x, y + calculateVerticalOffset() + bounds.y, 0xffa0a0a0);
        }

        if (window.getTextFocus() == this) {
            int w = mc.fontRendererObj.getStringWidth(text.substring(startOffset, cursor));
            Gui.drawRect(xx + 5 + w, yy + 2, xx + 5 + w + 1, yy + bounds.height - 3, StyleConfig.colorTextFieldCursor);
        }
    }

    public TextField addTextEvent(TextEvent event) {
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
        if (textEvents != null) {
            for (TextEvent event : textEvents) {
                event.textChanged(this, newText);
            }
        }
    }

    public TextField addSpecialKeyEvent(TextSpecialKeyEvent event) {
        if (textSpecialKeyEvents == null) {
            textSpecialKeyEvents = new ArrayList<>();
        }
        textSpecialKeyEvents.add(event);
        return this;
    }

    private void fireArrowUpEvents() {
        if (textSpecialKeyEvents != null) {
            for (TextSpecialKeyEvent event : textSpecialKeyEvents) {
                event.arrowUp(this);
            }
        }
    }

    private void fireArrowDownEvents() {
        if (textSpecialKeyEvents != null) {
            for (TextSpecialKeyEvent event : textSpecialKeyEvents) {
                event.arrowDown(this);
            }
        }
    }

    private void fireTabEvents() {
        if (textSpecialKeyEvents != null) {
            for (TextSpecialKeyEvent event : textSpecialKeyEvents) {
                event.tab(this);
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
        if (textEnterEvents != null) {
            for (TextEnterEvent event : textEnterEvents) {
                event.textEntered(this, newText);
            }
        }
    }


}
