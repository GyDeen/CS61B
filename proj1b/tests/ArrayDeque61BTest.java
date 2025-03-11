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
        lld1.addLast(0);   // [0]
        lld1.addLast(1);   // [0, 1]
        lld1.addFirst(-1); // [-1, 0, 1]
        lld1.addLast(2);   // [-1, 0, 1, 2]
        lld1.addFirst(-2); // [-2, -1, 0, 1, 2]

        assertThat(lld1.toList()).containsExactly(-2, -1, 0, 1, 2).inOrder();
    }


    // Below, you'll write your own tests for LinkedListDeque61B.
    @Test
    public void testAddFirstAndRemoveFirst() {
        Deque61B<String> lld1 = new ArrayDeque61B<>();

        lld1.addFirst("back"); // after this call we expect: ["back"]

        lld1.addFirst("middle"); // after this call we expect: ["middle", "back"]

        lld1.addFirst("front"); // after this call we expect: ["front", "middle", "back"]


        lld1.removeFirst();
        assertThat(lld1.toList()).containsExactly("middle", "back").inOrder();
        assertThat(lld1.size()).isEqualTo(2);

        lld1.removeFirst();
        assertThat(lld1.toList()).containsExactly("back").inOrder();
        assertThat(lld1.size()).isEqualTo(1);

        lld1.removeFirst();
        assertThat(lld1.isEmpty()).isTrue();
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
        assertThat(lld1.toList()).containsExactly("front", "middle").inOrder();
        assertThat(lld1.size()).isEqualTo(2);

        lld1.removeLast();
        assertThat(lld1.toList()).containsExactly("front").inOrder();
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

        assertThat(lld1.get(0)).isEqualTo("front");
        assertThat(lld1.get(1)).isEqualTo("middle");
        assertThat(lld1.get(2)).isEqualTo("back");
    }

    @Test
    @DisplayName("Test whether get() works correctly with both remove() methods")
    public void testGetWithRemove() {
        Deque61B<Integer> lld1 = new ArrayDeque61B<>();

        lld1.addFirst(5);
        lld1.addFirst(3);
        lld1.addFirst(726);
        lld1.addFirst(512); // it should be [512, 726, 3, 5]

        assertThat(lld1.get(3)).isEqualTo(5);
        assertThat(lld1.get(1)).isEqualTo(726);

        lld1.removeLast(); // it should be [512, 726, 3]
        assertThat(lld1.get(3)).isEqualTo(null); // index out of bound, it should return null
        assertThat(lld1.get(0)).isEqualTo(512);
        assertThat(lld1.size()).isEqualTo(3);

        lld1.removeFirst(); // it should be [726, 3]
        assertThat(lld1.get(0)).isEqualTo(726);
        assertThat(lld1.get(1)).isEqualTo(3);
        assertThat(lld1.get(2)).isEqualTo(null); // index out of bound
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
        lld1.addLast(19.7); // it should be [20.04, 5.13, 7.26, 10.25, 19.7]

        assertThat(lld1.get(2)).isEqualTo(7.26);
        assertThat(lld1.get(5)).isEqualTo(null);
        assertThat(lld1.get(0)).isEqualTo(20.04);
        assertThat(lld1.size()).isEqualTo(5);

        lld1.removeFirst(); // it should be [5.13, 7.26, 10.25, 19.7]
        assertThat(lld1.get(3)).isEqualTo(19.7);
        assertThat(lld1.get(4)).isEqualTo(null);
        assertThat(lld1.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("Test whether getRecursively() works correctly")
    public void testGetRecursiveOnly() {
        Deque61B<String> lld1 = new ArrayDeque61B<>();

        lld1.addFirst("back"); // after this call we expect: ["back"]

        lld1.addFirst("middle"); // after this call we expect: ["middle", "back"]

        lld1.addFirst("front"); // after this call we expect: ["front", "middle", "back"]

        assertThat(lld1.getRecursive(0)).isEqualTo("front");
        assertThat(lld1.getRecursive(1)).isEqualTo("middle");
        assertThat(lld1.getRecursive(2)).isEqualTo("back");
    }

    @Test
    @DisplayName("Test whether getRecursively() works correctly with both remove() methods")
    public void testGetRecursiveWithRemove() {
        Deque61B<Integer> lld1 = new ArrayDeque61B<>();

        lld1.addFirst(5);
        lld1.addFirst(3);
        lld1.addFirst(726);
        lld1.addFirst(512); // it should be [512, 726, 3, 5]

        assertThat(lld1.get(3)).isEqualTo(5);
        assertThat(lld1.get(1)).isEqualTo(726);

        lld1.removeLast(); // it should be [512, 726, 3]
        assertThat(lld1.getRecursive(3)).isEqualTo(null); // index out of bound, it should return null
        assertThat(lld1.getRecursive(0)).isEqualTo(512);
        assertThat(lld1.size()).isEqualTo(3);

        lld1.removeFirst(); // it should be [726, 3]
        assertThat(lld1.getRecursive(0)).isEqualTo(726);
        assertThat(lld1.getRecursive(1)).isEqualTo(3);
        assertThat(lld1.getRecursive(2)).isEqualTo(null); // index out of bound
        assertThat(lld1.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test whether getRecursively() works correctly with both remove and add after constructed queue")
    public void testGetRecursiveWithAddFirstAndLastWithRemove() {
        Deque61B<Double> lld1 = new ArrayDeque61B<>();

        lld1.addFirst(5.13);
        lld1.addLast(7.26);
        lld1.addLast(10.25);
        lld1.addFirst(20.04);
        lld1.addLast(19.7); // it should be [20.04, 5.13, 7.26, 10.25, 19.7]

        assertThat(lld1.getRecursive(2)).isEqualTo(7.26);
        assertThat(lld1.getRecursive(5)).isEqualTo(null);
        assertThat(lld1.getRecursive(0)).isEqualTo(20.04);
        assertThat(lld1.size()).isEqualTo(5);

        lld1.removeFirst(); // it should be [5.13, 7.26, 10.25, 19.7]
        assertThat(lld1.getRecursive(3)).isEqualTo(19.7);
        assertThat(lld1.getRecursive(4)).isEqualTo(null);
        assertThat(lld1.size()).isEqualTo(4);
    }
}


