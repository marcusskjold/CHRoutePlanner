package ITU.AA.AS03;

public class MCHQuery implements ShortestPathAlgorithm {
    //private MContractedGraph G;
    private Dijkstra c, l, r;
    private int s, t, d, meetPoint, relaxes;
    private boolean ready;
    private boolean[] settled;

    
    @Override public boolean calculate(int source, int target) {

        
        return false;
    }

    @Override public Iterable<DirectedEdge> retrievePath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public int relaxedEdges() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override public int distance() {
        // TODO Auto-generated method stub
        return 0;
    }


}
