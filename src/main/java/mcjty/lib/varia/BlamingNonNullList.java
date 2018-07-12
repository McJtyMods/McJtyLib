package mcjty.lib.varia;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class BlamingNonNullList<E> extends AbstractList<E>
{
    private final List<BlameHolder<E>> delegate;
    private boolean doRethrow = false;

    private static class BlameHolder<E> {
        final E object;
        final NullPointerException blame;

        BlameHolder(E object, NullPointerException blame) {
            this.object = object;
            this.blame = blame;
        }
    }

    public BlamingNonNullList()
    {
        this.delegate = new ArrayList<>();
    }

    public void setDoRethrow(boolean doRethrow) {
        this.doRethrow = doRethrow;
    }

    @Nonnull
    public E get(int index)
    {
        BlameHolder<E> bh = this.delegate.get(index);
        if(doRethrow && bh.object == null) throw new RuntimeException("A NullPointerException was previously ignored", bh.blame);
        return bh.object;
    }

    public E set(int index, E element)
    {
        NullPointerException ex = null;
        if(element == null) {
            ex = new NullPointerException();
        }
        BlameHolder<E> bh = this.delegate.set(index, new BlameHolder<E>(element, ex));
        if(doRethrow && bh.object == null) throw new RuntimeException("A NullPointerException was previously ignored", bh.blame);
        return bh.object;
    }

    public void add(int index, E element)
    {
        NullPointerException ex = null;
        if(element == null) {
            ex = new NullPointerException();
        }
        this.delegate.add(index, new BlameHolder<E>(element, ex));
    }

    public E remove(int index)
    {
        BlameHolder<E> bh = this.delegate.remove(index);
        if(doRethrow && bh.object == null) throw new RuntimeException("A NullPointerException was previously ignored", bh.blame);
        return bh.object;
    }

    public int size()
    {
        return this.delegate.size();
    }
}
