package ITU.AA.AS03;

public class LocalDijkstra {
    private final static int DEFAULT_LIMIT = 50;
    private boolean[] settled;
    private int[] distTo;
    private int[] searchGen; //Which search the node has last been a part of
    private int searchGeneration; //The number/generation of the current search
    private IndexedGraph G;
    private IndexMinPQ<Integer> pq;
    private int settledCount;
    private int limit;

    public LocalDijkstra(IndexedGraph graph) {
        this(graph, DEFAULT_LIMIT);
    }

    public LocalDijkstra(IndexedGraph graph, int settleLimit) {
        G                 = graph;
        int V             = graph.V();
        pq                = new IndexMinPQ<>(V); 
        distTo            = new int[V]; 
        searchGen         = new int[V];
        settled           = new boolean[V];
        searchGeneration  = 0;
        settledCount      = 0;
        limit             = settleLimit;
    }

    // For debugging purposes
    public int getSettledCount() { return settledCount; }
    public void setLimit(int limit) { this.limit = limit; }

    /* Performs local dijkstra search from given source node 
     * until having settled nodes equivalent to provided settle-limit (or V)
     * We also set a limit to search for
     * Also the contracted node is ignored 
     * TODO: maybe a whole array for contracted nodes in general would be useful
    */

    /** Performs a new search on the graph excluding node {@code ignored}.
     * Breaks the search as soon as it either tries to settle a node with a
     * higher distance than {@code distLimit}, or if it settles more than the
     * {@code limit} specified in the constructor.
     * To avoid resetting values between searches, a {@code searchGeneration}
     * counter and array is maintained. Every search, it is incremented by 2.
     * A node is reached if it is on the same generation as the search,
     * and is settled if it is one higher.
     */
    public void localSearch(int source, int distLimit, int ignored) {
        searchGeneration += 2;
        distTo[source]    = 0;
        settledCount      = 0;
        if (pq.contains(source)) pq.changeKey(source, distTo[source]);
        else                     pq.insert   (source, distTo[source]);
        
        while(!pq.isEmpty() && settledCount < limit) {
            int node         = pq.delMin();
            searchGen[node]  = searchGeneration + 1;
            settledCount++;
            if (distTo[node] > distLimit) return;
            for (DirectedEdge e : G.edgesFrom(node))
                relax(e, ignored);
        }

       
        //settles nodes not yet in this generation of search

        //TODO: Maybe return an edgedifference?
    }

    public boolean reached(int v) { return searchGen[v] >= searchGeneration; }

    //Returns distance to target node
    public int distance(int target) {
        if(reached(target)) return distTo[target];
        else                return Integer.MAX_VALUE;
    }
    public boolean settled(int v) { return searchGen[v] > searchGeneration; }

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

    public void localSearch(int source, int distLimit, int ignored, boolean[] contracted) {
        //increment the search count:
        searchGeneration++;
        //initialize track of settled nodes:
        settledCount = 0;
        //Initialize the dijkstra search (maybe fine not to check contracted)
        searchGen[source] = searchGeneration++;
        distTo[source] = 0;
        if(pq.contains(source)) {
            pq.changeKey(source, distTo[source]);
        } else {pq.insert(source, distTo[source]);}
        
        while(!pq.isEmpty() && settledCount<limit) {
            int node = pq.delMin();
            if(!contracted[node]) {
                settledCount++;
                //If we settle node greater than the max distance shortest distance through v, return
                if(distTo[node] > distLimit) {
                    break;
                }
                for (DirectedEdge e : G.edgesFrom(node)){
                    if(!contracted[e.to()]) {
                        relax(e, ignored);
                    }
                }
            }
        }
    }

}
