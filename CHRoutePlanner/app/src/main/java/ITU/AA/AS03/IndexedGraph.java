package ITU.AA.AS03;

import java.util.List;

public interface IndexedGraph {

    public void addUndirectedEdge(int u, int v, int w);
    public void addDirectedEdge(DirectedEdge e);
    public void addDirectedEdge(int u, int v, int w);

    // ============== Getters ==================

    public List<DirectedEdge> edgesFrom(int index);
    public List<DirectedEdge> edgesTo(int index);
    public int E();
    public int V();

}
