package mcjty.lib.varia;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedRandom<T> {

    private static Random random = new Random();
    public List<Pair<T, Float>> weightedList = new ArrayList<>();
    private boolean sorted = false;
    private float maximum = 0;

    public void add(T element, float weight) {
        weightedList.add(Pair.of(element, weight));
        sorted = false;
        maximum = 0;
    }

    public T getRandom() {
        if (!sorted) {
            // Sort the list with biggest weights first. That way we don't have to iterate as much
            // in most cases
            weightedList.sort((p1, p2) -> -p1.getRight().compareTo(p2.getRight()));
            sorted = true;
            maximum = 0;
            for (Pair<T, Float> pair : weightedList) {
                maximum += pair.getRight();
            }
        }
        float v = random.nextFloat() * maximum;
        for (Pair<T, Float> pair : weightedList) {
            if (v <= pair.getRight()) {
                return pair.getKey();
            }
            v -= pair.getRight();
        }
        return weightedList.get(0).getKey();
    }
}
