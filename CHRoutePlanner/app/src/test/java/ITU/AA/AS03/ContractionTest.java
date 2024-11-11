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
public class ContractionTest {
    
    LocalDijkstra ld;
    Contraction c;


    //A small graph to test contractions on
    IndexedGraph smallContractGraph;
    int pMax = 15;
    int addedShortcuts = 1;
    int settledNodes = 6;


    IndexedGraph smallGraph;
    IndexedGraph tooFarApartGraph;
    int sNormal = 0;
    int tNormal = 3;
    int tooHigh = 100;
    int tooLow = -100;
    int tClose = 1;
    int smallGraphRelaxedEdges = 4;
    LinkedList<DirectedEdge> multiplePathOne;
    LinkedList<DirectedEdge> multiplePathTwo;

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
        smallContractGraph = new WeightedDiGraph(9);
        smallContractGraph.addUndirectedEdge(0, 1, 1);
        smallContractGraph.addUndirectedEdge(0, 3, 2);
        smallContractGraph.addUndirectedEdge(0, 4, 10);
        smallContractGraph.addUndirectedEdge(0, 5, 17);
        smallContractGraph.addUndirectedEdge(0, 6, 6);
        smallContractGraph.addUndirectedEdge(1, 2, 5);
        smallContractGraph.addUndirectedEdge(1, 7, 10);
        smallContractGraph.addUndirectedEdge(2, 3, 1);
        smallContractGraph.addUndirectedEdge(6, 8, 10);





        noNodeGraph = new WeightedDiGraph(0);
        smallGraph = new WeightedDiGraph(4);
        smallGraph.addUndirectedEdge(0, 1, 10);
        smallGraph.addUndirectedEdge(0, 2, 20);
        smallGraph.addUndirectedEdge(1, 2, 10);
        smallGraph.addUndirectedEdge(1, 3, 30);
        smallGraph.addUndirectedEdge(2, 3, 10);

        tooFarApartGraph = new WeightedDiGraph(4);
        tooFarApartGraph.addUndirectedEdge(0, 1, 1000000000);
        tooFarApartGraph.addUndirectedEdge(1, 2, 1000000000);
        tooFarApartGraph.addUndirectedEdge(2, 3, 1000000000);
        // TestData

        // No calculation
        noCalculation = new TestData();
        noCalculation.graph = smallGraph;

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

        // Shortest path includes edge with zero weight
        IndexedGraph zeroWeightGraph = new WeightedDiGraph(4);
        zeroWeightGraph.addUndirectedEdge(0, 1, 10);
        zeroWeightGraph.addUndirectedEdge(0, 2, 0);
        zeroWeightGraph.addUndirectedEdge(1, 2, 10);
        zeroWeightGraph.addUndirectedEdge(1, 3, 30);
        zeroWeightGraph.addUndirectedEdge(2, 3, 10);
        containsZeroWeights = new TestData();
        containsZeroWeights.graph = zeroWeightGraph;
        LinkedList<DirectedEdge> zeroWeightPath = new LinkedList<>();
        zeroWeightPath.add(new DirectedEdge(0, 2, 0));
        zeroWeightPath.add(new DirectedEdge(2, 3, 10));
        containsZeroWeights.path = zeroWeightPath;
        containsZeroWeights.distance = 10;
        containsZeroWeights.relaxedEdges = 5;

        // Multiple shortest paths
        IndexedGraph multiplePathGraph = new WeightedDiGraph(4);
        multiplePathGraph.addUndirectedEdge(0, 1, 10);
        multiplePathGraph.addUndirectedEdge(0, 2, 10);
        multiplePathGraph.addUndirectedEdge(1, 2, 10);
        multiplePathGraph.addUndirectedEdge(1, 3, 10);
        multiplePathGraph.addUndirectedEdge(2, 3, 10);
        multiplePathOne = new LinkedList<DirectedEdge>();
        multiplePathOne.add(new DirectedEdge(0, 1, 10));
        multiplePathOne.add(new DirectedEdge(1, 3, 10));
        multiplePathTwo = new LinkedList<DirectedEdge>();
        multiplePathTwo.add(new DirectedEdge(0, 2, 10));
        multiplePathTwo.add(new DirectedEdge(2, 3, 10));
        multiplePaths = new TestData();
        multiplePaths.graph = multiplePathGraph;
        multiplePaths.relaxedEdges = 5;
        multiplePaths.distance = 20;

        // Directional issues
        directedGraph = new WeightedDiGraph(4);
        directedGraph.addDirectedEdge(0, 1, 10);
        directedGraph.addDirectedEdge(1, 2, 10);
        directedGraph.addDirectedEdge(3, 2, 10);

        // Bidirectional search specific test
        IndexedGraph falseShortestPathGraph = new WeightedDiGraph(6);
        falseShortestPathGraph.addUndirectedEdge(0, 1, 30);
        falseShortestPathGraph.addUndirectedEdge(0, 2, 10);

        falseShortestPathGraph.addUndirectedEdge(3, 4, 30); // To keep the naming of target consistent
        falseShortestPathGraph.addUndirectedEdge(3, 5, 10);

        falseShortestPathGraph.addUndirectedEdge(1, 4, 10);
        falseShortestPathGraph.addUndirectedEdge(2, 5, 40);
        falseShortestPath = new TestData();
        falseShortestPath.graph = falseShortestPathGraph;
        falseShortestPath.distance = 60;
        falseShortestPath.relaxedEdges = 8;
        LinkedList<DirectedEdge> trueShortestPath = new LinkedList<>();
        trueShortestPath.add(new DirectedEdge(0, 2, 10));
        trueShortestPath.add(new DirectedEdge(2, 5, 40));
        trueShortestPath.add(new DirectedEdge(5, 3, 10));
        falseShortestPath.path = trueShortestPath;

        

    }
    @BeforeEach
    void resetAlgorithm() {
        ld = null;
        c = null;
    }

    @Test void containsZeroWeight_distance_returnsCorrect() {
        ld = new LocalDijkstra(smallContractGraph);
        //c = new Contraction(directedGraph);
        ld.localSearch(0, 10, pMax, 1); 

        
        assertEquals(settledNodes, ld.getSettledCount());
    }


}
