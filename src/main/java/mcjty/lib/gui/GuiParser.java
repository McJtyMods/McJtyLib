package mcjty.lib.gui;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static java.io.StreamTokenizer.TT_EOF;
import static java.io.StreamTokenizer.TT_NUMBER;
import static java.io.StreamTokenizer.TT_WORD;

public class GuiParser {

    public static class Command {
        private final String id;
        private final List<Object> parameters = new ArrayList<>();
        private final List<Command> commands = new ArrayList<>();

        public Command(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void pushParameter(Object parameter) {
            parameters.add(parameter);
        }

        public void pushCommand(Command command) {
            commands.add(command);
        }

        public List<Object> getParameters() {
            return parameters;
        }

        public List<Command> getCommands() {
            return commands;
        }

        @Override
        public String toString() {
            return "Command{" +
                    "id='" + id + '\'' +
                    ", parameters=" + parameters +
                    ", commands=" + commands +
                    '}';
        }
    }

    public static List<Command> parse(Reader reader) {
        StreamTokenizer tokenizer = new StreamTokenizer(reader);

        List<Command> commands = new ArrayList<>();

        tokenizer.slashSlashComments(true);
        tokenizer.eolIsSignificant(false);
        tokenizer.quoteChar('"');
        tokenizer.parseNumbers();

        Command currentCommand = null;

        try {
            int token = tokenizer.nextToken();
            while (token != TT_EOF) {
                switch (token) {
                    case TT_WORD:
                        System.out.println("WORD = " + tokenizer.sval);
                        if (currentCommand != null) {
                            throw new RuntimeException("Syntax error 1!");
                        }
                        currentCommand = new Command(tokenizer.sval);
                        break;

                    case TT_NUMBER:
                        System.out.println("NUMBER = " + tokenizer.nval);
                        break;

                    case '"':
                        System.out.println("STRING = " + tokenizer.sval);
                        break;

                    default:
                        System.out.println("token = " + (char)token);
                        break;
                }

                token = tokenizer.nextToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return commands;
    }

    public static void main(String[] args) {
        StringReader reader = new StringReader(""+
"                panel() {"+
"            layout(positional)"+
"            bg_image(\"rftools:textures/gui/securitymanager.png\")"+
"            widgetlist(\"players\") {"+
"                bg_thickness(-1)"+
"                bg_filled1(-7631989)"+
"            }"+
"            slider() {"+
"                scrollable(\"players\")"+
"                desiredwidth(10)"+
"            }"+
"        }");

        parse(reader);
    }

}
