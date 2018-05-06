package mcjty.lib.typed;

public class TypeConvertors {

    public static boolean toBoolean(Object b) {
        if (b instanceof Boolean) {
            return (boolean) b;
        } else if (b == null) {
            return false;
        } else if (b instanceof Integer) {
            return ((Integer)b) != 0;
        } else if (b instanceof Double) {
            return ((Double)b) != 0;
        } else if (b instanceof String) {
            return !((String)b).isEmpty();
        } else {
            return false;
        }
    }

    public static int toInt(Object b) {
        if (b instanceof Integer) {
            return (int) b;
        } else if (b == null) {
            return 0;
        } else if (b instanceof Boolean) {
            return ((Boolean)b) ? 1 : 0;
        } else if (b instanceof Double) {
            return (int) b;
        } else if (b instanceof String) {
            return Integer.parseInt((String)b);
        } else {
            return 0;
        }
    }
}
