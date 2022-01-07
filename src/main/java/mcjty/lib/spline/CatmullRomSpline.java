package mcjty.lib.spline;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CatmullRomSpline<T> extends BSpline<T> {

    private int idx;
    private float t;

    @Override
    protected float baseFunction(int i, float t) {
        return switch (i) {
            case -2 -> ((-t + 2) * t - 1) * t / 2;
            case -1 -> (((3 * t - 5) * t) * t + 2) / 2;
            case 0 -> ((-3 * t + 4) * t + 1) * t / 2;
            case 1 -> ((t - 1) * t * t) / 2;
            default -> 0;
        };

        // We only get here if an invalid i is specified.
    }

    public CatmullRomSpline(Supplier<T> supplier, BiFunction<T, T, T> subtract, BiFunction<T, T, T> add, BiFunction<T, Float, T> scale) {
        super(supplier, subtract, add, scale);
    }
}
