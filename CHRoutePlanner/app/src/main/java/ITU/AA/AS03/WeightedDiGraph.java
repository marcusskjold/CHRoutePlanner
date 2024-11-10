package ITU.AA.AS03;

import java.util.LinkedList;
import java.util.List;

public class WeightedDiGraph {
    private int V;
    private int E;
    private List<DirectedEdge>[] edgesFrom;
    private List<DirectedEdge>[] edgesTo;

    protected WeightedDiGraph(int V) {
        edgesFrom = (LinkedList<DirectedEdge>[]) new LinkedList[V];
        edgesTo   = (LinkedList<DirectedEdge>[]) new LinkedList[V];
        for (int i = 0; i < V; i++) {
            edgesFrom[i] = new LinkedList<>();
            edgesTo[i]   = new LinkedList<>();
        }
        this.V = V;
        E = 0;
    }

    protected void addDirectedEdge(int u, int v, int w) {
        DirectedEdge e = new DirectedEdge(u, v, w);
        edgesFrom[u].add(e);
        edgesTo[u]  .add(e);
        E++;
    }

    protected void addUndirectedEdge(int u, int v, int w) {
        DirectedEdge other   = new DirectedEdge(v, u, w);
        DirectedEdge either  = new DirectedEdge(u, v, w);
        edgesFrom[u].add(either);
        edgesTo  [v].add(either);
        edgesFrom[v].add(other);
        edgesTo  [u].add(other);
        E += 2;
    }

    protected List<DirectedEdge> edgesTo(int index)   { return edgesTo[index]; }
    protected List<DirectedEdge> edgesFrom(int index) { return edgesFrom[index]; }

    protected int V() { return V; }

    protected int E() { return E; }
}
