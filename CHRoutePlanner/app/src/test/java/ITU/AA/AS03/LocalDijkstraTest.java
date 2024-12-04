package ITU.AA.AS03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class LocalDijkstraTest {
    
    LocalDijkstra ld;

    //A small graph to test contractions on
    IndexedGraph smallContractGraph;
    //For a search from node 0:
    int pMax = 15;
    int distTo2 = 2;
    int distTo4 = 10;

    IndexedGraph smallContractGraphProcessing;
    boolean[] noContracted;
    boolean[] intermediateContracted;

    IndexedGraph tooFarApartGraph;
    int sNormal = 0;
    int tNormal = 3;
    

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

        //The contracted arrays:
        noContracted = new boolean[11];

        intermediateContracted = new boolean[11];
        intermediateContracted[2] = true;
        intermediateContracted[7] = true;
        intermediateContracted[8] = true;
      
        tooFarApartGraph = new WeightedDiGraph(4);
        tooFarApartGraph.addUndirectedEdge(0, 1, 1000000000);
        tooFarApartGraph.addUndirectedEdge(1, 2, 1000000000);
        tooFarApartGraph.addUndirectedEdge(2, 3, 1000000000);

    }
    
    @BeforeEach
    void resetAlgorithm() {
        ld = null;
    }


    //Case: A single local search from node 0 is performed
    @Test void exceedPMax_settled_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph);
        ld.localSearch(0, pMax, 1, noContracted);   
        assertTrue(ld.settled(0));
        assertFalse(ld.settled(1));
        assertTrue(ld.settled(2));
        assertTrue(ld.settled(3));
        assertTrue(ld.settled(4));
        assertFalse(ld.settled(5));
        assertTrue(ld.settled(6));
        assertFalse(ld.settled(7));
        assertTrue(ld.settled(8));
        assertFalse(ld.settled(9));
        assertFalse(ld.settled(10));
    }


    @Test void exceedPMax_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph);
        ld.localSearch(0, pMax, 1, noContracted);   
        assertEquals(distTo2, ld.distance(2));
        assertEquals(distTo4, ld.distance(4));
    }

    @Test void nodeNotSettled_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph);
        ld.localSearch(0, pMax, 1, noContracted);   
        assertEquals(Integer.MAX_VALUE, ld.distance(7));
        assertEquals(Integer.MAX_VALUE, ld.distance(9));
        assertEquals(Integer.MAX_VALUE, ld.distance(1));
    }

    //Case: Multiple searches from node 0 is performed:
    //
    @Test void MultipleExceedPMax_settled_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph);
        ld.localSearch(7, pMax, 1, noContracted); 
        ld.localSearch(0, pMax, 1, noContracted);   
        assertTrue(ld.settled(0));
        assertFalse(ld.settled(1));
        assertTrue(ld.settled(2));
        assertTrue(ld.settled(3));
        assertTrue(ld.settled(4));
        assertFalse(ld.settled(5));
        assertTrue(ld.settled(6));
        assertFalse(ld.settled(7));
        assertTrue(ld.settled(8));
        assertFalse(ld.settled(9));
        assertFalse(ld.settled(10));
    }


    @Test void MultipleExceedPMax_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph);
        ld.localSearch(7, pMax, 1, noContracted);
        ld.localSearch(0, pMax, 1, noContracted);   
        assertEquals(distTo2, ld.distance(2));
        assertEquals(distTo4, ld.distance(4));
    }

    @Test void MultipleNodeNotSettled_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph);
        ld.localSearch(7, pMax, 1, noContracted);
        ld.localSearch(0, pMax, 1, noContracted);   
        assertEquals(Integer.MAX_VALUE, ld.distance(7));
        assertEquals(Integer.MAX_VALUE, ld.distance(9));
        assertEquals(Integer.MAX_VALUE, ld.distance(1));
    }

    //Case: A search with no connected nodes (Due to nodes being ignored):
    @Test void NoConnection_settled_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph);
        ld.localSearch(10, pMax, 7, noContracted);   
        assertTrue(ld.settled(10));
    }

    @Test void NoConnection_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph);
        ld.localSearch(10, pMax, 7, noContracted);   
        assertEquals(Integer.MAX_VALUE, ld.distance(7));
    }

    //Case: A search with nodes too far apart: 
    @Test void nodesTooFarApart_distance_returnsCorrect() {
        ld = new LocalDijkstra(tooFarApartGraph);
        ld.localSearch(sNormal, pMax, 100, noContracted);
        assertEquals(Integer.MAX_VALUE, ld.distance(tNormal));
    }


////Tests of the localDijkstra-version that takes account of contractions:
/// 

@Test void CexceedPMax_settled_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph);
        ld.localSearch(0, pMax, 1, intermediateContracted);   
        assertTrue(ld.settled(0));
        assertFalse(ld.settled(1));
        assertFalse(ld.settled(2));
        assertTrue(ld.settled(3));
        assertTrue(ld.settled(4));
        assertTrue(ld.settled(5));
        assertTrue(ld.settled(6));
        assertFalse(ld.settled(7));
        assertFalse(ld.settled(8));
        assertFalse(ld.settled(9));
        assertFalse(ld.settled(10));
    }

   
    @Test void CexceedPMax_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraphProcessing);
        ld.localSearch(9, pMax, 1, noContracted);
        ld.localSearch(10, pMax, 1, intermediateContracted);
        ld.localSearch(0, pMax, 1, intermediateContracted);   
        assertEquals(Integer.MAX_VALUE, ld.distance(2));
        assertEquals(distTo4, ld.distance(4));
        assertEquals(17, ld.distance(9));
    }

    @Test void CnodeNotSettled_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraphProcessing);
        ld.localSearch(0, pMax, 1, intermediateContracted);   
        assertEquals(Integer.MAX_VALUE, ld.distance(7));
        assertEquals(Integer.MAX_VALUE, ld.distance(1));
    }

}
