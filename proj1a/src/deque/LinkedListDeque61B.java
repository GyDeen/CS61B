package deque;

import java.util.LinkedList;
import java.util.List;

public class LinkedListDeque61B<T> implements Deque61B<T> {

    private class Node {
        T item;
        Node prev;
        Node next;

        // Constructor for Node class
        public Node(T item, Node n) {
            this.item = item;
            this.prev = n;
            this.next = null;
        }
    }

    private final Node sentinel;
    private int size;

    public LinkedListDeque61B() {
        sentinel = new Node(null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }


    @Override
    public void addFirst(T x) {
        Node new_node = new Node(x, sentinel);
        Node current_node = sentinel.next;

        sentinel.next = new_node;
        new_node.next = current_node;
        current_node.prev = new_node;

        this.size++;
    }

    @Override
    public void addLast(T x) {
        // Add last logic here
    }

    @Override
    public List<T> toList() {
        List<T> new_list = new LinkedList<>();

        Node current = sentinel.next;
        while (current != sentinel) {
            new_list.add(current.item);
            current = current.next;
        }

        return new_list;
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
        // Remove first logic here
        return null;
    }

    @Override
    public T removeLast() {
        // Remove last logic here
        return null;
    }

    @Override
    public T get(int index) {

        return null;
    }

    @Override
    public T getRecursive(int index) {
        // Recursive get logic here
        return null;
    }
}

