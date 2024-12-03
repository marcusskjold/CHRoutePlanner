package ITU.AA.AS03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * DijkstraSimpleTest
 */
@TestInstance(Lifecycle.PER_CLASS)
public class DijkstraBidirectionalTest {

    DijkstraBidirectional ds;

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
        ds = null;
    }

    // ================== TESTS ===========================
    // ====================================================


    // ================== THROW ===========================

    //        |||| Case: graph has no nodes ||||
    //        using noNodeGraph
    @Test void graphContainsNoNodes_constructor_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            ds = new DijkstraBidirectional(noNodeGraph));
    }

    //         |||| Case: graph is null ||||
    @Test void null_constructor_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            ds = new DijkstraBidirectional(null));
    }

    //     |||| Case: attempt to calculate twice ||||
    //          uses smallGraph
    @Test void calculateTwice_calculate_throws() {
        ds = new DijkstraBidirectional(smallGraph);
        ds.calculate(sNormal, tNormal);
        assertThrows(IllegalStateException.class, () ->
            ds.calculate(sNormal, tNormal));
    }

    //     |||| Case: calculate with bad arguments ||||
    @Test void sourceTooHigh_calculate_throws() {
        ds = new DijkstraBidirectional(smallGraph);
        assertThrows(IllegalArgumentException.class, () ->
            ds.calculate(tooHigh, tNormal));
    }

    @Test void sourceTooLow_calculate_throws() {
        ds = new DijkstraBidirectional(smallGraph);
        assertThrows(IllegalArgumentException.class, () ->
            ds.calculate(tooLow, tNormal));
    }

    @Test void targetTooHigh_calculate_throws() {
        ds = new DijkstraBidirectional(smallGraph);
        assertThrows(IllegalArgumentException.class, () ->
            ds.calculate(sNormal, tooHigh));
    }

    @Test void targetTooLow_calculate_throws() {
        ds = new DijkstraBidirectional(smallGraph);
        assertThrows(IllegalArgumentException.class, () ->
            ds.calculate(sNormal, tooLow));
    }

    // |||| Case: source and target are too far apart

    @Test void nodesTooFarApart_calculate_throws() {
        ds = new DijkstraBidirectional(tooFarApartGraph);
        assertThrows(ArithmeticException.class, () ->
            ds.calculate(sNormal, tNormal));
    }

    // ================== EDGE CASES ===========================

    //|||| Case: if calculate() has not been called ||||
    //           uses noCalculation test data

    @Test void noCalculation_distance_returnsErrorCode() {
        ds = new DijkstraBidirectional(noCalculation.graph);
        assertEquals(-1, ds.distance());
    }

    @Test void noCalculation_retrievePath_returnsNull() {
        ds = new DijkstraBidirectional(noCalculation.graph);
        assertEquals(null, ds.retrievePath());
    }

    @Test void noCalcultion_relaxedEdges_returnMinusOne() {
        ds = new DijkstraBidirectional(noCalculation.graph);
        assertEquals(-1, ds.relaxedEdges());
    }

    //     |||| Case: graph contains no edges ||||
    //                uses noEdges

    @Test void graphContainsNoEdges_calculate_returnsFalse() {
        ds = new DijkstraBidirectional(noEdges.graph);
        assertFalse(ds.calculate(sNormal, tNormal));
    }

    @Test void graphContainsNoEdges_distance_returnsMAX() {
        ds = new DijkstraBidirectional(noEdges.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(Integer.MAX_VALUE, ds.distance());
    }

    @Test void graphContainsNoEdges_retrievePath_returnsNull() {
        ds = new DijkstraBidirectional(noEdges.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(null, ds.retrievePath());

    }

    @Test void graphContainsNoEdges_relaxedEdges_returnsZero() {
        ds = new DijkstraBidirectional(noEdges.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(0, ds.relaxedEdges());
    }

    // |||| Case: Graph is not connected and source is disconnected from target ||||
    //            Uses disconnectedNodes
    @Test void nodesDisconnected_calculate_returnFalse() {
        ds = new DijkstraBidirectional(disconnectedNodes.graph);
        assertFalse(ds.calculate(sNormal, tNormal));
    }

    @Test void nodesDisconnected_distance_returnMAX() {
        ds = new DijkstraBidirectional(disconnectedNodes.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(Integer.MAX_VALUE, ds.distance());
    }

    @Test void nodesDisconnected_retrievePath_returnNull() {
        ds = new DijkstraBidirectional(disconnectedNodes.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(null, ds.retrievePath());
    }

    @Test void nodesDisconnected_relaxedEdges_returnCorrect() {
        ds = new DijkstraBidirectional(disconnectedNodes.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(disconnectedNodes.relaxedEdges, ds.relaxedEdges());
    }

    // |||| Case: The graph is directed and source and target both connect to a common node
    @Test void directedGraph_calculate_returnsFalse() {
        ds = new DijkstraBidirectional(directedGraph);
        assertFalse(ds.calculate(sNormal, tNormal));
    }

    // ================== VALIDATION ===========================

    // |||| Case: Source and target are the same ||||
    //            Uses nodesEqual

    @Test void nodesEqual_calculate_returnsTrue() {
        ds = new DijkstraBidirectional(smallGraph);
        assertTrue(ds.calculate(sNormal, sNormal));
    }

    @Test void nodesEqual_distance_returnsZero() {
        ds = new DijkstraBidirectional(smallGraph);
        ds.calculate(sNormal, sNormal);
        assertEquals(0, ds.distance());
    }

    @Test void nodesEqual_retrievePath_returnsEmptyList() {
        ds = new DijkstraBidirectional(smallGraph);
        ds.calculate(sNormal, sNormal);
        assertEquals(new LinkedList<DirectedEdge>(), ds.retrievePath());
    }

    @Test void nodesEqual_relaxedEdges_returnsCorrect() {
        ds = new DijkstraBidirectional(smallGraph);
        ds.calculate(sNormal, sNormal);
        assertEquals(0, ds.relaxedEdges());

    }

    // |||| Case: source and target are adjacent ||||

    @Test void targetClose_calculate_returnsTrue() {
        ds = new DijkstraBidirectional(smallGraph);
        assertTrue(ds.calculate(sNormal, tClose));
    }

    @Test void targetClose_distance_returnsCorrect() {
        ds = new DijkstraBidirectional(smallGraph);
        ds.calculate(sNormal, tClose);
        assertEquals(targetClose.distance, ds.distance());
    }

    @Test void targetClose_retrievePath_returnsCorrect() {
        ds = new DijkstraBidirectional(smallGraph);
        ds.calculate(sNormal, tClose);
        assertEquals(targetClose.path, ds.retrievePath());
    }

    @Test void targetClose_relaxedEdge_returnsCorrect() {
        ds = new DijkstraBidirectional(smallGraph);
        ds.calculate(sNormal, tClose);
        assertEquals(targetClose.relaxedEdges, ds.relaxedEdges());
    }

    // |||| Case: Shortest path contains edges with 0 weight |||| 

    @Test void containsZeroWeight_calculate_returnsTrue() {
        ds = new DijkstraBidirectional(containsZeroWeights.graph);
        assertTrue(ds.calculate(sNormal, tNormal));

    }

    @Test void containsZeroWeight_distance_returnsCorrect() {
        ds = new DijkstraBidirectional(containsZeroWeights.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(containsZeroWeights.distance, ds.distance());
    }

    @Test void containsZeroWeight_retrievePath_returnsCorrect() {
        ds = new DijkstraBidirectional(containsZeroWeights.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(containsZeroWeights.path, ds.retrievePath());
    }

    @Test void containsZeroWeight_relaxedEdges_returnsCorrect() {
        ds = new DijkstraBidirectional(containsZeroWeights.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(containsZeroWeights.relaxedEdges, ds.relaxedEdges());
    }

    // |||| Case: There are multiple shortest paths ||||

    @Test void multiplePaths_calculate_returnsTrue() {
        ds = new DijkstraBidirectional(multiplePaths.graph);
        assertTrue(ds.calculate(sNormal, tNormal));
    }

    @Test void multiplePaths_distance_returnsCorrect() {
        ds = new DijkstraBidirectional(multiplePaths.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(multiplePaths.distance, ds.distance());
    }

    @Test void multiplePaths_retrievePath_returnsEitherCorrect() {
        ds = new DijkstraBidirectional(multiplePaths.graph);
        ds.calculate(sNormal, tNormal);
        
        boolean isEither = (
            ds.retrievePath().equals(multiplePathOne) ||
            ds.retrievePath().equals(multiplePathTwo)
        );

        assertTrue(isEither);
        
    }

    @Test void multiplePaths_relaxedEdges_returnsCorrect() {
        ds = new DijkstraBidirectional(multiplePaths.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(multiplePaths.relaxedEdges, ds.relaxedEdges());
    }

    // |||| Case: A graph where the two Dijkstras will first meet at a meeting point
    //            that is not part of a shortest path ||||

    @Test void falseShortestPath_calculate_returnsTrue() {
        ds = new DijkstraBidirectional(falseShortestPath.graph);
        assertTrue(ds.calculate(sNormal, tNormal));
    }

    @Test void falseShortestPath_distance_returnsCorrect() {
        ds = new DijkstraBidirectional(falseShortestPath.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(falseShortestPath.distance, ds.distance());
    }

    @Test void falseShortestPath_relaxedEdges_returnsCorrect() {
        ds = new DijkstraBidirectional(falseShortestPath.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(falseShortestPath.relaxedEdges, ds.relaxedEdges());
    }

    @Test void falseShortestPath_retrievePath_returnsCorrect() {
        ds = new DijkstraBidirectional(falseShortestPath.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(falseShortestPath.path, ds.retrievePath());
    }

}
