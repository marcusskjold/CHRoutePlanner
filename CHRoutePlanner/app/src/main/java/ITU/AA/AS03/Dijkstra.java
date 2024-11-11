package ITU.AA.AS03;

public class Dijkstra {
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
