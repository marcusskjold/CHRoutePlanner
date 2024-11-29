package ITU.AA.AS03;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Main {

    private static final long DEFAULT_SEED = 4263372046854775800L;
    private static final IllegalArgumentException ILLEGALALGORITHM =
        new IllegalArgumentException("First argument must be one of: " + 
            Arrays.asList(AlgorithmType.values()));

    enum AlgorithmType {
        SIMPLEDIJKSTRA,
        EARLYSTOPDIJKSTRA,
        BIDIJKSTRA,
        INTERLEAVINGDIJKSTRA,
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
            case INTERLEAVINGDIJKSTRA:
                return new DijkstraInterleaving(graph);
            case BIDIJKSTRA:
                return new DijkstraBidirectional(graph);
            default:
                throw ILLEGALALGORITHM;
        }
    }

    //Compute shortest distance for a 1000 randomly generated pairs and count the mean query time and relaxed edges.
    //Returns the computed distances in an array
    private static int[] computePairs(AlgorithmType type, IndexedGraph graph, int pairNums, long seed) {
        int[] distances = new int[pairNums];
        int found = 0; 
        Random r = new Random(seed);
        long startTime;
        long endTime;
        long totalTime = 0;
        long totalEdgeRelax = 0;
        for(int i=0; i<pairNums; i++) {
            
            //System.out.println("generating pair no " + (i + 1));
            int range = graph.V();
            int[] pair = new int[]{r.nextInt(range), r.nextInt(range)};
            int source = pair[0];
            int target = pair[1];
            ////debug:
            //System.out.println("source: " + source);
            //System.out.println("target: " + target);
            //System.out.println("starting calculation no: " + i);
            ShortestPathAlgorithm sp = createAlgorithm(type, graph); 
            startTime = System.currentTimeMillis();
            if (sp.calculate(source, target)) found++;
            int d = sp.distance();
            endTime = System.currentTimeMillis();
            totalTime += (endTime - startTime);
            totalEdgeRelax += sp.relaxedEdges();
            distances[i] = d;
            ////debug print-statements:
            System.out.println(d);
            System.out.println("relaxed edges: " + sp.relaxedEdges());
        }
        double timeMean = (double) totalTime / pairNums;
        double edgeRelaxMean = (double) totalEdgeRelax / pairNums;
        System.out.println("mean duration (ms): " + timeMean);
        System.out.println("mean relaxed edges: " + edgeRelaxMean);
        System.out.println("found path these amount of times: " + found);
        return distances;
    }

    //Method that compares distances found by two different algorithms
    private static void compareDistances(AlgorithmType type1, AlgorithmType type2, IndexedGraph graph, int pairNums, long seed) {
        int[] distances1 = computePairs(type1, graph, pairNums, seed);
        int[] distances2 = computePairs(type1, graph, pairNums, seed);
        int totalDiffs = 0;
        int comparableDists = 0;
        for(int i=0;i<distances1.length;i++) {
            int d1 = distances1[i];
            int d2 = distances1[i];
            if(d1 < Integer.MAX_VALUE && d2 < Integer.MAX_VALUE) {
                totalDiffs += Math.abs(d2 - d1);
                comparableDists ++;
            }
        }
        if(comparableDists!=0) {
            System.out.println((double) totalDiffs / comparableDists);
        }
        else {
            System.out.println("No pairs where both algorithms found a distance");
        }
    }

   

    public static void main(String[] args) {
        //AlgorithmType a;
        //System.out.println("Current directory: " + System.getProperty("user.dir"));

        try {
            System.out.println("generating graph");
            InputStream input = new FileInputStream("denmark.graph.txt");
            IndexedGraph graph = new LocationGraph(input);
            System.out.println("finished generating graph");
            //System.out.println("benchmarking simple");
            //computePairs(AlgorithmType.SIMPLEDIJKSTRA, graph, 100, DEFAULT_SEED);
            //System.out.println("Benchmarking early stop");
            //computePairs(AlgorithmType.EARLYSTOPDIJKSTRA, graph, 100, DEFAULT_SEED);
            //System.out.println("Benchmarking bidirectional");
            //computePairs(AlgorithmType.BIDIJKSTRA, graph, 100, DEFAULT_SEED);
            //System.out.println("Benchmarking bidirectional (old)");
            //computePairs(AlgorithmType.BIDIRECTIONALDIJKSTRA, graph, 100, DEFAULT_SEED);
            System.out.println("Contracting graph");
            ContractedGraph cgraph = new ContractedGraph(graph);
            cgraph.contractGraph();
            System.out.println("Benchmarking interleaving with contracted graph");
            computePairs(AlgorithmType.INTERLEAVINGDIJKSTRA, cgraph, 1000, DEFAULT_SEED);
            System.out.println("Benchmarking bidirectional with uncontracted graph");
            computePairs(AlgorithmType.BIDIJKSTRA, cgraph, 1000, DEFAULT_SEED);
            //System.out.println("Benchmarking simple dijkstra with uncontracted graph");
            //computePairs(AlgorithmType.SIMPLEDIJKSTRA, graph, 1000, DEFAULT_SEED);
            //System.out.println("Benchmarking early stop dijkstra with uncontracted graph");
            //computePairs(AlgorithmType.EARLYSTOPDIJKSTRA, graph, 1000, DEFAULT_SEED);
            //System.out.println("Benchmarking Early stop with contracted graph");
            //computePairs(AlgorithmType.EARLYSTOPDIJKSTRA, cgraph, 10, DEFAULT_SEED);
            //System.out.println("Benchmarking simple dijkstra with contracted graph");
            //computePairs(AlgorithmType.SIMPLEDIJKSTRA, cgraph, 10, DEFAULT_SEED);
            //Contraction c = new Contraction(graph);
            //c.preProcess();
            //IndexMinPQ<Integer> hierarchy = c.getHierarchy();
            //HashSet<Integer> set = new HashSet<>();
            //for(int i=0;i<hierarchy.size();i++) {
            //    set.add(hierarchy.keyOf(i));
            //    System.out.println(hierarchy.keyOf(i));
            //}
            //System.out.println(set.size());
            //cgraph.printGraph();
            //cgraph.printGraphLocs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //try {
        //    a = AlgorithmType.valueOf(args[0]);
        //} catch (IllegalArgumentException e) { throw ILLEGALALGORITHM; }

        //WeightedDiGraph G = new WeightedDiGraph(3);
        //G.addUndirectedEdge(0, 2, 3);
        //G.addUndirectedEdge(1, 2, 3);
        //ContractedGraph CG = new ContractedGraph(G);
        //CG.contract(2);
        //System.out.println(CG.shortcutCount());



    }
}
