package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;
    private int chainSize;
    private int elementSize;
    private int totalSize;
    private double loadFactor;
    
    // You're encouraged to add extra fields (and helper methods) though!

    //make basic HashDictionary with 50 'chains'
    public ChainedHashDictionary() {
        chains = makeArrayOfChains(50);
        totalSize = 50;
        chainSize = 0;
        elementSize = 0;
        loadFactor = .7;
    }
    
    public ChainedHashDictionary(int size) {
        chains = makeArrayOfChains(size);
        totalSize = size;
        chainSize = 0;
        elementSize = 0;
        loadFactor = .7;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    @Override
    public V get(K key) {
        int hashVal = getHashVal(key);
        
        if (chains[hashVal] == null) {
            throw new NoSuchKeyException();
        } else {
            return chains[hashVal].get(key);
        }
    }
    
    private void resize() {
        IDictionary<K, V>[] tempList = makeArrayOfChains(totalSize * 2);
        totalSize = totalSize * 2;
        chainSize = 0;
        for (IDictionary<K, V> bucket : chains) {
            if (bucket != null) {

                for (KVPair<K, V> pair : bucket) {
                    int hashVal = getHashVal(pair.getKey());
                    
                    if (tempList[hashVal] == null) {
                        tempList[hashVal] = new ArrayDictionary<K, V>();
                        tempList[hashVal].put(pair.getKey(), pair.getValue());
                        chainSize++;
                    } else {
                        tempList[hashVal].put(pair.getKey(), pair.getValue());
                    }

                }
            }
        }
        chains = tempList;
    }

    @Override
    public void put(K key, V value) {       
        //check if there are too many chains in the array,
        //and if so resize the array
    
        
        int hashVal = getHashVal(key);
        
        //check if there is an arrayDictionary at the hash value,
        //if not create one and put the key + value in it,
        //then increase elementSize
        
        if (chains[hashVal] == null) {
            chains[hashVal] = new ArrayDictionary<K, V>();
            chains[hashVal].put(key, value);
            elementSize++;
            chainSize++;
        }
        //put the key and value into the existing arrayDictionary
        
        else {
            //if the key isn't in the dictionary already increase element size
            
            if (!chains[hashVal].containsKey(key)) {
                elementSize++;
            }
            chains[hashVal].put(key, value);
        }
        
        if (((double) (chainSize + 1)) / ((double) (totalSize + 1)) > loadFactor) {
            resize();
        }
    }

    @Override
    public V remove(K key) {
        int hashVal = getHashVal(key);
        
        if (chains[hashVal] == null) {
            throw new NoSuchKeyException();
        }
        elementSize--;
        return chains[hashVal].remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        int hashVal = getHashVal(key);
        
        if (chains[hashVal] == null) {
            return false;
        } else {
            return chains[hashVal].containsKey(key);
        }
    }

    @Override
    public int size() {
        return elementSize;
    }
    
    //returns the index in the chains array for the given key
    public int getHashVal(K key) {
        if (key == null) {
            return 0;
        } else if (key.hashCode() < 0) {
            return key.hashCode() % totalSize * -1;
        } else {
            return key.hashCode() % totalSize;
        }
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Think about what exactly your *invariants* are. Once you've
     *    decided, write them down in a comment somewhere to help you
     *    remember.
     *
     * 3. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 4. Think about what exactly your *invariants* are. As a 
     *    reminder, an *invariant* is something that must *always* be 
     *    true once the constructor is done setting up the class AND 
     *    must *always* be true both before and after you call any 
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int step = 0;

        private int index = 0;
        private int limit;
        
        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.limit = chains.length;
        }

        @Override
        public boolean hasNext() {
            int count = 0;

            for (int i = index; i < limit; i++) {
                IDictionary<K, V> bucket = chains[i];

                if (bucket != null) {
                Iterator<KVPair<K, V>> bucketIterator = bucket.iterator();
                while (bucketIterator.hasNext()) {
                    count++;
                    bucketIterator.next();
                    if (count > step) {
                        return true;
                    }

                    }           
                }
                step = 0;
                count = 0;
                index++;

            }
            return false;
         }
            
              

        @Override
        public KVPair<K, V> next() {
            int count = 0;

            for (int i = index; i < limit; i++) {
                IDictionary<K, V> bucket = chains[i];

                if (bucket != null) {
                Iterator<KVPair<K, V>> bucketIterator = bucket.iterator();
                while (bucketIterator.hasNext()) {
                    count++;
                    KVPair<K, V> temp = bucketIterator.next();
                    if (count > step) {
                        step++;
                        return temp;

                    } 
                }
            }
                step = 0;
                count = 0;
                index++;

            }
            throw new NoSuchElementException();    
        }
    }
}