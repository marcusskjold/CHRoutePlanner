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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj.getClass() != this.getClass()) { return false; }

        final DirectedEdge other = (DirectedEdge) obj;

        if (this.u != other.u) { return false; }
        if (this.v != other.v) { return false; }
        if (this.w != other.w) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * (hash + u * 1000);
        hash = 47 * (hash + v * 10);
        hash = 47 * (hash + w);
        hash = hash + (13 * (u + v + w));
        return hash;
    }
}
