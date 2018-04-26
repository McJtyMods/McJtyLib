package mcjty.lib.varia;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that can be used to intern strings
 */
public class StringRegister {

    public static final StringRegister STRINGS = new StringRegister();

    private int lastId = 1;
    private final Map<String, Integer> stringToId = new HashMap<>();
    private final Map<Integer, String> idToString = new HashMap<>();

    public int get(String s) {
        if (stringToId.containsKey(s)) {
            return stringToId.get(s);
        }
        int id = lastId;
        lastId++;
        stringToId.put(s, id);
        idToString.put(id, s);
        return id;
    }

    public String get(Integer id) {
        return idToString.get(id);
    }
}
