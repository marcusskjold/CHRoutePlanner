package ITU.AA.AS03;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** A graph representation of a physical map.
 * Corresponds to a road network.
 * This implementation only represents bidirectional / undirected graphs.
 */
public class LocationGraph implements IndexedGraph {
    private Map<Long, Integer> idTranslator;
    private long[] ids;
    private float[][] locs;
    private WeightedDiGraph G;

    /** Initializes graph with no edges */
    public LocationGraph(int maxSize, long[] ids, float[][] locations) {
        G = new WeightedDiGraph(maxSize);
        if (ids.length != maxSize)
            throw new IllegalArgumentException("maxSize and number of ids differ");
        if (locations.length != maxSize)
            throw new IllegalArgumentException("maxSize and number of ids differ");

        idTranslator = new HashMap<Long, Integer>(maxSize * 2, (float) 0.5);
        for (int i = 0; i < maxSize; i++) {
            if (idTranslator.put(ids[i], i) != null)
                throw new IllegalArgumentException("duplicate id in input");
            if (locations[i].length != 2)
                throw new IllegalArgumentException("Bad location format");
        }

        this.locs = locations;
        this.ids = ids;
    }

    /** Generate undirected graph from input stream.
     * This assumes the input stream is correct.
     */
    public LocationGraph(InputStream input) throws IOException {
        if (input == null) throw new IllegalArgumentException("Input is null");
        Scanner sc = new Scanner(input);
        int V = sc.nextInt();
        if (V < 1) {
            sc.close();
            throw new IllegalArgumentException("No nodes given");
        }
        int E = sc.nextInt();
        if (E < 0) {
            sc.close();
            throw new IllegalArgumentException("Negative edge count");
        }
        ids = new long[V];
        locs = new float[V][2];
        G = new WeightedDiGraph(V);
        idTranslator = new HashMap<Long, Integer>(V * 2, (float) 0.5);

        for (int i = 0; i < V; i++) {
            long id = sc.nextLong();
            ids[i] = id;
            idTranslator.put(id, i);
            locs[i][0] = sc.nextFloat();
            locs[i][1] = sc.nextFloat();
        }

        for (int i = 0; i < E; i++) {
            int u = idTranslator.get(sc.nextLong());
            int v = idTranslator.get(sc.nextLong());
            int w = sc.nextInt();
            G.addUndirectedEdge(u, v, w);
        }
        sc.close();
    }

    @Override public void addUndirectedEdge(int u, int v, int w) { G.addUndirectedEdge(u, v, w); }

    @Override public void addDirectedEdge(int u, int v, int w)   { G.addDirectedEdge  (u, v, w); }

    // ============== Getters ==================
    //
    public int getIndex(long id)                             { return idTranslator.get(id); }

    public float[] getLocation(int index)                    { return locs[index]; }

    public long getID(int index)                             { return ids[index]; }

    @Override public void addDirectedEdge(DirectedEdge e)              { G.addDirectedEdge(e); }

    @Override public List<DirectedEdge> edgesFrom(int index) { return G.edgesFrom(index); }

    @Override public List<DirectedEdge> edgesTo(int index)   { return G.edgesTo(index); }

    @Override public int V()                                 { return G.V(); }

    @Override public int E()                                 { return G.E(); }

}
