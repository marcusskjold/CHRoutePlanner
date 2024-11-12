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
        //TODO: Be aware of coupling with graph (if another object, then this might need to be passed to constructor)
        G= graph;
        V = graph.V();
        pq = new IndexMinPQ<>(V); 
        distTo= new int[V]; 
        searchGen = new int[V];
        searchCount = 0;
        settledCount = 0;

        
    }

    //For debugging purposes
    public int getSettledCount() {
        return settledCount;
    }

    /*Performs local dijkstra search from given source node until having settled nodes equivalent
     * to provided settle-limit (or V)
     * We also set a limit to search for
     * Also the contracted node is ignored (TODO: maybe a whole array for contracted nodes in general would be useful)
    */
    public void localSearch(int source, int settleLimit, int distLimit, int ignored) {
        //increment the search count:
        searchCount ++;
        //initialize track of settled nodes:
        settledCount = 0;
        //Initialize the dijkstra search
        searchGen[source] = searchCount;
        distTo[source] = 0;
        if(pq.contains(source)) {
            pq.changeKey(source, distTo[source]);
        } else {pq.insert(source, distTo[source]);}
        
        while(!pq.isEmpty() && settledCount<settleLimit) {
            int node = pq.delMin();
            settledCount++;
            //If we settle node greater than the max distance shortest distance through v, return
            if(distTo[node] > distLimit) {
                break;
            }
            

            for (DirectedEdge e : G.edgesFrom(node)){
                relax(e, ignored);
            }
        }

       
        //settles nodes not yet in this generation of search

        //TODO: Maybe return an edgedifference?
    }

    //returns whether node v was reached in this search (TODO: do we need settled, rather)
    public boolean reached(int v) {
        return searchGen[v] == searchCount;
    }

    //Returns distance to target node
    public int distance(int target) {
        if(reached(target))
            return distTo[target];
        else {
            return Integer.MAX_VALUE;
        }
    }
   

    protected void relax(DirectedEdge e, int ignored) {
        int v = e.from(), w = e.to();
        //If current edge goes to contracted node, we ignore it
        if(w == ignored) {
            return;
        }
        int newDist = distTo[v] + e.weight();
        if (newDist < distTo[v])
            throw new ArithmeticException("Integer overflow: Distances are too high");
        
        //If not yet reached, update distTo and searchGen
        if(searchGen[w] < searchCount) {
            distTo[w] = newDist;
            searchGen[w] = searchCount;
        }
        else if(distTo[w] > newDist) {
            distTo[w] = newDist;
        } else {
            return;
        } 
        if (pq.contains(w)) pq.changeKey(w, newDist);
        else                pq.insert(w, newDist);


    }


    public void localSearch(int source, int settleLimit, int distLimit, int ignored, boolean[] contracted) {
        //increment the search count:
        searchCount ++;
        //initialize track of settled nodes:
        settledCount = 0;
        //Initialize the dijkstra search (maybe fine not to check contracted)
        searchGen[source] = searchCount;
        distTo[source] = 0;
        if(pq.contains(source)) {
            pq.changeKey(source, distTo[source]);
        } else {pq.insert(source, distTo[source]);}
        
        while(!pq.isEmpty() && settledCount<settleLimit) {
            int node = pq.delMin();
            if(!contracted[node]) {
                settledCount++;
                //If we settle node greater than the max distance shortest distance through v, return
                if(distTo[node] > distLimit) {
                    break;
                }
                for (DirectedEdge e : G.edgesFrom(node)){
                    if(!contracted[e.to()]) {
                        relax(e, ignored);
                    }
                }
            }
        }

        
    }




}
