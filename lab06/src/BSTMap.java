import java.util.Iterator;
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
        root = removeHelper(root, key);
    }

    private BSTnode<K, V> removeHelper(BSTnode<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = removeHelper(node.left, key);
        } else if (cmp > 0) {
            node.right = removeHelper(node.right, key);
        } else { // find the target node
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
            node.right = removeHelper(node.right, smallChild.key); // remove the right-tree smallest node
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


        @Override
    public Iterator<K> iterator() {
        return null;
    }
}
