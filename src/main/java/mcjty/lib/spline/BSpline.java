package mcjty.lib.spline;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BSpline<T> extends Spline<T> {

    private int idx;
    private float t;

    public BSpline(Supplier<T> supplier, BiFunction<T, T, T> subtract, BiFunction<T, T, T> add, BiFunction<T, Float, T> scale) {
        super(supplier, subtract, add, scale);
    }

    protected float baseFunction(int i, float t) {
        return switch (i) {
            case -2 -> (((-t + 3) * t - 3) * t + 1) / 6;
            case -1 -> (((3 * t - 6) * t) * t + 4) / 6;
            case 0 -> (((-3 * t + 3) * t + 3) * t + 1) / 6;
            case 1 -> (t * t * t) / 6;
            default -> 0;
        };

        // We only get here if an invalid i is specified.
    }

    @Override
    public void calculate(float time) {
        // First find the current 'idx'.
        for (idx = 0; idx < points.size() - 1; idx++) {
            if (time >= times.get(idx) && time <= times.get(idx + 1)) {
                t = 1.0f - (times.get(idx + 1) - time)
                        / (times.get(idx + 1) - times.get(idx));
                return;
            }
        }
        t = 1.0f;
    }

    @Override
    public T getInterpolated() {
        T val = supplier.get();
//        float x = 0;
//        float y = 0;
//        float z = 0;
        int j;
        for (j = -2; j <= 1; j++) {
            T pp;
            int id = idx + j + 1;
            if (id == -1) {
                pp = subtract.apply(points.get(0), subtract.apply(points.get(1), points.get(0)));
//                pp = points.get(0).subtract(points.get(1).subtract(points.get(0)));
            } else if (id == -2) {
//                pp = points.get(0).subtract(points.get(1).subtract(points.get(0)).scale(2.0));
                pp = subtract.apply(points.get(0), scale.apply(subtract.apply(points.get(1), points.get(0)), 2.0f));
            } else if (id >= points.size()) {
//                pp = points.get(points.size() - 1).subtract((points.get(points.size() - 2).subtract(points.get(points.size() - 1))));
                pp = subtract.apply(points.get(points.size()-1), subtract.apply(points.get(points.size()-2), points.get(points.size()-1)));
            } else {
                pp = points.get(id);
            }
            float base = baseFunction(j, t);
            val = add.apply(val, scale.apply(pp, base));
//            x += base * pp.x;
//            y += base * pp.y;
//            z += base * pp.z;
        }

//        return new Vector3d(x, y, z);
        return val;
    }
}
