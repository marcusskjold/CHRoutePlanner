package ITU.AA.AS03;

import java.util.Stack;

/**
 *  The {@code DijkstraSimple} class represents a data type for solving the
 *  single-source shortest paths point queries in edge-weighted digraphs
 *  where the edge weights are non-negative.
 *  The graph must be accessible through indexes.
 *  This is a basic implementation of the Dijkstra shortest path algoritm,
 *  that calculates the shortest path to every vertex before it is able to
 *  answer the point query.
 *  @author Andreas Bagge
 *  @author Marcus Skjold
 *  This class is derived from the implementation by Robert Sedgewick and Kevin Wayne.
 *  For additional documentation,
 *  see <a href="https://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class DijkstraSimple implements ShortestPathAlgorithm {
        
    protected IndexedGraph G;
    private int[] distTo;
    private DirectedEdge[] edgeTo;
    protected IndexMinPQ<Integer> pq;
    private int s;
    protected int t;
    private int edgeRelaxationCount;
    private boolean ready;

    /** Initializes the algorithm for a given graph.
     * Does not compute shortest paths.
     * An object of this class corresponds to a single query on a graph,
     * if multiple queries are desired, either create an instance per query,
     * or sequentially reset the query object with reset();
     * @param  G the edge-weighted digraph
     */
    public DijkstraSimple(IndexedGraph graph) {
        this.G = graph;
        int V = G.V();
        pq = new IndexMinPQ<Integer>(V);
        edgeTo = new DirectedEdge[V];        // edgeTo[v] = last edge on shortest s->v path 
        distTo = new int[V];         // distTo[v] = distance  of shortest s->v path
        for (int v = 0; v < V; v++) { distTo[v] = Integer.MAX_VALUE; }

        s = -1;
        t = -1;
        edgeRelaxationCount = -1;
        ready = true;
    }

    /** Resets algorithm state
     */
    @Override public void reset() {
        s = -1;
        t = -1;
        edgeRelaxationCount = -1;
        for (int v = 0; v < G.V(); v++) {
            pq.delete(v);
            distTo[v] = Integer.MAX_VALUE;
            edgeTo[v] = null;
        }
        ready = true;
    }

    /** Computes a shortest-paths tree from the source vertex {@code s} to every other
     * vertex in the edge-weighted digraph {@code G}.
     * This mutates state.
     * @param source the index of the source node,
     * @param target the target node.
     * @return if a shortest path was found
     */
    @Override public boolean calculate (int source, int target) {
        if (ready == false) throw new Error("State must be reset before new calculation.");
        ready = false;
        s = source;
        t = target;
        distTo[source] = 0;
        pq.insert(source, distTo[source]);
        
        findShortestPath();

        if (!(distTo[target] < Integer.MAX_VALUE)) return false;
        else return true;

    }
    
    /** Returns the shortest distance between the source and target node.
     * These must have been calculated beforehand.
     * Does not mutate state.
     * @return the shortest distance between source and target.
     *         if there is no path, or if the path has not been calculated yet
     *         returns {@code Integer.MAX_VALUE}.
     */
    @Override public int distance() {
        return distTo[t];
    }

    /** Returns a shortest path from the source vertex {@code s} to vertex {@code v}.
     * @return a shortest path from the source vertex {@code s} to vertex {@code v}
     *         as an iterable of edges, and {@code null} if no such path, or if it
     *         has not been calculated yet.
     */
    @Override public Iterable<DirectedEdge> retrievePath() {
        if (s == -1 || t == -1) return null;
        if (!(distTo[t] < Integer.MAX_VALUE)) return null;
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge e = edgeTo[t]; e != null; e = edgeTo[e.from()]) {
            path.push(e);
        }
        return path;
    }

    /** Returns the number of calls to relax during calculation of shortest path
     */
    @Override public int relaxedEdges() {
        return edgeRelaxationCount;
    }

    // ============ Helper Methods =============
    
    /** Relax an edge
     */
    protected void relax(DirectedEdge e) {
        edgeRelaxationCount++;
        int v = e.from(), w = e.to();
        int newDist = distTo[v] + e.weight();
        if (distTo[w] > newDist) {
            distTo[w] = newDist;
            edgeTo[w] = e;
            if (pq.contains(w)) pq.decreaseKey(w, newDist);
            else                pq.insert(w, newDist);
        }
    }


    protected void findShortestPath() {
        while (!pq.isEmpty()) {
            int node = pq.delMin();
            for (DirectedEdge e : G.getEdges(node)){
                relax(e);
            }
        }
    }

}
