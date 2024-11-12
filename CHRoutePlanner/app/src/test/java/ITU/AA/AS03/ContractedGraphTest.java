package ITU.AA.AS03;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ContractedGraphTest {
    private ContractedGraph CG;

    @Test void emptyGraph_constructor() {
        CG = new ContractedGraph(new WeightedDiGraph(0));
        assertEquals(0, CG.V());
        assertEquals(0, CG.E());
    }

    @Test void initialRank_removesSingleDegreeNodes() {
        WeightedDiGraph G = new WeightedDiGraph(3);
        G.addUndirectedEdge(0, 2, 3);
        G.addUndirectedEdge(1, 2, 3);
        //G.addUndirectedEdge(1, 0, 3);
        CG = new ContractedGraph(G);
        CG.contractGraph();
        assertEquals(1, CG.contractions());
    }

    @Test void oneHop_creates_shortcut() {
        WeightedDiGraph G = new WeightedDiGraph(3);
        G.addUndirectedEdge(0, 2, 3);
        G.addUndirectedEdge(1, 2, 3);
        //G.addUndirectedEdge(1, 0, 3);
        CG = new ContractedGraph(G);
        CG.contract(2);
        assertEquals(1, CG.shortcutCount());
    }
}
