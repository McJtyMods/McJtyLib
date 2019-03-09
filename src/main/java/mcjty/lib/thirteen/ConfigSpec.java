package mcjty.lib.thirteen;

import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class ConfigSpec {

    private ConfigSpec(Builder builder, Configuration cfg) {
        for (Builder.V v : builder.values) {
            v.build(cfg);
        }
    }

    public static class Builder {

        private List<V> values = new ArrayList<>();

        private List<String> path = new ArrayList<>();
        private String comment;

        private String getCurrentComment() {
            return comment == null ? "" : comment;
        }

        private String getCurrentCategory() {
            return path.get(path.size()-1);
        }

        public Builder push(String p) {
            path.add(p);
            CV v = new CV(p, "", getCurrentComment());
            comment = null;
            values.add(v);
            return this;
        }

        public Builder pop() {
            path.remove(path.size()-1);
            return this;
        }

        public IntValue defineInRange(String name, int def, int min, int max) {
            IV v = new IV(name, getCurrentCategory(), getCurrentComment(), def, min, max);
            comment = null;
            values.add(v);
            return v.get();
        }

        public DoubleValue defineInRange(String name, double def, double min, double max) {
            DV v = new DV(name, getCurrentCategory(), getCurrentComment(), def, min, max);
            comment = null;
            values.add(v);
            return v.get();
        }

        public BooleanValue define(String name, boolean def) {
            BV v = new BV(name, getCurrentCategory(), getCurrentComment(), def);
            comment = null;
            values.add(v);
            return v.get();
        }

        public <T extends Enum<T>> ConfigValue<T> defineEnum(String name, T def, T... enumValues) {
            EV v = new EV(name, getCurrentCategory(), getCurrentComment(), def, enumValues);
            comment = null;
            values.add(v);
            return v.get();
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        private static abstract class V {
            protected final String name;
            protected final String category;
            protected final String comment;

            public V(String name, String category, String comment) {
                this.name = name;
                this.category = category;
                this.comment = comment;
            }

            public abstract void build(Configuration cfg);
        }

        private static class CV extends V {

            public CV(String name, String category, String comment) {
                super(name, category, comment);
            }

            @Override
            public void build(Configuration cfg) {
                if (comment != null) {
                    cfg.addCustomCategoryComment(name, comment);
                }
            }
        }

        private static class BV extends V {
            private final BooleanValue value;

            public BV(String name, String category, String comment, boolean def) {
                super(name, category, comment);
                this.value = new BooleanValue(def);
            }

            public BooleanValue get() {
                return value;
            }

            @Override
            public void build(Configuration cfg) {
                value.set(cfg.getBoolean(name, category, value.get(), comment));
            }
        }

        private static class DV extends V {
            private final DoubleValue value;
            private final double min;
            private final double max;

            public DV(String name, String category, String comment, double def, double min, double max) {
                super(name, category, comment);
                this.value = new DoubleValue(def);
                this.min = min;
                this.max = max;
            }

            public DoubleValue get() {
                return value;
            }

            @Override
            public void build(Configuration cfg) {
                value.set(cfg.getFloat(name, category, (float) value.get(), (float) min, (float) max, comment));
            }
        }

        private static class IV extends V {
            private final IntValue value;
            private final int min;
            private final int max;

            public IV(String name, String category, String comment, int def, int min, int max) {
                super(name, category, comment);
                this.value = new IntValue(def);
                this.min = min;
                this.max = max;
            }

            public IntValue get() {
                return value;
            }

            @Override
            public void build(Configuration cfg) {
                value.set(cfg.getInt(name, category, value.get(), min, max, comment));
            }
        }

        private static class EV<T extends Enum<T>> extends V {
            private final ConfigValue<T> value;
            private final T[] values;

            public EV(String name, String category, String comment, T def, T... values) {
                super(name, category, comment);
                this.values = values;
                this.value = new ConfigValue<>(def);
            }

            public ConfigValue<T> get() {
                return value;
            }

            @Override
            public void build(Configuration cfg) {
                String result = cfg.getString(name, category, value.get().name(), comment);
                for (T t : values) {
                    if (t.name().equalsIgnoreCase(result)) {
                        value.set(t);
                        return;
                    }
                }
                throw new IllegalArgumentException("Unknown value '" + result + "' for configuration '" + name + "'!");
            }
        }

        public ConfigSpec build(Configuration cfg) {
            return new ConfigSpec(this, cfg);
        }
    }

    public static class BooleanValue {

        private boolean value;

        public BooleanValue(boolean value) {
            this.value = value;
        }

        public boolean get() {
            return value;
        }

        private void set(boolean value) {
            this.value = value;
        }
    }

    public static class DoubleValue {

        private double value;

        public DoubleValue(double value) {
            this.value = value;
        }

        public double get() {
            return value;
        }

        private void set(double value) {
            this.value = value;
        }
    }

    public static class IntValue {

        private int value;

        public IntValue(int value) {
            this.value = value;
        }

        public int get() {
            return value;
        }

        private void set(int value) {
            this.value = value;
        }
    }

    public static class ConfigValue<T> {

        private T value;

        public ConfigValue(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }

        private void set(T value) {
            this.value = value;
        }
    }
}
