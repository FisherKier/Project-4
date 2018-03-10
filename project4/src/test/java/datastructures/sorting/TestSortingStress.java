package datastructures.sorting;

import misc.BaseTest;
import misc.Searcher;

import org.junit.Test;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestSortingStress extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }
    
    protected List<Integer> makeUnsortedList() {
        Integer[] list = new Integer[5000000];
        for (int i = 0; i < 5000000; i++) {
           list[i] = (3376 * i) % 6002;
        }
        return Arrays.asList(list);
    }
    
    protected IList<Integer> listtoIList(List<Integer> list) {
        IList<Integer> iList = new DoubleLinkedList<>();
        for (Integer entry: list) {
           iList.add(entry);
        }
        return iList;
    }
    

    
    @Test(timeout=10*SECOND)
    public void testHeapForwardInsertOfMany() {
        int runs = 5000000;
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < runs; i++) {
            heap.insert(i);
        }
        assertEquals(runs, heap.size());
    }
    
    @Test(timeout=10*SECOND)
    public void testHeapBackwardsInsertOfMany() {
        int runs = 5000000;
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = runs-1; i >= 0; i--) {
            heap.insert(i);
        }
        assertEquals(runs, heap.size());
    }
    
    
    @Test(timeout=10*SECOND)
    public void testHeapInsertAndRemoveOfMany() {
        int runs = 5000000;
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < runs; i++) {
            heap.insert(i);
        }
        assertEquals(runs, heap.size());
        for (int i = 0; i < runs; i++) {
            assertEquals(i, heap.removeMin());
        }
        assertTrue(heap.isEmpty());
    }
    
    @Test(timeout=10*SECOND)
    public void testHeapInsertAndPeakOfMany() {
        int runs = 5000000;
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < runs; i++) {
            heap.insert(i);
        }
        assertEquals(runs, heap.size());
        for (int i = 0; i < runs; i++) {
            int temp = heap.peekMin();
            assertEquals(i, temp);
            heap.removeMin();
        }
        assertTrue(heap.isEmpty());
    }
    
    
    
    
    @Test(timeout=10*SECOND)
    public void testInputofUnsortedSmallK() {
        int runs = 5000000;
        int k = 5;
        List<Integer> list = makeUnsortedList();
        IList<Integer> output = Searcher.topKSort(k, listtoIList(list));
        IList<Integer> expected = new DoubleLinkedList<>();
        Collections.sort(list);
        for (int i = runs - k; i < runs; i++) {
            expected.add(list.get(i));
        }
        for (int i = 0; i < k; i++) {
        assertEquals(expected.remove(), output.remove()); 
        }
    }
    
    @Test(timeout=30*SECOND)
    public void testInputofUnsortedMediumK() {
        int runs = 5000000;
        int k = 500;
        List<Integer> list = makeUnsortedList();
        IList<Integer> output = Searcher.topKSort(k, listtoIList(list));
        IList<Integer> expected = new DoubleLinkedList<>();
        Collections.sort(list);
        for (int i = runs - k; i < runs; i++) {
            expected.add(list.get(i));
        }
        for (int i = 0; i < k; i++) {
        assertEquals(expected.remove(), output.remove()); 
        }
    }
    
    @Test(timeout=15*SECOND)
    public void testInputofUnsortedLargeK() {
        int runs = 5000000;
        int k = 50000;
        List<Integer> list = makeUnsortedList();
        IList<Integer> output = Searcher.topKSort(k, listtoIList(list));
        IList<Integer> expected = new DoubleLinkedList<>();
        Collections.sort(list);
        for (int i = runs - k; i < runs; i++) {
            expected.add(list.get(i));
        }
        for (int i = 0; i < k; i++) {
        assertEquals(expected.remove(), output.remove()); 
        }
    }
    
    @Test(timeout=20*SECOND)
    public void testInputofUnsortedFullK() {
        int runs = 5000000;
        int k = 5000000;
        List<Integer> list = makeUnsortedList();
        IList<Integer> output = Searcher.topKSort(k, listtoIList(list));
        IList<Integer> expected = new DoubleLinkedList<>();
        Collections.sort(list);
        for (int i = runs - k; i < runs; i++) {
            expected.add(list.get(i));
        }
        for (int i = 0; i < k; i++) {
        assertEquals(expected.remove(), output.remove()); 
        }
    }
    
    @Test(timeout=20*SECOND)
    public void testInputofUnsortedOverK() {
        int runs = 5000000;
        int k = runs * 2;
        List<Integer> list = makeUnsortedList();
        IList<Integer> output = Searcher.topKSort(k, listtoIList(list));
        IList<Integer> expected = new DoubleLinkedList<>();
        Collections.sort(list);
        for (int i = 0; i < runs; i++) {
            expected.add(list.get(i));
        }
        for (int i = 0; i < runs; i++) {
        assertEquals(expected.remove(), output.remove()); 
        }
    }
}

