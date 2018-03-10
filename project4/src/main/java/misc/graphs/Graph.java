package misc.graphs;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import mazes.entities.Room;
import misc.Searcher;
import misc.exceptions.NoPathExistsException;

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

        for (E edge : this.edges) {
            if (edge.getWeight() < 0) {
                throw new IllegalArgumentException();
            }
            
            if (!vertices.contains(edge.getVertex1()) || !vertices.contains(edge.getVertex2())) {
                throw new IllegalArgumentException();
            }
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
        
        if (edges.size() == 1) {
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
        
        //Initialize vertex distance values
        IDictionary<V, Double> vertexDistance = new ChainedHashDictionary<V, Double>();
        for (V vertex : vertices) {
            vertexDistance.put(vertex, Double.POSITIVE_INFINITY);
        }
        vertexDistance.put(start, 0.0);
        //store the path from the start to every vertex, to be returned one the ending vertex is found
        IDictionary<V, IList<E>> vertexPath = new ChainedHashDictionary<V, IList<E>>();
        vertexPath.put(start, new DoubleLinkedList<E>());
        //keep track of which nodes have been visited and which can be explored
        ISet<V> explored = new ChainedHashSet<V>();
        ISet<V> available = new ChainedHashSet<V>();
        V current = start;
        //create a dictionary to find the edges of any vertex
        IDictionary<V, ISet<E>> vertexEdges = new ChainedHashDictionary<V, ISet<E>>();
        for (E edge : edges) {
           V tempVertex = edge.getVertex1();
                   if (vertexEdges.containsKey(tempVertex)) {
                       ISet<E> tempSet = vertexEdges.get(tempVertex);
                       tempSet.add(edge);
                       vertexEdges.put(tempVertex, tempSet);
                   }else {
                       ISet<E> tempSet = new ChainedHashSet<E>();
                       tempSet.add(edge);
                       vertexEdges.put(tempVertex, tempSet); 
                   }
          tempVertex = edge.getVertex2();
                   if (vertexEdges.containsKey(tempVertex)) {
                       ISet<E> tempSet = vertexEdges.get(tempVertex);
                       tempSet.add(edge);
                       vertexEdges.put(tempVertex, tempSet);
                   }else {
                       ISet<E> tempSet = new ChainedHashSet<E>();
                       tempSet.add(edge);
                       vertexEdges.put(tempVertex, tempSet); 
                   }
        }
        
        
        while (current != null) {
            if (current.equals(end)) {
                return vertexPath.get(current);
            }
            
            explored.add(current);
            //explore currents edges
            for (E edge : vertexEdges.get(current)) {
                V otherEdge = edge.getOtherVertex(current);
                //check if values are locked
                if (!explored.contains(otherEdge)) {
                //update available vertices for next run
                available.add(otherEdge);
                //update distance
                Double tempDistance = vertexDistance.get(current) + edge.getWeight();
                if (vertexDistance.get(otherEdge) > tempDistance) {
                vertexDistance.put(otherEdge, tempDistance);
                //update the new explored paths
                IList<E> newPath = new DoubleLinkedList<E>();
                for (E pathEdge : vertexPath.get(current)) {
                    newPath.add(pathEdge);
                }
                newPath.add(edge);
                vertexPath.put(otherEdge, newPath);
                }
                }
            }
            //find next vertex, one with shortest distance and update current
            current = findNextCurrent(available, vertexDistance);
            //check if current is the end vertex, if so return
            
        }
        //could not find end return null or throw exception
        throw new NoPathExistsException();
    }
    
    private V findNextCurrent(ISet<V> available, IDictionary<V, Double> vertexDistance) {
        V newCurrent = null;
        for (V next : available) {
            if (newCurrent == null || vertexDistance.get(next) < vertexDistance.get(newCurrent)) {
                newCurrent = next;
            }
        }
        if (newCurrent != null) {
            available.remove(newCurrent);
        }
        return newCurrent;
    }
}
