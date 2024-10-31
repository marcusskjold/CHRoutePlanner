package ITU.AA.AS03;

//For now: inspiration from: https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/DirectedEdge.java.html


public class DirectedEdge {
    private final int u;
    private final int v;
    private final int w;

    public DirectedEdge(int u, int v, int w) {
        if (u < 0) throw new IllegalArgumentException("Vertex names must be non-negative integers");
        if (v < 0) throw new IllegalArgumentException("Vertex names must be non-negative integers");
        this.u = u;
        this.v = v;
        this.w = w;
    }
    
     /**
     * Returns the tail vertex of the directed edge.
     * @return the tail vertex of the directed edge
     */
    public int from() {
        return u;
    }

    /**
     * Returns the head vertex of the directed edge.
     * @return the head vertex of the directed edge
     */
    public int to() {
        return v;
    }

    /**
     * Returns the weight of the directed edge.
     * @return the weight of the directed edge
     */
    public int weight() {
        return w;
    }
}
