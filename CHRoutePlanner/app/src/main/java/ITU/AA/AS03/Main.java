package ITU.AA.AS03;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {

    private static final long DEFAULT_SEED = 4263372046854775800L;
    private static final String DEFAULT_GRAPH = "denmark.graph";
    private static final int DEFAULT_REPETITIONS = 1000;

    public enum AlgorithmType { SIMPLE, EARLYSTOP, BIDIJKSTRA, INTERLEAVING, }

    private record Result(
        AlgorithmType type,
        boolean contracted,
        double meanDuration,
        double meanEdgesRelaxed,
        int found,
        int[] distances,
        double meanDistance
    ) {}

    /** Prepares an instance of a query algorithm on a given graph. 
     * Does not call calculate.
     */
    public static ShortestPathAlgorithm createAlgorithm(AlgorithmType type, IndexedGraph graph) {
        switch(type) {
            case SIMPLE:       return new DijkstraSimple(graph);
            case EARLYSTOP:    return new DijkstraEarlyStop(graph);
            case INTERLEAVING: return new DijkstraInterleaving(graph);
            case BIDIJKSTRA:   return new DijkstraBidirectional(graph);
            default: throw new IllegalArgumentException(
                "First argument must be one of: " + Arrays.asList(AlgorithmType.values()));
        }
    }

    /** Run an experiment on the given algorithm and graph.
     * Tries {@code pairNum} random querie (random source and target node).
     * Prints to standard out the mean time taken and edges relaxed.
     * @param type The algorithm type to test.
     * @param graph The graph to perform the queires on. Will not be modified.
     * @param seed for the random generator, for reproducibility.
     * @return the resulting distances for comparison / correctness check.
     */
    public static Result randomPairExperiment(AlgorithmType type, IndexedGraph graph, int pairNums, long seed) {
        Random r = new Random(seed);
        int[] distances = new int[pairNums];
        int found = 0, range = graph.V();
        long startTime, endTime, totalTime = 0, totalEdgeRelax = 0;

        for (int i = 0; i < pairNums; i++) {
            int[] pair = new int[]{r.nextInt(range), r.nextInt(range)}; // Setup
            int source = pair[0], target = pair[1];
            ShortestPathAlgorithm sp = createAlgorithm(type, graph);

            startTime       = System.currentTimeMillis();               // Measure time
            if (sp.calculate(source, target)) found++;
            endTime         = System.currentTimeMillis();

            totalTime      += (endTime - startTime);                    // Collect data
            totalEdgeRelax += sp.relaxedEdges();
            distances[i]    = sp.distance();
        }

        double meanTime      = (double) totalTime / pairNums;
        double meanEdgeRelax = (double) totalEdgeRelax / pairNums;
        boolean isContracted = graph instanceof ContractedGraph;
        double meanDistance  = averageDistances(distances);

        return new Result(type, isContracted, meanTime, meanEdgeRelax, found, distances, meanDistance);
    }

    public static double averageDistances(int[] distances) {
        long x = 0;
        int l = distances.length;
        int valid = l;
        for (int i = 0; i < l; i++) {
            int y = distances[i];
            if (y < Integer.MAX_VALUE) x += y;
            else valid--;
        }
        if (valid == 0) throw new ArithmeticException("I WILL NOT DIVIDE BY ZERO!!!!");
        return (double) x / (double) valid;

    }

    public static double compareDistances(int[] either, int[] other) {
        if (either.length != other.length)
            throw new IllegalArgumentException("The arrays must be the same length");
        int n = either.length, totalDiffs = 0, comparableDists = 0;
        boolean set = false, eitherLower = false, disagree = false;

        for (int i = 0; i < n; i++) {
            int d1 = either[i], d2 = other[i];
            boolean eitherFound = d1 < Integer.MAX_VALUE;
            boolean otherFound  = d2 < Integer.MAX_VALUE;
            if (eitherFound && otherFound && d1 != d2) {
                if (!set) {
                    set = true;
                    eitherLower = (d1 < d2);
                } else if (set && eitherLower != (d1 <= d2))
                    disagree = true;

                totalDiffs += Math.abs(d2 - d1);
                comparableDists++;
            }
        }

        // Print and return
        if (comparableDists != 0) {
            double x = (double) totalDiffs / (double) comparableDists;
            System.out.printf("Average difference: %5.2f, with %d disagreements ", x, comparableDists);
            if (disagree)         System.out.println("(Both had lower distance than the other)");
            else if (eitherLower) System.out.println("(Second never had lower distance)");
            else                  System.out.println("(First never had lower distance)");
            return x;
        } else {
            System.out.println("No pairs where both algorithms found a distance");
            System.out.println();
            return -1d;
        }
    }

    public static int[] determineSetsOf(List<Result> results) {
        int n = results.size();
        int[] resultsets = new int[n];
        for (int i = 0; i < n; i++) resultsets[i] = Integer.MAX_VALUE;

        int count = 0;
        for (int i = 0; i < n; i++) {
            if (resultsets[i] == Integer.MAX_VALUE) resultsets[i] = count++;
            for (int j = i + 1; j < n; j++) {
                if (resultsets[i] < resultsets[j]) {
                    Result ri = results.get(i), rj = results.get(j);
                    if (Arrays.equals(ri.distances(), rj.distances()))
                        resultsets[j] = resultsets[i];
                }
            }
        }
        return resultsets;
    }

    public static void main(String[] args) {
        long start, end;
        int repetitions = DEFAULT_REPETITIONS;

        // Graph Generation
        // ----------------

        System.out.print("Generating graph.                      ");

        IndexedGraph graph;
        start = System.currentTimeMillis();
        try {
            graph = new LocationGraph(new FileInputStream(DEFAULT_GRAPH));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed while generating graph");
            return;
        }
        end = System.currentTimeMillis();

        System.out.println("Finished generating graph in " + (end - start) + " milliseconds.");
        System.out.printf("Normal graph info:                     Nodes: %d, Edges (directed): %d%n",
                            graph.V(), graph.E());

        // Uncontracted experiments
        // ------------------------

        System.out.print("Running uncontracted experiments.      ");

        List<Result> results = new ArrayList<>();
        for (AlgorithmType a : AlgorithmType.values())
            results.add(randomPairExperiment(a, graph, repetitions, DEFAULT_SEED));

        System.out.println("Finished running uncontracted experiments. Results are reported at the end.");
        System.out.println();

        // Graph contraction
        // -----------------

        System.out.print("Contracting graph.                     ");

        start = System.currentTimeMillis();
        ContractedGraph cgraph = new ContractedGraph(graph);
        cgraph.contractGraph();
        end = System.currentTimeMillis();

        System.out.println("Finished contracting graph in " + (end - start) + " milliseconds.");
        System.out.printf("Contracted graph info:                 Nodes: %d, Edges (undirected): %d, Of which shortcuts: %d%n",
                            cgraph.V(), cgraph.E(), cgraph.shortcutCount()*2);
             
        // Contracted experiments
        // ----------------------

        System.out.print("Running contracted experiments.        ");

        results.add(randomPairExperiment(AlgorithmType.BIDIJKSTRA, cgraph, repetitions, DEFAULT_SEED));
        results.add(randomPairExperiment(AlgorithmType.INTERLEAVING, cgraph, repetitions, DEFAULT_SEED));

        System.out.println("Finished running contracted experiments.");
        System.out.println();

        // Report data
        // -----------

        System.out.println("Results of experiments with " + repetitions + " repetitons");
        System.out.println("Duration and relaxations is the average (mean). Duration is in milliseconds.");
        System.out.println();
        System.out.println("algorithm    | contracted | duration | relax   | found | average distance | resultset");
        System.out.println("-------------+------------+----------+---------+-------+------------------+----------");

        int[] resultsets = determineSetsOf(results);
        int n = results.size();
        for (int i = 0; i < n; i++) {
            Result r = results.get(i);
            System.out.printf("%-12s | %-10b | %-8.2f | %-7.0f | %-5d | %-16.2f | %-3d%n",
                r.type, r.contracted, r.meanDuration, r.meanEdgesRelaxed, r.found, r.meanDistance, resultsets[i]);
        }

        System.out.println();

        // Error statistics
        // ----------------

        int count = 0;
        for (int i = 0; i < n; i++) {
            int setA = resultsets[i];
            if (setA != count) continue;
            count++;
            for (int j = i + 1; j < n; j++) {
                int setB = resultsets[j];
                if (setA >= setB) continue;
                Result ri = results.get(i), rj = results.get(j);
                System.out.print("Comparing result set " + setA + " to result " + setB + ":    ");
                compareDistances(ri.distances, rj.distances);
            }
        }
        if (count == 1) System.out.println("All algorithms generate the same set of distances. Great!");

    }
}
