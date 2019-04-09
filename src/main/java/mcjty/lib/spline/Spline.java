package mcjty.lib.spline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class Spline<T> {

    protected final List<T> points = new ArrayList<>();
    protected final List<Float> times = new ArrayList<>();

    protected final Supplier<T> supplier;
    protected final BiFunction<T, T, T> subtract;
    protected final BiFunction<T, T, T> add;
    protected final BiFunction<T, Float, T> scale;

    public Spline(Supplier<T> supplier, BiFunction<T, T, T> subtract, BiFunction<T, T, T> add, BiFunction<T, Float, T> scale) {
        this.supplier = supplier;
        this.subtract = subtract;
        this.add = add;
        this.scale = scale;
    }

    public void addPoint(T point, float time) {
        points.add(point);
        times.add(time);
    }

    // Insert a new point at the specific index. Points already at this index and further will be shifted
    public void insertPoint(T point, float time, int idx) {
        points.add(idx, point);
        times.add(idx, time);
    }

    public abstract void calculate(float time);

    public abstract T getInterpolated();

}
