public class EqualizeDresses {

    public static void main(String[] args) {
        // Given input data
        int[] dresses = {1, 0, 5};

        // Output the minimum number of moves needed
        System.out.println(minimumMovesToEqualize(dresses));
    }

    public static int minimumMovesToEqualize(int[] dresses) {
        int totalDresses = 0;
        int n = dresses.length;

        // Calculate the total number of dresses
        for (int dress : dresses) {
            totalDresses += dress;
        }

        // Check if equal distribution is possible
        if (totalDresses % n != 0) {
            return -1;
        }

        int targetDresses = totalDresses / n;
        int moves = 0;

        // Calculate the moves needed to equalize the dresses
        for (int i = 0; i < n - 1; i++) {
            int diff = dresses[i] - targetDresses;
            dresses[i + 1] += diff;
            moves += Math.abs(diff);
        }

        return moves;
    }
}
