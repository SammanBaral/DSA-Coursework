import java.util.*;

class Edge implements Comparable<Edge> {
    int source, destination, weight;

    public Edge(int source, int destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge otherEdge) {
        return Integer.compare(this.weight, otherEdge.weight);
    }
}

public class KruskalAlgorithm {

    private int vertices;
    private List<Edge> edges;

    public KruskalAlgorithm(int vertices) {
        this.vertices = vertices;
        this.edges = new ArrayList<>();
    }

    public void addEdge(int source, int destination, int weight) {
        Edge edge = new Edge(source, destination, weight);
        edges.add(edge);
    }

    private int find(int[] parent, int vertex) {
        if (parent[vertex] == -1)
            return vertex;
        return find(parent, parent[vertex]);
    }

    private void union(int[] parent, int x, int y) {
        int xRoot = find(parent, x);
        int yRoot = find(parent, y);
        parent[xRoot] = yRoot;
    }

    public List<Edge> kruskalMST() {
        List<Edge> result = new ArrayList<>();

        // Sort the edges in ascending order based on weights
        Collections.sort(edges);

        int[] parent = new int[vertices];
        Arrays.fill(parent, -1);

        for (Edge edge : edges) {
            int x = find(parent, edge.source);
            int y = find(parent, edge.destination);

            if (x != y) {
                result.add(edge);
                union(parent, x, y);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int vertices = 4;
        KruskalAlgorithm graph = new KruskalAlgorithm(vertices);

        // Adding edges with weights
        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 2, 6);
        graph.addEdge(0, 3, 5);
        graph.addEdge(1, 3, 15);
        graph.addEdge(2, 3, 4);

        List<Edge> result = graph.kruskalMST();

        System.out.println("Edges in the Minimum Spanning Tree (MST):");
        for (Edge edge : result) {
            System.out.println(edge.source + " - " + edge.destination + " Weight: " + edge.weight);
        }
    }
}
