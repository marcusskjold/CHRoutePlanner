package ITU.AA.AS03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedList;
import java.util.List;

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
public class DijkstraSimpleTest {

    DijkstraSimple ds;

    IndexedGraph smallGraph;
    int sNormal = 0;
    int tNormal = 3;
    int tooHigh = 100;
    int tooLow = -100;

    // Errors
    IndexedGraph noNodeGraph;
    TestData noCalculation;
    TestData noEdges;
    TestData disconnectedNodes;
    TestData sourceIllegal;
    TestData targetIllegal;

    // Validation
    TestData sourcesEqual;
    TestData sourcesClose;
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
        int smallGraphRelaxedEdges = 4;

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
            ds = new DijkstraSimple(noNodeGraph));
    }

    //         |||| Case: graph is null ||||
    @Test void null_constructor_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            ds = new DijkstraSimple(null));
    }

    //     |||| Case: attempt to calculate twice ||||
    //          uses smallGraph
    @Test void calculateTwice_calculate_throws() {
        ds = new DijkstraSimple(smallGraph);
        ds.calculate(sNormal, tNormal);
        assertThrows(Error.class, () -> 
            ds.calculate(sNormal, tNormal));
    }

    //     |||| Case: calculate with bad arguments ||||
    @Test void sourceTooHigh_calculate_throws() {
        ds = new DijkstraSimple(smallGraph);
        assertThrows(IllegalArgumentException.class, () -> 
            ds.calculate(tooHigh, tNormal));
    }

    @Test void sourceTooLow_calculate_throws() {
        ds = new DijkstraSimple(smallGraph);
        assertThrows(IllegalArgumentException.class, () -> 
            ds.calculate(tooLow, tNormal));
    }

    @Test void targetTooHigh_calculate_throws() {
        ds = new DijkstraSimple(smallGraph);
        assertThrows(IllegalArgumentException.class, () -> 
            ds.calculate(sNormal, tooHigh));
    }

    @Test void targetTooLow_calculate_throws() {
        ds = new DijkstraSimple(smallGraph);
        assertThrows(IllegalArgumentException.class, () -> 
            ds.calculate(sNormal, tooLow));
    }

    // ================== EDGE CASES ===========================

    //|||| Case: if calculate() has not been called ||||
    //           uses noCalculation test data

    @Test void noCalculation_distance_returnsErrorCode() {
        ds = new DijkstraSimple(noCalculation.graph);
        assertEquals(-1, ds.distance());
    }

    @Test void noCalculation_retrievePath_returnsNull() {
        ds = new DijkstraSimple(noCalculation.graph);
        assertEquals(null, ds.retrievePath());
    }

    @Test void noCalcultion_relaxedEdges_returnMinusOne() {
        ds = new DijkstraSimple(noCalculation.graph);
        assertEquals(-1, ds.relaxedEdges());
    }

    //     |||| Case: graph contains no edges ||||
    //                uses noEdges

    @Test void graphContainsNoEdges_calculate_returnsFalse() {
        ds = new DijkstraSimple(noEdges.graph);
        assertFalse(ds.calculate(sNormal, tNormal));
    }

    @Test void graphContainsNoEdges_distance_returnsMAX() {
        ds = new DijkstraSimple(noEdges.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(Integer.MAX_VALUE, ds.distance());
    }

    @Test void graphContainsNoEdges_retrievePath_returnsNull() {
        ds = new DijkstraSimple(noEdges.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(null, ds.retrievePath());

    }

    @Test void graphContainsNoEdges_relaxedEdges_returnsZero() {
        ds = new DijkstraSimple(noEdges.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(0, ds.relaxedEdges());
    }
    
    // |||| Case: Graph is not connected and source is disconnected from target ||||
    //            Uses disconnectedNodes
    @Test void nodesDisconnected_calculate_returnFalse() {
        ds = new DijkstraSimple(disconnectedNodes.graph);
        assertFalse(ds.calculate(sNormal, tNormal));
    }

    @Test void nodesDisconnected_distance_returnMAX() {
        ds = new DijkstraSimple(disconnectedNodes.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(Integer.MAX_VALUE, ds.distance());
    }

    @Test void nodesDisconnected_retrievePath_returnNull() {
        ds = new DijkstraSimple(disconnectedNodes.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(null, ds.retrievePath());
    }

    @Test void nodesDisconnected_relaxedEdges_returnCorrect() {
        ds = new DijkstraSimple(disconnectedNodes.graph);
        ds.calculate(sNormal, tNormal);
        assertEquals(disconnectedNodes.relaxedEdges, ds.relaxedEdges());
    }

}
        /* # List of test cases.
         *
         * ## Data input
         *
         * Input nodes:
         *
         * int sNeutral = 0
         * int tNeutral = 3
         * int tooHigh = 9
         * int tooLow = -1
         * int tClose = 1
         *
         * Return values:
         *
         * int componentRelaxedEdges = 2
         * int normalRelaxedEdges = 5
         * int targetCloseDistance = 10
         * int longDistance =
         * int longRelaxedEdges =
         *
         * Paths:
         * targetClosePath =
         * longPath
         *
         * Graphs:
         *
         * Graph noNodeGraph ()
         * Graph noEdgeGraph (4, 0)
         * Graph nodesDisconnectedGraph (4 1, 0 1 10, 1 2 10)
         * Graph neutralGraph (4 5, 0 1 10, 0 2 20, 1 2 5, 1 3 30, 2 3 10)
         * Graph longGraph
         *
         *
         *
         * ## Test for wrong uses
         *
         * If graph illegal (negative weights)
         * - [x] No test, graph is responsible
         *
         * If graph illegal (no nodes)
         * - [x] Constructor should catch this  graphContainsNoNodes_constructor_throws(noNodeGraph)
         *
         * If graph is null
         * - [x] Constructor should catch this  null_constructor_throws(null)
         *
         * Query, but not calculated
         * - [x] distance                       noCalculation_distance_returnsMAX()                 ->  MAX_VALUE
         * - [x] retrievePath                   noCalculation_retrievePath_returnsNull()            ->  null
         * - [x] relaxedEdges                   noCalcultion_relaxedEdges_returnMinusOne()          ->  -1
         *
         * Calculate twice
         * - [x] calculate                      calculateTwice_calculate_returnsFalse()
         *
         * If graph contains no edges           noEdgeGraph sNeutral tNeutral
         * - [x] calculate                      graphContainsNoEdges_calculate_returnsFalse()       -> false
         * - [x] distance                       graphContainsNoEdges_distance_returnsMAX()          -> MAX_VALUE
         * - [x] retrievePath                   graphContainsNoEdges_retrievePath_returnsNull()     -> null
         * - [x] relaxedEdges                   graphContainsNoEdges_relaxedEdges_returnsZero()     -> 0
         *
         * If s & t not connected               nodesDisconnectedGraph sNeutral tNeutral
         * - [x] calculate                      nodesDisconnected_calculate_returnFalse()           -> false
         * - [x] distance                       nodesDisconnected_distance_returnMAX()              -> MAX_VALUE
         * - [x] retrievePath                   nodesDisconnected_retrievePath_returnNull()         -> null
         * - [x] relaxedEdges                   nodesDisconnected_relaxedEdges_returnCorrect()      -> componentRelaxedEdges
         *
         * If s or t is illegal.                neutralGraph
         * - [x] calculate should catch this    sourceNotInGraph_calculate_throws(tooHigh, tNeutral)
         *                                      sourceNotInGraph_calculate_throws(tooLow, tNeutral)
         *                                      targetNotInGraph_calculate_throws(sNeutral, tooHigh)
         *                                      targetNotInGraph_calculate_throws(sNeutral, tooLow)
         *
         * ## Verification
         *
         * If s & t is equal                    neutralGraph, s = sNeutral, t = sNeutral
         * - [ ] calculate                      sourceEqual_calculate_returnsTrue()                 -> true
         * - [ ] distance                       sourceEqual_distrance_returnsZero()                 -> 0
         * - [ ] retrievePath                   sourceEqual_retrievePath_returnsNull()              -> null
         * - [ ] relaxedEdge                    sourceEqual_relaxedEdge_returnsCorrect()            -> normalRelaxedEdges
         *
         * If s & t are one apart               neutralGraph, s = sNeutral, t = tClose
         * - [ ] calculate                      targetClose_calculate_returnsTrue()                 -> true
         * - [ ] distance                       targetClose_distance_returnsCorrect()               -> targetCloseDistance
         * - [ ] retrievePath                   targetClose_retrievePath_returnsCorrect()           -> targetClosePath
         * - [ ] relaxedEdge                    targetClose_relaxedEdge_returnsCorrect()            -> normalRelaxedEdges
         *
         * If s & t are far apart               longGraph, s = sNeutral, t = tNeutral
         * - [ ] calculate                      sourcesFar_calculate_returnsTrue()                  -> true
         * - [ ] distance                       sourcesFar_distance_returnsCorrect()                -> longDistance
         * - [ ] retrievePath                   sourcesFar_retrievePath_returnsCorrect()            -> longPath
         * - [ ] relaxedEdge                    sourcesFar_relaxedEdge_returnsCorrect()             -> longRelaxedEdges
         *
         * If contains 0 weight edges in path   zeroWeightGraph, s = sNeutral, t = tNeutral
         * - [ ] calculate                      containsZeroWeight_calculate_returnsTrue()          -> true
         * - [ ] distance                       containsZeroWeight_distance_returnsCorrect()        -> zeroWeightDistance
         * - [ ] retrievePath                   containsZeroWeight_retrievePath_returnsCorrect()    -> zeroWeightPath
         * - [ ] relaxedEdge                    containsZeroWeight_relaxedEdges_returnsCorrect()    -> zeroRelaxedEdges
         *
         * If multiple shortest paths           multiplePathGraph, s = sNeutral, t = tNeutral
         * - [ ] calculate                      multiplePaths_calculate_returnsTrue()               -> true
         * - [ ] distance                       multiplePaths_distance_returnsCorrect()             -> multiplePathDistance
         * - [ ] retrievePath                   multiplePaths_retrievePath_returnsCorrect()         -> mPathOne || mPathTwo
         * - [ ] relaxedEdge                    multiplePaths_relaxedEdges_returnsCorrect()         -> multiplePathRelaxedEdges
         *
         *
         */
