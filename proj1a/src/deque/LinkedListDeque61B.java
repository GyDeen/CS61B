package deque;

import java.util.LinkedList;
import java.util.List;

public class LinkedListDeque61B<T> implements Deque61B<T> {

    private class Node {
        T item;
        Node prev;
        Node next;

        // Constructor for Node class
        public Node(T item, Node p) {
            this.item = item;
            this.prev = p;
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

        new_node.next = sentinel.next;
        sentinel.next = new_node;
        new_node.next.prev = new_node; // updating original node

        this.size++; //updating the size
    }

    @Override
    public void addLast(T x) {
        Node new_node = new Node(x, sentinel.prev);

        sentinel.prev = new_node;
        new_node.next = sentinel;
        sentinel.prev.prev.next = new_node;

        this.size++;
    }

    @Override
    public List<T> toList() {
        List<T> new_list = new LinkedList<>();

        Node current = sentinel.next;

        // add item to the list except the sentinel (last_node.next == sentinel)
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

        // when there is no more node
        if (sentinel.next == sentinel) {
            return null;
        }

        Node remove_node = sentinel.next;

        sentinel.next = remove_node.next;
        remove_node.next.prev = sentinel;

        this.size--;
        return remove_node.item;
    }

    @Override
    public T removeLast() {

        // when there is no more node
        if (sentinel.next == sentinel) {
            return null;
        }

        Node remove_node = sentinel.prev;

        sentinel.prev = remove_node.prev;
        remove_node.prev.next = sentinel;

        this.size--;
        return remove_node.item;
    }

    @Override
    public T get(int index) {

        int i = 0;

        Node crt_node = sentinel.next;
        while (i != index) {
            // if the index is out of bound, return null
            if (crt_node == sentinel) {
                return null;
            }

            // doesn't reach the target node
            crt_node = crt_node.next;
            i++;
        }

        return crt_node.item;

    }

    @Override
    public T getRecursive(int index) {

        if (sentinel.next == sentinel) {
            return null;
        }

        return getRecursiveHelper(index, sentinel.next);
    }

    private T getRecursiveHelper(int index, Node currentNode) {
        if (index == 0) {
            return currentNode.item;  // get the item
        }

        if (currentNode.next != sentinel) {
            return getRecursiveHelper(index - 1, currentNode.next);
        }

        return null; // reach sentinel without getting the desire index
    }

}

