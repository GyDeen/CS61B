import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.lang.Math;


public class ArrayDeque61B<T> implements Deque61B<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int front;
    private int back;

    private static final int INITIAL_CAPACITY = 8;
    private static final int INITIAL_FRONT = 4;
    private static final int INITIAL_BACK = 5;


    public Iterator<T> iterator() {
        return new ArrayDeque61BIterator();
    }


    private class ArrayDeque61BIterator implements Iterator<T> {
        private int index = Math.floorMod(front, items.length);
        private int checkedItems = 0;

        @Override
        public boolean hasNext() {
            return checkedItems < size;
        }

        @Override
        public T next() {
            T returnItem = items[index];
            index = Math.floorMod(index + 1, items.length);
            checkedItems++;
            return returnItem;

        }
    }

    public ArrayDeque61B() {
        items = (T[]) new Object[INITIAL_CAPACITY];
        size = 0;
        front = INITIAL_FRONT;
        back = INITIAL_BACK;
    }

    // there is no enough space for new item, double current array
    public void resizeUp(int newSize) {
        T[] newItems = (T[]) new Object[newSize];

        int start = newSize / 4;

        // placing front at index 0 of the new array
        for (int i = 0; i < size / 2; i++) {
            newItems[newSize + i] = items[Math.floorMod(front + i, items.length)];
        }

        front = newSize - 1;
        back = size;
        items = newItems;

    }

    public void resizeDown(int newSize) {
        T[] newItems = (T[]) new Object[newSize];
        int newStart = newSize / 4;

        for (int i = 0; i <= size / 2; i++) {
            newItems[newStart + i] = items[Math.floorMod(front + i, items.length)];
        }

        front = newSize - 1;
        back = size;
        items = newItems;

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


    // need to handle cases such as addLast then removeFirst immediately
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        if (size == items.length / 4 && items.length > 16) {
            resizeDown(items.length / 2);
        }
        T removed = items[front + 1];

        while (removed == null && size != 0) {
            front = Math.floorMod(front + 1, items.length);
            removed = items[front + 1];
        }

        items[front + 1] = null;
        front = Math.floorMod(front + 1, items.length);
        size--;

        return removed;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }

        if (size == items.length / 4 && items.length > 16) {
            resizeDown(items.length / 2);
        }

        T removed = items[back - 1];

        while (removed == null && size != 0) {
            back = Math.floorMod(back - 1, items.length);
            removed = items[back - 1];
        }
        items[back - 1] = null;
        back = Math.floorMod(back - 1, items.length);
        size--;
        return removed;
    }

    @Override
    public T get(int index) {
        if (index > items.length - 1 || index < 0) {
            return null;
        }
        return items[index];
    }

    @Override
    public T getRecursive(int index) {
        throw new UnsupportedOperationException("No need to implement getRecursive for proj 1b");
    }
}
