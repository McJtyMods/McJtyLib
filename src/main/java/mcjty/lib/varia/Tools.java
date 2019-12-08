package mcjty.lib.varia;

import net.minecraft.state.Property;

import java.util.function.Consumer;
import java.util.function.Function;

public class Tools {

    public static <INPUT extends BASE, BASE> void safeConsume(BASE o, Consumer<INPUT> consumer, String error) {
        try {
            consumer.accept((INPUT) o);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(error, e);
        }
    }

    public static <INPUT extends BASE, BASE> void safeConsume(BASE o, Consumer<INPUT> consumer) {
        try {
            consumer.accept((INPUT) o);
        } catch (ClassCastException ignore) {
        }
    }

    public static <INPUT extends BASE, BASE, RET> RET safeMap(BASE o, Function<INPUT, RET> consumer, String error) {
        try {
            return consumer.apply((INPUT) o);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(error, e);
        }
    }

    private static <T extends Comparable<T>> String getValueName(Property<T> property, Object value) {
        return property.getName((T)value);
    }

    private static void permutateProperties(Consumer<String> consumer, String left, Property<?>... properties) {
        if (properties.length > 0) {
            Property<?>[] tail = new Property<?>[properties.length-1];
            System.arraycopy(properties, 1, tail, 0, properties.length - 1);
            String name = properties[0].getName();
            for (Comparable<?> value : properties[0].getAllowedValues()) {
                permutateProperties(consumer, (left.isEmpty() ? "" : (left + ",")) + name + "=" + getValueName(properties[0], value), tail);
            }
        } else {
            consumer.accept(left);
        }
    }

    // Permutate over all properties and construct a property string that is given to the consumer
    public static void permutateProperties(Consumer<String> consumer, Property<?>... properties) {
        permutateProperties(consumer, "", properties);
    }
}
