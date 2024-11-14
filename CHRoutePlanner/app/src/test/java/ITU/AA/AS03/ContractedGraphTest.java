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


    
    }
    @BeforeEach
    void resetAlgorithm() {
        CG = null;
    }

    //How can ranks still be inf after initial ranking, but proper when printed
    //why no shortcuts added here?
    @Test void variousNodes_initialOrderings_returnsCorrect() {
        //CG = new ContractedGraph(smallContractGraph);
        CG = new ContractedGraph(needsShortcutsGraph); 
        CG.contractGraph();

        //CG.initialOrdering();
        //IndexMinPQ<Integer> pq = CG.getPq();
        //while(!pq.isEmpty()) {
        //    System.out.println("index: " + pq.minIndex() + " key: " + pq.keyOf(pq.minIndex()));
        //    pq.delMin();
        //}
        CG.printGraph();
        assertTrue(false);

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
