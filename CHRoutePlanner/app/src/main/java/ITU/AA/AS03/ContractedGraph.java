package ITU.AA.AS03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContractedGraph implements IndexedGraph {
    
    //TODO: Might need to be set higher:
    private final static int MAX_RANK_FAILS = 20;
    private IndexedGraph G;
    private IndexedGraph original;
    private IndexMinPQ<Integer> pq;
    private boolean[] contracted;
    private int[] contractedNeighbours;
    private int[] rank;
    //private int[] shortcutsTo;
    private List<DirectedEdge>[] higherEdgesTo;
    private List<DirectedEdge>[] higherEdgesFrom;
    private int shortcutCount, globalRank;
    private LocalDijkstra d;

    private int contractions;
    private int bContained;

    public ContractedGraph(IndexedGraph G) {
        this.G               = G;
        //G = original;
        int V                = G.V();
        d                    = new LocalDijkstra(G);
        pq                   = new IndexMinPQ<>(V);
        contracted           = new boolean[V];
        contractedNeighbours = new int[V];
        //shortcutsTo          = new int[V];
        rank                 = new int[V];
        higherEdgesFrom      = (LinkedList<DirectedEdge>[]) new LinkedList[V];
        higherEdgesTo        = (LinkedList<DirectedEdge>[]) new LinkedList[V];
        shortcutCount        = 0;
        contractions         = 0;
        bContained           = 0;
        globalRank           = 0;
        for (int i = 0; i < V; i++) {
            rank[i]            = Integer.MAX_VALUE;
            higherEdgesFrom[i] = new LinkedList<>();
            higherEdgesTo[i]   = new LinkedList<>();
        }
    }

    public void contractGraph() {
        contract();
        for (int i = 0; i < V(); i++) {
            int degree = 0;
            int r = rank[i];
            //System.out.println(r);
            for (DirectedEdge e : G.edgesFrom(i)) {
                if (r <= rank[e.to()]) {
                    higherEdgesFrom[i].add(e);
                    degree++;
                }
            }
            for (DirectedEdge e : G.edgesTo(i)) {
                if (r <= rank[e.to()]) {
                    higherEdgesTo[i].add(e);
                    degree++;
                }
            }
            //System.out.println(degree);
        }
        System.out.println("Graph contracted");
        System.out.println("V               = " + V());
        System.out.println("E (directed)    = " + E());
        System.out.println("shortcuts       = " + shortcutCount);
        System.out.println("hops            = " + contractions);
        //System.out.println("total bs contained in map " + bContained);
    }

    public int contractions()   { return contractions; }
    public int shortcutCount()  { return shortcutCount; }

    //Unused for now, I think
    private void addShortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv) {
        //Shortcut s = new Shortcut(u, v, w, ut, tv);
        //G.addDirectedEdge(s);
        
        Shortcut s = new Shortcut(u, v, w, ut, tv);
        //G.addUndirectedEdge(s);
        G.addDirectedEdge(s);
        Shortcut s2 = new Shortcut(v, u, w, ut, tv);
        G.addDirectedEdge(s2);
        shortcutCount++;
    }

    private void addShortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv, int contracted) {
        //Shortcut only seems to be added in one direction: 
        //Shortcut s = new Shortcut(u, v, w, ut, tv, contracted);
        //G.addDirectedEdge(s);

        //Instead:
        Shortcut s = new Shortcut(u, v, w, ut, tv, contracted);
        //Maybe this new method could do it:
        //G.addUndirectedEdge(s);
        
        //Until this method exists:
        G.addDirectedEdge(s);
        Shortcut s2 = new Shortcut(v, u, w, ut, tv, contracted);
        G.addDirectedEdge(s2);


        shortcutCount++;
    }

    // ===================== Forward ==================

    @Override public void addDirectedEdge(int u, int v, int w)   { G.addDirectedEdge(u, v, w); }
    @Override public void addUndirectedEdge(int u, int v, int w) { G.addUndirectedEdge(u, v, w); }
    @Override public void addDirectedEdge(DirectedEdge e) { G.addDirectedEdge(e); }
    //Tried to add this
    @Override public void addUndirectedEdge(DirectedEdge e) { G.addUndirectedEdge(e); }

    @Override public List<DirectedEdge> edgesTo(int index) {
        return higherEdgesTo[index];
    }
    
    @Override public List<DirectedEdge> edgesFrom(int index) {
        return higherEdgesFrom[index];
    }

    @Override public int V()                                     { return G.V(); }
    @Override public int E()                                     { return G.E(); }


    // ====================== Private ================

    private void contract() {
        int contractNo = 0;
        int failed = 0, V = G.V();

        for (int i = 0; i < V; i++) {
            int r = initialRank(i);
            if (r != Integer.MIN_VALUE) pq.insert(i, r); 
                
        }
        
        while (!pq.isEmpty()) {
            int n = pq.minIndex();
            //TODO: This isn't updated in the PQ!!!!!
            int r = rank(n); 
            //try this:
            //pq.changeKey(n, r);
            //or
            //if (pq.keyOf(pq.minIndex()) == r) {
            if (n == pq.minIndex()) {
                
                contractNo++;
                System.out.println("contracting no: " + contractNo);
                //if (contractNo < 1)             // Doesn't seem to make a differenct
                //    oneHop(pq.delMin());
                //else 
                    //System.out.println("contracting " + n);
                dijkstraContract(pq.delMin());
                rank[n] = globalRank++;
                failed = 0;
            } else {
                pq.changeKey(n, r);
                failed++;
                if (failed <= MAX_RANK_FAILS) continue;
                for (int i = 0; i < V; i++) //Maybe just scan through pq?
                    if (!contracted[i]) rank(i);
            }
        }
    }


    private int initialRank(int v) {
        int shortCutsAdded = 0;
        List<DirectedEdge> edges = G.edgesTo(v);
        int size = edges.size();
        int maxDist = 0;
        //Check whether only one neighbor
        //If so, mark as contracted, give rank, and return MIN-Value
        if(size == 1) {
            //Second check to adapt to multidirection
            if(G.edgesFrom(v).size() == 1) { 
                contracted[v] = true;
                rank[v] = globalRank++;
                return Integer.MIN_VALUE;
            }
        }

        //get max-distance for path u-v-w
        maxDist = findMaxDist(v, edges);
        
        //Maybe assign both edges (in loops) as neighbours, and then assign further from there?
        for(int i=0; i<size-1; i++) {
            int firstNeighbour = edges.get(i).from();
            d.localSearch(firstNeighbour, maxDist, v);
            for (int j=i+1; j<size; j++) {
                int secondNeighbour = edges.get(j).from();
                if(d.distance(secondNeighbour) > edges.get(i).weight() + edges.get(j).weight()) {
                    shortCutsAdded++;
                }
            }
        }
        return shortCutsAdded - size;
    }

    private int rank(int v) {
        int order = 0;
        int shortCutsAdded = 0;
        //get incident edges connected to uncontracted neighbour nodes
        List<DirectedEdge> edges = findUncontractedEdges(v);
        
        int size = edges.size();
        
        //If only one un-contracted neighbor, order is just -1 (link removed and no shortcuts)
        if(size == 1) return -1; 

        //Find max weight path from an edge u to an edge w through v:
        int maxDist = 0;
        maxDist = findMaxDist(v, edges);
        
        //
        for(int i=0; i<size-1; i++) {
            int firstNeighbour = edges.get(i).from();
            d.localSearch(firstNeighbour, maxDist, v, contracted);
            for(int j=i+1; j<size; j++) {
                int secondNeighbour = edges.get(j).from();
                if(d.distance(secondNeighbour) > edges.get(i).weight() + edges.get(j).weight()) {
                    shortCutsAdded++;
                }
            }
        }
        //The edge-difference is counted (changes in edges from contraction + contracted neighbours)
        order = (shortCutsAdded - size) + contractedNeighbours[v]; //+ shortcutsTo[v]
        return order;
    }


    private void dijkstraContract(int v) {
        List<DirectedEdge> edges = findUncontractedEdges(v);
        
        int size = edges.size();
        
        //If just one neighbour, no shortcut needed (Just contract and update neighbours)
        if(size == 1) {
            contracted[v] = true;
            int i = edges.get(0).from();
            contractedNeighbours[i]++;
            if (pq.contains(i)) pq.changeKey(i,rank(i));
            return;
        }
           
        int maxDist = findMaxDist(v, edges);

        for(int i= 0; i <size-1;i++) {
            int firstNeighbour = edges.get(i).from();
            d.localSearch(firstNeighbour, maxDist, v, contracted);
            for(int j= i+1; j< size; j++ ) {
                int secondNeighbour = edges.get(j).from();
                int newDistance = edges.get(i).weight() + edges.get(j).weight();
                if(d.distance(secondNeighbour) > newDistance) {
                    addShortcut(firstNeighbour, secondNeighbour, newDistance, edges.get(i), edges.get(j), v);
                    //shortcutsTo[f]++;
                }
            }
        }
        //mark contracted
        contracted[v] = true;
        //Update rank and register contracted neighbor for all neighbours
        for (int n= 0; n< edges.size();n++) {
            int i = edges.get(n).from();
            contractedNeighbours[i]++;
            if (pq.contains(i)) pq.changeKey(i,rank(i));
        }
    }

    //Method that prints graph
    public void printGraph() {
        System.out.println(G.V() + " " + G.E()/2); 
        for(int i=0;i<G.V();i++) {
            System.out.println(i + " " + rank[i]);
        }
        Set<DirectedEdge> allEdges = new HashSet<>();
        for(int i=0;i<G.V();i++) {
            List<DirectedEdge>  l = G.edgesFrom(i);
            for (DirectedEdge directedEdge : l) {
                if(!allEdges.contains(directedEdge)) {
                    allEdges.add(directedEdge);
                    if(directedEdge instanceof Shortcut) {
                        Shortcut s = (Shortcut) directedEdge;
                        allEdges.add(new Shortcut(directedEdge.to(), directedEdge.from(),directedEdge.weight(), s.ut, s.tv, s.c));
                        System.out.println(s.from() + " " + s.to() + " " + s.c);
                    } 
                    else {
                        allEdges.add(new DirectedEdge(directedEdge.to(), directedEdge.from(), directedEdge.weight()));
                        System.out.println(directedEdge.from() + " " + directedEdge.to() + " " + -1);
                    } 
                }
            }
        }
    }


//--------------------Common methods for different contract methods:

    public int findMaxDist(int v, List<DirectedEdge> neighbours) {
        //Find longest path, u-v-w, where u, w are neighbours (and  it might be that u!=w?)
        int maxDist = 0;
        int size = neighbours.size();
        for(int i=0;i<size-1;i++) {
            //Define edge from next neighbour to check, u
            DirectedEdge edge_u = neighbours.get(i); 
                //Go through additional neighbours whose edge's index is bigger than edge_u
                for(int j=i+1;j<size;j++) { 
                    //Define edge from other neighbour, w, to check for witnesspaths (from u to w excluding v)
                    DirectedEdge edge_w = neighbours.get(j); 
                        //Debug: Check that we are not having two edges to current node 
                        //from the same neighbouring node (parallel edges, u=w)
                        //TODO: Just delete this check, since its in inner loop
                        if(edge_w.from()==edge_u.from()) {
                            //System.out.println("from: ");
                            //System.out.println(edge_w.from() + " " + edge_u.from());
                            //System.out.println("to ");
                            //System.out.println(edge_w.to() + " " + edge_u.to());
                            System.out.println("Parallel edge???");
                            if(edge_w instanceof Shortcut || edge_u instanceof Shortcut)
                            System.out.println("Shortcut!");
                            //System.exit(1);
                            //maybe just continue when parallel edge, since redundant?
                            //continue;
                        }
                        //Compute length of path u-v-w, and update max length of those 
                        int pathLength = edge_u.weight() + edge_w.weight();
                        if(pathLength > maxDist) { maxDist = pathLength; }
                }
        }
        return maxDist;
    }

    //Method to return a list, `edges`, of edges incident on `v` that are not gone due to contraction
    private List<DirectedEdge> findUncontractedEdges(int v) {
        List<DirectedEdge> edges = new ArrayList<>();
        List<DirectedEdge> l = G.edgesTo(v);
        for(int i=0; i<l.size(); i++) {
            DirectedEdge e = l.get(i);
            if(!contracted[e.from()]) edges.add(e);
        }
        return edges;
    }


//-----------Shortcut class:

    private class Shortcut extends DirectedEdge {
        DirectedEdge ut, tv;
        int c;

        Shortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv) { //TODO: For now the orders of ut and tv hasn't been thought about
            super(u, v, w); this.ut = ut; this.tv = tv;
        }
        Shortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv, int contracted) {
            super(u, v, w); this.ut = ut; this.tv = tv; c = contracted;
        }
        DirectedEdge edgeFrom() { return ut; }
        DirectedEdge edgeTo()   { return tv; }

    }

//---------------------------------------------------------------------------------
//---------------------------------------------------------------------------------
    //helper methods for testing Contraction:

    //Method that sets the initial orderings of nodes
    //Maybe should take another V as parameter***
    public void initialOrdering() {
        for (int i = 0; i < G.V(); i++) {
            int r = initialRank(i);
            if (r != Integer.MIN_VALUE) pq.insert(i, r); 
        }
    }

    public IndexMinPQ<Integer> getPq() {
        return pq;
    }



///----------------------------------
/// Not used for now:
public void oneHop(int c) {
        
    List<DirectedEdge> cbs = G.edgesFrom(c);
    List<DirectedEdge> acs = G.edgesTo(c);
    int s = cbs.size();
    Set<Integer> neighbors = new HashSet<>(s * 2, 1.0f);
    for (DirectedEdge ac : acs) {
        int a = ac.from(), w_ac = ac.weight();
        if (contracted[a]) continue;
        neighbors.add(a);
        for (DirectedEdge cb : cbs) {
            int b = cb.to(), w_cb = cb.weight();
            if (contracted[b]) continue;
            if (b == a) continue;
            neighbors.add(b);
            boolean witness = false; 
            for (DirectedEdge e : G.edgesFrom(a)) {
                if (e.to() != b) continue;
                if (e.weight() <= w_cb + w_ac) {
                    witness = true;
                    break;
                }
            }
            if (!witness) {
                addShortcut(a, b, c, ac, cb);
                //shortcutsTo[b]++;
            }
        }


    }
    contracted[c] = true;
    for (int n : neighbors) {
        contractedNeighbours[n]++;
        if (pq.contains(n)) pq.changeKey(n,rank(n));
    }
}


}
