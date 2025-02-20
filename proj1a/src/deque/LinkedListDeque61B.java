package deque;

import java.util.List;

public class LinkedListDeque61B<T> implements Deque61B<T>{

    private class Node {
        T item;
        Node prev;
        Node next;

        // Constructor for Node class
        public Node(T item) {
            this.item = item;
            this.prev = null;
            this.next = null;
        }
    }

    private Node sentinal;
    private int size;

    public LinkedListDeque61B() {
        sentinal = new Node(null);
        sentinal.next = sentinal;
        sentinal.prev = sentinal;
        size = 0;
    }

    @Override
    public void addFirst(T x) {

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
