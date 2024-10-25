package ITU.AA.AS03;


//TODO: adapt to how we implement the graph, edges, path etc.

import java.util.Stack;

import com.google.common.collect.HashBiMap;

import java.util.HashMap;

public class SimpleDijkstra implements SPFinder {
    
    
    
    /**
     *  The {@code DijkstraSP} class represents a data type for solving the
     *  single-source shortest paths problem in edge-weighted digraphs
     *  where the edge weights are non-negative.
     *  <p>
     *  This implementation uses <em>Dijkstra's algorithm</em> with a
     *  <em>binary heap</em>. The constructor takes
     *  &Theta;(<em>E</em> log <em>V</em>) time in the worst case,
     *  where <em>V</em> is the number of vertices and <em>E</em> is
     *  the number of edges. Each instance method takes &Theta;(1) time.
     *  It uses &Theta;(<em>V</em>) extra space (not including the
     *  edge-weighted digraph).
     *  <p>
     *  This correctly computes shortest paths if all arithmetic performed is
     *  without floating-point rounding error or arithmetic overflow.
     *  This is the case if all edge weights are integers and if none of the
     *  intermediate results exceeds 2<sup>52</sup>. Since all intermediate
     *  results are sums of edge weights, they are bounded by <em>V C</em>,
     *  where <em>V</em> is the number of vertices and <em>C</em> is the maximum
     *  weight of any edge.
     *  <p>
     *  For additional documentation,
     *  see <a href="https://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
     *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
     */
        
    



        /**
         * Computes a shortest-paths tree from the source vertex {@code s} to every other
         * vertex in the edge-weighted digraph {@code G}.
         *
         * @param  G the edge-weighted digraph
         * @param  s the source vertex
         * @throws IllegalArgumentException if an edge weight is negative
         * @throws IllegalArgumentException unless {@code 0 <= s < V}
         */

        public SimpleDijkstra() {
            
        }
    
        // relax edge e and update pq if changed
        private void relax(Edge e, double[] distTo, Edge[] edgeTo, IndexMinPQ<Double> pq) {
            int v = e.from(), w = e.to();
            if (distTo[w] > distTo[v] + e.weight()) {
                distTo[w] = distTo[v] + e.weight();
                edgeTo[w] = e;
                if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
                else                pq.insert(w, distTo[w]);
            }
        }


        public double findSP(Graph G, int s, int t) {
            //Initialize distTo and edgeTo arrays

            double[] distTo= new double[G.V()];          // distTo[v] = distance  of shortest s->v path
            //HashMap<Long,Double> distTo = new HashMap<>();
            Edge[] edgeTo= new Edge[G.V()];    // edgeTo[v] = last edge on shortest s->v path 
            //HashMap<Long,Edge> edgeTo = new HashMap<>();
            //Initialize distances
            for (int v = 0; v < G.V(); v++)
                    {distTo[v] = Double.POSITIVE_INFINITY;}
            distTo[s] = 0.0;
            //for (Long v = 0L; v < G.V(); v++) {
            //    distTo.put(v, Double.POSITIVE_INFINITY);
            //}
            //distTo.put(s, 0.0);
        
            
            IndexMinPQ<Double> pq = new IndexMinPQ<Double>(G.V()); // priority queue of vertices
            //insert the source into the priorityqueue
            pq.insert(s, distTo[s]);
            //pq.insert(s, distTo.get(s));
            // relax vertices in order of distance from s
            while (!pq.isEmpty()) {
                int v = pq.delMin();
                for (Edge e : G.adj(v)){
                    relax(e, distTo, edgeTo, pq);
                }
            }
            //If there is not a path:
            if (!(distTo[t] < Double.POSITIVE_INFINITY)) return -1.0;
            //Else return distance
            else return distTo[t];

            ////Option for later: Recreate a path and return it
            //Path path = new Path();
            //for (Edge e = edgeTo[t]; e != null; e = edgeTo[e.from()]) {
            //    path.add(e);
            //}
            //return path;
        }
    
        ///**
        // * Returns the length of a shortest path from the source vertex {@code s} to vertex {@code v}.
        // * @param  v the destination vertex
        // * @return the length of a shortest path from the source vertex {@code s} to vertex {@code v};
        // *         {@code Double.POSITIVE_INFINITY} if no such path
        // * @throws IllegalArgumentException unless {@code 0 <= v < V}
        // */
        //public double distTo(int v) {
        //    //validateVertex(v);
        //    return distTo[v];
        //}
    
    
    //    /**
    //     * Returns a shortest path from the source vertex {@code s} to vertex {@code v}.
    //     *
    //     * @param  v the destination vertex
    //     * @return a shortest path from the source vertex {@code s} to vertex {@code v}
    //     *         as an iterable of edges, and {@code null} if no such path
    //     * @throws IllegalArgumentException unless {@code 0 <= v < V}
    //     */
    //    public Iterable<DirectedEdge> pathTo(int v) {
    //        //validateVertex(v);
    //        //If there is not a path:
    //        if (!(distTo[v] < Double.POSITIVE_INFINITY)) return null;
    //        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
    //        for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
    //            path.push(e);
    //        }
    //        return path;
    //    }
    
    
        //// check optimality conditions:
        //// (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
        //// (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
        //private boolean check(EdgeWeightedDigraph G, int s) {
    //
        //    // check that edge weights are non-negative
        //    for (DirectedEdge e : G.edges()) {
        //        if (e.weight() < 0) {
        //            System.err.println("negative edge weight detected");
        //            return false;
        //        }
        //    }
    
            //// check that distTo[v] and edgeTo[v] are consistent
            //if (distTo[s] != 0.0 || edgeTo[s] != null) {
            //    System.err.println("distTo[s] and edgeTo[s] inconsistent");
            //    return false;
            //}
            //for (int v = 0; v < G.V(); v++) {
            //    if (v == s) continue;
            //    if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
            //        System.err.println("distTo[] and edgeTo[] inconsistent");
            //        return false;
            //    }
            //}
    //
            //// check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
            //for (int v = 0; v < G.V(); v++) {
            //    for (DirectedEdge e : G.adj(v)) {
            //        int w = e.to();
            //        if (distTo[v] + e.weight() < distTo[w]) {
            //            System.err.println("edge " + e + " not relaxed");
            //            return false;
            //        }
            //    }
            //}
    //
            //// check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
            //for (int w = 0; w < G.V(); w++) {
            //    if (edgeTo[w] == null) continue;
            //    DirectedEdge e = edgeTo[w];
            //    int v = e.from();
            //    if (w != e.to()) return false;
            //    if (distTo[v] + e.weight() != distTo[w]) {
            //        System.err.println("edge " + e + " on shortest path not tight");
            //        return false;
            //    }
            //}
            //return true;
        //}
    
        //// throw an IllegalArgumentException unless {@code 0 <= v < V}
        //private void validateVertex(int v) {
        //    int V = distTo.length;
        //    if (v < 0 || v >= V)
        //        throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
        //}
    
        /**
         * Unit tests the {@code DijkstraSP} data type.
         *
         * @param args the command-line arguments
         */
//        public static void main(String[] args) {
//            In in = new In(args[0]);
//            EdgeWeightedDigraph G = new EdgeWeightedDigraph(in);
//            int s = Integer.parseInt(args[1]);
//    
//            // compute shortest paths
//            DijkstraSP sp = new DijkstraSP(G, s);
//    
//    
//            // print shortest path
//            for (int t = 0; t < G.V(); t++) {
//                if (distTo[v] < Double.POSITIVE_INFINITY) {
//                    StdOut.printf("%d to %d (%.2f)  ", s, t, distTo[v]); //Edited this from using distoTo method
//                    for (DirectedEdge e : sp.pathTo(t)) {
//                        StdOut.print(e + "   ");
//                    }
//                    StdOut.println();
//                }
//                else {
//                    StdOut.printf("%d to %d         no path\n", s, t);
//                }
//            }
//        }
//    
//    }
    
}



