package ITU.AA.AS03;

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
        boolean calculate;
        int distance;
        int relaxedEdges;
        LinkedList<DirectedEdge> path;
    }

    @BeforeAll
    void createTestData() {

        noNodeGraph = new IndexedGraph(0);

        noCalculation = new TestData();
        noCalculation.path = null;
        noCalculation.relaxedEdges = -1;
        noCalculation.distance = Integer.MAX_VALUE;

        


    }

    @BeforeEach
    void resetAlgorithm() {
        ds = null;
    }

    // ================== TESTS ===========================

    // ================== ERROR ===========================

    @Test void graphContainsNoNodes_constructor_throws() {
        assertThrows(IllegalArgumentException.class, () -> 
            ds = new DijkstraSimple(noNodeGraph));
    }

    @Test void null_constructor_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            ds = new DijkstraSimple(null));
    }
    @Test void noCalculation_distance_returnsMAX() {}
    @Test void noCalculation_retrievePath_returnsNull() {}
    @Test void noCalcultion_relaxedEdges_returnMinusOne() {}


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
         * - [ ] distance                       noCalculation_distance_returnsMAX()                 ->  MAX_VALUE
         * - [ ] retrievePath                   noCalculation_retrievePath_returnsNull()            ->  null
         * - [ ] relaxedEdges                   noCalcultion_relaxedEdges_returnMinusOne()          ->  -1
         *
         * Calculate twice
         * - [ ] calculate                      calculateTwice_calculate_returnsFalse()
         *
         * If graph contains no edges           noEdgeGraph sNeutral tNeutral
         * - [ ] calculate                      graphContainsNoEdges_calculate_returnsFalse()       -> false
         * - [ ] distance                       graphContainsNoEdges_distance_returnsMAX()          -> MAX_VALUE
         * - [ ] retrievePath                   graphContainsNoEdges_retrievePath_returnsNull()     -> null
         * - [ ] relaxedEdges                   graphContainsNoEdges_relaxedEdges_returnsZero()     -> 0
         *
         * If s & t not connected               nodesDisconnectedGraph sNeutral tNeutral
         * - [ ] calculate                      nodesDisconnected_calculate_returnFalse()           -> false
         * - [ ] distance                       nodesDisconnected_distance_returnMAX()              -> MAX_VALUE
         * - [ ] retrievePath                   nodesDisconnected_retrievePath_returnNull()         -> null
         * - [ ] relaxedEdges                   nodesDisconnected_relaxedEdges_returnCorrect()      -> componentRelaxedEdges
         *
         * If s or t is illegal.                neutralGraph
         * - [ ] calculate should catch this    sourceNotInGraph_calculate_throws(tooHigh, tNeutral)
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
