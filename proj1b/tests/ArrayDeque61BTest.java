import jh61b.utils.Reflection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class ArrayDeque61BTest {

         @Test
         @DisplayName("ArrayDeque61B has no fields besides backing array and primitives")
         void noNonTrivialFields() {
             List<Field> badFields = Reflection.getFields(ArrayDeque61B.class)
                     .filter(f -> !(f.getType().isPrimitive() || f.getType().equals(Object[].class) || f.isSynthetic()))
                     .toList();

             assertWithMessage("Found fields that are not array or primitives").that(badFields).isEmpty();
         }

    @Test
    /* In this test, we have three different assert statements that verify that addFirst works correctly. */
    public void addFirstTestBasic() {
        Deque61B<String> lld1 = new ArrayDeque61B<>();

        lld1.addFirst("back"); // after this call we expect: ["back"]
        assertThat(lld1.toList()).containsExactly(null,null,null,null,"back",null,null,null).inOrder();

        lld1.addFirst("middle"); // after this call we expect: ["middle", "back"]
        assertThat(lld1.toList()).containsExactly(null,null,null,"middle", "back",null,null,null).inOrder();

        lld1.addFirst("front"); // after this call we expect: ["front", "middle", "back"]
        assertThat(lld1.toList()).containsExactly(null,null,"front", "middle", "back",null,null,null).inOrder();

         /* Note: The first two assertThat statements aren't really necessary. For example, it's hard
            to imagine a bug in your code that would lead to ["front"] and ["front", "middle"] failing,
            but not ["front", "middle", "back"].
          */
    }

    @Test
    public void addLastTestBasic() {
        Deque61B<String> lld1 = new ArrayDeque61B<>();

        lld1.addLast("front"); // after this call we expect: ["front"]
        lld1.addLast("middle"); // after this call we expect: ["front", "middle"]
        lld1.addLast("back"); // after this call we expect: ["front", "middle", "back"]
        assertThat(lld1.toList()).containsExactly(null,null,null,null,null,"front","middle","back").inOrder();
    }

    @Test
    /* This test performs interspersed addFirst and addLast calls. */
    public void addFirstAndAddLastTest() {
        Deque61B<Integer> lld1 = new ArrayDeque61B<>();

         /* I've decided to add in comments the state after each call for the convenience of the
            person reading this test. Some programmers might consider this excessively verbose. */
        lld1.addLast(0);
        lld1.addLast(1);
        lld1.addFirst(-1);
        lld1.addLast(2);
        lld1.addFirst(-2);
        lld1.addLast(3);

        assertThat(lld1.toList()).containsExactly(3,null,null,-2,-1,0,1,2).inOrder();
    }


    // Below, you'll write your own tests for LinkedListDeque61B.
    @Test
    public void testAddFirstAndRemoveFirst() {
        Deque61B<String> lld1 = new ArrayDeque61B<>();

        lld1.addFirst("back"); // after this call we expect: ["back"]

        lld1.addFirst("middle"); // after this call we expect: ["middle", "back"]

        lld1.addFirst("front"); // after this call we expect: ["front", "middle", "back"]


        lld1.removeFirst();
        assertThat(lld1.toList()).containsExactly(null,null,null,"middle", "back",null,null,null).inOrder();
        assertThat(lld1.size()).isEqualTo(2);

        lld1.removeFirst();
        assertThat(lld1.toList()).containsExactly(null,null,null,null, "back",null,null,null).inOrder();
        assertThat(lld1.size()).isEqualTo(1);

        lld1.removeFirst();
        assertThat(lld1.toList()).containsExactly(null,null,null,null,null,null,null,null).inOrder();
        assertThat(lld1.size()).isEqualTo(0);

        // edge case: already empty
        lld1.removeFirst();
        assertThat(lld1.isEmpty()).isTrue();
        assertThat(lld1.size()).isEqualTo(0);

    }


    @Test
    public void testMultiple () {
        Deque61B<String> lld1 = new ArrayDeque61B<>();

        lld1.addFirst("back"); // after this call we expect: ["back"]

        lld1.addFirst("middle"); // after this call we expect: ["middle", "back"]

        lld1.addFirst("front"); // after this call we expect: ["front", "middle", "back"]

        lld1.removeLast();
        assertThat(lld1.toList()).containsExactly(null, null, "front", "middle",null, null, null, null).inOrder();
        assertThat(lld1.size()).isEqualTo(2);

        lld1.removeLast();
        assertThat(lld1.toList()).containsExactly(null, null, "front", null, null, null, null, null).inOrder();
        assertThat(lld1.size()).isEqualTo(1);

        lld1.removeLast();
        assertThat(lld1.isEmpty()).isTrue();
        assertThat(lld1.size()).isEqualTo(0);

        // edge case: already empty
        lld1.removeLast();
        assertThat(lld1.isEmpty()).isTrue();
        assertThat(lld1.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Test whether get() works correctly")
    public void testGetOnly() {
        Deque61B<String> lld1 = new ArrayDeque61B<>();

        lld1.addFirst("back"); // after this call we expect: ["back"]

        lld1.addFirst("middle"); // after this call we expect: ["middle", "back"]

        lld1.addFirst("front"); // after this call we expect: ["front", "middle", "back"]

        assertThat(lld1.get(4)).isEqualTo("back");
        assertThat(lld1.get(3)).isEqualTo("middle");
        assertThat(lld1.get(2)).isEqualTo("front");
    }

    @Test
    @DisplayName("Test whether get() works correctly with both remove() methods")
    public void testGetWithRemove() {
        Deque61B<Integer> lld1 = new ArrayDeque61B<>();

        lld1.addFirst(5);
        lld1.addFirst(3);
        lld1.addFirst(726);
        lld1.addFirst(512); // it should be [null, 512, 726, 3, 5, null, null, null]

        assertThat(lld1.get(4)).isEqualTo(5);
        assertThat(lld1.get(1)).isEqualTo(512);

        lld1.removeLast(); // it should be [null, 512, 726, 3, null, null, null, null]
        assertThat(lld1.get(4)).isEqualTo(null); // index out of bound, it should return null
        assertThat(lld1.get(1)).isEqualTo(512);
        assertThat(lld1.size()).isEqualTo(3);

        lld1.removeFirst(); // it should be [null, null, 726, 3, null, null, null, null]
        assertThat(lld1.get(3)).isEqualTo(3);
        assertThat(lld1.get(1)).isEqualTo(null);
        assertThat(lld1.get(2)).isEqualTo(726);
        assertThat(lld1.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test whether get() works correctly with both remove and add after constructed queue")
    public void testGetWithAddFirstAndLastWithRemove() {
        Deque61B<Double> lld1 = new ArrayDeque61B<>();

        lld1.addFirst(5.13);
        lld1.addLast(7.26);
        lld1.addLast(10.25);
        lld1.addFirst(20.04);
        lld1.addLast(19.7); // it should be [ null, null, null, 20.04, 5.13, 7.26, 10.25, 19.7]

        assertThat(lld1.get(5)).isEqualTo(7.26);
        assertThat(lld1.get(2)).isEqualTo(null);
        assertThat(lld1.get(3)).isEqualTo(20.04);
        assertThat(lld1.size()).isEqualTo(5);

        lld1.removeFirst(); // it should be [ null, null, null, null, 5.13, 7.26, 10.25, 19.7]
        assertThat(lld1.get(3)).isEqualTo(null);
        assertThat(lld1.get(4)).isEqualTo(5.13);
        assertThat(lld1.size()).isEqualTo(4);
    }

    /**
     * Test resizing up when exceeding capacity
     */
    @Test
    @DisplayName("Test that resizeUp() doubles capacity and maintains order")
    public void testResizeUp() {
        Deque61B<Integer> deque = new ArrayDeque61B<>();

        for (int i = 0; i < 8; i++) {
            deque.addLast(i);
        }

        // Now adding another item should trigger resizeUp()
        deque.addLast(8);
        deque.addLast(9);

        // Check new size
        assertThat(deque.size()).isEqualTo(10);

        // Check that all items remain in the correct order after resizing
        for (int i = 0; i < 10; i++) {
            assertThat(deque.get(4 + i)).isEqualTo(i);
        }
    }

    /**
     * Test resizing down when items are removed
     */
    @Test
    @DisplayName("Test that resizeDown() shrinks capacity when usage is low")
    public void testResizeDown() {
        Deque61B<Integer> deque = new ArrayDeque61B<>();

        // Add elements beyond capacity to ensure resizing up happens
        for (int i = 0; i < 32; i++) {
            deque.addLast(i);
        }

        // Remove most elements to trigger resizeDown()
        for (int i = 0; i < 25; i++) {
            deque.removeFirst();
        }

        // Ensure size has updated correctly
        assertThat(deque.size()).isEqualTo(7);

        // Ensure items are still accessible correctly
        for (int i = 25; i < 32; i++) {
            assertThat(deque.get(5 + i - 25)).isEqualTo(i);
        }
    }

    /**
     * Test alternating add and remove operations while resizing
     */
    @Test
    @DisplayName("Test alternating adds and removes with resizing")
    public void testResizeWithAlternatingOperations() {
        Deque61B<Integer> deque = new ArrayDeque61B<>();

        // Add items to fill the deque and trigger resize
        for (int i = 0; i < 12; i++) {
            if (i % 2 == 0) {
                deque.addFirst(i);
            } else {
                deque.addLast(i);
            }
        }

        assertThat(deque.size()).isEqualTo(12);

        // Remove half of the items
        for (int i = 0; i < 6; i++) {
            deque.removeFirst();
            deque.removeLast();
        }

        assertThat(deque.size()).isEqualTo(6);

        // Add more elements to trigger another resize
        for (int i = 12; i < 18; i++) {
            deque.addLast(i);
        }

        // Ensure deque maintains correct ordering
        assertThat(deque.size()).isEqualTo(12);
        assertThat(deque.get(4)).isNotNull();
    }

    /**
     * Test adding and removing all elements multiple times to force resizing
     */
    @Test
    @DisplayName("Test resizeUp and resizeDown multiple times with add/remove operations")
    public void testRepeatedResizing() {
        Deque61B<Integer> deque = new ArrayDeque61B<>();

        // Add and remove elements repeatedly to force resizing multiple times
        for (int i = 0; i < 5; i++) {
            // Fill the deque
            for (int j = 0; j < 16; j++) {
                deque.addLast(j);
            }

            assertThat(deque.size()).isEqualTo(16);

            // Remove all elements
            for (int j = 0; j < 16; j++) {
                deque.removeFirst();
            }

            assertThat(deque.size()).isEqualTo(0);
            assertThat(deque.isEmpty()).isTrue();
        }
    }
}


