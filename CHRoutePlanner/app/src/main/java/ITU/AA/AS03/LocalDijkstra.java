package ITU.AA.AS03;

public class LocalDijkstra {
    private int[] distTo;
    private int[] searchGen; //Which search the node has last been a part of
    private int searchCount; //The number/generation of the current search
    private IndexedGraph G;
    private IndexMinPQ<Integer> pq;
    private int V;
    private int settledCount;

    public LocalDijkstra(IndexedGraph graph) {
        //TODO:
        G= graph;
        V = graph.V();
        pq = new IndexMinPQ<>(V); 
        distTo= new int[V]; 
        searchGen = new int[V];
        searchCount = 0;
        settledCount = 0;

        
    }


    /*Performs local dijkstra search from given source node until having settled nodes equivalent
     * to provided settle-limit (or V)
    */
    public void localSearch(int source, int settleLimit) {
    //TODO: Implement
        //increment the search count:
        searchCount ++;
        //initialize track of settled nodes:
        settledCount = 0;
        //Initialize the dijkstra search
        searchGen[source] = searchCount;
        distTo[source] = 0;
        pq.insert(source, distTo[source]);
        
        while(!pq.isEmpty() || settledCount<settleLimit) {
            int node = pq.delMin();

            for (DirectedEdge e : G.edgesFrom(node)){
                relax(e);
            }
            settledCount++; //TODO: place this in loop
        }

       
        //settles nodes not yet in this generation of search

        //TODO: Maybe return an edgedifference?
    }

   

    protected void relax(DirectedEdge e) {
        int v = e.from(), w = e.to();
        int newDist = distTo[v] + e.weight();
        if (newDist < distTo[v])
            throw new ArithmeticException("Integer overflow: Distances are too high");
        
        //If not yet reached, update distTo and searchGen
        if(searchGen[w] < searchCount) {
            distTo[w] = newDist;
            searchGen[w] = searchCount;
            pq.insert(w, newDist);
        }
        else if(distTo[w] > newDist) {
            distTo[w] = newDist;
            if (pq.contains(w)) pq.decreaseKey(w, newDist);
            else                pq.insert(w, newDist);
        } else {
            return;
        }
        settledCount++;
        
        
    }





}
