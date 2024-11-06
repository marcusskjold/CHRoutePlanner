package ITU.AA.AS03;

import java.util.LinkedList;
import java.util.List;

/**
 * DijkstraBi
 */
public class DijkstraBi implements ShortestPathAlgorithm {

    private IndexedGraph G;
    private int V;
    private boolean ready;
    private int edgeRelaxationCount;
    private int s;                      // Source
    private int t;                      // Target
    private int d;                      // Distance
    private boolean[] settled;
    private int meetPoint;              // For path retrieval

    private Dijkstra c;
    private Dijkstra r;
    private Dijkstra l;

    
    private class Dijkstra {
        IndexMinPQ<Integer> pq;
        int[] distTo;
        DirectedEdge[] edgeTo;

        Dijkstra(int V) {
            pq = new IndexMinPQ<Integer>(V);
            edgeTo = new DirectedEdge[V];
            distTo = new int[V];
            for (int v = 0; v < V; v++)
                distTo[v] = Integer.MAX_VALUE;
        }
    }

    /** Initializes the algorithm for a given graph.
     * Does not compute shortest paths.
     * An object of this class corresponds to a single query on a graph,
     * if multiple queries are desired, either create an instance per query,
     * or sequentially reset the query object with reset();
     * @param  G the edge-weighted digraph
     * @throws if graph is null or has no nodes.
     */
    public DijkstraBi(IndexedGraph graph) {

        if (graph == null)
            throw new IllegalArgumentException("Graph must not be null.");
        V = graph.V();
        if (V < 1)
            throw new IllegalArgumentException("Graph must contain nodes.");
        G = graph;
        
        l = new Dijkstra(V);
        r = new Dijkstra(V);

        s = -1;
        t = -1;
        edgeRelaxationCount = -1;
        d = Integer.MAX_VALUE;
        settled = new boolean[V]; //Initialize array to check whether already visited

        ready = true;
    }

    /** Computes a shortest-path from the source vertex to target vertex in the graph.
     * This mutates state.
     * @param source the index of the source node,
     * @param target the target node.
     * @return if a shortest path was found
     * @throws IllegalStateException if calculate is called multiple times.
     * @throws IllegalArgumentException if source or target is invalid indexes in the graph
     */
    @Override public boolean calculate(int source, int target) {

        if (!ready)
            throw new IllegalStateException("State must be reset before new calculation.");
        if (source < 0 || source >= V)
            throw new IllegalArgumentException("source is not a valid node index");
        if (target < 0 || target >= V)
            throw new IllegalArgumentException("target is not a valid node index");

        ready = false;
        edgeRelaxationCount = 0;
        s = source; t = target;

        l.distTo[s] = 0; l.pq.insert(source, 0);
        r.distTo[t] = 0; r.pq.insert(target, 0);

        findShortestPath();

        if (d < Integer.MAX_VALUE) return true;
        else return false;
    }

    /** Returns the shortest distance between the source and target node.
     * These must have been calculated beforehand.
     * Does not mutate state.
     * @return the shortest distance between source and target.
     *         if there is no path returns {@code Integer.MAX_VALUE}.
     *         if the path has not been calculated yet, returns {@code -1}.
     */
    @Override public int distance() {
        if (t == -1) return -1;
        return d;
    }

    /** Returns the number of calls to relax during calculation of shortest path */
    @Override public int relaxedEdges() { return edgeRelaxationCount; }

    /** Returns a shortest path from the source vertex {@code s} to vertex {@code v}.
     * @return a shortest path from the source vertex {@code s} to vertex {@code v}
     *         as an iterable of edges.
     *         Returns an empty list if source and target are equal
     *         Returns {@code null} if:
     *          - no such path exists
     *          - path has not been calculated yet
     */
    @Override public List<DirectedEdge> retrievePath() {
        if (ready) return null;                                 // If not calculated
        if (d == Integer.MAX_VALUE) return null;                // If not connected

        LinkedList<DirectedEdge> path = new LinkedList<DirectedEdge>();
        for (
            DirectedEdge e = l.edgeTo[meetPoint]; e != null; e = l.edgeTo[e.from()]
        ) { path.addFirst(e); }
        for (
            DirectedEdge e = r.edgeTo[meetPoint]; e != null; e = r.edgeTo[e.from()]
        ) { path.add(e); }

        return path;
    }

    // =================== Private helper methods ========================

    private void findShortestPath() {

        while (true) {
            boolean eR = (r.pq.isEmpty());
            boolean eL = (l.pq.isEmpty());
            if (eR && eL) return;
            
            // Determine side
            if      (eR)                              c = l;
            else if (eL)                              c = r;
            else if ((l.pq.minKey() < r.pq.minKey())) c = l;
            else                                      c = r;

            int u = c.pq.delMin();

            if (settled[u]) return;

            settled[u] = true;

            for (DirectedEdge e : G.getEdges(u)) {
                relax(e);
                int v = e.to();
                int distL = l.distTo[v];
                int distR = r.distTo[v];

                // First check that they have each been reached (otherwise overflow***)
                if(distL < Integer.MAX_VALUE && distR < Integer.MAX_VALUE) {
                    int distCandidate = distL + distR;
                    if (distCandidate < distL)
                        throw new ArithmeticException("Integer overflow: Distances are too high");
                    if(distCandidate < d) {
                        d = distCandidate;
                        meetPoint = v;
                    }
                }
            }
        }
    }

    protected void relax(DirectedEdge e) {
        int v = e.from(), w = e.to();
        int newDist = c.distTo[v] + e.weight();
        if (newDist < c.distTo[v])
            throw new ArithmeticException("Integer overflow: Distances are too high");
        if (c.distTo[w] > newDist) {
            edgeRelaxationCount++;
            c.distTo[w] = newDist;
            c.edgeTo[w] = e;
            if (c.pq.contains(w)) c.pq.decreaseKey(w, newDist);
            else                  c.pq.insert(w, newDist);
        }
    }

}
