package ITU.AA.AS03;

import java.util.Stack;

/** Simple implementation of the Dijsktra algorithm for shortest path point queries.
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
    protected IndexMinPQ<Integer> pq;
    protected int t;
    private int[] distTo;
    private DirectedEdge[] edgeTo;
    private int s;
    private int V;
    private int edgeRelaxationCount;
    private boolean ready;

    // =========== State Changing Methods =============

    /** Initializes the algorithm for a given graph.
     * Does not compute shortest paths.
     * An object of this class corresponds to a single query on a graph,
     * if multiple queries are desired, either create an instance per query,
     * or sequentially reset the query object with reset();
     * @param  G the edge-weighted digraph
     * @throws if graph is null or has no nodes.
     */
    public DijkstraSimple(IndexedGraph graph) {
        if (graph == null)
            throw new IllegalArgumentException("Graph must not be null.");
        V = graph.V(); 
        if (V < 1)
            throw new IllegalArgumentException("Graph must contain nodes.");
        this.G = graph;
        pq = new IndexMinPQ<Integer>(V);

        edgeTo = new DirectedEdge[V];        // edgeTo[v] = last edge on shortest s->v path 
        distTo = new int[V];         // distTo[v] = distance  of shortest s->v path
        for (int v = 0; v < V; v++) { distTo[v] = Integer.MAX_VALUE; }

        s = -1;
        t = -1;
        edgeRelaxationCount = -1;
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
        if (!ready) 
            throw new Error("State must be reset before new calculation.");
        if (source < 0 || source >= V)
            throw new IllegalArgumentException("source is not a valid node index");
        if (target < 0 || target >= V)
            throw new IllegalArgumentException("target is not a valid node index");
        ready = false;
        s = source;
        t = target;
        distTo[source] = 0;
        pq.insert(source, distTo[source]);
        edgeRelaxationCount = 0;
        
        findShortestPath();

        if (!(distTo[target] < Integer.MAX_VALUE)) return false;
        else return true;

    }

    //Potential refactoring from calculate to make bidirectional possible
    //Name could be better
    public void setUpSearch(int source, int target) {
        ready = false;
        s = source;
        t = target;
        distTo[source] = 0;
        pq.insert(source, distTo[source]);
        edgeRelaxationCount = 0;
    }

    //Helper method to make fetching priorityqueue in Bi-Dijkstra possible
    public IndexMinPQ<Integer> getPq() {
        return pq;
    }

    //Helper method to retreive a specific distance
    public int distance(int v) {
        return distTo[v];
    }

    //Generalized helper method to retreive shortest path to specific vertex
    public Iterable<DirectedEdge> retrievePath(int v) {
        if (ready) return null;                                 // If not calculated
        if (!(distTo[v] < Integer.MAX_VALUE)) return null;      // If not connected
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
            path.push(e);
        }
        return path;
    }

    
    // ============ Query methods, non-state changing =======

    /** Returns the shortest distance between the source and target node.
     * These must have been calculated beforehand.
     * Does not mutate state.
     * @return the shortest distance between source and target.
     *         if there is no path, or if the path has not been calculated yet
     *         returns {@code Integer.MAX_VALUE}.
     */
    @Override public int distance() { return distTo[t]; }

    /** Returns a shortest path from the source vertex {@code s} to vertex {@code v}.
     * @return a shortest path from the source vertex {@code s} to vertex {@code v}
     *         as an iterable of edges.
     *         Returns {@code null} if:
     *          - no such path exists
     *          - path has not been calculated yet
     *          - source and target are equal
     */
    @Override public Iterable<DirectedEdge> retrievePath() {
        if (ready) return null;                                 // If not calculated
        if (!(distTo[t] < Integer.MAX_VALUE)) return null;      // If not connected
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge e = edgeTo[t]; e != null; e = edgeTo[e.from()]) {
            path.push(e);
        }
        return path;
    }

    /** Returns the number of calls to relax during calculation of shortest path */
    @Override public int relaxedEdges() { return edgeRelaxationCount; }

    // ============ Helper Methods =============
    
    /** Relax an edge */
    protected void relax(DirectedEdge e) {
        int v = e.from(), w = e.to();
        int newDist = distTo[v] + e.weight();
        if (distTo[w] > newDist) {
            edgeRelaxationCount++;
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
