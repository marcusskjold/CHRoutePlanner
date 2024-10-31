package ITU.AA.AS03;

interface ShortestPathAlgorithm { 

    /** Queries the graph for a shortest path between source index and target index.
     * This sets the state of the algorithm instance, allowing it to answer other
     * questions.
     * @param source  The index of node to calculate a shortest path from.
     * @param target  The index of node to calculate a shortest path to.
     * @return if a shortest path was found.
     */
    boolean calculate(int source, int target); 
    
    /** 
     * @return the number of edges relaxed for the query.
     */
    int relaxedEdges();

    /**
     * @return the total distance cost of shortest path.
     */
    int distance();

    /**
     * @return A path of edges that constitutes a shortest path.
     */
    Iterable<DirectedEdge> retrievePath();
}
