package datastructures.concrete;

import java.util.Arrays;

import datastructures.concrete.dictionaries.ArrayDictionary;

import datastructures.concrete.dictionaries.ChainedHashDictionary;

import datastructures.interfaces.IDisjointSet;

/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    
    
    
    
    //TODO remove this variable (for testing)
    private ArrayDictionary<Integer, T> items;
    
    
    //stores the vertices traversed for findSet path compression
    private int[] verticesTraversed;
    
    //stores the current value of an integer representative,
    //which increases by 1 each time an item is added
    private int currentVertex;
    
    //stores the generic items and their integer representatives
    private ChainedHashDictionary<T, Integer> ids;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        pointers = new int[50];
        currentVertex = 0;
        ids = new ChainedHashDictionary<T, Integer>();
        verticesTraversed= new int[50];
        
        //TODO remove
        items = new ArrayDictionary<Integer, T>(50);
        
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
            
            //TODO remove
            items.put(currentVertex, item);

            currentVertex++;
    }

    //returns the representative of the set / the index of the pointers array
    @Override
    public int findSet(T item) {
        if(!ids.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        int vertex = ids.get(item);
        int parent = pointers[vertex];
        
        
        int counter = 0;
        
        //TODO remove
        /*
        System.out.println("FIND SET");
        System.out.println("Looking for: " + item.toString());
        System.out.println("Vertex: " + vertex);
        System.out.println("Parent: " + parent);
        System.out.println("Pointers Array: " + Arrays.toString(pointers));
        System.out.print("Items Array: ");
        for(KVPair<Integer, T> pair : items) {
            System.out.print(pair.getValue() + ", ");
        }
        System.out.println();
        System.out.println("Next lines are traversing the tree");
        System.out.println();
        */
        
        
        boolean hasFound = false;         
        while(!hasFound) {
            if(parent < 0) {
                
                //TODO remove
                /*
                System.out.println("Found set");
                System.out.println("Representative: " + vertex);
                System.out.println();
                */
                
                hasFound = true;
                
                for(int i = 0; i < counter; i++) {
                    pointers[verticesTraversed[i]] = vertex;
                }
                
                return vertex;
                
            } else {
                verticesTraversed[counter] = vertex;
                counter++;
                
                vertex = parent;
                parent = pointers[parent];
                
                
                //TODO remove
                /*
                System.out.println("Traversing...");
                System.out.println("Vertex: " + vertex);
                System.out.println("Parent: " + parent);
                */
                
            }
        }
        return -1;
    }

    @Override
    public void union(T item1, T item2) {
        if(!ids.containsKey(item1) || !ids.containsKey(item2)) {
            throw new IllegalArgumentException();
        }
        
        int leftVertex = findSet(item1);
        int rightVertex = findSet(item2);
        
        if(leftVertex == rightVertex) {
            throw new IllegalArgumentException();
        }
        
        //TODO remove
        /*
        System.out.println("UNION");
        System.out.println("Unioning: " + item1.toString() + " and " + item2.toString());
        System.out.println("LeftVertex: " + leftVertex);
        System.out.println("rightVertex: " + rightVertex);
        System.out.println("Pointers Array: " + Arrays.toString(pointers));
        System.out.print("Items Array: ");
        for(KVPair<Integer, T> pair : items) {
            System.out.print(pair.getValue() + ", ");
        }
        System.out.println();
        System.out.println();
        */
        
        if(pointers[leftVertex] < pointers[rightVertex]) {
            pointers[rightVertex] = leftVertex;
        } else if(pointers[leftVertex] > pointers[rightVertex]) {
            pointers[leftVertex] = rightVertex;
        } else if(pointers[leftVertex] == pointers[rightVertex]) {
            pointers[leftVertex] = rightVertex;
            pointers[rightVertex]--;
        }
        
        //TODO remove
        /*
        System.out.println("Union Complete");
        System.out.println("Pointers Array: " + Arrays.toString(pointers));
        System.out.println();
        System.out.println();
        */
        
    }
    
    //returns the integer representative of the given item
    private int getItemVal(T item) {
        return ids.get(item);
    }
    
    private void resize() {
        int[] temp = new int[pointers.length * 2];
        for(int i = 0; i < pointers.length; i ++) {
            temp[i] = pointers[i];
        }
        pointers = temp;
        verticesTraversed = new int[pointers.length * 2];
    }
}
