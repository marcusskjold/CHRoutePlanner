package ITU.AA.AS03;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** A graph representation of a physical map.
 * Corresponds to a road network.
 * This implementation only represents bidirectional / undirected graphs.
 */
public class GraphLocations {
    private Map<Long, Integer> idTranslator;
    private long[] ids;
    private float[][] locs;
    private WeightedDiGraph G;
    // PLAN: Split up the graph so there is a minimal class containing the graph itself,
    //       then locations and id can live in the wrapper class.
    //       This will make it much easier to e.g. have an inverted graph.

    public GraphLocations(int maxSize) {
        G = new WeightedDiGraph(maxSize);

    }
    /** Initializes graph with no edges */
    public GraphLocations(int maxSize, long[] ids, float[][] locations) {
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
        G = new WeightedDiGraph(maxSize);
    }

    /** Generate undirected graph from input stream.
     * This assumes the input stream is correct.
     */
    public GraphLocations(InputStream input) throws IOException {
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

    // ============== Getters ==================

    public void addUndirectedEdge(int u, int v, int w) {
        validateEdge(u, v, w);
        G.addUndirectedEdge(u, v, w);
    }

    public void addDirectedEdge(int u, int v, int w) {
        validateEdge(u, v, w);
        G.addDirectedEdge(u, v, w);
    }

    public int getIndex(long id) { return idTranslator.get(id); }

    public List<DirectedEdge> edgesFrom(int index) { return G.edgesFrom(index); }

    public List<DirectedEdge> edgesTo(int index)   { return G.edgesTo(index); }

    public float[] getLocation(int index) { return locs[index]; }

    public long getID(int index) { return ids[index]; }

    public int V() { return G.V(); }

    public int E() { return G.E(); }

    // ===========================
    
    private void validateEdge(int u, int v, int w) {
        if (u >= G.V() || u < 0) throw new IllegalArgumentException("u does not correspond to a node");
        if (v >= G.V() || v < 0) throw new IllegalArgumentException("v does not correspond to a node");
        if (w < 0)               throw new IllegalArgumentException("weight is negative");
    }
}
