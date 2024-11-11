package ITU.AA.AS03;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class MContractedGraph {

    private LocationGraph G;
    private IndexMinPQ<Integer> pq;


    private class Shortcut extends DirectedEdge {
        DirectedEdge ut;
        DirectedEdge tv;

        Shortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv) {
            super(u, v, w);
            this.ut = ut;
            this.tv = tv;

        }
    }
    
    public MContractedGraph(LocationGraph G) {
        this.G = G;
        int V = G.V();
        pq = new IndexMinPQ<>(V);
    }

    private void localSearch() {

    }

    private void addShortcut(Shortcut s) {
        G.addDirectedEdge(s);
    }

}
