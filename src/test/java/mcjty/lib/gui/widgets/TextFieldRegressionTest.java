package mcjty.lib.gui.widgets;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TextFieldRegressionTest {
    @Test
    void clearingTextDropsTheOldSelectionBeforeTyping() {
        var field = new TextField().text("abc");
        // The state after Shift+Left from the end of "abc".
        field.setSelection(3, 2);
        // Right-click clears the field through this setter.
        field.text("");
        assertFalse(field.isRegionSelected());
        assertTrue(field.charTyped('x'));
        assertTrue(field.charTyped('y'));
        assertEquals("xy", field.getText());
    }

    @Test
    void replacingTextDropsSelectionsInEitherDirection() {
        for (String replacement : new String[] {"a", "xyz", "longer text"}) {
            for (boolean reversed : new boolean[] {false, true}) {
                var field = new TextField().text("abc");
                field.setSelection(reversed ? 3 : 2, reversed ? 2 : 3);
                field.text(replacement);
                assertFalse(field.isRegionSelected());
                assertTrue(field.charTyped('!'));
                assertEquals(replacement + "!", field.getText());
            }
        }
    }
}
