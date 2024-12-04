package ITU.AA.AS03;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Class representing a contracted graph. Contracts a given graph by constructing an
 * instance, taking the given graph as parameter, and calling 'contractGraph()'. 
 * Modifies the original graph */
public class ContractedGraph implements IndexedGraph {

    private final static int MAX_RANK_FAILS = 2000;
    private IndexedGraph G;
    private IndexMinPQ<Integer> pq;
    private List<DirectedEdge>[] higherEdgesTo, higherEdgesFrom;
    private boolean[] contracted;
    private int[] contractedNeighbours, rank;
    private int shortcutCount, globalRank, contractions;
    private LocalDijkstra d;

    public ContractedGraph(IndexedGraph G) {
        this.G               = G;
        int V                = G.V();
        d                    = new LocalDijkstra(G);
        pq                   = new IndexMinPQ<>(V);
        contracted           = new boolean[V];
        contractedNeighbours = new int[V];
        rank                 = new int[V];
        @SuppressWarnings("unchecked") List<DirectedEdge>[] x = (ArrayList<DirectedEdge>[]) new ArrayList[V];
        @SuppressWarnings("unchecked") List<DirectedEdge>[] y = (ArrayList<DirectedEdge>[]) new ArrayList[V];
        higherEdgesTo        = x;
        higherEdgesFrom      = y;
        shortcutCount        = 0;
        contractions         = 0;
        globalRank           = 0;
        for (int i = 0; i < V; i++) {
            rank[i]            = Integer.MAX_VALUE;
            higherEdgesFrom[i] = new ArrayList<>();
            higherEdgesTo[i]   = new ArrayList<>();
        }
    }
    // ===================== Adders ==================

    @Override public void addDirectedEdge(int u, int v, int w)   { G.addDirectedEdge(u, v, w); }
    @Override public void addUndirectedEdge(int u, int v, int w) { G.addUndirectedEdge(u, v, w); }
    @Override public void addDirectedEdge(DirectedEdge e)        { G.addDirectedEdge(e); }

    // ===================== Getters ==================
    public int contractions()                                    { return contractions; }
    public int shortcutCount()                                   { return shortcutCount; }
    @Override public List<DirectedEdge> edgesTo(int index)       { return higherEdgesTo[index]; }
    @Override public List<DirectedEdge> edgesFrom(int index)     { return higherEdgesFrom[index]; }
    @Override public int V()                                     { return G.V(); }
    @Override public int E()                                     { return G.E(); }

    // ===================== Public ==================

    public void contractGraph() {
        int failed = 0, V = G.V();

        for (int i = 0; i < V; i++) 
            pq.insert(i, prioritize(i));

        while (!pq.isEmpty()) {
            int n = pq.minIndex();
            int r = prioritize(n);
            if (r == pq.minKey()) {
                n = pq.delMin();
                dijkstraContract(n);
                rank[n] = globalRank++;
                failed = 0;
            } else {
                pq.changeKey(n, r);
                failed++;
                if (failed <= MAX_RANK_FAILS) continue;
                for (int i = 0; i < V; i++)
                    if (!contracted[i]) pq.changeKey(i, prioritize(i));
            }
        }

        for (int i = 0; i < V(); i++) {
            int r = rank[i];
            for (DirectedEdge e : G.edgesFrom(i))
                if (r < rank[e.to()])
                    higherEdgesFrom[i].add(e);
            for (DirectedEdge e : G.edgesTo(i))
                if (r < rank[e.from()])
                    higherEdgesTo[i].add(e);
        }
    }

    // ====================== Private ================

    private int prioritize(int v) {
        List<DirectedEdge> edges = findUncontractedEdges(v);
        int shortCutsAdded = 0, size = edges.size();

        if (size > 1) {
            int maxDist = findMaxDist(v, edges);

            for (int i = 0; i < size - 1; i++) {
                int uv = edges.get(i).from();
                d.localSearch(uv, maxDist, v, contracted);
                for(int j = i + 1; j < size; j++) {
                    int vw = edges.get(j).from();
                    if (uv == vw) continue;
                    int dist = edges.get(i).weight() + edges.get(j).weight();
                    if (d.distance(vw) > dist)
                        shortCutsAdded++;
                }
            }
        }
        return (shortCutsAdded - size) + contractedNeighbours[v];
    }

    private void dijkstraContract(int v) {
        List<DirectedEdge> edges = findUncontractedEdges(v);
        int size = edges.size();

        if (size > 1) {
            int maxDist = findMaxDist(v, edges);

            for(int i = 0; i < size - 1; i++) {
                int uv = edges.get(i).from();
                d.localSearch(uv, maxDist, v, contracted);
                for(int j = i + 1; j < size; j++) {
                    int vw = edges.get(j).from();
                    if (uv == vw) continue;
                    int dist = edges.get(i).weight() + edges.get(j).weight();
                    if (d.distance(vw) > dist)
                        addShortcut(uv, vw, dist, edges.get(i), edges.get(j), v);
                }
            }
        }
        contracted[v] = true;
        for (int n= 0; n< edges.size();n++) { // Update rank and register contracted neighbor for all neighbours
            int i = edges.get(n).from();
            contractedNeighbours[i]++;
            if (pq.contains(i)) pq.changeKey(i,prioritize(i));
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
                        System.out.println(s.from() + " " + s.to() + " " + s.weight() + " " + s.c);
                    }
                    else {
                        allEdges.add(new DirectedEdge(directedEdge.to(), directedEdge.from(), directedEdge.weight()));
                        System.out.println(directedEdge.from() + " " + directedEdge.to() + " " + directedEdge.weight() + " " + -1);
                    }
                }
            }
        }
    }

    //Method that prints graph according to original coordinates
    public void printGraphLocs() {
        if(!(G instanceof LocationGraph)) {
            throw new ClassCastException("This method should only be used for LocationGraphs");
        }
        LocationGraph locG = (LocationGraph) G;
        int nodeSize = locG.V();
        System.out.println(nodeSize + " " + locG.E()/2);
        for(int i=0;i<nodeSize;i++) {
            float[] locs = locG.getLocation(i);
            System.out.println(locG.getID(i) + " " + locs[0] + " " + locs[1] + " " + rank[i]);
        }
        Set<DirectedEdge> allEdges = new HashSet<>();
        for(int i=0;i<nodeSize;i++) {
            List<DirectedEdge>  l = locG.edgesFrom(i);
            for (DirectedEdge directedEdge : l) {
                if(!allEdges.contains(directedEdge)) {
                    allEdges.add(directedEdge);
                    if(directedEdge instanceof Shortcut) {
                        Shortcut s = (Shortcut) directedEdge;
                        allEdges.add(new Shortcut(directedEdge.to(), directedEdge.from(),directedEdge.weight(), s.ut, s.tv, s.c));
                        System.out.println(locG.getID(s.from()) + " " + locG.getID(s.to()) + " " + s.weight() + " " + locG.getID(s.c));
                    }
                    else {
                        allEdges.add(new DirectedEdge(directedEdge.to(), directedEdge.from(), directedEdge.weight()));
                        System.out.println(locG.getID(directedEdge.from()) + " " + locG.getID(directedEdge.to()) + " " + directedEdge.weight() + " " + -1);
                    }
                }
            }
        }
    }

    //--------------------Common methods for different contract methods:
    //Find longest path, u-v-w, where u, w are neighbours (and  it might be that u!=w?)
    public int findMaxDist(int v, List<DirectedEdge> neighbours) {
        int maxDist = 0, size = neighbours.size();
        for (int i = 0; i < size - 1; i++) {
            DirectedEdge edge_u = neighbours.get(i);                // Define edge from next neighbour to check, u
            for (int j = i + 1; j < size; j++) {
                DirectedEdge edge_w = neighbours.get(j);
                if (edge_w.from() == edge_u.from()) continue;       // Check for parrallel edges

                int pathLength = edge_u.weight() + edge_w.weight(); // Compute length of path u-v-w
                if(pathLength > maxDist) { maxDist = pathLength; }  // and update max length of those
            }
        }
        return maxDist;
    }

    //Method to return a list, `edges`, of edges incident on `v` that are not gone due to contraction
    public List<DirectedEdge> findUncontractedEdges(int v) {
        return G.edgesTo(v).stream().filter(e -> !contracted[e.from()]).toList();
    }

    private void addShortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv, int contracted) {
        Shortcut s1 = new Shortcut(u, v, w, ut, tv, contracted);
        Shortcut s2 = new Shortcut(v, u, w, ut, tv, contracted);
        G.addDirectedEdge(s1); G.addDirectedEdge(s2);
        shortcutCount++;
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
                    addShortcut(a, b, c, ac, cb, c);
                }
            }
        }
        contracted[c] = true;
        for (int n : neighbors) {
            contractedNeighbours[n]++;
            if (pq.contains(n)) pq.changeKey(n,prioritize(n));
        }
    }

    //-----------Shortcut class:
    private class Shortcut extends DirectedEdge {
        DirectedEdge ut, tv;
        int c;

        Shortcut(int u, int v, int w, DirectedEdge ut, DirectedEdge tv, int contracted) {
            super(u, v, w); this.ut = ut; this.tv = tv; c = contracted;
        }
    }
}
