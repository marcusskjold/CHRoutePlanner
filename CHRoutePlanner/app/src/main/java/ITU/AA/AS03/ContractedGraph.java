package ITU.AA.AS03;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class ContractedGraph implements IndexedGraph {

    private final static int MAX_RANK_FAILS = 20;
    private WeightedDiGraph G;
    private IndexMinPQ<Integer> pq;
    private boolean[] contracted;
    private int[] contractedNeighbours;
    private int[] rank;
    private int shortcutCount;
    private LocalDijkstra d;

    public ContractedGraph(WeightedDiGraph G) {
        this.G               = G;
        int V                = G.V();
        pq                   = new IndexMinPQ<>(V);
        contracted           = new boolean[V];
        contractedNeighbours = new int[V];
        rank                 = new int[V];
        d                    = new LocalDijkstra(G);
        shortcutCount        = 0;
        contract(G);
    }

    public void oneHop(int c) {
        List<DirectedEdge> edges = G.edgesFrom(c);
        int longest = 0, s = edges.size();
        Set<Integer> neighbors = new HashSet<>(s, 1.0f);
        Map<Integer, DirectedEdge> froms = 
            new HashMap<Integer, DirectedEdge>(s, 1.0f);

        for (DirectedEdge cb : edges) {
            int b = cb.to();
            neighbors.add(b);
            if (contracted[b]) continue;
            DirectedEdge e = froms.putIfAbsent(b, cb);
            if (e != null && e.weight() > cb.weight()) froms.put(b, cb);
        }

        for (DirectedEdge ac : G.edgesTo(c)) {
            int a = ac.from(), w_acb = ac.weight();
            if (contracted[a]) continue;
            neighbors.add(a);
            for (DirectedEdge ab : G.edgesFrom(a)) {
                int b = ab.to(), w_ab = ab.weight();
                if (froms.containsKey(b)) {
                    DirectedEdge cb = froms.get(b);
                    w_acb += cb.weight();
                    if (w_acb > longest) longest = w_acb;
                    if (w_acb < w_ab) addShortcut(a, b, w_acb, ac, cb);
                }
            }
        }
        contracted[c] = true;
        for (int n : neighbors) {
            contractedNeighbours[n]++;
            rank(n);
        }
    }

    private void addShortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv) {
        Shortcut s = new Shortcut(u, v, w, ut, tv);
        G.addDirectedEdge(s);
        shortcutCount++;
    }

    // ===================== Forward ==================

    @Override public void addDirectedEdge(int u, int v, int w)   { G.addDirectedEdge(u, v, w); }
    @Override public void addUndirectedEdge(int u, int v, int w) { G.addUndirectedEdge(u, v, w); }
    @Override public List<DirectedEdge> edgesTo(int index) {
        return G.edgesTo(index);
    }
    
    @Override public List<DirectedEdge> edgesFrom(int index) {
        return G.edgesFrom(index);
    }

    @Override public int V()                                     { return G.V(); }
    @Override public int E()                                     { return G.E(); }


    // ====================== Private ================

    private void contract(IndexedGraph G) {
        int failed = 0, V = G.V();

        for (int i = 0; i < V; i++) {
            int r = initialRank(i);
            if (r != Integer.MIN_VALUE)
                pq.insert(i, r);
        }

        while (!pq.isEmpty()) {
            int n = pq.minIndex();
            int r = rank(n);
            if (n == pq.minIndex()) {
                oneHop(pq.delMin());
                rank[n] = r;
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
        int shortcutCount = 0;
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

            //if(G.edgesTo(i).size() == 1) {
            //    if(G.edgesFrom(i).size() == 1) {
            //        continue;

        for(int i = 0; i < size-1; i++) {
            DirectedEdge to = edges.get(i);
            for(int j=i+1;j<size;j++) {
                DirectedEdge from = edges.get(j);
                int pathLength = to.weight() + from.weight();
                if(pathLength > maxDist && to.to() == from.to())
                    maxDist = pathLength;
            }
        }

        for(int i= 0; i <size-1;i++) {
            int t = edges.get(i).from();
            d.localSearch(t, maxDist, node);
            for ( int j= i+1; j< size; j++ ) {
                int f = edges.get(j).from();
                    if(d.distance(f) > edges.get(i).weight() + edges.get(j).weight()) {
                        shortcutCount++;
                    }
            }
        }
        return shortcutCount - size;
    }

    private int rank(int node) {
        int rank = 1;
        pq.insert(node, rank);
        return rank;
    }


    private class Shortcut extends DirectedEdge {
        DirectedEdge ut, tv;

        Shortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv) {
            super(u, v, w); this.ut = ut; this.tv = tv;
        }
        DirectedEdge edgeFrom() { return ut; }
        DirectedEdge edgeTo()   { return tv; }
    }
}
