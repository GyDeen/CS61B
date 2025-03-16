import net.sf.saxon.z.IntArraySet;

import java.util.ArrayList;

public class UnionFind {
    private final ArrayList<Integer> union;
    private final int size;

    /* Creates a UnionFind data structure holding N items. Initially, all
       items are in disjoint sets. */
    public UnionFind(int N) {
        union = new ArrayList<>(N);

        for (int i = 0;i < N; i++) {
            union.add(i, -1);
        }

        size = N;
    }

    /* Returns the parent of V. If V is the root of a tree, returns the
   negative size of the tree for which V is the root. */
    public int parent(int v) {
        return union.get(v);
    }

    /* Returns the size of the set V belongs to. */
    public int sizeOf(int v) {
        int root = find(v);
        return -union.get(root);
    }

    public int size() {
        return size;
    }

    /* Returns true if nodes/vertices V1 and V2 are connected. */
    public boolean connected(int v1, int v2) {
        return find(v1) == find(v2);
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. If invalid items are passed into this
       function, throw an IllegalArgumentException. */
    public int find(int v) {
        if (v < 0 || v > size - 1) {
            throw new IllegalArgumentException("Input out of set index");
        }

        ArrayList<Integer> path = new ArrayList<Integer>();
        int current = v;

        while (union.get(parent(current)) > 0) {
            path.add(current);
            current = parent(current);
        }

        int sizeOfPath = path.size();
        for (int toAssignRoot : path) {
            union.set(toAssignRoot, current);
        }

        return current;
    }

    /* Connects two items V1 and V2 together by connecting their respective
       sets. V1 and V2 can be any element, and a union-by-size heuristic is
       used. If the sizes of the sets are equal, tie break by connecting V1's
       root to V2's root. Union-ing an item with itself or items that are
       already connected should not change the structure. */
    public void union(int v1, int v2) {
        if (connected(v1, v2)) {
            return;
        }

        int root1 = find(v1), root2 = find(v2);
        int size1 = sizeOf(v1), size2 = sizeOf(v2);

        if (size1 > size2) {
            union.set(root2, root1);
        } else {
            union.set(root1, root2);
        }

    }

}
