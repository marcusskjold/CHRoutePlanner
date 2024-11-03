package ITU.AA.AS03;

import java.util.Stack;

public class DijkstraBidirectional implements ShortestPathAlgorithm{

    //from simple-Dijkstra
    protected IndexedGraph G;
    //protected int t;
    //private int s;
    private int V;
    private boolean ready;
    ////If using two Dijkstras
    private DijkstraEarlyStop dijkstraL;
    private DijkstraEarlyStop dijkstraR;
    private int d; //shortest path distance
    protected boolean[] settled; //Array to keep track of visited nodes (by either end)
    private int meetPoint; //Point where the shortest paths meet (for path retreival)
    



    public DijkstraBidirectional(IndexedGraph graph) {
        G= graph;
        ////If using two dijkstras
        dijkstraL = new DijkstraEarlyStop(graph);
        dijkstraR = new DijkstraEarlyStop(graph);
        V = graph.V();
        //New stuff:
        settled = new boolean[V]; //Initialize array to check whether already visited
        d = Integer.MAX_VALUE;
        //initilaize meetpoint to some dummy that indicates no path found (maybe not necessary?***)
        meetPoint = -1;
    }


    @Override public boolean calculate (int source, int target) {
        ////from dijkstra simple
        if (!ready) 
            throw new Error("State must be reset before new calculation.");
        if (source < 0 || source >= V)
            throw new IllegalArgumentException("source is not a valid node index");
        if (target < 0 || target >= V)
            throw new IllegalArgumentException("target is not a valid node index");

        //shortest distance to be returned (should maybe be a field?)***
        d = Integer.MAX_VALUE;
        dijkstraL.setUpSearch(source, target);
        dijkstraR.setUpSearch(source, target);
        
        
        findShortestPath();

        //d used here instead
        if (!(d < Integer.MAX_VALUE)) return false; //Make this compatible with superclass maybe, or refactor first part to make it possible?***
        else return true;

    }

    
    protected void findShortestPath() {
        //new
        
        //fetches priority-queues to compare
        IndexMinPQ<Integer> pqL = dijkstraL.getPq();
        IndexMinPQ<Integer> pqR = dijkstraR.getPq();
        while (!pqL.isEmpty() || !pqR.isEmpty()) {
                //define next node to be looked at
                int u;
                //makes reference for current PQ to be used in current iteration
                IndexMinPQ<Integer> currentPq; 
                //Choose node from left or right pq depending on order
                //We would always go with pqR first with this model (Which seems okay?)***
            if(pqR.isEmpty() || (!pqL.isEmpty() && (pqL.minKey() < pqR.minKey()) )) { //Lazy evaluation -> if pqR empty then pqL can't be
                currentPq = pqL;
            } else { //All other cases (either pqL empty or pqR.minkey() < pqL.minkey() (if second clause above false))
                currentPq = pqR;
            }
            u = currentPq.delMin();
            if (settled[u]) { //If current min settled from other direction, stop searching for shortest path (because ...)
                break;
            }
            settled[u] = true;
                for (DirectedEdge e : G.getEdges(u)) { //Maybe refactor this
                    //relaxes from either side depending on which had lowest min-value in pq
                    if(currentPq == pqL) {
                        relax(e, dijkstraL);
                    } else {
                        relax(e, dijkstraR);
                    }
                    //Checks whether the distance to the vertex newly relaxed (or tried to relax) edge pointed to is shorter than current shortest path
                    //If so updates shortest path
                    int v = e.to();
                    int distCandidate = dijkstraL.distance(v) + dijkstraR.distance(v);
                    if(d > distCandidate) {
                        d = distCandidate;
                        //And update place where new shortest path meet
                        meetPoint = v;
                    }
                }
            
        }
    }

    //The same except it also takes a priorityQueue
    //Might want to change it to this way in superclass (if we want this one to inherit)***
    //and then always use same priorityqueue there
    protected void relax(DirectedEdge e, DijkstraEarlyStop d) {
        d.relax(e);
    }

    @Override
    public int relaxedEdges() {
        return dijkstraL.relaxedEdges() + dijkstraR.relaxedEdges();
    }

    @Override
    public int distance() {
        return d;
    }


    //TODO: Implement
    @Override
    public Iterable<DirectedEdge> retrievePath() {
        Iterable<DirectedEdge> pathL = dijkstraL.retrievePath(meetPoint);
        Iterable<DirectedEdge> pathR = dijkstraR.retrievePath(meetPoint);
        
        //Concatenate the two paths in some way. Maybe use other structure than stack
        return null;
    }
}