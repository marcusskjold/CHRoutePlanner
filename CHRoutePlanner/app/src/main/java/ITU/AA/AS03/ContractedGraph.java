package ITU.AA.AS03;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class ContractedGraph implements IndexedGraph {

    private WeightedDiGraph G;
    private IndexMinPQ<Integer> pq;
    private boolean[] contracted;


    private class Shortcut extends DirectedEdge {
        DirectedEdge ut, tv;

        Shortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv) {
            super(u, v, w);
            this.ut = ut;
            this.tv = tv;

        }
        DirectedEdge edgeFrom() { return ut; }
        DirectedEdge edgeTo()   { return tv; }



    }

    public int oneHop(int c) {
        int longest = 0;
        List<DirectedEdge> edges = G.edgesFrom(c);

        Map<Integer, DirectedEdge> froms = new HashMap<Integer, DirectedEdge>(edges.size(), 1.0f);
        for (DirectedEdge cn : G.edgesFrom(c)) {
            int n = cn.to();
            if (contracted[n]) continue;
            DirectedEdge e = froms.putIfAbsent(n, cn);
            if (e != null && e.weight() > cn.weight()) froms.put(n, cn);
        }

        for (DirectedEdge ac : G.edgesTo(c)) {
            int a = ac.from(), w_acb = ac.weight();
            for (DirectedEdge ab : G.edgesFrom(a)) {
                int b = ab.to(), abw = ab.weight();
                if (froms.containsKey(b)) {
                    DirectedEdge cb = froms.get(b);
                    w_acb += cb.weight();
                    if (w_acb > longest) longest = w_acb;
                    if (w_acb < abw) addShortcut(a, b, w_acb, ac, cb);
                }
            }
        }
        contracted[c] = true;
        return longest;
    }


    public void addShortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv) {
        Shortcut s = new Shortcut(u, v, w, ut, tv);
        G.addDirectedEdge(s);
    }

    @Override public void addDirectedEdge(int u, int v, int w) { G.addDirectedEdge(u, v, w); }
    @Override public void addUndirectedEdge(int u, int v, int w) { G.addUndirectedEdge(u, v, w); }
    @Override public List<DirectedEdge> edgesTo(int index) { return G.edgesTo(index); }
    @Override public List<DirectedEdge> edgesFrom(int index) { return G.edgesFrom(index); }
    @Override public int V() { return G.V(); }
    @Override public int E() { return G.E(); }


public ContractedGraph(WeightedDiGraph G) {
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
