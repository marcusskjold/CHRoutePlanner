package ITU.AA.AS03;

public class DijkstraEarlyStop extends DijkstraSimple {

    public DijkstraEarlyStop(IndexedGraph graph) {
        super(graph);
    }

    @Override protected void findShortestPath() {
        while (!pq.isEmpty()) {
            int node = pq.delMin();
            if (node == t) {
                break;
            }
            for (DirectedEdge e : G.edgesFrom(node)){
                relax(e);
            }
        }


    }
}



