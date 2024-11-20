package ITU.AA.AS03;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;


@TestInstance(Lifecycle.PER_CLASS)
public class ContractedGraphTest {
    private ContractedGraph CG;
    

    //A small graph to test contractions on
    IndexedGraph smallContractGraph;
    //For a search from node 0:
    int pMax = 15;
    



    IndexedGraph needsShortcutsGraph;
    //IndexPQ after initial ordering:
    IndexMinPQ<Integer> initPq;
    //Neighbours for node 9
    LinkedList<DirectedEdge> node9Neighbours;
    
    //Fictional neighbours with node where parallel edges exist:
    LinkedList<DirectedEdge> parallelNeighbours;



    //A graph as expected when contracting the above graph
    //TODO 
    IndexedGraph expectedGraph;

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

        

        //Priority queue created with capacity for number of vertices
        initPq = new IndexMinPQ<>(11);
        //The expected index-key pairs inserted
        initPq.insert(0, -2);
        initPq.insert(1, -3);
        initPq.insert(2, -2);
        initPq.insert(3, -4);
        initPq.insert(4, -3);
        initPq.insert(5, -3);
        initPq.insert(6, -3);
        initPq.insert(7, -1);
        initPq.insert(8, -4);
        initPq.insert(9, 5);
        initPq.insert(10, -1);

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



    //Tests that the initial ordering creates the expected orderings of nodes in the pq
    //TODO: Maybe also test for empty ones
    //TODO: Could also add ranked
    @Test void variousNodes_initialOrderings_returnsCorrect() {
        CG = new ContractedGraph(needsShortcutsGraph); 
        CG.initialOrdering(); 
        IndexMinPQ<Integer> pq = CG.getPq();
        for(int i =0;i<pq.size();i++) {
            assertEquals(initPq.keyOf(i), pq.keyOf(i));
        }

        ////Old test client
        //while(!pq.isEmpty()) {
        //    System.out.println("index: " + pq.minIndex() + " key: " + pq.keyOf(pq.minIndex()));
        //    pq.delMin();
        //}
        ////CG.printGraph();
        //assertTrue(true);

      
    }

    //Test that the longest path between neighbours is actually returned
    @Test void variousEdges_findMax_returnsCorrect() {
        CG = new ContractedGraph(needsShortcutsGraph); 
        assertEquals(CG.findMaxDist(9, node9Neighbours), 8);
    }

    //Test that sum of two parallel edges is considered 0 (and not otherwise max in this case)
    //TODO: (returns 10, but should return 9): fix this in findMax (albeit not huge error, it might make more efficient?)
    @Test void parllelEdges_findMax_returnsCorrect() {
        CG = new ContractedGraph(needsShortcutsGraph); 
        assertEquals(CG.findMaxDist(9, parallelNeighbours), 9);
    }

    //TODO: No neighbours maybe?

    //TODO: method for creating list of uncontracted neighbours and test it


    //How can ranks still be inf after initial ranking, but proper when printed
    //why no shortcuts added here?
    @Test void variousNodes_contraction_returnsCorrect() {
        //CG = new ContractedGraph(smallContractGraph);
        CG = new ContractedGraph(needsShortcutsGraph); 
        CG.contractGraph();

        //CG.initialOrdering();
        //IndexMinPQ<Integer> pq = CG.getPq();
        //while(!pq.isEmpty()) {
        //    System.out.println("index: " + pq.minIndex() + " key: " + pq.keyOf(pq.minIndex()));
        //    pq.delMin();
        //}
        //CG.printGraph();
        assertTrue(true);

        //c = new Contraction(smallContractGraph);
        //c.getHierarchy().insert(0, c.initialRank(0));
        //c.getHierarchy().insert(1, c.initialRank(1)); 
        //c.getHierarchy().insert(2, c.initialRank(2));
        //assertEquals(5, c.getHierarchy().keyOf(0));
        //assertEquals(-1, c.getHierarchy().keyOf(1));
        //assertEquals(-2, c.getHierarchy().keyOf(2));
    }



    

   //@Test void emptyGraph_constructor() {
    //    CG = new ContractedGraph(new WeightedDiGraph(0));
    //    assertEquals(0, CG.V());
    //    assertEquals(0, CG.E());
    //}
    //
    //@Test void initialRank_removesSingleDegreeNodes() {
    //    WeightedDiGraph G = new WeightedDiGraph(3);
    //    G.addUndirectedEdge(0, 2, 3);
    //    G.addUndirectedEdge(1, 2, 3);
    //    //G.addUndirectedEdge(1, 0, 3);
    //    CG = new ContractedGraph(G);
    //    CG.contractGraph();
    //    assertEquals(1, CG.contractions());
    //}
    //
    //@Test void oneHop_creates_shortcut() {
    //    WeightedDiGraph G = new WeightedDiGraph(3);
    //    G.addUndirectedEdge(0, 2, 3);
    //    G.addUndirectedEdge(1, 2, 3);
    //    //G.addUndirectedEdge(1, 0, 3);
    //    CG = new ContractedGraph(G);
    //    CG.contract(2);
    //    assertEquals(1, CG.shortcutCount());
    //}
}
