package ITU.AA.AS03;

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

    public Contraction(IndexedGraph graph) {
        int V= graph.V();
        contracted = new boolean[V];
        rank = new int[V];
        this.graph = graph;
        nodeHierarchy = new IndexMinPQ<>(V);
        
    }

    public void contract(int v) {
        //First find max weight path from an edge u to an edge w through v:
        List<DirectedEdge> edges = graph.getEdges(v);
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
            //check for witness path from a node in U to a node in W exists
            List<DirectedEdge> altEdges = graph.getEdges(to.to() );
            for (DirectedEdge directedEdge : altEdges) {
                if(edges.contains(directedEdge.to())) {
                    if(directedEdge.weight() < ) {
                        // (Maybe max don't have to be found, and rather one could try to
                        // just look for other shortest paths)
                    }
                }
                
            }

        
        }
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
    private int computeOrder(int v) {
        int order = 0;
        //TODO: implement
        return order;
    }

     public IndexedGraph preProcess(IndexedGraph graph) {
        IndexedGraph contractedGraph = graph;
        //First order the nodes
        //maybe not all nodes need to be ordered here, or at least at once?
        for(int i=0;i< graph.V();i++) {
            nodeHierarchy.insert(i, computeOrder(i)); //Order could also be a collection that is filled before (different options)
        }

        //Go through the ordered notes
        while(!nodeHierarchy.isEmpty()) {
            //get the minimum element of the queue (continue lazy evaluating till viable)
            int next;
            do {
            next = nodeHierarchy.minIndex();
            nodeHierarchy.changeKey(next, computeOrder(next));
            } while (nodeHierarchy.minIndex()!=next);
            //contract the node
            //TODO (implement contract): 
        }


        return contractedGraph;

     }

     //TODO: Implement something that writes graph to file
     //TODO: Make design choices for graph.



}
