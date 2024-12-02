package ITU.AA.AS03;

import java.util.LinkedList;
import java.util.List;

public class WeightedDiGraph implements IndexedGraph {
    private int V;
    private int E;
    private List<DirectedEdge>[] edgesFrom;
    private List<DirectedEdge>[] edgesTo;

    public WeightedDiGraph(int V) {
        if (V < 0) throw new IllegalArgumentException("Graph cannot have negative size");
        edgesFrom = (LinkedList<DirectedEdge>[]) new LinkedList[V];
        edgesTo   = (LinkedList<DirectedEdge>[]) new LinkedList[V];
        for (int i = 0; i < V; i++) {
            edgesFrom[i] = new LinkedList<>();
            edgesTo[i]   = new LinkedList<>();
        }
        this.V = V;
        E = 0;
    }

    @Override public void addDirectedEdge(int u, int v, int w) {
        DirectedEdge e = validateEdge(u, v, w);
        edgesFrom[u].add(e);
        edgesTo[v]  .add(e);
        E++;
    }

    @Override public void addDirectedEdge(DirectedEdge e) {
        validateEdge(e);
        edgesFrom[e.from()].add(e);
        edgesTo[e.to()]    .add(e);
        E++;
    }

    
    @Override public void addUndirectedEdge(int u, int v, int w) {
        DirectedEdge other   = validateEdge(v, u, w);
        DirectedEdge either  = new DirectedEdge(u, v, w);
        edgesFrom[u].add(either);
        edgesTo  [v].add(either);
        edgesFrom[v].add(other);
        edgesTo  [u].add(other);
        E += 2;
    }

    @Override public List<DirectedEdge> edgesTo(int index)   { return edgesTo[index]; }

    @Override public List<DirectedEdge> edgesFrom(int index) { return edgesFrom[index]; }

    @Override public int V() { return V; }

    @Override public int E() { return E; }

    // =================== Helper methods =================
    
    private DirectedEdge validateEdge(int u, int v, int w) {
        if (u >= V || u < 0) throw new IllegalArgumentException("u does not correspond to a node");
        if (v >= V || v < 0) throw new IllegalArgumentException("v does not correspond to a node");
        if (w < 0)           throw new IllegalArgumentException("weight is negative");
        return new DirectedEdge(u, v, w);
    }

    private void validateEdge(DirectedEdge e) {
        validateEdge(e.from(), e.to(), e.weight());
    }

    //public String toString() {
    //    return V + " " + E;
    //}
}
