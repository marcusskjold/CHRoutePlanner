package ITU.AA.AS03;

import org.junit.jupiter.api.Test;

/**
 * DijkstraSimpleTest
 */
public class DijkstraSimpleTest {

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
         *
         * Graphs:
         *
         * Graph noNodeGraph ()
         * Graph noEdgeGraph (4, 0)
         * Graph nodesDisconnectedGraph (4 1, 0 1 10, 1 2 10)
         * Graph neutralGraph (4 5, 0 1 10, 0 2 20, 1 2 5, 1 3 30, 2 3 10)
         *
         *
         * ## Test for wrong uses
         *
         * If graph illegal (negative weights)
         * - [x] No test, graph is responsible
         *
         * If graph illegal (no nodes)
         * - [ ] Constructor should catch this  graphContainsNoNodes_constructor_throws(noNodeGraph)
         *
         * If graph is null
         * - [ ] Constructor should catch this  null_constructor_throws(null)
         *
         * Query, but not calculated            
         * - [ ] distance                       noCalculation_distance_returnsMAX()                 ->  MAX_VALUE
         * - [ ] retrievePath                   noCalculation_retrievePath_returnsNull()            ->  null
         * - [ ] relaxedEdges                   noCalcultion_relaxedEdges_returnMinusOne()          ->  -1
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
         * If s & t is equal                    neutralGraph, s = sNeutral, t = tNeutral
         * - [ ] calculate                      sourceEqual_calculate_returnsTrue()                 -> true
         * - [ ] distance                       sourceEqual_distrance_returnsZero()                 -> 0
         * - [ ] retrievePath                   sourceEqual_retrievePath_returnsNull()              -> null
         * - [ ] relaxedEdge                    sourceEqual_relaxedEdge_returnsCorrect()            -> normalRelaxedEdges
         *
         * If s & t are one apart               neutralGraph, s = sNeutral, t = tClose
         * - [ ] calculate                      targetClose_calculate_returnsTrue()                 -> true
         * - [ ] distance                       targetClose_distance_returnsCorrect()               -> targetCloseDistance
         * - [ ] retrievePath                   targetClose_retrievePath_returnsCorrect()           -> 
         * - [ ] relaxedEdge                    targetClose_relaxedEdge_returnsCorrect()            -> normalRelaxedEdges
         *
         * If s & t are far apart
         * - [ ] calculate (return true)
         * - [ ] distance (return distance)
         * - [ ] retrievePath (return correct path)
         * - [ ] relaxedEdge (return correct value)
         *
         * If contains 0 weight edges in path
         * - [ ] calculate (return true)
         * - [ ] distance (return distance)
         * - [ ] retrievePath (return correct path)
         * - [ ] relaxedEdge (return correct value)
         *
         * If have multiple paths but only one shortest
         * - [ ] calculate (return true)
         * - [ ] distance (return distance)
         * - [ ] retrievePath (return correct path)
         * - [ ] relaxedEdge (return correct value)
         *
         * If multiple shortest paths
         * - [ ] calculate (return true)
         * - [ ] distance (return distance)
         * - [ ] retrievePath (return either of the correct paths)
         * - [ ] relaxedEdge (return correct value)
         * 
         *
         */

    @Test
    void constructorTest() {
        /* Potential test cases:
         * initialized with graph = null
         * Graph.V() is less that 1;
         * Graph is in an otherwise illegal state
         * Graph is proper
         *
         * other methods report expected values
         * - retrievePath -> null
         * - relaxedEdges -> -1
         * - distance     -> MAX_VALUE
         *
         */

    }

    @Test
    void calculate() {
        DijkstraSimple ds = new DijkstraSimple(null);

    }

    @Test
    void distance() {

    }

    @Test
    void retrievePath() {

    }

    @Test
    void relaxedEdges() {
        /* If not computed, return -1
         * If not found, return correct
         * If found, return correct
         */

    }

    @Test
    // usecases

}
