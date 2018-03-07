package datastructures.concrete.dictionaries;

import java.util.Iterator;
import java.util.NoSuchElementException;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs;
    private int totalSize;
    private int actualSize;
    // You're encouraged to add extra fields (and helper methods) though!

    public ArrayDictionary() {
        pairs = makeArrayOfPairs(10);
        totalSize = 10;
        actualSize = 0;
    }
    
    public ArrayDictionary(int size) {
        pairs = makeArrayOfPairs(size);
        totalSize = size;
        actualSize = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }
    
    @Override
    public V get(K key) {
        for (int i = 0; i < totalSize; i++) {
            if (pairs[i] != null && (pairs[i].key == key || pairs[i].key.equals(key))) {
                return pairs[i].value;
            }
        }
        throw new NoSuchKeyException();
    }
    
    @Override
    public void put(K key, V value) {
        
            boolean isOpen = false;
            boolean isCopy = false;
            int index = -1;
            for (int i = 0; i < totalSize; i++) {
                
                if (pairs[i] == null) {
                    isOpen = true;
                    index = i;
                    break;
                }
                
                else if (key == null) {
                    if (pairs[i] != null && pairs[i].key == null) {
                        pairs[i].value = value;
                        isCopy = true;
                        break;
                    }
                }
                
                else if (pairs[i] != null && 
                        pairs[i].key != null && 
                        key != null &&
                        (pairs[i].key.equals(key) || 
                         pairs[i].key == key)) {
                    
                    pairs[i].value = value;
                    isCopy = true;
                    break;
                }
                
            }
            
            if (isOpen && !isCopy) {
                pairs[index] = new Pair<K, V>(key, value);
                actualSize++;
            }

            else if (!isOpen && !isCopy) {
                Pair<K, V>[] tempList;
                tempList = makeArrayOfPairs(totalSize * 2);
                
                for (int i = 0; i < totalSize; i++) {
                        tempList[i] = new Pair<K, V>(pairs[i].key, pairs[i].value);
                }
                tempList[totalSize] = new Pair<K, V>(key, value);
                totalSize = totalSize * 2;
                actualSize++;
                pairs = tempList;
            }
        
    }

    @Override
    public V remove(K key) {
        for (int i = 0; i < totalSize; i++) {
            if (pairs[i] != null && (pairs[i].key == key || pairs[i].key.equals(key))) {
                V temp = pairs[i].value;
                pairs[i] = null;
                actualSize--;
                return temp;
            }
        }
        
        throw new NoSuchKeyException();
    }

    @Override
    public boolean containsKey(K key) {
        for (int i = 0; i < totalSize; i++) {
            if (key == null) {
                if (pairs[i] != null && pairs[i].key == null) {
                    return true;
                }
            }
            
            else if (pairs[i] != null && 
                    pairs[i].key != null && 
                    key != null &&
                    (pairs[i].key.equals(key) || 
                     pairs[i].key == key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return actualSize;
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        public K getKey() {
            return key;
        }
        
        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
    
    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        private Pair<K, V>[] pairs;
        int current;
        
        public ArrayDictionaryIterator(Pair<K, V>[] pairs) {
            this.pairs = pairs;
            current = -1;
        }
        
        public boolean hasNext() {
            if (pairs != null && current < pairs.length) {
                for (int i = current + 1; i < pairs.length; i++) {
                    if (pairs[i] != null) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        public KVPair<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            current++;
            for (int i = current; i < pairs.length; i++) {
                if (pairs[i] != null) {
                    return new KVPair<K, V>(pairs[i].key, pairs[i].value);
                }
            }
            throw new NoSuchElementException();
        }
    }
    
    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictionaryIterator<>(pairs);
    }
}