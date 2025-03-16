import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.fail;

public class UnionFindTest {

    /**
     * Checks that the initial state of the disjoint sets are correct (this will pass with the skeleton
     * code, but ensure it still passes after all parts are implemented).
     */
    @Test
    public void initialStateTest() {
        UnionFind uf = new UnionFind(4);
        assertThat(uf.connected(0, 1)).isFalse();
        assertThat(uf.connected(0, 2)).isFalse();
        assertThat(uf.connected(0, 3)).isFalse();
        assertThat(uf.connected(1, 2)).isFalse();
        assertThat(uf.connected(1, 3)).isFalse();
        assertThat(uf.connected(2, 3)).isFalse();
    }

    /**
     * Checks that invalid inputs are handled correctly.
     */
    @Test
    public void illegalFindTest() {
        UnionFind uf = new UnionFind(4);
        try {
            uf.find(10);
            fail("Cannot find an out of range vertex!");
        } catch (IllegalArgumentException e) {
            return;
        }
        try {
            uf.union(1, 10);
            fail("Cannot union with an out of range vertex!");
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    /**
     * Checks that union is done correctly (including the tie-breaking scheme).
     */
    @Test
    public void basicUnionTest() {
        UnionFind uf = new UnionFind(10);
        uf.union(0, 1);
        assertThat(uf.find(0)).isEqualTo(1);
        uf.union(2, 3);
        assertThat(uf.find(2)).isEqualTo(3);
        uf.union(0, 2);
        assertThat(uf.find(1)).isEqualTo(3);

        uf.union(4, 5);
        uf.union(6, 7);
        uf.union(8, 9);
        uf.union(4, 8);
        uf.union(4, 6);

        assertThat(uf.find(5)).isEqualTo(9);
        assertThat(uf.find(7)).isEqualTo(9);
        assertThat(uf.find(8)).isEqualTo(9);

        uf.union(9, 2);
        assertThat(uf.find(3)).isEqualTo(9);
    }

    /**
     * Unions the same item with itself. Calls on find and checks that the outputs are correct.
     */
    @Test
    public void sameUnionTest() {
        UnionFind uf = new UnionFind(4);
        uf.union(1, 1);
        for (int i = 0; i < 4; i += 1) {
            assertThat(uf.find(i)).isEqualTo(i);
        }
    }

    @Test
    public void testFind() {
        UnionFind uf = new UnionFind(10);
        uf.union(3, 8);
        uf.union(3, 0);
        uf.union(3, 4);

        assertThat(uf.find(4)).isEqualTo(8);
    }

    /**
     * Test that initially, each element is in its own set of size 1.
     */
    @Test
    public void testInitialSetSizes() {
        int n = 5;
        UnionFind uf = new UnionFind(n);
        for (int i = 0; i < n; i++) {
            assertThat(uf.sizeOf(i)).isEqualTo(1);
            // Each element should be its own representative initially.
            assertThat(uf.find(i)).isEqualTo(i);
        }
    }

    /**
     * Test basic unions and verify that connectivity and size updates are correct.
     */
    @Test
    public void testUnionAndSizeUpdates() {
        UnionFind uf = new UnionFind(6);
        // Union a couple of pairs and check sizes.
        uf.union(0, 1);
        assertThat(uf.connected(0, 1)).isTrue();
        assertThat(uf.sizeOf(0)).isEqualTo(2);
        uf.union(2, 3);
        assertThat(uf.sizeOf(2)).isEqualTo(2);

        // Union the two sets.
        uf.union(1, 2);
        // Now 0, 1, 2, 3 should all be connected.
        assertThat(uf.connected(0, 3)).isTrue();
        // The size should reflect the total elements.
        assertThat(uf.sizeOf(0)).isEqualTo(4);

        // Union remaining elements.
        uf.union(4, 5);
        assertThat(uf.sizeOf(4)).isEqualTo(2);
    }

    /**
     * Test that unioning an element with itself does nothing.
     */
    @Test
    public void testSelfUnion() {
        UnionFind uf = new UnionFind(4);
        uf.union(2, 2);
        // The find of each element should remain unchanged.
        for (int i = 0; i < 4; i++) {
            assertThat(uf.find(i)).isEqualTo(i);
        }
    }

    /**
     * Test that invalid inputs throw IllegalArgumentException.
     */
    @Test
    public void testIllegalInput() {
        UnionFind uf = new UnionFind(3);
        try {
            uf.find(-1);
            fail("Expected IllegalArgumentException for find(-1)");
        } catch (IllegalArgumentException e) {
            // Expected exception.
        }
        try {
            uf.union(0, 3);
            fail("Expected IllegalArgumentException for union(0, 3)");
        } catch (IllegalArgumentException e) {
            // Expected exception.
        }
    }

    /**
     * Test a more complex union structure.
     */
    @Test
    public void testComplexUnionStructure() {
        UnionFind uf = new UnionFind(10);
        // Create several unions to form multiple groups.
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(3, 4);
        uf.union(5, 6);
        uf.union(7, 8);
        uf.union(8, 9);
        // Merge groups.
        uf.union(2, 3);
        uf.union(6, 7);
        // Now merge the two large groups.
        uf.union(4, 5);

        // Verify that all elements now share the same root.
        int root = uf.find(0);
        for (int i = 1; i < 10; i++) {
            assertThat(uf.find(i)).isEqualTo(root);
        }
        // And the overall size should be 10.
        assertThat(uf.sizeOf(0)).isEqualTo(10);
    }

}


