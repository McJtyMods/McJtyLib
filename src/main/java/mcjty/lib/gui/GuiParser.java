package mcjty.lib.gui;

import mcjty.lib.varia.Logging;
import org.apache.logging.log4j.util.Strings;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.stream.Stream;

import static java.io.StreamTokenizer.*;

public class GuiParser {

    public static class ParserException extends Exception {
        public ParserException(String s, int linenr) {
            super(s + " (line " + linenr + ")");
        }
    }

    public static class GuiCommand {
        private final String id;
        private final List<Object> parameters = new ArrayList<>();
        private final List<GuiCommand> guiCommands = new ArrayList<>();
        private final Map<String, GuiCommand> commandMap = new HashMap<>();

        public GuiCommand(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public GuiCommand parameter(Object parameter) {
            parameters.add(parameter);
            return this;
        }

        public GuiCommand command(GuiCommand guiCommand) {
            guiCommands.add(guiCommand);
            commandMap.put(guiCommand.getId(), guiCommand);
            return this;
        }

        public List<Object> getParameters() {
            return parameters;
        }

        public List<GuiCommand> getGuiCommands() {
            return guiCommands;
        }

        public Optional<GuiCommand> findCommand(String cmd) {
            return Optional.ofNullable(commandMap.get(cmd));
        }

        public Stream<GuiCommand> commands() {
            return guiCommands.stream();
        }

        public void removeParameter(int index) {
            parameters.remove(index);
        }

        public Stream<Object> parameters() {
            return parameters.stream();
        }

        public <T> T getOptionalPar(int par, T def) {
            if (par >= getParameters().size()) {
                return def;
            }
            return (T) getParameters().get(par);
        }

        @Override
        public String toString() {
            return "Command{" +
                    "id='" + id + '\'' +
                    ", parameters=" + parameters +
                    ", commands=" + guiCommands +
                    '}';
        }

        private static String ind(int i) {
            return "                                                                        ".substring(0, i);
        }

        public void write(PrintWriter writer, int indent) {
            writer.print(ind(indent) + id);
            if (!parameters.isEmpty()) {
                if (parameters().anyMatch(o -> o != null && !"".equals(o))) {
                    writer.print('(');
                    String comma = "";
                    for (Object parameter : parameters) {
                        Object par = parameter;
                        if (par instanceof String) {
                            par = Strings.quote((String) par);
                        }
                        writer.print(comma + par);
                        comma = ",";
                    }
                    writer.print(')');
                }
            }
            if (guiCommands.isEmpty()) {
                writer.println("");
            } else {
                writer.println(" {");
                for (GuiCommand cmd : guiCommands) {
                    cmd.write(writer, indent+4);
                }
                writer.println(ind(indent) + "}");
            }
        }

        public void dump(int indent) {
            System.out.print(ind(indent) + id + "(");
            String comma = "";
            for (Object parameter : parameters) {
                Object par = parameter;
                if (par instanceof String) {
                    par = Strings.quote((String)par);
                }
                System.out.print(comma + par);
                comma = ",";
            }
            System.out.print(")");
            if (guiCommands.isEmpty()) {
                System.out.println("");
            } else {
                System.out.println(" {");
                for (GuiCommand cmd : guiCommands) {
                    cmd.dump(indent+4);
                }
                System.out.println(ind(indent) + "}");
            }
        }
    }

    @Nullable
    private static GuiCommand parseCommand(StreamTokenizer tokenizer) throws IOException, ParserException {
        int token = tokenizer.nextToken();
        if (token == TT_EOF) {
            return null;
        }

        if (token != TT_WORD) {
            throw new ParserException("Expected a command token, got a '" + (char) token + "' instead!", tokenizer.lineno());
        }
        GuiCommand guiCommand = new GuiCommand(tokenizer.sval);

        token = tokenizer.nextToken();
        if (token == '(') {
            token = tokenizer.nextToken();
            while (token != ')') {
                if (token == TT_WORD || token == '\'') {
                    if (token == TT_WORD && "true".equals(tokenizer.sval.toLowerCase())) {
                        guiCommand.parameter(true);
                    } else if (token == TT_WORD && "false".equals(tokenizer.sval.toLowerCase())) {
                        guiCommand.parameter(false);
                    } else {
                        guiCommand.parameter(tokenizer.sval);
                    }
                } else if (token == TT_NUMBER) {
                    guiCommand.parameter((int) tokenizer.nval);
                } else {
                    throw new ParserException("Expected parameter! Got '" + (char) token + "' instead", tokenizer.lineno());
                }
                token = tokenizer.nextToken();
                if (token == ',') {
                    token = tokenizer.nextToken();
                }
            }

            token = tokenizer.nextToken();
        }

        if (token != '{') {
            tokenizer.pushBack();
        } else {
            while (token != '}') {
                if (token != '{') {
                    tokenizer.pushBack();
                }
                guiCommand.command(parseCommand(tokenizer));
                token = tokenizer.nextToken();
            }
        }

        return guiCommand;
    }

    public static List<GuiCommand> parse(Reader reader) throws IOException, ParserException {
        StreamTokenizer tokenizer = new StreamTokenizer(reader);

        tokenizer.slashSlashComments(true);
        tokenizer.eolIsSignificant(false);
        tokenizer.quoteChar('\'');
        tokenizer.parseNumbers();

        List<GuiCommand> commands = new ArrayList<>();
        GuiCommand command = parseCommand(tokenizer);
        while (command != null) {
            commands.add(command);
            command = parseCommand(tokenizer);
        }
        return commands;
    }

    public static void main(String[] args) {
        StringReader reader = new StringReader(""+
"                panel() {\n"+
"            layout(positional)\n"+
"            bgimage('rftools:textures/gui/securitymanager.png')\n"+
"            widgetlist('players') {\n"+
"                bgthickness(-1)\n"+
"                bgfilled1(-7631989)\n"+
"            }\n"+
"            slider() {\n"+
"                scrollable('players')\n"+
"                desiredwidth(10)\n"+
"            }\n"+
"        }");

        try {
            parse(reader).stream().forEach(command -> command.dump(1));
        } catch (IOException e) {
            Logging.logError("Error parsing!", e);
        } catch (ParserException e) {
            Logging.logError("Error parsing!", e);
        }
    }

    public static <T> void put(GuiCommand parent, String name, T value, T def) {
        if (value == null) {
            return;
        }
        if (value.equals(def)) {
            return;
        }
        parent.command(new GuiCommand(name).parameter(value));
    }

    public static <T> T get(GuiCommand parent, String name, T def) {
        return parent.findCommand(name).map(cmd -> cmd.getOptionalPar(0, def)).orElse(def);
    }

    public static <T> T getIndexed(GuiCommand parent, String name, int idx, T def) {
        return parent.findCommand(name).map(cmd -> cmd.getOptionalPar(idx, def)).orElse(def);
    }
}
