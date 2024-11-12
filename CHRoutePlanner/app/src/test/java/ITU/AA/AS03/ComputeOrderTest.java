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
public class ComputeOrderTest {
    
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
        c = null;
    }

    //Case: Ordering of graph, where some nodes needs shortcuts, and some doesn't
    //e.g. Node 1 have 3 edges removed and 2 shortcuts added
    @Test void variousNodes_ranks_returnsCorrect() {
        c = new Contraction(smallContractGraph);
        c.getHierarchy().insert(0, c.computeOrder(0));
        c.getHierarchy().insert(1, c.computeOrder(1)); 
        c.getHierarchy().insert(2, c.computeOrder(2));
        assertEquals(5, c.getHierarchy().keyOf(0));
        assertEquals(-1, c.getHierarchy().keyOf(1));
        assertEquals(-2, c.getHierarchy().keyOf(2));
    }


    //Case: A (Lazy) ordering of nodes in a graph in the middle of getting contracted.
    @Test void CvariousNodes_ranks_returnsCorrect() {
        c = new Contraction(smallContractGraphProcessing);
        c.getHierarchy().insert(0, c.computeOrder(0, contracted));
        c.getHierarchy().insert(1, c.computeOrder(1, contracted)); 
        c.getHierarchy().insert(6, c.computeOrder(6, contracted));
        c.getHierarchy().insert(10, c.computeOrder(10, contracted));
        assertEquals(5, c.getHierarchy().keyOf(0));
        assertEquals(-1, c.getHierarchy().keyOf(1));
        assertEquals(-1, c.getHierarchy().keyOf(6));
        assertEquals(-1, c.getHierarchy().keyOf(10));
    }

    


}
