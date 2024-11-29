package ITU.AA.AS03;

import java.util.LinkedList;
import java.util.List;

/** DijkstraBi
 *
 */
public class DijkstraInterleaving implements ShortestPathAlgorithm {

    private IndexedGraph G;
    private Dijkstra c;
    private int s, t, d, meetPoint, relaxes;
    private boolean ready;
    private boolean[] settled;

    /** Initializes the algorithm for a given graph.
     * Does not compute shortest paths.
     * An object of this class corresponds to a single query on a graph,
     * if multiple queries are desired, either create an instance per query,
     * or sequentially reset the query object with reset();
     * @param  G the edge-weighted digraph
     * @throws if graph is null or has no nodes.
     */
    public DijkstraInterleaving(IndexedGraph graph) {
        if (graph == null) throw new IllegalArgumentException("Graph must not be null.");
        int V = graph.V();
        if (V < 1)         throw new IllegalArgumentException("Graph must contain nodes.");
        G = graph;

        c = new Dijkstra(V, false);
        c.opposite = new Dijkstra(V, true);
        c.opposite.opposite = c;
        s = -1; t = -1; relaxes = -1;
        d = Integer.MAX_VALUE;
        settled = new boolean[V];
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

        c.distTo[s] = 0;     c.opposite.distTo[t] = 0;
        c.reached[s] = true; c.opposite.reached[t] = true;
        c.pq.insert(s, 0);   c.opposite.pq.insert(t, 0);

        findShortestPath2();
        //System.out.println("shortest path " + d);

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
        Dijkstra r = c.inverted ? c.opposite : c;
        Dijkstra l = r.opposite;
        for ( DirectedEdge e = r.edgeTo[meetPoint]; e != null; e = r.edgeTo[e.from()]) { path.addFirst(e); }
        for ( DirectedEdge e = l.edgeTo[meetPoint]; e != null; e = l.edgeTo[e.to()])   { path.add(e); }

        return path;
    }

    // =================== Private helper methods ========================

    private void findShortestPath() {
        if (s == t) { d = 0; return; }

        while (true){

            if (!c.opposite.pq.isEmpty()) c = c.opposite;
            else if (c.pq.isEmpty()) return;

            int u = c.pq.delMin();
            if (settled[u]) return;

            settled[u] = true;
            List<DirectedEdge> edges = c.inverted ? G.edgesTo(u)
                                                  : G.edgesFrom(u);
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

    private void findShortestPath2() {
        if (s == t) { d = 0; return; }

        while ((!c.opposite.pq.isEmpty()||!c.opposite.pq.isEmpty()) && ((c.opposite.pq.isEmpty() || d>c.opposite.pq.minKey()) && (c.pq.isEmpty() || d>c.pq.minKey()))){

            if (!c.opposite.pq.isEmpty()) c = c.opposite;
            else if (c.pq.isEmpty()) return;

            int u = c.pq.delMin();
            //if (settled[u]) return;

            settled[u] = true;
            List<DirectedEdge> edges = c.inverted ? G.edgesTo(u)
                                                  : G.edgesFrom(u);
            for (DirectedEdge e : edges) relax(e);
        }
    }

}
