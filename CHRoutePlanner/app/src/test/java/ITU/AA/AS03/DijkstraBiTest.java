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
public class DijkstraBiTest {

    DijkstraBi ds;

    GraphLocations smallGraph;
    GraphLocations tooFarApartGraph;
    int sNormal = 0;
    int tNormal = 3;
    int tooHigh = 100;
    int tooLow = -100;
    int tClose = 1;
    int smallGraphRelaxedEdges = 4;
    LinkedList<DirectedEdge> multiplePathOne;
    LinkedList<DirectedEdge> multiplePathTwo;

    // Errors
    GraphLocations noNodeGraph;
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
        GraphLocations graph;
        int distance;
        int relaxedEdges;
        LinkedList<DirectedEdge> path;
    }

    @BeforeAll
    void createTestData() {

        // Graphs

        noNodeGraph = new GraphLocations(0);
        smallGraph = new GraphLocations(4);
        smallGraph.addUndirectedEdge(0, 1, 10);
        smallGraph.addUndirectedEdge(0, 2, 20);
        smallGraph.addUndirectedEdge(1, 2, 10);
        smallGraph.addUndirectedEdge(1, 3, 30);
        smallGraph.addUndirectedEdge(2, 3, 10);

        tooFarApartGraph = new GraphLocations(4);
        tooFarApartGraph.addUndirectedEdge(0, 1, 1000000000);
        tooFarApartGraph.addUndirectedEdge(1, 2, 1000000000);
        tooFarApartGraph.addUndirectedEdge(2, 3, 1000000000);
        // TestData

        // No calculation
        noCalculation = new TestData();
        noCalculation.graph = smallGraph;

        // No edges
        GraphLocations noEdgeGraph = new GraphLocations(4);
        noEdges = new TestData();
        noEdges.graph = noEdgeGraph;
        noEdges.relaxedEdges = 0;

        // Disconnected nodes
        GraphLocations disconnectedGraph = new GraphLocations(5);
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
        GraphLocations zeroWeightGraph = new GraphLocations(4);
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
        GraphLocations multiplePathGraph = new GraphLocations(4);
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
            ds = new DijkstraBi(noNodeGraph));
    }

    //         |||| Case: graph is null ||||
    @Test void null_constructor_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            ds = new DijkstraBi(null));
    }

    //     |||| Case: attempt to calculate twice ||||
    //          uses smallGraph
    @Test void calculateTwice_calculate_throws() {
        ds = new DijkstraBi(smallGraph);
        ds.calculate(sNormal, tNormal);
        assertThrows(IllegalStateException.class, () ->
            ds.calculate(sNormal, tNormal));
    }

    //     |||| Case: calculate with bad arguments ||||
    @Test void sourceTooHigh_calculate_throws() {
        ds = new DijkstraBi(smallGraph);
        assertThrows(IllegalArgumentException.class, () ->
            ds.calculate(tooHigh, tNormal));
    }

    @Test void sourceTooLow_calculate_throws() {
        ds = new DijkstraBi(smallGraph);
        assertThrows(IllegalArgumentException.class, () ->
            ds.calculate(tooLow, tNormal));
    }

    @Test void targetTooHigh_calculate_throws() {
        ds = new DijkstraBi(smallGraph);
        assertThrows(IllegalArgumentException.class, () ->
            ds.calculate(sNormal, tooHigh));
    }

    @Test void targetTooLow_calculate_throws() {
        ds = new DijkstraBi(smallGraph);
        assertThrows(IllegalArgumentException.class, () ->
            ds.calculate(sNormal, tooLow));
    }

    // |||| Case: source and target are too far apart

    @Test void nodesTooFarApart_calculate_throws() {
        ds = new DijkstraBi(tooFarApartGraph);
        assertThrows(ArithmeticException.class, () ->
            ds.calculate(sNormal, tNormal));
    }

    // ================== EDGE CASES ===========================

    //|||| Case: if calculate() has not been called ||||
    //           uses noCalculation test data

    @Test void noCalculation_distance_returnsErrorCode() {
        ds = new DijkstraBi(noCalculation.graph);
        assertEquals(-1, ds.distance());
    }

    @Test void noCalculation_retrievePath_returnsNull() {
        ds = new DijkstraBi(noCalculation.graph);
        assertEquals(null, ds.retrievePath());
    }

    @Test void noCalcultion_relaxedEdges_returnMinusOne() {
        ds = new DijkstraBi(noCalculation.graph);
        assertEquals(-1, ds.relaxedEdges());
    }

    //     |||| Case: graph contains no edges ||||
    //                uses noEdges

    @Test void graphContainsNoEdges_calculate_returnsFalse() {
        ds = new DijkstraBi(noEdges.graph);
        assertFalse(ds.calculate(sNormal, tNormal));
    }

    @Test void graphContainsNoEdges_distance_returnsMAX() {
        ds = new DijkstraBi(noEdges.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(Integer.MAX_VALUE, ds.distance());
    }

    @Test void graphContainsNoEdges_retrievePath_returnsNull() {
        ds = new DijkstraBi(noEdges.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(null, ds.retrievePath());

    }

    @Test void graphContainsNoEdges_relaxedEdges_returnsZero() {
        ds = new DijkstraBi(noEdges.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(0, ds.relaxedEdges());
    }

    // |||| Case: Graph is not connected and source is disconnected from target ||||
    //            Uses disconnectedNodes
    @Test void nodesDisconnected_calculate_returnFalse() {
        ds = new DijkstraBi(disconnectedNodes.graph);
        assertFalse(ds.calculate(sNormal, tNormal));
    }

    @Test void nodesDisconnected_distance_returnMAX() {
        ds = new DijkstraBi(disconnectedNodes.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(Integer.MAX_VALUE, ds.distance());
    }

    @Test void nodesDisconnected_retrievePath_returnNull() {
        ds = new DijkstraBi(disconnectedNodes.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(null, ds.retrievePath());
    }

    @Test void nodesDisconnected_relaxedEdges_returnCorrect() {
        ds = new DijkstraBi(disconnectedNodes.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(disconnectedNodes.relaxedEdges, ds.relaxedEdges());
    }

    // ================== VALIDATION ===========================

    // |||| Case: Source and target are the same ||||
    //            Uses nodesEqual

    @Test void nodesEqual_calculate_returnsTrue() {
        ds = new DijkstraBi(smallGraph);
        assertTrue(ds.calculate(sNormal, sNormal));
    }

    @Test void nodesEqual_distance_returnsZero() {
        ds = new DijkstraBi(smallGraph);
        ds.calculate(sNormal, sNormal);
        assertEquals(0, ds.distance());
    }

    @Test void nodesEqual_retrievePath_returnsEmptyList() {
        ds = new DijkstraBi(smallGraph);
        ds.calculate(sNormal, sNormal);
        assertEquals(new LinkedList<DirectedEdge>(), ds.retrievePath());
    }

    @Test void nodesEqual_relaxedEdges_returnsCorrect() {
        ds = new DijkstraBi(smallGraph);
        ds.calculate(sNormal, sNormal);
        assertEquals(0, ds.relaxedEdges());

    }

    // |||| Case: source and target are adjacent ||||

    @Test void targetClose_calculate_returnsTrue() {
        ds = new DijkstraBi(smallGraph);
        assertTrue(ds.calculate(sNormal, tClose));
    }

    @Test void targetClose_distance_returnsCorrect() {
        ds = new DijkstraBi(smallGraph);
        ds.calculate(sNormal, tClose);
        assertEquals(targetClose.distance, ds.distance());
    }

    @Test void targetClose_retrievePath_returnsCorrect() {
        ds = new DijkstraBi(smallGraph);
        ds.calculate(sNormal, tClose);
        assertEquals(targetClose.path, ds.retrievePath());
    }

    @Test void targetClose_relaxedEdge_returnsCorrect() {
        ds = new DijkstraBi(smallGraph);
        ds.calculate(sNormal, tClose);
        assertEquals(targetClose.relaxedEdges, ds.relaxedEdges());
    }

    // |||| Case: Shortest path contains edges with 0 weight |||| 

    @Test void containsZeroWeight_calculate_returnsTrue() {
        ds = new DijkstraBi(containsZeroWeights.graph);
        assertTrue(ds.calculate(sNormal, tNormal));

    }

    @Test void containsZeroWeight_distance_returnsCorrect() {
        ds = new DijkstraBi(containsZeroWeights.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(containsZeroWeights.distance, ds.distance());
    }

    @Test void containsZeroWeight_retrievePath_returnsCorrect() {
        ds = new DijkstraBi(containsZeroWeights.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(containsZeroWeights.path, ds.retrievePath());
    }

    @Test void containsZeroWeight_relaxedEdges_returnsCorrect() {
        ds = new DijkstraBi(containsZeroWeights.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(containsZeroWeights.relaxedEdges, ds.relaxedEdges());
    }

    // |||| Case: There are multiple shortest paths ||||

    @Test void multiplePaths_calculate_returnsTrue() {
        ds = new DijkstraBi(multiplePaths.graph);
        assertTrue(ds.calculate(sNormal, tNormal));
    }

    @Test void multiplePaths_distance_returnsCorrect() {
        ds = new DijkstraBi(multiplePaths.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(multiplePaths.distance, ds.distance());
    }

    @Test void multiplePaths_retrievePath_returnsEitherCorrect() {
        ds = new DijkstraBi(multiplePaths.graph);
        ds.calculate(sNormal, tNormal);
        
        boolean isEither = (
            ds.retrievePath().equals(multiplePathOne) ||
            ds.retrievePath().equals(multiplePathTwo)
        );

        assertTrue(isEither);
        
    }

    @Test void multiplePaths_relaxedEdges_returnsCorrect() {
        ds = new DijkstraBi(multiplePaths.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(multiplePaths.relaxedEdges, ds.relaxedEdges());
    }
}
