public class MinEndBuildTime {
    public static int findMinimumConstructionTime(int[] constructionDurations, int splitExpense) {
        int totalDuration = 0;
        for (int duration : constructionDurations) {
            if (splitExpense + duration / 2 < duration) {
                totalDuration += splitExpense;
            } else {
                totalDuration += duration;
            }
        }
        return totalDuration;
    }

    public static void main(String[] args) {
        int[] constructionDurations = {1, 2, 3};
        int splitExpense = 1; // Cost to split construction time

        System.out.println("Minimum time to construct engines: " + findMinimumConstructionTime(constructionDurations, splitExpense));
    }
}