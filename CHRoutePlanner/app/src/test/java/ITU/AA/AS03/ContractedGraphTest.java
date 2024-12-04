package ITU.AA.AS03;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;


@TestInstance(Lifecycle.PER_CLASS)
public class ContractedGraphTest {
    private ContractedGraph CG;


    //-----Different aspects pertaining to the needsShortcutsGraph------
    IndexedGraph needsShortcutsGraph;
    //Neighbours for node 9
    LinkedList<DirectedEdge> node9Neighbours;

    //Fictional neighbours with node where parallel edges exist:
    LinkedList<DirectedEdge> parallelNeighbours;



    @BeforeAll
    void createTestData() {        

        //Initialize and fill neighbourlist with neighbours:
        node9Neighbours = new LinkedList<>();
        node9Neighbours.add(new DirectedEdge(2, 9, 2));
        node9Neighbours.add(new DirectedEdge(3, 9, 4));
        node9Neighbours.add(new DirectedEdge(4, 9, 3));
        node9Neighbours.add(new DirectedEdge(7, 9, 2));
        node9Neighbours.add(new DirectedEdge(8, 9, 4));
        node9Neighbours.add(new DirectedEdge(10, 9, 3));
        
        //Initialize neighbourlist with neighbours (two edges between 9 and 5):
        parallelNeighbours = new LinkedList<>();
        parallelNeighbours.add(new DirectedEdge(2, 9, 2));
        parallelNeighbours.add(new DirectedEdge(3, 9, 4));
        parallelNeighbours.add(new DirectedEdge(4, 9, 3));
        parallelNeighbours.add(new DirectedEdge(5, 9, 5));
        parallelNeighbours.add(new DirectedEdge(5, 9, 5));
    }

    @BeforeEach
    void resetAlgorithm() {
        //Must take new graph each time, since it is modified under contraction:
        needsShortcutsGraph = new WeightedDiGraph(11);
        needsShortcutsGraph.addUndirectedEdge(0,1,3);
        needsShortcutsGraph.addUndirectedEdge(0,2,5);
        needsShortcutsGraph.addUndirectedEdge(0,10,3);
        needsShortcutsGraph.addUndirectedEdge(1,2,3);
        needsShortcutsGraph.addUndirectedEdge(1,3,5);
        needsShortcutsGraph.addUndirectedEdge(2,3,2);
        needsShortcutsGraph.addUndirectedEdge(2,9,2);
        needsShortcutsGraph.addUndirectedEdge(3,9,4);
        needsShortcutsGraph.addUndirectedEdge(3,4,7);
        needsShortcutsGraph.addUndirectedEdge(4,9,3);
        needsShortcutsGraph.addUndirectedEdge(4,5,6);
        needsShortcutsGraph.addUndirectedEdge(5,6,4);
        needsShortcutsGraph.addUndirectedEdge(5,7,2);
        needsShortcutsGraph.addUndirectedEdge(6,7,3);
        needsShortcutsGraph.addUndirectedEdge(6,8,5);
        needsShortcutsGraph.addUndirectedEdge(7,8,3);
        needsShortcutsGraph.addUndirectedEdge(7,9,2);
        needsShortcutsGraph.addUndirectedEdge(8,9,4);
        needsShortcutsGraph.addUndirectedEdge(8,10,6);
        needsShortcutsGraph.addUndirectedEdge(9,10,3);

        CG = null;
    }

    
    //Test that the longest path between neighbours through contracted node is actually returned
    @Test void variousEdges_findMax_returnsCorrect() {
        CG = new ContractedGraph(needsShortcutsGraph); 
        assertEquals(CG.findMaxDist(9, node9Neighbours), 8);
    }

    //Test that sum of two parallel edges is considered 0 (rather than their sum counted towards max)
    @Test void parllelEdges_findMax_returnsCorrect() {
        CG = new ContractedGraph(needsShortcutsGraph); 
        assertEquals(CG.findMaxDist(9, parallelNeighbours), 9);
    }


    //Tests that no uncontracted nodes when graph contracted
    @Test void connectedGraph_contractGraph_contractsAllNodes() {
        CG = new ContractedGraph(needsShortcutsGraph); 
        CG.contractGraph();
        for(int i= 0;i<needsShortcutsGraph.V();i++) {
            assertEquals(new LinkedList<DirectedEdge>(), CG.findUncontractedEdges(i));
        }
    }

    //Tests that identical distances is found in the contracted graph compared to the uncontracted graph
    //For all pairs of nodes in the graph
    @Test void connectedGraph_contractGraph_findsCorrectDistances() {
        CG = new ContractedGraph(needsShortcutsGraph); 
        CG.contractGraph();
        for(int i =0;i<needsShortcutsGraph.V()-1;i++) {
            for(int j=i+1;j<needsShortcutsGraph.V();j++) {
                DijkstraInterleaving dsUncontracted = new DijkstraInterleaving(needsShortcutsGraph);
                DijkstraInterleaving dsContracted = new DijkstraInterleaving(CG);
                dsUncontracted.calculate(i, j);
                dsContracted.calculate(i, j);
                assertEquals(dsUncontracted.distance(), dsContracted.distance());
            }
        }
    }

}
