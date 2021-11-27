package mcjty.lib.varia;

import java.util.ArrayList;
import java.util.List;

/**
 * A very efficient class to handle lists in a lazy manner
 */
public class LazyList<T> {

    private List<T> internalList = new ArrayList<>();
    private List<T> list;
    private boolean needsCopy = false;

    public LazyList() {
        list = internalList;
    }

    /**
     * Copy the contents of a list on top of the current list. This is done in a lazy manner.
     * The copy is only done as soon as the list is first modified
     */
    public void copyList(List<T> list) {
        this.list = list;
        needsCopy = true;
    }

    public void add(T object) {
        resolve();
        list.add(object);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public void clear() {
        needsCopy = false;
        list = internalList;
        list.clear();
    }

    // Get the list (note, this is not guaranteed to be a copy so don't modify this)
    public List<T> getList() {
        return list;
    }

    /**
     * Extract this list. That means that after calling this it will no longer be coupled to this lazy list.
     * In addition this lazy list will be empty
     */
    public List<T> extractList() {
        if (needsCopy) {
            // Our list is pointing to some other list. Best to make a copy
            List<T> extracted = new ArrayList<>(list);
            clear();
            return extracted;
        } else {
            // Our list is pointing to the internal list so we can return that and make a new internal list
            List<T> extracted = internalList;
            internalList = new ArrayList<>();
            return extracted;
        }
    }

    private void resolve() {
        if (needsCopy) {
            needsCopy = false;
            list = new ArrayList<>(list);
        }
    }

}
