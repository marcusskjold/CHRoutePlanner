package ITU.AA.AS03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class Graph {
    private Map<Long, Integer> idTranslator;
    private int V; // Number of nodes
    private int E;
    private long[] ids;
    private float[][] locs;
    private LinkedList<Edge>[] edges;

    public Graph(InputStream input) throws IOException {
        if (input == null) throw new IllegalArgumentException("Input is null");
        Scanner sc = new Scanner(input);
        V = sc.nextInt();
        if (V < 1) {
            sc.close();
            throw new IllegalArgumentException("No nodes given");
        }
        E = sc.nextInt();
        if (E < 0) {
            sc.close();
            throw new IllegalArgumentException("Negative edge count");
        }
        ids = new long[V];
        locs = new float[V][2];
        edges = (LinkedList<Edge>[]) new LinkedList[V];
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
            edges[u].add(new Edge(u, v, w));
            edges[v].add(new Edge(v, u, w));
        }

        sc.close();
    }
}
