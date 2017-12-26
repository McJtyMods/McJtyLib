package mcjty.lib.varia;

public class MathTools {

    public static int floor(float value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    public static int ceiling(float value) {
        int i = (int) value;
        return value > i ? i + 1 : i;
    }

    public static int floor(double value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

}
