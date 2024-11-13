package ITU.AA.AS03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContractedGraph implements IndexedGraph {
    
    private final static int MAX_RANK_FAILS = 20;
    private IndexedGraph G;
    private IndexedGraph original;
    private IndexMinPQ<Integer> pq;
    private boolean[] contracted;
    private int[] contractedNeighbours;
    private int[] rank;
    private int[] shortcutsTo;
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
        shortcutsTo          = new int[V];
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
            System.out.println(r);
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
            System.out.println(degree);
        }
        System.out.println("Graph contracted");
        System.out.println("V         = " + V());
        System.out.println("E         = " + E());
        System.out.println("shortcuts = " + shortcutCount);
        System.out.println("hops      = " + contractions);
        System.out.println("total bs contained in map " + bContained);
    }

    public int contractions()   { return contractions; }
    public int shortcutCount()  { return shortcutCount; }

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
                    shortcutsTo[b]++;
                }
            }


        }
        contracted[c] = true;
        for (int n : neighbors) {
            contractedNeighbours[n]++;
            if (pq.contains(n)) pq.changeKey(n,rank(n));
        }
    }

    private void addShortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv) {
        Shortcut s = new Shortcut(u, v, w, ut, tv);
        G.addDirectedEdge(s);
        shortcutCount++;
    }

    private void addShortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv, int contracted) {
        Shortcut s = new Shortcut(u, v, w, ut, tv, contracted);
        G.addDirectedEdge(s);
        shortcutCount++;
    }

    // ===================== Forward ==================

    @Override public void addDirectedEdge(int u, int v, int w)   { G.addDirectedEdge(u, v, w); }
    @Override public void addUndirectedEdge(int u, int v, int w) { G.addUndirectedEdge(u, v, w); }
    @Override public void addDirectedEdge(DirectedEdge e) { G.addDirectedEdge(e); }
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
            int r = rank(n); 
            if (n == pq.minIndex()) {
                contractNo++;
                if (contractNo < 1)             // Doesn't seem to make a differenct
                    oneHop(pq.delMin());
                else 
                    dijkstraContract(pq.delMin());
                rank[n] = globalRank++;
                failed = 0;
            } else {
                pq.changeKey(n, r);
                failed++;
                if (failed <= MAX_RANK_FAILS) continue;
                for (int i = 0; i < V; i++)
                    if (!contracted[i]) rank(i);
            }
        }
    }

    private int initialRank(int node) {
        int shortCutsAdded = 0;
        List<DirectedEdge> edges = G.edgesTo(node);
        int size = edges.size();
        int maxDist = 0;
        if(G.edgesTo(node).size() == 1) {
            if(G.edgesFrom(node).size() == 1) {
                contracted[node] = true;
                return Integer.MIN_VALUE;
            }
        }

        for(int i = 0; i < size-1; i++) {
            DirectedEdge to = edges.get(i);
            for(int j=i+1;j<size;j++) {
                DirectedEdge from = edges.get(j);
                int pathLength = to.weight() + from.weight();
                if(pathLength > maxDist)
                    maxDist = pathLength;
            }
        }

        for(int i= 0; i <size-1;i++) {
            int t = edges.get(i).from();
            d.localSearch(t, maxDist, node);
            for ( int j= i+1; j< size; j++ ) {
                int f = edges.get(j).from();
                    if(d.distance(f) > edges.get(i).weight() + edges.get(j).weight()) {
                        shortCutsAdded++;
                    }
            }
        }
        return shortCutsAdded - size;
    }

    private int rank(int v) {
        int order = 0;
        int shortCutsAdded = 0;
        
        List<DirectedEdge> edges = new ArrayList<>();
        List<DirectedEdge> l = G.edgesTo(v);
        for(int i=0;i<l.size();i++) {
            DirectedEdge e = l.get(i);
            if(!contracted[e.from()]) edges.add(e);
        }
        //First find max weight path from an edge u to an edge w through v:
        int size = edges.size();
        
        if(size == 1) return -1; 
        int maxDist = 0;
        for(int i=0;i<size-1;i++) {
            DirectedEdge to = edges.get(i); //define edge going to: Will be different each time since no parallel edges?
                for(int j=i+1;j<size;j++) { //Here one could loop through adjacency-list and check for witness-paths, simply
                    DirectedEdge from = edges.get(j); //define edge going from.
                        int pathLength = to.weight() + from.weight();
                        if(pathLength > maxDist) { maxDist = pathLength; }
                }
        }
        for(int i= 0; i <size-1;i++) {
            int t = edges.get(i).from();
                d.localSearch(t, maxDist, v, contracted);
                for(int j= i+1; j< size; j++ ) {
                    int f = edges.get(j).from();
                        if(d.distance(f) > edges.get(i).weight() + edges.get(j).weight()) {
                            shortCutsAdded++;
                        }
                }
        }
        //The edge-difference is counted (changes in edges from contraction
        order = (shortCutsAdded - size) + contractedNeighbours[v] + shortcutsTo[v];
        return order;
    }

    private void dijkstraContract(int v) {
        
        List<DirectedEdge> edges = new ArrayList<>();
        List<DirectedEdge> l = G.edgesTo(v);
        for(int i=0;i<l.size();i++) {
            DirectedEdge e = l.get(i);
            if(!contracted[e.from()]) edges.add(e);
        }
        int size = edges.size();
        
        if(size == 1) {
            contracted[v] = true;
            return;
        }
                                            //Maybe cap for how big it should be before we stop using max as stopping criterion
        int maxDist = 0;
        for(int i=0;i<size-1;i++) {
            DirectedEdge to = edges.get(i); //define edge going to: Will be different each time since no parallel edges?
                for(int j=i+1;j<size;j++) { //Here one could loop through adjacency-list and check for witness-paths, simply
                    DirectedEdge from = edges.get(j); //define edge going from.
                        int pathLength = to.weight() + from.weight();
                        if(pathLength > maxDist) { maxDist = pathLength; }
                }
        }
        //System.out.println(maxDist);

        for(int i= 0; i <size-1;i++) {
            int t = edges.get(i).from();
                d.localSearch(t, maxDist, v, contracted);
                for(int j= i+1; j< size; j++ ) {
                    int f = edges.get(j).from();
                        int w = edges.get(i).weight() + edges.get(j).weight();
                        if(d.distance(f) > w) {
                            //shortCutsAdded++;
                            addShortcut(t, f, w, edges.get(i), edges.get(j));
                            shortcutsTo[f]++;
                        }
                }
        }
        contracted[v] = true;
        for (int n= 0; n< edges.size();n++) {
            int i = edges.get(n).from();
            contractedNeighbours[i]++;
            if (pq.contains(i)) pq.changeKey(i,rank(i));

        }
        //The edge-difference is counted (changes in edges from contraction
        //order = (shortCutsAdded - size) + contractedNeighbours[v];
        //return order;
    }


    private class Shortcut extends DirectedEdge {
        DirectedEdge ut, tv;
        int c;

        Shortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv) {
            super(u, v, w); this.ut = ut; this.tv = tv;
        }
        Shortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv, int contracted) {
            super(u, v, w); this.ut = ut; this.tv = tv; c = contracted;
        }
        DirectedEdge edgeFrom() { return ut; }
        DirectedEdge edgeTo()   { return tv; }
    }

    public void printGraph() {
        System.out.println(G.V() + " " + G.E());
            for(int i=0;i<G.V();i++) {
                System.out.println(i + " " + rank[i]);
            }
            Set<DirectedEdge> allEdges = new HashSet<>();
            for(int i=0;i<G.V();i++) {
                List<DirectedEdge>  l = G.edgesFrom(i);
                for (DirectedEdge directedEdge : l) {
                    if(!allEdges.contains(directedEdge)) {
                        allEdges.add(directedEdge);
                        allEdges.add(new DirectedEdge(directedEdge.to(), directedEdge.from(), directedEdge.weight()));
                        if(directedEdge instanceof Shortcut) {
                            Shortcut s = (Shortcut) directedEdge;
                            System.out.println(s.from() + " " + s.to() + " " + s.c);
                        } else {
                            System.out.println(directedEdge.from() + " " + directedEdge.to() + " " + -1);
                        }
                        
                    }
                    
                }
            }
    }


}
