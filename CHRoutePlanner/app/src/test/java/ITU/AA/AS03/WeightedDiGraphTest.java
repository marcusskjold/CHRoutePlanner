package ITU.AA.AS03;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedList;

import org.junit.jupiter.api.Test;

public class WeightedDiGraphTest {
    
    // Constructor tests
    
    @Test void negativeV_constructor_throws() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
            new WeightedDiGraph(-1));
        assertEquals("Graph cannot have negative size", e.getMessage());
    }

    @Test void zeroNodeGraph_hasCorrectProperties() {
        WeightedDiGraph g = new WeightedDiGraph(0);
        assertEquals(0, g.V());
        assertEquals(0, g.E());
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> g.edgesTo(0));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> g.edgesFrom(0));
        assertThrows(IllegalArgumentException.class,       () -> g.addDirectedEdge(0, 0, 1));
        assertThrows(IllegalArgumentException.class,       () -> g.addUndirectedEdge(0, 0, 1));

    }

    @Test void onNodeGraph_hasCorrectProperties() {
        WeightedDiGraph g = new WeightedDiGraph(1);
        assertEquals(1, g.V());
        assertEquals(0, g.E());
        assertEquals(new LinkedList<>(), g.edgesTo(0));
        assertEquals(new LinkedList<>(), g.edgesFrom(0));
        g.addDirectedEdge(0, 0, 1);
        g.addUndirectedEdge(0, 0, 1);
        assertThrows(IllegalArgumentException.class,       () -> g.addDirectedEdge(0, 1, 1));
        assertThrows(IllegalArgumentException.class,       () -> g.addUndirectedEdge(0, 1, 1));
    }

    @Test void normalGraph_addEdges_modifiesCorrectly() {
        WeightedDiGraph g = new WeightedDiGraph(4);
        assertEquals(4, g.V());
        assertEquals(0, g.E());
        g.addDirectedEdge(0, 1, 1);
        g.addUndirectedEdge(1, 2, 1);
        LinkedList<DirectedEdge> listOne = new LinkedList<>();
        listOne.add(new DirectedEdge(0, 1, 1));
        listOne.add(new DirectedEdge(2, 1, 1));
        assertEquals(listOne, g.edgesTo(1));
        LinkedList<DirectedEdge> listTwo = new LinkedList<>();
        listTwo.add(new DirectedEdge(2, 1, 1));
        assertEquals(listTwo, g.edgesFrom(2));
        assertEquals(3, g.E());
    }

    @Test void normalGraph_addEdges_throwsCorrectly() {
        WeightedDiGraph g = new WeightedDiGraph(4);
        Exception e = assertThrows(IllegalArgumentException.class, () ->
            g.addDirectedEdge(-1, 3, 0));
        assertEquals("u does not correspond to a node", e.getMessage());
        e = assertThrows(IllegalArgumentException.class, () ->
            g.addDirectedEdge(3, 10, 0));
        assertEquals("v does not correspond to a node", e.getMessage());
        e = assertThrows(IllegalArgumentException.class, () ->
            g.addDirectedEdge(3, 1, -1));
        assertEquals("weight is negative", e.getMessage());
    }
}
