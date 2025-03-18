import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private BSTnode<K, V> root;
    private int size;


    private class BSTnode<K, V> {
        private K key;
        private V value;
        private BSTnode<K, V> left;
        private BSTnode<K, V> right;

        public BSTnode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    private class BSTnodeIterator implements Iterator<K> {

        private BSTnode<K, V> nextNode;


        public BSTnodeIterator() {
            this.nextNode = root;
            if (nextNode != null) {
                while (nextNode.left != null) {
                    nextNode = nextNode.left;
                }
            }
        }


        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            K nextKey = nextNode.key;
            nextNode = getNextNode(nextNode);
            return nextKey;
        }

        private BSTnode<K, V> getNextNode(BSTnode<K, V>node) {
            if(node.right != null) {
                BSTnode<K, V> smallestNode = node.right;
                while (smallestNode.left != null) {
                    smallestNode = smallestNode.left;
                }

                return smallestNode;
            }
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTnodeIterator();
    }

    public BSTMap() {
        root = null;
        size = 0;
    }

    public BSTMap(K key, V value) {
        this.root = new BSTnode<K, V>(key, value);
    }

    @Override
    public void put(K key, V value) {
        root = putHelper(root, key, value);
    }

    private BSTnode<K, V> putHelper(BSTnode<K, V>node, K key, V value) {
        if (node == null) {
            size++;
            return new BSTnode<K, V>(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = putHelper(node.left, key, value);
        } else if (cmp > 0) {
            node.right = putHelper(node.right, key, value);
        } else {
            node.value = value;
        }
        return node;
    }

    @Override
    public V get(K key) {

        BSTnode<K, V> crtNode = root;

        while (crtNode != null && crtNode.key != key) {
            if (key.compareTo(crtNode.key) == 0){
                return crtNode.value;
            } else if (key.compareTo(crtNode.key) < 0) {
                crtNode = crtNode.left;
            } else if(key.compareTo(crtNode.key) > 0) {
                crtNode = crtNode.right;
            }
        }

        return null;
    }

    @Override
    public boolean containsKey(K key) {
        BSTnode<K, V> crtNode = root;

        while (crtNode != null) {
            if (key.compareTo(crtNode.key) == 0) {
                return true;
            } else if (key.compareTo(crtNode.key) < 0) {
                crtNode = crtNode.left;
            } else if(key.compareTo(crtNode.key) > 0) {
                crtNode = crtNode.right;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        return Set.of();
    }

    @Override
    public V remove(K key) {
        BSTnode<K, V> removedNode = new BSTnode<>(null, null);
        root = removeHelper(root, key, removedNode);
        return removedNode.value;
    }

    private BSTnode<K, V> removeHelper(BSTnode<K, V> node, K key, BSTnode<K, V> removedNode) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = removeHelper(node.left, key, removedNode);
        } else if (cmp > 0) {
            node.right = removeHelper(node.right, key, removedNode);
        } else {// find the target node

            removedNode.key = node.key;
            removedNode.value = node.value;
            if (node.left == null ) {
                size--;
                return node.right;
            } else if(node.right == null) {
                size--;
                return node.left;
            }

            // there are two children
            BSTnode<K, V> smallChild = findSmallest(node.right); // use right-tree the smallest node to replace node
            node.key = smallChild.key;
            node.value = smallChild.value;
            node.right = removeHelper(node.right, smallChild.key,  new BSTnode<>(null, null)); // remove the right-tree smallest node
        }

        return node;
    }

    // Find the smallest node at given tree
    private BSTnode<K, V> findSmallest(BSTnode<K, V> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }



}
