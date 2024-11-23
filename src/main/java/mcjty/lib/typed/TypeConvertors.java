package mcjty.lib.typed;

public class TypeConvertors {

    public static boolean toBoolean(Object v) {
        if (v instanceof Boolean) {
            return (boolean) v;
        } else if (v == null) {
            return false;
        } else if (v instanceof Byte b) {
            return b != 0;
        } else if (v instanceof Short b) {
            return b != 0;
        } else if (v instanceof Long b) {
            return b != 0;
        } else if (v instanceof Integer b) {
            return b != 0;
        } else if (v instanceof Double b) {
            return b != 0;
        } else if (v instanceof Float b) {
            return b != 0;
        } else if (v instanceof String b) {
            return !b.isEmpty();
        } else {
            return false;
        }
    }

    public static int toInt(Object v) {
        if (v instanceof Integer) {
            return (int) v;
        } else if (v == null) {
            return 0;
        } else if (v instanceof Boolean b) {
            return b ? 1 : 0;
        } else if (v instanceof Double b) {
            return b.intValue();
        } else if (v instanceof Float b) {
            return (int) b.floatValue();
        } else if (v instanceof Long b) {
            return b.intValue();
        } else if (v instanceof Short b) {
            return b;
        } else if (v instanceof Byte b) {
            return b;
        } else if (v instanceof String b) {
            return Integer.parseInt(b);
        } else {
            return 0;
        }
    }
}
