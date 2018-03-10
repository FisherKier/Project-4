package datastructures.concrete;



import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;

/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;

    
    //stores the vertices traversed for findSet path compression
    private int[] verticesTraversed;
    
    //stores the current value of an integer representative,
    //which increases by 1 each time an item is added
    private int currentVertex;
    
    //stores the generic items and their integer representatives
    private IDictionary<T, Integer> ids;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        pointers = new int[50];
        currentVertex = 0;
        ids = new ChainedHashDictionary<T, Integer>();
        verticesTraversed= new int[50];
        
        
    }

    //if trying to make a set whose item already exists in another set then
    //an exception will be thrown
    @Override
    public void makeSet(T item) {
        if (ids.containsKey(item)) {
            throw new IllegalArgumentException();
        } if (currentVertex == pointers.length) {
            resize();
        }
            pointers[currentVertex] = -1;
            ids.put(item, currentVertex);
            


            currentVertex++;
    }

    //returns the representative of the set / the index of the pointers array
    @Override
    public int findSet(T item) {
        if (!ids.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        int vertex = ids.get(item);
        int parent = pointers[vertex];
        
        
        int counter = 0;
        
        boolean hasFound = false;         
        while (!hasFound) {
            if (parent < 0) {

                hasFound = true;
                
                for (int i = 0; i < counter; i++) {
                    pointers[verticesTraversed[i]] = vertex;
                }
                
                return vertex;
                
            } else {
                verticesTraversed[counter] = vertex;
                counter++;
                
                vertex = parent;
                parent = pointers[parent];
                
            }
        }
        return -1;
    }

    @Override
    public void union(T item1, T item2) {
        if (!ids.containsKey(item1) || !ids.containsKey(item2)) {
            throw new IllegalArgumentException();
        }
        
        int leftVertex = findSet(item1);
        int rightVertex = findSet(item2);
        
        if (leftVertex == rightVertex) {
            throw new IllegalArgumentException();
        }
        
        
        if (pointers[leftVertex] < pointers[rightVertex]) {
            pointers[rightVertex] = leftVertex;
        } else if (pointers[leftVertex] > pointers[rightVertex]) {
            pointers[leftVertex] = rightVertex;
        } else if (pointers[leftVertex] == pointers[rightVertex]) {
            pointers[leftVertex] = rightVertex;
            pointers[rightVertex]--;
        }
        
    }
    
    //returns the integer representative of the given item
    private int getItemVal(T item) {
        return ids.get(item);
    }
    
    private void resize() {
        int[] temp = new int[pointers.length * 2];
        for (int i = 0; i < pointers.length; i++) {
            temp[i] = pointers[i];
        }
        pointers = temp;
        verticesTraversed = new int[pointers.length * 2];
    }
}
