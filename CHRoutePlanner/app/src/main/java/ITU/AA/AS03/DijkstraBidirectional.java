package ITU.AA.AS03;

import java.util.LinkedList;
import java.util.List;

/** DijsktraBidirectional variant that searches from source and target in order of shortest distance from
 * either source or target.
 *
 */
public class DijkstraBidirectional implements ShortestPathAlgorithm {

    private IndexedGraph G;
    private Dijkstra c, r, l;
    private int s, t, d, meetPoint, relaxes;
    private boolean ready, contract;

    /** Initializes the algorithm for a given graph.
     * Does not compute shortest paths.
     * An object of this class corresponds to a single query on a graph,
     * if multiple queries are desired, either create an instance per query,
     * or sequentially reset the query object with reset();
     * @param  G the edge-weighted digraph
     * @throws if graph is null or has no nodes.
     */
    public DijkstraBidirectional(IndexedGraph graph) {
        if (graph == null) throw new IllegalArgumentException("Graph must not be null.");
        int V = graph.V();
        if (V < 1)         throw new IllegalArgumentException("Graph must contain nodes.");
        G = graph;
        contract = (graph instanceof ContractedGraph);

        r = new Dijkstra(V, false);
        l = new Dijkstra(V, true);
        r.opposite = l; l.opposite = r;
        s = -1; t = -1; relaxes = -1;
        d = Integer.MAX_VALUE;
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
        if (source < 0 || source >= G.V())
            throw new IllegalArgumentException("source is not a valid node index");
        if (target < 0 || target >= G.V())
            throw new IllegalArgumentException("target is not a valid node index");

        ready = false;
        relaxes = 0;
        s = source; t = target;

        r.distTo[s] = 0; r.reached[s] = true; r.pq.insert(s, 0);
        l.distTo[t] = 0; l.reached[t] = true; l.pq.insert(t, 0);
        c = r;

        if (contract) findShortestPathContracted();
        else          findShortestPath();
        //findShortestPathContracted();

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
    @Override public int relaxedEdges() { return relaxes; }

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
        for ( DirectedEdge e = r.edgeTo[meetPoint]; e != null; e = r.edgeTo[e.from()]) { path.addFirst(e); }
        for ( DirectedEdge e = l.edgeTo[meetPoint]; e != null; e = l.edgeTo[e.to()])   { path.add(e); }

        return path;
    }

    // =================== Private helper methods ========================

    private void findShortestPath() {
        if (s == t) { d = 0; return; }
        boolean[] settled = new boolean[G.V()];

        while (true) {
            boolean eR = (r.pq.isEmpty());
            boolean eL = (l.pq.isEmpty());
            if (eR && eL) return;

            // Determine side
            if (eR || (!eL && l.pq.minKey() < r.pq.minKey())) c = l;
            else                                              c = r;

            int u = c.pq.delMin();
            if (settled[u]) return;

            settled[u] = true;
            List<DirectedEdge> edges = c.inverted ? G.edgesTo(u)
                                                  : G.edgesFrom(u);
            for (DirectedEdge e : edges) relax(e);
        }
    }

    private void findShortestPathContracted() {
        if (s == t) { d = 0; return; }

        while (true) {
            boolean eR = (r.pq.isEmpty());
            boolean eL = (l.pq.isEmpty());
            if (eR && eL) return;

            /* The issue is that d is found as the combination of two distances,
             * but we only break when we hit a single distance that is larger
             * This makes for horrible performance on normal graphs but is essential for correctness
             * on contracted graphs. */
            if (d != Integer.MAX_VALUE)
                if ((eR || d <= r.pq.minKey()) && (eL || d <= l.pq.minKey()))
                    return;

            // Determine side
            if (eR || (!eL && l.pq.minKey() < r.pq.minKey())) c = l;
            else                                              c = r;

            int u = c.pq.delMin();
            List<DirectedEdge> edges = c.inverted ? G.edgesTo(u) : G.edgesFrom(u);
            for (DirectedEdge e : edges) relax(e);
        }
    }

    private void relax(DirectedEdge e) {
        // Setup & early stopping
        int u, v;
        if (!c.inverted) { u = e.from(); v = e.to(); }
        else             { v = e.from(); u = e.to(); }
        int newDist  = c.distTo[u] + e.weight();
        if (newDist >= c.distTo[v]) return;
        if (newDist  < c.distTo[u]) throw new ArithmeticException(
            "Integer overflow: Distances are too high");

        relaxes++;
        c.distTo[v] = newDist;
        c.edgeTo[v] = e;
        if (c.pq.contains(v)) c.pq.decreaseKey(v, newDist);
        else                  c.pq.insert(v, newDist);
        c.reached[v] = true;

        if (c.opposite.reached[v]) {
            int dCandidate = newDist + c.opposite.distTo[v];
            if(dCandidate < d) {
                if (dCandidate < newDist) throw new ArithmeticException(
                    "Integer overflow: Distances are too high");
                d = dCandidate;
                meetPoint = v;
            }
        }
    }

}
