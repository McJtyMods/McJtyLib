package mcjty.lib.thirteen;

import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class ConfigSpec {

    private ConfigSpec(Builder builder, Configuration cfg) {

    }

    public static class Builder {

        private Configuration cfg;

        private List<String> path = new ArrayList<>();
        private String comment;

        private String getCurrentComment() {
            return comment == null ? "" : comment;
        }

        private String getCurrentCategory() {
            return path.get(path.size()-1);
        }

        public Builder configuration(Configuration cfg) {
            this.cfg = cfg;
            return this;
        }

        public Builder push(String p) {
            path.add(p);
            if (comment != null) {
                cfg.addCustomCategoryComment(p, comment);
                comment = null;
            }
            return this;
        }

        public Builder pop() {
            path.remove(path.size()-1);
            return this;
        }

        public IntValue defineInRange(String name, int def, int min, int max) {
            int value = cfg.getInt(name, getCurrentCategory(), def, min, max, getCurrentComment());
            comment = null;
            return new IntValue(value);
        }

        public DoubleValue defineInRange(String name, double def, double min, double max) {
            float value = cfg.getFloat(name, getCurrentCategory(), (float) def, (float) min, (float) max, getCurrentComment());
            comment = null;
            return new DoubleValue(value);
        }

        public BooleanValue define(String name, boolean def) {
            boolean value = cfg.getBoolean(name, getCurrentCategory(), def, getCurrentComment());
            comment = null;
            return new BooleanValue(value);
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public ConfigSpec build(Configuration cfg) {
            return new ConfigSpec(this, cfg);
        }
    }
}
