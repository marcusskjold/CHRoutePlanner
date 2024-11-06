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

/**
 * DijkstraSimpleTest
 */
@TestInstance(Lifecycle.PER_CLASS)
public class DijkstraEarlyStopTest {

    DijkstraSimple ds;

    IndexedGraph smallGraph;
    IndexedGraph mediumGraph;
    IndexedGraph tooFarApartGraph;
    int sNormal = 0;
    int tNormal = 3;
    int tooHigh = 100;
    int tooLow = -100;
    int tClose = 1;
    int nodesEqualRelaxededges = 0;
    int targetCloseRelaxedEdges = 2;
    int mediumGraphRelaxedEdges = 4;
    LinkedList<DirectedEdge> multiplePathOne;
    LinkedList<DirectedEdge> multiplePathTwo;

    // Errors
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

        noNodeGraph = new IndexedGraph(0);
        smallGraph = new IndexedGraph(4);
        smallGraph.addEdge(new DirectedEdge(0, 1, 10));
        smallGraph.addEdge(new DirectedEdge(0, 2, 20));
        smallGraph.addEdge(new DirectedEdge(1, 2, 10));
        smallGraph.addEdge(new DirectedEdge(1, 3, 30));
        smallGraph.addEdge(new DirectedEdge(2, 3, 10));
        
        mediumGraph = new IndexedGraph(5);
        mediumGraph.addEdge(new DirectedEdge(0, 1, 10));
        mediumGraph.addEdge(new DirectedEdge(0, 2, 20));
        mediumGraph.addEdge(new DirectedEdge(1, 2, 10));
        mediumGraph.addEdge(new DirectedEdge(1, 3, 30));
        mediumGraph.addEdge(new DirectedEdge(2, 3, 10));
        mediumGraph.addEdge(new DirectedEdge(3,4,50));




        tooFarApartGraph = new IndexedGraph(4);
        tooFarApartGraph.addEdge(new DirectedEdge(0, 1, 1000000000));
        tooFarApartGraph.addEdge(new DirectedEdge(1, 2, 1000000000));
        tooFarApartGraph.addEdge(new DirectedEdge(2, 3, 1000000000));
        // TestData

        // No calculation
        noCalculation = new TestData();
        noCalculation.graph = smallGraph;

        // No edges
        IndexedGraph noEdgeGraph = new IndexedGraph(4);
        noEdges = new TestData();
        noEdges.graph = noEdgeGraph;
        noEdges.relaxedEdges = 0;

        // Disconnected nodes
        IndexedGraph disconnectedGraph = new IndexedGraph(5);
        disconnectedGraph.addEdge(new DirectedEdge(0, 1, 10));
        disconnectedGraph.addEdge(new DirectedEdge(0, 2, 20));
        disconnectedGraph.addEdge(new DirectedEdge(1, 2, 10));
        disconnectedGraph.addEdge(new DirectedEdge(4, 3, 30));
        disconnectedNodes = new TestData();
        disconnectedNodes.graph = disconnectedGraph;
        disconnectedNodes.relaxedEdges = 2;

        // ===========================
        // Validation

        // Target is adjacent to source
        targetClose = new TestData();
        targetClose.path = new LinkedList<>();
        targetClose.path.add(new DirectedEdge(0, 1, 10));
        targetClose.distance = 10;

        // Shortest path includes edge with zero weight
        IndexedGraph zeroWeightGraph = new IndexedGraph(4);
        zeroWeightGraph.addEdge(new DirectedEdge(0, 1, 10));
        zeroWeightGraph.addEdge(new DirectedEdge(0, 2, 0));
        zeroWeightGraph.addEdge(new DirectedEdge(1, 2, 10));
        zeroWeightGraph.addEdge(new DirectedEdge(1, 3, 30));
        zeroWeightGraph.addEdge(new DirectedEdge(2, 3, 10));
        containsZeroWeights = new TestData();
        containsZeroWeights.graph = zeroWeightGraph;
        LinkedList<DirectedEdge> zeroWeightPath = new LinkedList<>();
        zeroWeightPath.add(new DirectedEdge(0, 2, 0));
        zeroWeightPath.add(new DirectedEdge(2, 3, 10));
        containsZeroWeights.path = zeroWeightPath;
        containsZeroWeights.distance = 10;
        containsZeroWeights.relaxedEdges = 3;

        // Multiple shortest paths
        IndexedGraph multiplePathGraph = new IndexedGraph(4);
        multiplePathGraph.addEdge(new DirectedEdge(0, 1, 10));
        multiplePathGraph.addEdge(new DirectedEdge(0, 2, 10));
        multiplePathGraph.addEdge(new DirectedEdge(1, 2, 10));
        multiplePathGraph.addEdge(new DirectedEdge(1, 3, 10));
        multiplePathGraph.addEdge(new DirectedEdge(2, 3, 10));
        multiplePathOne = new LinkedList<DirectedEdge>();
        multiplePathOne.add(new DirectedEdge(0, 1, 10));
        multiplePathOne.add(new DirectedEdge(1, 3, 10));
        multiplePathTwo = new LinkedList<DirectedEdge>();
        multiplePathTwo.add(new DirectedEdge(0, 2, 10));
        multiplePathTwo.add(new DirectedEdge(2, 3, 10));
        multiplePaths = new TestData();
        multiplePaths.graph = multiplePathGraph;
        multiplePaths.relaxedEdges = 3;
        multiplePaths.distance = 20;


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
        Exception e = assertThrows(IllegalArgumentException.class, () ->
            ds = new DijkstraEarlyStop(noNodeGraph));
        assertEquals("Graph must contain nodes.", e.getMessage());
    }

    //         |||| Case: graph is null ||||
    @Test void null_constructor_throws() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
            ds = new DijkstraEarlyStop(null));
        assertEquals("Graph must not be null.", e.getMessage());    
    }

    //     |||| Case: attempt to calculate twice ||||
    //          uses smallGraph
    @Test void calculateTwice_calculate_throws() {
        ds = new DijkstraEarlyStop(smallGraph);
        ds.calculate(sNormal, tNormal);
        Exception e = assertThrows(IllegalStateException.class, () -> 
            ds.calculate(sNormal, tNormal));
        assertEquals("State must be reset before new calculation.", e.getMessage());
    }

    //     |||| Case: calculate with bad arguments ||||
    @Test void sourceTooHigh_calculate_throws() {
        ds = new DijkstraEarlyStop(smallGraph);
        Exception e = assertThrows(IllegalArgumentException.class, () -> 
            ds.calculate(tooHigh, tNormal));
        assertEquals("source is not a valid node index", e.getMessage());
    }

    @Test void sourceTooLow_calculate_throws() {
        ds = new DijkstraEarlyStop(smallGraph);
        Exception e = assertThrows(IllegalArgumentException.class, () -> 
            ds.calculate(tooLow, tNormal));
        assertEquals("source is not a valid node index", e.getMessage());
    }

    @Test void targetTooHigh_calculate_throws() {
        ds = new DijkstraEarlyStop(smallGraph);
        Exception e = assertThrows(IllegalArgumentException.class, () -> 
            ds.calculate(sNormal, tooHigh));
        assertEquals("target is not a valid node index", e.getMessage());
    }

    @Test void targetTooLow_calculate_throws() {
        ds = new DijkstraEarlyStop(smallGraph);
        Exception e = assertThrows(IllegalArgumentException.class, () -> 
            ds.calculate(sNormal, tooLow));
        assertEquals("target is not a valid node index", e.getMessage());
    }

    // |||| Case: source and target are too far apart

    @Test void nodesTooFarApart_calculate_throws() {
        ds = new DijkstraEarlyStop(tooFarApartGraph);
        Exception e = assertThrows(ArithmeticException.class, () ->
            ds.calculate(sNormal, tNormal));
        assertEquals("Integer overflow: Distances are too high", e.getMessage());
        
    }

    // ================== EDGE CASES ===========================

    //|||| Case: if calculate() has not been called ||||
    //           uses noCalculation test data

    @Test void noCalculation_distance_returnsErrorCode() {
        ds = new DijkstraEarlyStop(noCalculation.graph);
        assertEquals(-1, ds.distance());
    }

    @Test void noCalculation_retrievePath_returnsNull() {
        ds = new DijkstraEarlyStop(noCalculation.graph);
        assertEquals(null, ds.retrievePath());
    }

    @Test void noCalcultion_relaxedEdges_returnMinusOne() {
        ds = new DijkstraEarlyStop(noCalculation.graph);
        assertEquals(-1, ds.relaxedEdges());
    }

    //     |||| Case: graph contains no edges ||||
    //                uses noEdges

    @Test void graphContainsNoEdges_calculate_returnsFalse() {
        ds = new DijkstraEarlyStop(noEdges.graph);
        assertFalse(ds.calculate(sNormal, tNormal));
    }

    @Test void graphContainsNoEdges_distance_returnsMAX() {
        ds = new DijkstraEarlyStop(noEdges.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(Integer.MAX_VALUE, ds.distance());
    }

    @Test void graphContainsNoEdges_retrievePath_returnsNull() {
        ds = new DijkstraEarlyStop(noEdges.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(null, ds.retrievePath());

    }

    @Test void graphContainsNoEdges_relaxedEdges_returnsZero() {
        ds = new DijkstraEarlyStop(noEdges.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(0, ds.relaxedEdges());
    }

    // |||| Case: Graph is not connected and source is disconnected from target ||||
    //            Uses disconnectedNodes
    @Test void nodesDisconnected_calculate_returnFalse() {
        ds = new DijkstraEarlyStop(disconnectedNodes.graph);
        assertFalse(ds.calculate(sNormal, tNormal));
    }

    @Test void nodesDisconnected_distance_returnMAX() {
        ds = new DijkstraEarlyStop(disconnectedNodes.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(Integer.MAX_VALUE, ds.distance());
    }

    @Test void nodesDisconnected_retrievePath_returnNull() {
        ds = new DijkstraEarlyStop(disconnectedNodes.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(null, ds.retrievePath());
    }

    @Test void nodesDisconnected_relaxedEdges_returnCorrect() {
        ds = new DijkstraEarlyStop(disconnectedNodes.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(disconnectedNodes.relaxedEdges, ds.relaxedEdges());
    }

    // ================== VALIDATION ===========================

    // |||| Case: Source and target are the same ||||
    //            Uses nodesEqual

    @Test void nodesEqual_calculate_returnsTrue() {
        ds = new DijkstraEarlyStop(smallGraph);
        assertTrue(ds.calculate(sNormal, sNormal));
    }

    @Test void nodesEqual_distance_returnsZero() {
        ds = new DijkstraEarlyStop(smallGraph);
        ds.calculate(sNormal, sNormal);
        assertEquals(0, ds.distance());
    }

    @Test void nodesEqual_retrievePath_returnsEmptyList() {
        ds = new DijkstraEarlyStop(smallGraph);
        ds.calculate(sNormal, sNormal);
        assertEquals(new LinkedList<DirectedEdge>(), ds.retrievePath());
    }

    @Test void nodesEqual_relaxedEdges_returnsCorrect() {
        ds = new DijkstraEarlyStop(smallGraph);
        ds.calculate(sNormal, sNormal);
        assertEquals(nodesEqualRelaxededges, ds.relaxedEdges());

    }

    // |||| Case: source and target are adjacent ||||

    @Test void targetClose_calculate_returnsTrue() {
        ds = new DijkstraEarlyStop(smallGraph);
        assertTrue(ds.calculate(sNormal, tClose));
    }

    @Test void targetClose_distance_returnsCorrect() {
        ds = new DijkstraEarlyStop(smallGraph);
        ds.calculate(sNormal, tClose);
        assertEquals(targetClose.distance, ds.distance());
    }

    @Test void targetClose_retrievePath_returnsCorrect() {
        ds = new DijkstraEarlyStop(smallGraph);
        ds.calculate(sNormal, tClose);
        assertEquals(targetClose.path, ds.retrievePath());
    }

    @Test void targetClose_relaxedEdge_returnsCorrect() {
        ds = new DijkstraEarlyStop(smallGraph);
        ds.calculate(sNormal, tClose);
        assertEquals(targetCloseRelaxedEdges, ds.relaxedEdges());
    }

    // |||| Case: Shortest path contains edges with 0 weight |||| 

    @Test void containsZeroWeight_calculate_returnsTrue() {
        ds = new DijkstraEarlyStop(containsZeroWeights.graph);
        assertTrue(ds.calculate(sNormal, tNormal));

    }

    @Test void containsZeroWeight_distance_returnsCorrect() {
        ds = new DijkstraEarlyStop(containsZeroWeights.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(containsZeroWeights.distance, ds.distance());
    }

    @Test void containsZeroWeight_retrievePath_returnsCorrect() {
        ds = new DijkstraEarlyStop(containsZeroWeights.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(containsZeroWeights.path, ds.retrievePath());
    }

    @Test void containsZeroWeight_relaxedEdges_returnsCorrect() {
        ds = new DijkstraEarlyStop(containsZeroWeights.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(containsZeroWeights.relaxedEdges, ds.relaxedEdges());
    }

    // |||| Case: There are multiple shortest paths ||||

    @Test void multiplePaths_calculate_returnsTrue() {
        ds = new DijkstraEarlyStop(multiplePaths.graph);
        assertTrue(ds.calculate(sNormal, tNormal));
    }

    @Test void multiplePaths_distance_returnsCorrect() {
        ds = new DijkstraEarlyStop(multiplePaths.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(multiplePaths.distance, ds.distance());
    }

    @Test void multiplePaths_retrievePath_returnsEitherCorrect() {
        ds = new DijkstraEarlyStop(multiplePaths.graph);
        ds.calculate(sNormal, tNormal);
        
        boolean isEither = (
            ds.retrievePath().equals(multiplePathOne) ||
            ds.retrievePath().equals(multiplePathTwo)
        );

        assertTrue(isEither);
        
    }

    @Test void multiplePaths_relaxedEdges_returnsCorrect() {
        ds = new DijkstraEarlyStop(multiplePaths.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(multiplePaths.relaxedEdges, ds.relaxedEdges());
    }

    // |||| Case: target not the last node to be relaxed (early stop activated)||||
    @Test void targetRelaxedEarly_relaxedEdges_returnsCorrect() {
        ds = new DijkstraEarlyStop(mediumGraph);
        ds.calculate(sNormal, tNormal);
        assertEquals(mediumGraphRelaxedEdges, ds.relaxedEdges());
    }
}

