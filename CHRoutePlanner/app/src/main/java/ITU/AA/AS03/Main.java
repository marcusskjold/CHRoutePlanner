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
    private static final int DEFAULT_REPETITIONS = 100;

    enum AlgorithmType { SIMPLE, EARLYSTOP, BIDIJKSTRA, INTERLEAVING, }

    private record Result(AlgorithmType type, boolean contracted, double meanDuration, double meanEdgesRelaxed, int found, int[] distances) { }

    /** Prepares an instance of a query algorithm on a given graph. */
    private static ShortestPathAlgorithm createAlgorithm(AlgorithmType type, IndexedGraph graph) {
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
    private static Result computePairs(AlgorithmType type, IndexedGraph graph, int pairNums, long seed) {
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

        double timeMean      = (double) totalTime / pairNums;
        double edgeRelaxMean = (double) totalEdgeRelax / pairNums;
        boolean isContracted = graph instanceof ContractedGraph;

        return new Result(type, isContracted, timeMean, edgeRelaxMean, found, distances);
    }

    // Method that compares distances found by two different algorithms
    private static double compareDistances(int[] either, int[] other) {
        if (either.length != other.length)
            throw new IllegalArgumentException("The arrays must be the same length");
        int n = either.length, totalDiffs = 0, comparableDists = 0;
        boolean set = false, eitherLower = false, disagree = false;

        for (int i = 0; i < n; i++) {
            int d1 = either[i], d2 = other[i];
            boolean eitherFound = d1 < Integer.MAX_VALUE;
            boolean otherFound  = d2 < Integer.MAX_VALUE;
            if (eitherFound && otherFound) {
                if      (!set && d1 != d2)                 { set = true; eitherLower = (d1 < d2); }
                else if (set && eitherLower != (d1 <= d2))   disagree = true; 

                totalDiffs += Math.abs(d2 - d1);
                comparableDists++;
            }
        }

        // Print and return
        if (comparableDists != 0) {
            double x = (double) totalDiffs / (double) comparableDists;
            System.out.printf("Average difference: %.2f. ", x);
            if (disagree)         System.out.println("(No result never had lower distances than the other)");
            else if (eitherLower) System.out.println("(First result never had lower distances)");
            else                  System.out.println("(Second result never had lower distances)");
            return x;
        } else {
            System.out.println("No pairs where both algorithms found a distance");
            System.out.println();
            return -1d;
        }
    }

    public static void main(String[] args) {
        long start, end;
        int repetitions = DEFAULT_REPETITIONS;

        // Graph Generation
        // ----------------

        IndexedGraph graph;
        System.out.print("Generating graph. ");
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

        // Uncontracted experiments
        // ------------------------

        List<Result> results = new ArrayList<>();
        System.out.print("Running uncontracted experiments. ");
        for (AlgorithmType a : AlgorithmType.values()) {
            Result r = computePairs(a, graph, repetitions, DEFAULT_SEED);
            results.add(r);
        }
        System.out.println("Finished running uncontracted experiments. Results are reported at the end.");

        // Graph contraction
        // -----------------

        start = System.currentTimeMillis();
        System.out.println("Contracting graph. ");
        ContractedGraph cgraph = new ContractedGraph(graph);
        cgraph.contractGraph();
        end = System.currentTimeMillis();
        System.out.println("Finished contracting graph in " + (end - start) + " milliseconds.");

        // Contracted experiments
        // ----------------------

        System.out.print("Running contracted experiments. ");
        results.add(computePairs(AlgorithmType.BIDIJKSTRA, cgraph, repetitions, DEFAULT_SEED));
        results.add(computePairs(AlgorithmType.INTERLEAVING, cgraph, repetitions, DEFAULT_SEED));
        System.out.println("Finished running contracted experiments.");

        // Experiment report
        // -----------------

        System.out.println("Results of experiments with " + repetitions + " repetitons");
        System.out.println("Duration and relaxations is the average (mean). Duration is in milliseconds.");
        System.out.println("algorithm    | contracted | duration | relax   | found");
        System.out.println("-------------+------------+----------+---------+------");
        for (Result r : results) {
            System.out.printf("%-12s | %-10b | %-8.2f | %-7.0f | %-3d%n",
                r.type, r.contracted, r.meanDuration, r.meanEdgesRelaxed, r.found);
        }
        System.out.println();
        
        // Error statistics
        // ----------------

        List<Result> distances = new ArrayList<>(results);

        System.out.print("Out of " + distances.size() + " results, ");

        for (int i = 0; i < distances.size(); i++) {
            for (int j = i + 1; j < distances.size(); j++) {
                while (Arrays.equals(distances.get(i).distances(), distances.get(j).distances()))
                    distances.remove(j);
            }
        }

        System.out.println(distances.size() + " different set of distances reported");
        System.out.println("nr | algorithm    | contracted | found | average distance");
        System.out.println("---+--------------+------------|-------|-----------------");
        for (Result r : distances) {
            long x = 0;
            int l = r.distances.length;
            int valid = l;
            for (int i = 0; i < l; i++) {
                int y = r.distances[i];
                if (y < Integer.MAX_VALUE) x += y;
                else valid--;
            }
            if (valid == 0) throw new ArithmeticException("I WILL NOT DIVIDE BY ZERO!!!!");
            double avg = (double) x / (double) valid;
            System.out.printf("%-2d | %-12s | %-10b | %-5d | %.2f%n",
                distances.indexOf(r), r.type, r.contracted, r.found, avg);
        }
        System.out.println();

        for (int i = 0; i < distances.size(); i++) {
            for (int j = i + 1; j < distances.size(); j++) {
                System.out.print("Comparing result " + i + " to result " + j + ":    ");
                double x = compareDistances(distances.get(i).distances, distances.get(j).distances);
            }
        }
        
    }
}
