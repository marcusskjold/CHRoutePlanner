## Specification notes

### Main class
*Main.java*

Deals with all printin, file IO, initializing graph and reporting result.

### Data objects

**Graph**
*Graph.java*

Contains Edges and Nodes.
Can be built from a stream.

**Node**

**Edge**

**Path**

### Algorithms

Interface SPFinder.java

**SPDijkstraBasic**

... more to come


## Requirements

INPUT
- The graph is given as a file containing a list of $n$ nodes and $m$ edges.
- For each node, we are given an ID (`long`) and a pair of coordinates (`float`)
- For each edge, we are given two node IDs and a cost (`int`, could be `short`)
- The graph is bidirectional
- This should be read as standard input.

EXPANSION
- The program should be easy to extend to directed graphs

REQUIRED TESTS
- (3.1) "Add a unit test that reads a small file and tests that the graph is built correctly." (basic Dijkstra)
- (3.2) "Add a test case to your unit test suite that checks whether the correct output is produced on your test graph." (early stopping Dijkstra)
- (3.4) "Make sure to add test cases that show that your implementation behaves as intended." (Bidirectional Dijkstra)

REPORT
- (3.3) "Report on the average running time and the average" (for all algorithms) and compare (3.5) (4.2)
- (4.1) Report the preprocessing time and the resulting number of edges
    - How did you order the vertices?
    - How did your graph data structure support the contract operation? 
    - How did you decide that during the contracting of a vertex v, the edge (u, w) should be added as a shortcut?

CLASSES
- Graph
    - Edge (+shortcut edge)
    - Node
        - ID: long, Rank: long, Location (float,float)
- Algorithms
    - Given a Graph and a pair of Nodes, an algorithm should be able to report:
        - distance between the Nodes
        - the number of relaxed edges.
        - the running time of the algorithm. (or should that be somewhere else?)
    - Basic Dijkstra
    - Early stopping Dijkstra
    - Bidirectional Dijkstra
    - (A*)
    - Contraction Hierarchies Preprocessor
        - Perhaps this should works such that an algorithm gets a directory and checks if their desired graph is there. 
        - It should output to file: n, m, then n {id, location, rank}, m {u,v,w,contractnode(default -1)}
        - It should report the preprocessing time and the resulting number of edges
        - Should have a NodeOrderingStrategy.
        - Node contraction (G3.1)
    - Contraction Hierarchies Querier (G3.2)
- Random picker: Must use a seed and pick $n$ (1000) random pairs of nodes in the graph.

