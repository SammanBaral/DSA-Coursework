import java.util.*;

public class PowerOutage {

    public static List<Integer> getNodesWithOnlyTargetAsParent(int[][] edges, int target) {
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        Map<Integer, Integer> inDegreeCount = new HashMap<>();

        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            adjacencyList.putIfAbsent(from, new ArrayList<>());
            adjacencyList.get(from).add(to);
            inDegreeCount.put(to, inDegreeCount.getOrDefault(to, 0) + 1);
        }

        List<Integer> result = new ArrayList<>();
        depthFirstSearch(adjacencyList, inDegreeCount, target, target, result);

        return result;
    }

    private static void depthFirstSearch(Map<Integer, List<Integer>> adjacencyList, Map<Integer, Integer> inDegreeCount, int node, int target, List<Integer> result) {

        if (inDegreeCount.getOrDefault(node, 0) == 1 && node != target) {
            result.add(node);
        }

        // Recursively explore the children of the current node
        if (adjacencyList.containsKey(node)) {
            for (int child : adjacencyList.get(node)) {
                depthFirstSearch(adjacencyList, inDegreeCount, child, target, result);
            }
        }
    }

    public static void main(String[] args) {
        int[][] edges = { { 0, 1 }, { 0, 2 }, { 1, 3 }, { 1, 6 }, { 2, 4 }, { 4, 6 }, { 4, 5 }, { 5, 7 } };
        int target = 4;

        List<Integer> uniqueParents = getNodesWithOnlyTargetAsParent(edges, target);

        System.out.print("impacted devices " + target + ": {");
        for (int i = 0; i < uniqueParents.size(); i++) {
            System.out.print(uniqueParents.get(i));
            if (i < uniqueParents.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("}");
    }
}
