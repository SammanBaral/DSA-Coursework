import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecretSharing {

    public static void main(String[] args) {
        // Given input data
        int n = 5;
        int[][] intervals = {{0, 2}, {1, 3}, {2, 4}};
        int firstPerson = 0;

        System.out.println(findSecretSet(n, intervals, firstPerson));
    }

    public static List<Integer> findSecretSet(int n, int[][] intervals, int firstPerson) {
        boolean[] knowsSecret = new boolean[n];
        Arrays.fill(knowsSecret, false);

        knowsSecret[firstPerson] = true;

        for (int[] interval : intervals) {
            int start = interval[0];
            int end = interval[1];

            for (int i = start; i <= end; i++) {
                if (knowsSecret[i]) {
                    // If a person in the interval already knows the secret, update others in the interval
                    for (int j = start; j <= end; j++) {
                        knowsSecret[j] = true;
                    }
                    break;
                }
            }
        }

        List<Integer> secretSet = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (knowsSecret[i]) {
                secretSet.add(i);
            }
        }

        return secretSet;
    }
}
