import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.Math;


public class ArrayDeque61B<T> implements Deque61B<T> {
    private T[] items;
    private int size;
    private int front;
    private int back;

    private static final int INITIAL_CAPACITY = 8;
    private static final int INITIAL_FRONT = 4;
    private static final int INITIAL_BACK = 5;

    public ArrayDeque61B() {
        items = (T[]) new Object[INITIAL_CAPACITY];
        size = 0;
        front = INITIAL_FRONT;
        back = INITIAL_BACK;
    }

    // there is no enough space for new item, double current array
    public void resizeUp(int size) {

    }

    public void resizeDown(int size) {

    }



    @Override
    public void addFirst(T x) {
        if (size == items.length) {
            resizeUp(size * 2);
        }

        front = Math.floorMod(front - 1, items.length);
        items[front + 1] = x;
        size++;
    }

    @Override
    public void addLast(T x) {
        if (size == items.length) {
            resizeUp(size * 2);
        }

        items[back] = x;
        back = Math.floorMod(back + 1, items.length);
        size++;

    }

    @Override
    public List<T> toList() {
        return new ArrayList<>(Arrays.asList(items));
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        if (size == items.length / 2) {
            resizeDown(size / 2);
        }

        front = Math.floorMod(front - 1, items.length);
        T removed = items[front + 1];
        items[front + 1] = null;
        size--;

        return removed;
    }

    @Override
    public T removeLast() {
        if (size == items.length / 2) {
            resizeDown(size / 2);
        }

        T removed = items[back];
        back = Math.floorMod(back + 1, items.length);
        size--;
        return removed;
    }

    @Override
    public T get(int index) {
        if (index > size || index < 0) {
            return null;
        }
        return items[index];
    }

    @Override
    public T getRecursive(int index) {
        throw new UnsupportedOperationException("No need to implement getRecursive for proj 1b");
    }
}
