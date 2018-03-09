package misc.graphs;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.dictionaries.ArrayDictionary;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import misc.Searcher;
import misc.exceptions.NoPathExistsException;
import misc.exceptions.NotYetImplementedException;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends Edge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated then usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've contrained Graph
    //   so that E *must* always be an instance of Edge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the Edge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    private int currentVertex;
    private ArrayDictionary<V, Double>[] VAndE;
    private IList<V> vertices;
    private IList<E> edges;

    
    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException  if any of the edges have a negative weight
     * @throws IllegalArgumentException  if one of the edges connects to a vertex not
     *                                   present in the 'vertices' list
     */
    public Graph(IList<V> vertices, IList<E> edges) {
        this.vertices = vertices;
        this.edges =  edges;
        this.VAndE = new ArrayDictionary[vertices.size()];
        
        int counter = 0;
        for (V vertex : this.vertices) {
            ArrayDictionary<V, Double> adjacentVertices = new ArrayDictionary<>();
            
            for (E edge : this.edges) {
                if (edge.getWeight() < 0) {
                    throw new IllegalArgumentException();
                }
                
                if (!this.vertices.contains(edge.getVertex1()) || !this.vertices.contains(edge.getVertex2())) {
                    throw new IllegalArgumentException();
                }

                if (edge.getVertex1() == vertex) {
                    adjacentVertices.put(edge.getVertex1(), edge.getWeight());
                } else if (edge.getVertex2() == vertex) {
                    adjacentVertices.put(edge.getVertex2(), edge.getWeight());
                }
            }
            VAndE[counter] = adjacentVertices;
            counter++;
        }
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return vertices.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return edges.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        int counter = 0;
        ISet<E> minEdges = new ChainedHashSet<>();
        ArrayDisjointSet<V> forest = new ArrayDisjointSet<>();
        IList<E> sortedEdges = Searcher.topKSort(edges.size(), edges);
        
        //TODO maybe delete sortedVertices
        IList<V> sortedVertices = new  DoubleLinkedList<>();
        
        if(edges.size() == 1) {
            minEdges.add(sortedEdges.get(0));
            return minEdges;
        }
        
        for (int i = 0; i < vertices.size(); i++) {
            forest.makeSet(vertices.get(i));
        }
        
        while (minEdges.size() < vertices.size() - 1) {
            E edge = sortedEdges.get(counter);
            counter++;
            V v1 = edge.getVertex1();
            V v2 = edge.getVertex2();
            
            //TODO delete test code
            System.out.println("SortedEdges size: " + sortedEdges.size());
            System.out.println("minEdges size: " + minEdges.size());
            System.out.println("Num Vertices: " + vertices.size());
            System.out.println("Counter: " + counter);
            System.out.println("Checking vertices: v1 = " + v1 + " and v2 = " + v2 );
            System.out.println();
            
            
            
            if (forest.findSet(v1) != forest.findSet(v2)) {
                forest.union(v1, v2);
                minEdges.add(edge);
            }
        }
        
        return minEdges;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        ISet<E> MST = findMinimumSpanningTree();
        V vertex = null;
        
        for (E edge : MST) {
            if (edge.getVertex1() == start) {
                vertex = edge.getVertex1();
                break;
            } else if (edge.getVertex2() == start) {
                vertex = edge.getVertex2();
                break;
            }
        }
        
        IList<E> path = new DoubleLinkedList<>();
        path = findPathHelper(vertex, end, MST, path);
        if (path.isEmpty()) {
            throw new NoPathExistsException();
        }
        return path;
    }
    
    private IList<E> findPathHelper(V current, V end, ISet<E> mst, IList<E> path) {
        if (current == end) {
            return path;
        } else {
            
        }
    }
}
