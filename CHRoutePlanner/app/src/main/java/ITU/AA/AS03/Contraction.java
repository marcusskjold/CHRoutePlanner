package ITU.AA.AS03;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Contraction {
    //Take input Graph 'graph'

    //maybe make clone 'contractedGraph' (Or just write clone directly in file (but this isn't too many lines, so should be okay?)?)

    //Gradually remove edges and node from 'graph' and add shortcuts etc. to 'contractedGraph'
    //Maybe don't "remove" as such, as it's expensive? Could we have boolean array instead?

    //Write contracted Graph to file


    private boolean[] contracted; //field for maintaining whether ignored
    private int[] rank; //field for maintaining ranks (maybe not needed or maybe Node class preferred)
    private IndexedGraph graph;
    private IndexMinPQ<Integer> nodeHierarchy;
    private LocalDijkstra ld;
    private int shortcuts;
    //private DijkstraSimple dijkstra; 

    public Contraction(IndexedGraph graph) {
        int V= graph.V();
        contracted = new boolean[V];
        rank = new int[V];
        this.graph = graph;
        nodeHierarchy = new IndexMinPQ<>(V);
        ld = new LocalDijkstra(graph);
        shortcuts = 0;
        //dijkstra = new DijkstraSimple(graph);
        
    }

    //Unsafe helper for now
    public IndexMinPQ<Integer> getHierarchy() {
        return nodeHierarchy;
    }



    public int computeOrder(int v) {
        int order = 0;
        int shortcutCount = 0;
        
        
        //First find max weight path from an edge u to an edge w through v:
        List<DirectedEdge> edges = graph.edgesTo(v);
        int size = edges.size();
        
        int maxDist = 0;
        for(int i=0;i<size-1;i++) {
            DirectedEdge to = edges.get(i); //define edge going to: Will be different each time since no parallel edges?
            
            for(int j=i+1;j<size;j++) { //Here one could loop through adjacency-list and check for witness-paths, simply
                DirectedEdge from = edges.get(j); //define edge going from.
                //if(to.to() == from.to()) { //Is this needed?
                //    break;
                //}
                int pathLength = to.weight() + from.weight();
                if(pathLength > maxDist) {
                    maxDist = pathLength;
                }
            }

        }

        for(int i= 0; i <size-1;i++) {
            int t = edges.get(i).from();
            ld.localSearch(t, 50, maxDist, v);
            //witness[i] = true;
            for(int j= i+1; j< size; j++ ) {
                //TODO: Maybe test if they are equal (in case of parallel edges?)
                //if(!witness[j]) {
                //If it reached the given node in the local search on subset, and it is shorter:
                int f = edges.get(j).from();
                    if(ld.distance(f) > edges.get(i).weight() + edges.get(j).weight()) {
                        //add shortcut or count for shortcut:
                        shortcutCount++;
                        //System.out.println("shortcut counted from " + edges.get(i).from() + "to " + edges.get(j).from());
                    }
                //}
            }
        }
        //The edge-difference is counted (changes in edges from contraction)
        order = shortcutCount - size;
        return order;
    }

    /*Contraction plan
     * 
     * Fill up PriorityQueue with all the nodes. Their priority based on edge-difference.
     * Edge difference made based on hypothetical contraction.
     * The local searches here can just be based on cap for amount of nodes to settle?
     * 
     * Next pick the first one to contract:
     * Update its priority (and proceed if still lowest?)
     * Do a local search with a certain hop limit (maybe just 1, or a bucket.
     * Or maybe with a switchup at some point. Maybe we should experiment with it). 
     * Here one looks for witness paths,
     * using uncontracted nodes, excluding the one getting contracted (contracted-array or field?).
     * Maybe one Dijkstra can cover multiple witness paths for one node?
     * Add shortcuts for when no available witness paths.
     *           In principle, one can choose to subsequently ignore edges that are longer than found paths
     * Also, if a node is settled with distance longer than the longest <u,v,w>, we should terminate the search.
     */


    //
   

     public IndexedGraph preProcess() {
        //LocalDijkstra ld = new LocalDijkstra(graph);
        IndexedGraph contractedGraph = graph;
        //First order the nodes
        //maybe not all nodes need to be ordered here, or at least at once?
        for(int i=0;i< graph.V();i++) {
            System.out.println("ranking node no: " + i);
            if(graph.edgesTo(i).size() == 1) {
                if(graph.edgesFrom(i).size() == 1) {
                    continue;
                }
            }
            nodeHierarchy.insert(i, computeOrder(i)); //Order could also be a collection that is filled before (different options)
        }

        ////Go through the ordered notes
        //while(!nodeHierarchy.isEmpty()) {
        //    //get the minimum element of the queue (continue lazy evaluating till viable)
        //    int next;
        //    do {
        //    next = nodeHierarchy.minIndex();
        //    nodeHierarchy.changeKey(next, computeOrder(next, graph));
        //    } while (nodeHierarchy.minIndex()!=next);
        //    //contract the node
        //    next = nodeHierarchy.delMin();
        //    contract(next); //Maybe should also take graph as input
        //    //order the neighbors
        //    for (DirectedEdge e : graph.edgesTo(next)) { //Need to be all neighbors (maybe method like 'getNeighbors')
        //        int neighbor = e.from();
        //        if(!contracted[neighbor])
        //            computeOrder(neighbor, graph); //Just needs to be the other point
        //    }
//
        //    //TODO: (implement contract): 
        //}


        return contractedGraph;

     }

     //TODO: Implement something that writes graph to file
     //TODO: Make design choices for graph. (maybe undirected actually easiesr here?)



     //Version of graph that takes account of contractions
     public int computeOrder(int v, boolean[] contracted) {
        int order = 0;
        int shortcutCount = 0;
        
        List<DirectedEdge> edges = new ArrayList<>();
        List<DirectedEdge> l = graph.edgesTo(v);
        for(int i=0;i<graph.edgesTo(v).size();i++) {
            DirectedEdge e = l.get(i);
            if(!contracted[e.from()])
            edges.add(e);
        }
        //First find max weight path from an edge u to an edge w through v:
        //List<DirectedEdge> edges = graph.edgesTo(v);
        int size = edges.size();
        
        if(size == 1) {
            return -1;
        }
        int maxDist = 0;
        for(int i=0;i<size-1;i++) {
            DirectedEdge to = edges.get(i); //define edge going to: Will be different each time since no parallel edges?
            
                for(int j=i+1;j<size;j++) { //Here one could loop through adjacency-list and check for witness-paths, simply
                    DirectedEdge from = edges.get(j); //define edge going from.
                    //if(to.to() == from.to()) { //Is this needed?
                    //    break;
                    //}
                    
                        int pathLength = to.weight() + from.weight();
                        if(pathLength > maxDist) {
                            maxDist = pathLength;
                        }
                    
                }
            
        }

        for(int i= 0; i <size-1;i++) {
            int t = edges.get(i).from();
            
                ld.localSearch(t, 50, maxDist, v, contracted);
                for(int j= i+1; j< size; j++ ) {
                    //TODO: Maybe test if they are equal (in case of parallel edges?)
                    
                    //If it reached the given node in the local search on subset, and it is shorter:
                    int f = edges.get(j).from();
                    
                        if(ld.distance(f) > edges.get(i).weight() + edges.get(j).weight()) {
                            //add shortcut or count for shortcut:
                            shortcutCount++;
                            //System.out.println("shortcut counted from " + edges.get(i).from() + "to " + edges.get(j).from());
                        }
                    
                    
                }
            
        }
        //The edge-difference is counted (changes in edges from contraction)
        order = shortcutCount - size;
        return order;
    }
     


}
