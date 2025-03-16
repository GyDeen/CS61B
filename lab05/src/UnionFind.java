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

        // It is root. Returns itself
        if (union.get(v) < 0) {
            return v;
        }

        ArrayList<Integer> path = new ArrayList<>();
        int current = v;

        while (union.get(parent(current)) > 0) {
            path.add(current);
            current = parent(current);
        }

        for (int toAssignRoot : path) {
            union.set(toAssignRoot, current);
        }

        return union.get(current);
    }

    /* Connects two items V1 and V2 together by connecting their respective
       sets. V1 and V2 can be any element, and a union-by-size heuristic is
       used. If the sizes of the sets are equal, tie break by connecting V1's
       root to V2's root. Union-ing an item with itself or items that are
       already connected should not change the structure. */
    public void union(int v1, int v2) {
        int root1 = find(v1);
        int root2 = find(v2);

        // If they're already in the same set, do nothing.
        if (root1 == root2) {
            return;
        }

        // Now, instead of calling sizeOf(v1) and sizeOf(v2),
        // use the fact that the root stores the negative size.
        int size1 = -union.get(root1);
        int size2 = -union.get(root2);

        // Tie breaking by size:
        if (size1 > size2) {
            union.set(root2, root1);
            union.set(root1, -(size1 + size2));
        } else {
            union.set(root1, root2);
            union.set(root2, -(size1 + size2));
        }
    }


}
