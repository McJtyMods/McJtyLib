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
}
