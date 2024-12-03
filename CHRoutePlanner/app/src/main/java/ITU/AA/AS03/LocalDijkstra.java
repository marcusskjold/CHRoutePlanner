package ITU.AA.AS03;

public class LocalDijkstra {
    private final static int DEFAULT_LIMIT = 50;
    private int[] distTo;
    private int[] searchGen; //Which search the node has last been a part of
    private int searchGeneration; //The number/generation of the current search
    private IndexedGraph G;
    private IndexMinPQ<Integer> pq;
    private int settledCount;

    public LocalDijkstra(IndexedGraph graph) {
        this(graph, DEFAULT_LIMIT);
    }

    public LocalDijkstra(IndexedGraph graph, int settleLimit) {
        G                 = graph;
        int V             = graph.V();
        pq                = new IndexMinPQ<>(V); 
        distTo            = new int[V]; 
        searchGen         = new int[V];
        searchGeneration  = 0;
        settledCount      = 0;
    }

    // For debugging purposes
    public int getSettledCount()    { return settledCount; }
    public boolean reached(int v)   { return searchGen[v] >= searchGeneration; }
    public boolean settled(int v)   { return searchGen[v] > searchGeneration; }
    //Returns distance to target node
    public int distance(int target) {
        if(reached(target)) return distTo[target];
        else                return Integer.MAX_VALUE;
    }


    /** Performs a new search on the graph excluding node {@code ignored}.
     * Breaks the search as soon as it either tries to settle a node with a
     * higher distance than {@code distLimit}, or if it settles more than the
     * {@code limit} specified in the constructor.
     * To avoid resetting values between searches, a {@code searchGeneration}
     * counter and array is maintained. Every search, it is incremented by 2.
     * A node is reached if it is on the same generation as the search,
     * and is settled if it is one higher.
     */
    public void localSearch(int source, int distLimit, int ignored, boolean[] contracted) {
        searchGeneration += 2;
        distTo[source]    = 0;
        settledCount      = 0;
        if (pq.contains(source))  pq.changeKey(source, distTo[source]);
        else                      pq.insert   (source, distTo[source]);
        
        while (!pq.isEmpty()) { 
            int node        = pq.delMin();
            searchGen[node] = searchGeneration + 1;
            if (contracted[node]) continue;
            settledCount++;
            if(distTo[node] > distLimit) break;
            for (DirectedEdge e : G.edgesFrom(node)){
                if (!contracted[e.to()]) {
                    relax(e, ignored);
                }
            }
        }
        while (!pq.isEmpty()) pq.delMin();
    }

    private void relax(DirectedEdge e, int ignored) {
        int w = e.to();
        if (w == ignored) return;
        int fromDist = distTo[e.from()]; 

        int toDist = fromDist + e.weight(); 
        if (toDist < fromDist) throw new ArithmeticException(
            "Integer overflow: Distances are too high");
        int gen = searchGen[w];

        if (gen < searchGeneration) {
            distTo[w]    = toDist;
            searchGen[w] = searchGeneration;
        } 
        else if (gen > searchGeneration) return;
        else if (distTo[w] > toDist)     distTo[w] = toDist;
        else                             return;
        
        if (pq.contains(w)) pq.changeKey(w, toDist); 
        else                pq.insert   (w, toDist);
    }

}
