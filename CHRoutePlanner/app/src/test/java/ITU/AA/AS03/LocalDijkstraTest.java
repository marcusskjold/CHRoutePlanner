package ITU.AA.AS03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class LocalDijkstraTest {
    
    LocalDijkstra ld;
    Contraction c;


    //A small graph to test contractions on
    IndexedGraph smallContractGraph;
    //For a search from node 0:
    int pMax = 15;
    int addedShortcuts = 1;
    int settledNodesMax = 6;
    int settledNodesLim4 = 4;
    int distTo2 = 2;
    int distTo4 = 10;

    IndexedGraph smallContractGraphProcessing;
    boolean[] contracted;



    
    IndexedGraph tooFarApartGraph;
    int sNormal = 0;
    int tNormal = 3;
    int tooHigh = 100;
    int tooLow = -100;
    int tClose = 1;
    
   

    // Errors
    IndexedGraph directedGraph;
    IndexedGraph noNodeGraph;
    TestData noCalculation;
    TestData noEdges;
    TestData disconnectedNodes;
    TestData sourceIllegal;
    TestData targetIllegal;

    // Validation
    TestData nodesEqual;
    TestData targetClose;
    TestData sourcesFar;
    TestData multiplePaths;
    TestData containsZeroWeights;
    TestData falseShortestPath;

    @Nested
    class TestData {
        IndexedGraph graph;
        int distance;
        int relaxedEdges;
        LinkedList<DirectedEdge> path;
    }

    @BeforeAll
    void createTestData() {

        // Graphs
        smallContractGraph = new WeightedDiGraph(11);
        smallContractGraph.addUndirectedEdge(0, 1, 1);
        smallContractGraph.addUndirectedEdge(0, 3, 2);
        smallContractGraph.addUndirectedEdge(0, 4, 10);
        smallContractGraph.addUndirectedEdge(0, 5, 17);
        smallContractGraph.addUndirectedEdge(0, 6, 6);
        smallContractGraph.addUndirectedEdge(1, 2, 5);
        smallContractGraph.addUndirectedEdge(1, 7, 10);
        smallContractGraph.addUndirectedEdge(2, 3, 0);
        smallContractGraph.addUndirectedEdge(6, 8, 10);
        smallContractGraph.addUndirectedEdge(7, 10, 2);
        smallContractGraph.addUndirectedEdge(8, 9, 1);

        //The graph in the middle of being contracted
        smallContractGraphProcessing = new WeightedDiGraph(11);
        smallContractGraphProcessing.addUndirectedEdge(0, 1, 1);
        smallContractGraphProcessing.addUndirectedEdge(0, 3, 2);
        smallContractGraphProcessing.addUndirectedEdge(0, 4, 10);
        smallContractGraphProcessing.addUndirectedEdge(0, 5, 17);
        smallContractGraphProcessing.addUndirectedEdge(0, 6, 6);
        smallContractGraphProcessing.addUndirectedEdge(1, 2, 5);
        smallContractGraphProcessing.addUndirectedEdge(1, 7, 10);
        smallContractGraphProcessing.addUndirectedEdge(2, 3, 0);
        smallContractGraphProcessing.addUndirectedEdge(6, 8, 10);
        smallContractGraphProcessing.addUndirectedEdge(7, 10, 2);
        smallContractGraphProcessing.addUndirectedEdge(8, 9, 1);
        //Additional shortcuts
        smallContractGraphProcessing.addUndirectedEdge(1, 10, 12);
        smallContractGraphProcessing.addUndirectedEdge(6, 9, 11);
        //The contracted array:
        contracted = new boolean[11];
        contracted[2] = true;
        contracted[7] = true;
        contracted[8] = true;








        noNodeGraph = new WeightedDiGraph(0);
      
        tooFarApartGraph = new WeightedDiGraph(4);
        tooFarApartGraph.addUndirectedEdge(0, 1, 1000000000);
        tooFarApartGraph.addUndirectedEdge(1, 2, 1000000000);
        tooFarApartGraph.addUndirectedEdge(2, 3, 1000000000);
        // TestData

   

        // No edges
        IndexedGraph noEdgeGraph = new WeightedDiGraph(4);
        noEdges = new TestData();
        noEdges.graph = noEdgeGraph;
        noEdges.relaxedEdges = 0;

        // Disconnected nodes
        IndexedGraph disconnectedGraph = new WeightedDiGraph(5);
        disconnectedGraph.addUndirectedEdge(0, 1, 10);
        disconnectedGraph.addUndirectedEdge(0, 2, 20);
        disconnectedGraph.addUndirectedEdge(1, 2, 10);
        disconnectedGraph.addUndirectedEdge(4, 3, 30);
        disconnectedNodes = new TestData();
        disconnectedNodes.graph = disconnectedGraph;
        disconnectedNodes.relaxedEdges = 3;

        // ===========================
        // Validation

        // Target is adjacent to source
        targetClose = new TestData();
        targetClose.path = new LinkedList<>();
        targetClose.path.add(new DirectedEdge(0, 1, 10));
        targetClose.distance = 10;
        targetClose.relaxedEdges = 5;

    }
    @BeforeEach
    void resetAlgorithm() {
        ld = null;
        c = null;
    }



    //Case: A single local search from node 0 is performed
    @Test void exceedPMax_settled_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph, 10);
        ld.localSearch(0, pMax, 1);   
        assertEquals(settledNodesMax, ld.getSettledCount());
    }

    @Test void exceedSettledcount_settled_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph, 4);
        ld.localSearch(0, pMax, 1);   
        assertEquals(settledNodesLim4, ld.getSettledCount());
    }

    @Test void exceedPMax_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph, 10);
        ld.localSearch(0, pMax, 1);   
        assertEquals(distTo2, ld.distance(2));
        assertEquals(distTo4, ld.distance(4));
    }

    @Test void nodeNotSettled_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph, 10);
        ld.localSearch(0, pMax, 1);   
        assertEquals(Integer.MAX_VALUE, ld.distance(7));
        assertEquals(Integer.MAX_VALUE, ld.distance(9));
        assertEquals(Integer.MAX_VALUE, ld.distance(1));
    }

    //Case: Multiple searches from node 0 is performed:
    //
    @Test void MultipleExceedPMax_settled_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph, 10);
        ld.localSearch(7, pMax, 1); 
        ld.localSearch(0, pMax, 1);   
        assertEquals(settledNodesMax, ld.getSettledCount());
    }

    @Test void MultipleExceedSettledcount_settled_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph, 10);
        ld.localSearch(7, pMax, 1);
        ld.setLimit(4);
        ld.localSearch(0, pMax, 1);   
        assertEquals(settledNodesLim4, ld.getSettledCount());
    }

    @Test void MultipleExceedPMax_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph, 10);
        ld.localSearch(7, pMax, 1);
        ld.localSearch(0, pMax, 1);   
        assertEquals(distTo2, ld.distance(2));
        assertEquals(distTo4, ld.distance(4));
    }

    @Test void MultipleNodeNotSettled_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph, 10);
        ld.localSearch(7, pMax, 1);
        ld.localSearch(0, pMax, 1);   
        assertEquals(Integer.MAX_VALUE, ld.distance(7));
        assertEquals(Integer.MAX_VALUE, ld.distance(9));
        assertEquals(Integer.MAX_VALUE, ld.distance(1));
    }

    //Case: A search with no connected nodes (Due to nodes being ignored):
    @Test void NoConnection_settled_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph, 100);
        ld.localSearch(10, pMax, 7);   
        assertEquals(1, ld.getSettledCount());
    }

    @Test void NoConnection_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph, 100);
        ld.localSearch(10, pMax, 7);   
        assertEquals(Integer.MAX_VALUE, ld.distance(7));
    }

    //Case: A search with nodes too far apart: 
    @Test void nodesTooFarApart_calculate_throws() {
        ld = new LocalDijkstra(tooFarApartGraph, 100);
        //Exception e = assertThrows(ArithmeticException.class, () ->
            ld.localSearch(sNormal, pMax, 100);
        assertEquals(Integer.MAX_VALUE, ld.distance(tNormal));
    }


////Tests of the localDijkstra-version that takes account of contractions:
/// 

@Test void CexceedPMax_settled_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph, 10);
        ld.localSearch(0, pMax, 1, contracted);   
        assertEquals(settledNodesMax, ld.getSettledCount());
    }

    @Test void CexceedSettledcount_settled_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph, 4);
        ld.localSearch(0, pMax, 1, contracted);   
        assertEquals(settledNodesLim4, ld.getSettledCount());
    }

   
    @Test void CexceedPMax_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraphProcessing, 3);
        ld.localSearch(9, pMax, 1);
        ld.localSearch(10, pMax, 1, contracted);
        ld.setLimit(10);
        ld.localSearch(0, pMax, 1, contracted);   
        assertEquals(Integer.MAX_VALUE, ld.distance(2));
        assertEquals(distTo4, ld.distance(4));
        assertEquals(17, ld.distance(9));
    }

     //TODO: something with settled vs reached?
    @Test void CnodeNotSettled_distance_returnsCorrect() {
<<<<<<< HEAD
        ld = new LocalDijkstra(smallContractGraphProcessing);
        ld.localSearch(0, 10, pMax, 1, contracted);   
=======
        ld = new LocalDijkstra(smallContractGraph, 10);
        ld.localSearch(0, pMax, 1);   
>>>>>>> 0fa0fdc (Start combining methods into a complete contraction pipeline)
        assertEquals(Integer.MAX_VALUE, ld.distance(7));
        assertEquals(Integer.MAX_VALUE, ld.distance(1));
    }







}
