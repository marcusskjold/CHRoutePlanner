package ITU.AA.AS03;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.stream.IntStream;

public class Main {

    private static final long DEFAULT_SEED = 4263372046854775800L;
    private static final IllegalArgumentException ILLEGALALGORITHM = new IllegalArgumentException(
        "First argument must be one of: " + AlgorithmType.values());

    enum AlgorithmType {
        SIMPLEDIJKSTRA,
        EARLYSTOPDIJKSTRA,
        BIDIJKSTRA,
        CONTRACTIONHIERARCHIES
    }

   

    //Helper method to create new instance of the provided algorithmtype
    //on the provided graph
    private static ShortestPathAlgorithm createAlgorithm(AlgorithmType type, IndexedGraph graph) {
        switch(type) {
            case SIMPLEDIJKSTRA:
                return new DijkstraSimple(graph);
            case EARLYSTOPDIJKSTRA:
                return new DijkstraEarlyStop(graph);
            case BIDIJKSTRA:
                return new DijkstraBidirectional(graph);
            default:
                throw ILLEGALALGORITHM;
        }
    }

    //Compute shortest distance for a 1000 randomly generated pairs (maybe refactor pair generation***)
    private static void computePairs(AlgorithmType type, IndexedGraph graph, int pairNums, long seed) {
        Random r = new Random(seed);
        long startTime;
        long endTime;
        long totalTime = 0;
        long totalEdgeRelax = 0;
        for(int i=0; i<pairNums; i++) {
            ShortestPathAlgorithm sp = createAlgorithm(type, graph); //Can throw exception
            //System.out.println("generating pair no " + (i + 1));
            int range = graph.V();
            int[] pair = new int[]{r.nextInt(range), r.nextInt(range)};
            int source = pair[0];
            int target = pair[1];
            //System.out.println("source: " + source);
            //System.out.println("target: " + target);
            //System.out.println("starting calculation no: " + i);
            startTime = System.currentTimeMillis();
            sp.calculate(source, target);
            int d = sp.distance();
            endTime = System.currentTimeMillis();
            totalTime += (endTime - startTime);
            ////debug print-statement:
            //System.out.println(d);
            //System.out.println("relaxed edges: " + sp.relaxedEdges());

            totalEdgeRelax += sp.relaxedEdges();
        }
        double timeMean = (double) totalTime / pairNums;
        double edgeRelaxMean = (double) totalEdgeRelax / pairNums;
        System.out.println("mean duration (nanoseconds): " + timeMean);
        System.out.println("mean relaxed edges: " + edgeRelaxMean);
    }



    public static void main(String[] args) {
        //AlgorithmType a;
        //System.out.println("Current directory: " + System.getProperty("user.dir"));

        try {
            System.out.println("generating graph");
            InputStream input = new FileInputStream("denmark.graph.txt");
            IndexedGraph graph = new IndexedGraph(input);
            System.out.println("finished generating graph");
            System.out.println("benchmarking simple");
            computePairs(AlgorithmType.SIMPLEDIJKSTRA, graph, 100, DEFAULT_SEED);
            System.out.println("Benchmarking early stop");
            computePairs(AlgorithmType.EARLYSTOPDIJKSTRA, graph, 100, DEFAULT_SEED);
            System.out.println("Benchmarking bidirectional");
            computePairs(AlgorithmType.BIDIJKSTRA, graph, 100, DEFAULT_SEED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //try {
        //    a = AlgorithmType.valueOf(args[0]);
        //} catch (IllegalArgumentException e) { throw ILLEGALALGORITHM; }



    }
}
