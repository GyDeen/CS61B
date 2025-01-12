import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListExercises {

    /** Returns the total sum in a list of integers */
	public static int sum(List<Integer> L) {
        int length = L.size(), total = 0;
        for (Integer integer : L) {
            total += integer;
        }
        return total;
    }

    /** Returns a list containing the even numbers of the given list */
    public static List<Integer> evens(List<Integer> L) {
        List<Integer> even_list = new ArrayList<>();
        for (int num : L){
            if (num % 2 == 0) {
                even_list.add(num);
            }
        }
        return even_list;
    }

    /** Returns a list containing the common item of the two given lists */
    public static List<Integer> common(List<Integer> L1, List<Integer> L2) {
        Set<Integer> set2 = new HashSet<>(L2);
        Set<Integer> common_set = new HashSet<>();
        for (int num : L1) {
            if (set2.contains(num)) {
                common_set.add(num);
            }
        }
                return new ArrayList<>(common_set);
    }


    /** Returns the number of occurrences of the given character in a list of strings. */
    public static int countOccurrencesOfC(List<String> words, char c) {
        // TODO: Fill in this function.
        int times = 0;
        for (String str : words){
            char[] chars = str.toCharArray();
            for (char ch : chars){
                if (ch == c){
                    times++;
                }
            }
        }
        return times;
    }
}
