import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private BSTnode root;
    private int size;


    private class BSTnode {
        private K key;
        private V value;
        private BSTnode left;
        private BSTnode right;

        public BSTnode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    private class BSTnodeIterator implements Iterator<K> {

        private BSTnode nextNode;


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

        private BSTnode getNextNode(BSTnode node) {
            if (node == null) {
                return null;
            }

            // the next smallest is at right-tree left-most node
            if(node.right != null) {
                BSTnode smallestNode = node.right;
                while (smallestNode.left != null) {
                    smallestNode = smallestNode.left;
                }

                return smallestNode;
            }

            BSTnode successor = null;
            BSTnode  ancestor = root;

            while (ancestor != null) {
                int cmp = node.key.compareTo(ancestor.key);

                // currentNode is smaller than ancestor,
                // which means it may have a smaller node not being returned yet
                if (cmp < 0) {
                    successor = ancestor;
                    ancestor = ancestor.left;

                    // currentNode is bigger than ancestor, which means ancestor has being passed.
                    // we need to move to ancestor right
                } else if (cmp > 0) {
                    ancestor = ancestor.right;
                } else {
                    break;
                }
            }

            return successor;


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
        this.root = new BSTnode(key, value);
    }

    @Override
    public void put(K key, V value) {
        root = putHelper(root, key, value);
    }

    private BSTnode putHelper(BSTnode node, K key, V value) {
        if (node == null) {
            size++;
            return new BSTnode(key, value);
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

        BSTnode crtNode = root;

        if (crtNode == null) {
            return null;
        }

        while (crtNode != null) {
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
        BSTnode crtNode = root;

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
        Set<K> keys = new TreeSet<>();
        collectKeys(root, keys);
        return keys;
    }

    private void collectKeys(BSTnode node, Set<K> keys) {
        if (node == null) {
            return;
        }

        collectKeys(node.left, keys);
        keys.add(node.key);
        collectKeys(node.right, keys);
    }

    @Override
    public V remove(K key) {
        BSTnode removedNode = new BSTnode(null, null);
        root = removeHelper(root, key, removedNode);
        return removedNode.value;
    }

    private BSTnode removeHelper(BSTnode node, K key, BSTnode removedNode) {
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
            BSTnode smallChild = findSmallest(node.right); // use right-tree the smallest node to replace node
            node.key = smallChild.key;
            node.value = smallChild.value;
            node.right = removeHelper(node.right, smallChild.key,  new BSTnode(null, null)); // remove the right-tree smallest node
        }

        return node;
    }

    // Find the smallest node at given tree
    private BSTnode findSmallest(BSTnode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }



}
