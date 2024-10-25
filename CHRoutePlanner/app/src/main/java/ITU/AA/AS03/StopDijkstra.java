package ITU.AA.AS03;


//TODO: adapt to how we implement the graph, edges, path etc.

import java.util.Stack;

import com.google.common.collect.HashBiMap;

import java.util.HashMap;

public class StopDijkstra implements SPFinder {
    

        public StopDijkstra() {
            
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
            Edge[] edgeTo= new Edge[G.V()];    // edgeTo[v] = last edge on shortest s->v path 
            //Initialize distances
            for (int v = 0; v < G.V(); v++)
                    {distTo[v] = Double.POSITIVE_INFINITY;}
            distTo[s] = 0.0;
            
            IndexMinPQ<Double> pq = new IndexMinPQ<Double>(G.V()); // priority queue of vertices
            //insert the source into the priorityqueue
            pq.insert(s, distTo[s]);
            // relax vertices in order of distance from s
            while (!pq.isEmpty()) {
                int v = pq.delMin();
                //early stopping criterion (stops when target added to longest path tree)
                if (v == t) {
                    break;
                }
                for (Edge e : G.adj(v)){
                    relax(e, distTo, edgeTo, pq);
                }
            }
            //If there is not a path:
            if (!(distTo[t] < Double.POSITIVE_INFINITY)) return -1.0;
            //Else return distance
            else return distTo[t];
        }
    
}



