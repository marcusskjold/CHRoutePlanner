package ITU.AA.AS03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContractedGraph implements IndexedGraph {

    private final static int MAX_RANK_FAILS = 20;
    private IndexedGraph G;
    private IndexMinPQ<Integer> pq;
    private boolean[] contracted;
    private int[] contractedNeighbours;
    private int[] rank;
    private int shortcutCount;
    private LocalDijkstra d;

    private int contractions;
    private int bContained;

    public ContractedGraph(IndexedGraph G) {
        this.G               = G;
        int V                = G.V();
        pq                   = new IndexMinPQ<>(V);
        contracted           = new boolean[V];
        contractedNeighbours = new int[V];
        rank                 = new int[V];
        d                    = new LocalDijkstra(G);
        shortcutCount        = 0;
        contractions         = 0;
        bContained           = 0;
    }

    public void contractGraph() {
        contract(G);
        System.out.println("Graph contracted");
        System.out.println("V         = " + V());
        System.out.println("E         = " + E());
        System.out.println("shortcuts = " + shortcutCount);
        System.out.println("hops      = " + contractions);
        System.out.println("total bs contained in map " + bContained);
    }

    public int contractions()   { return contractions; }
    public int shortcutCount()  { return shortcutCount; }
    public void contract(int n) { oneHop(n); }

    public void oneHop(int c) {
        int shortcutsAdded = 0;
        
        contractions++;
        System.out.println("Contraction nr. " + contractions);
        List<DirectedEdge> cbs = G.edgesFrom(c);
        ////System.out.println("edges is size " + edges.size());
        int s = cbs.size();
        Set<Integer> neighbors = new HashSet<>(s * 2, 1.0f);
        //Map<Integer, DirectedEdge> froms = 
        //    new HashMap<Integer, DirectedEdge>(s * 2, 1.0f);
        //
        //for (DirectedEdge cb : edges) {
        //    int b = cb.to();
        //    neighbors.add(b);
        //    if (contracted[b]) continue;
        //    DirectedEdge e = froms.putIfAbsent(b, cb);
        //    if (e != null && e.weight() > cb.weight()) froms.put(b, cb);
        //}
        //System.out.println("froms is length " + froms.size());
        //
        for (DirectedEdge ac : G.edgesTo(c)) {
            int a = ac.from(), w_ac = ac.weight();
            //System.out.println("a is " + a);
            if (contracted[a]) continue;
            neighbors.add(a);
            for (DirectedEdge cb : cbs) {
                int b = cb.to(), w_cb = cb.weight();
                //System.out.println("b is " + b);
                if (contracted[b]) continue;
                if (b == a) continue;
                neighbors.add(b);
                boolean witness = false; 
                for (DirectedEdge e : edgesFrom(a)) {
                    //System.out.println("match");
                    //System.out.println("n is " + e.to());
                    if (e.to() != b) continue;
                    //System.out.println("found b");
                    if (e.weight() <= w_cb + w_ac) {
                        witness = true;
                        break;
                    }
                }
                if (!witness) {
                    addShortcut(a, b, c, ac, cb);
                    shortcutsAdded++;
                    //System.out.println("added shortcut number " + shortcutCount);
                }
            }


            //for (DirectedEdge ab : G.edgesFrom(a)) {
            //List<DirectedEdge> aEdges = G.edgesFrom(a);
            //for (int i = 0; i < aEdges.size(); i++) {
            //    DirectedEdge ab = aEdges.get(i);
            //    int b = ab.to(), w_ab = ab.weight();
            //    //System.out.println("b is " + b);
            //    if (froms.containsKey(b)) {
            //        bContained++;
            //        //System.out.println("b is contained");
            //        DirectedEdge cb = froms.get(b);
            //        w_acb += cb.weight();
            //        //System.out.println("w_acb " + w_acb);
            //        //System.out.println("w_ab " + w_ab);
            //        if (w_acb < w_ab) addShortcut(a, b, w_acb, ac, cb);
            //    }
            //}
        }
        System.out.println("Shortcuts added = " + shortcutsAdded);
        System.out.println("Edges removed = " + (edgesFrom(c).size() + edgesTo(c).size()));
        contracted[c] = true;
        for (int n : neighbors) {
            contractedNeighbours[n]++;
            pq.changeKey(n,rank(n));
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
        // TODO: Only return edges from higher ranks
        return G.edgesTo(index);
    }
    
    @Override public List<DirectedEdge> edgesFrom(int index) {
        // TODO: Only return edges from higher ranks
        return G.edgesFrom(index);
    }

    @Override public int V()                                     { return G.V(); }
    @Override public int E()                                     { return G.E(); }


    // ====================== Private ================

    private void contract(IndexedGraph G) {
        int contractNo = 0;
        int failed = 0, V = G.V();

        for (int i = 0; i < V; i++) {
            int r = initialRank(i);
            if (r != Integer.MIN_VALUE) 
                pq.insert(i, r);
            else                        contracted[i] = true;
        }

        while (!pq.isEmpty()) {
            int n = pq.minIndex();
            int r = rank(n); 
            //System.out.println("ranking " + n + " with " + r);
            if (n == pq.minIndex()) {
                contractNo++;
                //System.out.println(E() / V());
//                System.out.println("next ranking");
                //System.out.println("Contract number: " + contractNo);
                dijkstraContract(pq.delMin());
                rank[n] = r;
                failed = 0;
            } else {
                pq.changeKey(n, r);
                failed++;
                System.out.println("fail");
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
        //Map<Integer, DirectedEdge> froms = 
        //    new HashMap<Integer, DirectedEdge>(s, 1.0f);

        //for (DirectedEdge cb : edges) {
        //    int b = cb.to();
        //    DirectedEdge e = froms.putIfAbsent(b, cb);
        //    if (e != null && e.weight() > cb.weight()) froms.put(b, cb);
        //}

        if(G.edgesTo(node).size() == 1) {
            if(G.edgesFrom(node).size() == 1) {
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
        
        if(size == 1) {
            return -1;
        }
        int maxDist = 0;
        for(int i=0;i<size-1;i++) {
            DirectedEdge to = edges.get(i); //define edge going to: Will be different each time since no parallel edges?
                for(int j=i+1;j<size;j++) { //Here one could loop through adjacency-list and check for witness-paths, simply
                    DirectedEdge from = edges.get(j); //define edge going from.
                        int pathLength = to.weight() + from.weight();
                        if(pathLength > maxDist) { maxDist = pathLength; }
                }
        }
        //System.out.println("Maxdist = " + maxDist);
        //System.out.println("edges of neighbor = " + size);
        //System.out.println("edges of neighbor = " + maxDist);

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
        order = (shortCutsAdded - size) + contractedNeighbours[v];
        return order;
    }

    private void dijkstraContract(int v) {
        //int order = 0;
        //int shortCutsAdded = 0;
        
        List<DirectedEdge> edges = new ArrayList<>();
        List<DirectedEdge> l = G.edgesTo(v);
        for(int i=0;i<l.size();i++) {
            DirectedEdge e = l.get(i);
            if(!contracted[e.from()]) edges.add(e);
        }
        //First find max weight path from an edge u to an edge w through v:
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
        //System.out.println("Maxdist = " + maxDist);
        //System.out.println("edges of neighbor = " + size);
        //System.out.println("edges of neighbor = " + maxDist);

        for(int i= 0; i <size-1;i++) {
            int t = edges.get(i).from();
                d.localSearch(t, maxDist, v, contracted);
                for(int j= i+1; j< size; j++ ) {
                    int f = edges.get(j).from();
                        int w = edges.get(i).weight() + edges.get(j).weight();
                        if(d.distance(f) > w) {
                            //shortCutsAdded++;
                            addShortcut(t, f, w, edges.get(i), edges.get(j), v);
                        }
                }
        }
        contracted[v] = true;
        for (int n= 0; n< edges.size();n++) {
            int i = edges.get(n).from();
            contractedNeighbours[i]++;
            //System.out.println(i);
            pq.changeKey(i,rank(i));
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
