import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AntColony {

    private double[][] distances;
    private double[][] pheromones;
    private int numAnts;
    private double decay;
    private double alpha;
    private double beta;

    public AntColony(double[][] distances, int numAnts, double decay, double alpha, double beta) {
        this.distances = distances;
        this.numAnts = numAnts;
        this.decay = decay;
        this.alpha = alpha;
        this.beta = beta;

        int numCities = distances.length;
        pheromones = new double[numCities][numCities];

        initializePheromones();
    }

    private void initializePheromones() {
        int numCities = distances.length;
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                pheromones[i][j] = 1.0;
            }
        }
    }

    public int[] solve(int numIterations) {
        int[] bestTour = null;
        double bestTourLength = Double.POSITIVE_INFINITY;

        for (int iteration = 0; iteration < numIterations; iteration++) {
            int[][] antTours = generateAntTours();

            updatePheromones(antTours);

            for (int ant = 0; ant < numAnts; ant++) {
                double tourLength = tourLength(antTours[ant]);
                if (tourLength < bestTourLength) {
                    bestTourLength = tourLength;
                    bestTour = antTours[ant].clone();
                }
            }

            decayPheromones();
        }

        return bestTour;
    }

    private int[][] generateAntTours() {
        int[][] antTours = new int[numAnts][];

        for (int ant = 0; ant < numAnts; ant++) {
            antTours[ant] = generateAntTour();
        }

        return antTours;
    }

    private int[] generateAntTour() {
        int numCities = distances.length;
        int startCity = new Random().nextInt(numCities);

        int[] tour = new int[numCities];
        boolean[] visited = new boolean[numCities];
        tour[0] = startCity;
        visited[startCity] = true;

        for (int i = 1; i < numCities; i++) {
            int nextCity = selectNextCity(tour, visited);
            tour[i] = nextCity;
            visited[nextCity] = true;
        }

        return tour;
    }

    private int selectNextCity(int[] tour, boolean[] visited) {
        int numCities = distances.length;
        int currentCity = tour[tour.length - 1];

        List<Integer> possibleCities = new ArrayList<>();

        for (int i = 0; i < numCities; i++) {
            if (!visited[i]) {
                possibleCities.add(i);
            }
        }

        double[] probabilities = calculateProbabilities(currentCity, possibleCities);

        return selectCityWithProbability(probabilities, possibleCities);
    }

    private double[] calculateProbabilities(int currentCity, List<Integer> possibleCities) {
        int numPossibleCities = possibleCities.size();
        double[] probabilities = new double[numPossibleCities];
        double sum = 0.0;

        for (int i = 0; i < numPossibleCities; i++) {
            int nextCity = possibleCities.get(i);
            probabilities[i] = Math.pow(pheromones[currentCity][nextCity], alpha)
                    * Math.pow(1.0 / distances[currentCity][nextCity], beta);
            sum += probabilities[i];
        }

        for (int i = 0; i < numPossibleCities; i++) {
            probabilities[i] /= sum;
        }

        return probabilities;
    }

    private int selectCityWithProbability(double[] probabilities, List<Integer> possibleCities) {
        double rand = Math.random();
        double cumulativeProbability = 0.0;

        for (int i = 0; i < probabilities.length; i++) {
            cumulativeProbability += probabilities[i];
            if (rand <= cumulativeProbability) {
                return possibleCities.get(i);
            }
        }

        // This should not happen, but just in case
        return possibleCities.get(possibleCities.size() - 1);
    }

    private void updatePheromones(int[][] antTours) {
        int numCities = distances.length;

        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                pheromones[i][j] *= decay;
            }
        }

        for (int ant = 0; ant < numAnts; ant++) {
            double pheromoneDelta = 1.0 / tourLength(antTours[ant]);

            for (int i = 0; i < numCities - 1; i++) {
                int city1 = antTours[ant][i];
                int city2 = antTours[ant][i + 1];
                pheromones[city1][city2] += pheromoneDelta;
                pheromones[city2][city1] += pheromoneDelta;
            }
        }
    }

    private double tourLength(int[] tour) {
        double length = 0.0;
        int numCities = distances.length;

        for (int i = 0; i < numCities - 1; i++) {
            int city1 = tour[i];
            int city2 = tour[i + 1];
            length += distances[city1][city2];
        }

        // Add distance from the last city back to the starting city
        int lastCity = tour[numCities - 1];
        int startCity = tour[0];
        length += distances[lastCity][startCity];

        return length;
    }

    private void decayPheromones() {
        int numCities = distances.length;

        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                pheromones[i][j] *= decay;
            }
        }
    }

    public static void main(String[] args) {
        // Example usage
        double[][] distances = {
                {0, 2, 9, 10},
                {1, 0, 6, 4},
                {15, 7, 0, 8},
                {6, 3, 12, 0}
        };

        int numAnts = 10;
        double decay = 0.95;
        double alpha = 1;
        double beta = 2;

        AntColony antColony = new AntColony(distances, numAnts, decay, alpha, beta);
        int[] bestTour = antColony.solve(1000);

        System.out.print("Best Tour: ");
        for (int city : bestTour) {
            System.out.print(city + " ");
        }
    }
}
