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
    public void resize(int size) {

    }


    @Override
    public void addFirst(T x) {
        if (size == items.length) {
            resize(size * 2);
        }
        front = Math.floorMod(front - 1, items.length);
        items[front] = x;
        size++;
    }

    @Override
    public void addLast(T x) {

    }

    @Override
    public List<T> toList() {
        return List.of();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public T removeFirst() {
        return null;
    }

    @Override
    public T removeLast() {
        return null;
    }

    @Override
    public T get(int index) {
        return null;
    }

    @Override
    public T getRecursive(int index) {
        return null;
    }
}
