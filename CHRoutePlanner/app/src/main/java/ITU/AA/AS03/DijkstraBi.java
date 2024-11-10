package ITU.AA.AS03;

import java.util.LinkedList;
import java.util.List;

// TODO: Found a major source of errors in our implementation.
//       The indexed graph makes no guarantees of being bidirectial,
//       but the algorithm assumes.
//       I propose to make implementation be able to work with any kind of
//       graph, by calling on the inverse of the graph for the lest side dijkstra.
//       To accomodate this, the graph object should be an interface.


/** DijkstraBi
 *
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
        boolean[] reached;
        boolean inverted;
        Dijkstra opposite;

        Dijkstra(int V, boolean inverted) {
            this.inverted = inverted;
            pq = new IndexMinPQ<Integer>(V);
            edgeTo = new DirectedEdge[V];
            distTo = new int[V];
            reached = new boolean[V];
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
        if (graph == null) throw new IllegalArgumentException("Graph must not be null.");
        V = graph.V();
        if (V < 1) throw new IllegalArgumentException("Graph must contain nodes.");
        G = graph;

        r = new Dijkstra(V, false);
        l = new Dijkstra(V, true);
        r.opposite = l; l.opposite = r;
        s = -1; t = -1; edgeRelaxationCount = -1;
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
        if (source < 0 || source >= V)
            throw new IllegalArgumentException("source is not a valid node index");
        if (target < 0 || target >= V)
            throw new IllegalArgumentException("target is not a valid node index");

        ready = false;
        edgeRelaxationCount = 0;
        s = source; t = target;

        r.distTo[s] = 0; r.reached[s] = true; r.pq.insert(s, 0);
        l.distTo[t] = 0; l.reached[t] = true; l.pq.insert(t, 0);

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
        for ( DirectedEdge e = r.edgeTo[meetPoint]; e != null; e = r.edgeTo[e.from()]) { path.addFirst(e); }
        for ( DirectedEdge e = l.edgeTo[meetPoint]; e != null; e = l.edgeTo[e.to()])   { path.add(e); }

        return path;
    }

    // =================== Private helper methods ========================

    private void findShortestPath() {
        if (s == t) { d = 0; return; }

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

    private void relax(DirectedEdge e) {
        // Setup & early stopping
        int u, v;
        if (!c.inverted) { u = e.from(); v = e.to(); }
        else             { v = e.from(); u = e.to(); }
        int newDist = c.distTo[u] + e.weight();
        if (newDist < c.distTo[u]) throw new ArithmeticException(
            "Integer overflow: Distances are too high");
        if (newDist < c.distTo[v]) {
            edgeRelaxationCount++;
            c.distTo[v] = newDist;
            c.edgeTo[v] = e;
            if (c.pq.contains(v)) c.pq.decreaseKey(v, newDist);
            else                  c.pq.insert(v, newDist);
            c.reached[v] = true;
        }

        if (c.opposite.reached[v]) {
            int dCandidate = newDist + c.opposite.distTo[v];
            if (dCandidate < newDist) throw new ArithmeticException(
                "Integer overflow: Distances are too high");
            if(dCandidate < d) {
                d = dCandidate;
                meetPoint = v;
            }
        }
    }

}
