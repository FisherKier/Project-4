package datastructures;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.interfaces.IDisjointSet;
import misc.BaseTest;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

public class TestArrayDisjointSet extends BaseTest {
    private <T> IDisjointSet<T> createForest(T[] items) {
        IDisjointSet<T> forest = new ArrayDisjointSet<>();
        for (T item : items) {
            forest.makeSet(item);
        }
        return forest;
    }

    private <T> void check(IDisjointSet<T> forest, T[] items, int[] expectedIds) {
        for (int i = 0; i < items.length; i++) {
            assertEquals(expectedIds[i], forest.findSet(items[i]));
        }
    }

    
    
    @Test
    public void testPathCompressionSimple() {
        String[] items = {"a", "b", "c", "d", "e"};
        IDisjointSet<String> forest = createForest(items);
        
        for (int i = 0; i < 4; i++) {
            check(forest, items, new int[] {0, 1, 2, 3, 4});
        }
            
        forest.union(items[0], items[1]);
        check(forest, items, new int[] {1, 1, 2, 3, 4});
        
        forest.union(items[2], items[1]);
        check(forest, items, new int[] {1, 1, 1, 3, 4});
        
        forest.union(items[3], items[4]);
        check(forest, items, new int[] {1, 1, 1, 4, 4});
        
        forest.union(items[0], items[3]);
        check(forest, items, new int[] {4, 4, 4, 4, 4});
    }
    
    @Test(timeout=8*SECOND)
    public void testPathCompressionLarge() {
        IDisjointSet<Integer> forest = new ArrayDisjointSet<>();
        Random rand = new Random();
        
        int numItems = 5000;
        for (int i = 0; i < numItems; i++) {
            forest.makeSet(i);
        }
        
        for (int i = 0; i < numItems; i++) {
            int n  = rand.nextInt(numItems);
            int m = rand.nextInt(numItems);
            
            if(forest.findSet(m) != forest.findSet(n)) {
                forest.union(n,m);
            }
        }

        int cap = 6000;
        for (int i = 0; i < cap; i++) {
            for (int j = 0; j < numItems; j++) {
                forest.findSet(j);
            }
        }
    }
    
    @Test(timeout=SECOND)
    public void testMakeSetAndFindSetSimple() {
        String[] items = new String[] {"a", "b", "c", "d", "e"};
        IDisjointSet<String> forest = this.createForest(items);
        
        for (int i = 0; i < 5; i++) {
            check(forest, items, new int[] {0, 1, 2, 3, 4});
        }
    }

    

    @Test(timeout=SECOND)
    public void testUnionSimple() {
        String[] items = new String[] {"a", "b", "c", "d", "e"};
        IDisjointSet<String> forest = this.createForest(items);

        forest.union("a", "b");
        int id1 = forest.findSet("a");
        assertTrue(id1 == 0 || id1 == 1);
        assertEquals(id1, forest.findSet("b"));

        forest.union("c", "d");
        int id2 = forest.findSet("c");
        assertTrue(id2 == 2 || id2 == 3);
        assertEquals(id2, forest.findSet("d"));

        assertEquals(4, forest.findSet("e"));
    }
    
    

    @Test(timeout=SECOND)
    public void testUnionUnequalTrees() {
        String[] items = new String[] {"a", "b", "c", "d", "e"};
        IDisjointSet<String> forest = this.createForest(items);

        forest.union("a", "b");
        int id = forest.findSet("a");

        forest.union("a", "c");

        for (int i = 0; i < 5; i++) {
            check(forest, items, new int[] {id, id, id, 3, 4});
        }
    }

    @Test(timeout=SECOND)
    public void testIllegalFindSet() {
        String[] items = new String[] {"a", "b", "c", "d", "e"};
        IDisjointSet<String> forest = this.createForest(items);

        try {
            forest.findSet("f");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // All ok -- expected result
        }
    }

    @Test(timeout=SECOND)
    public void testIllegalUnion() {
        String[] items = new String[] {"a", "b", "c", "d", "e"};
        IDisjointSet<String> forest = this.createForest(items);

        try {
            forest.union("a", "f");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // All ok -- expected result
        }

        forest.union("a", "b");

        try {
            forest.union("a", "b");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // All ok -- expected result
        }
    }

    @Test(timeout=4 * SECOND)
    public void testLargeForest() {
        IDisjointSet<Integer> forest = new ArrayDisjointSet<>();
        forest.makeSet(0);

        int numItems = 5000;
        for (int i = 1; i < numItems; i++) {
            forest.makeSet(i);
            forest.union(0, i);
        }

        int cap = 6000;
        int id = forest.findSet(0);
        for (int i = 0; i < cap; i++) {
            for (int j = 0; j < numItems; j++) {
                assertEquals(id, forest.findSet(j));
            }
        }
    }
    
    @Test(timeout=8 * SECOND)
    public void testReallyLargeForest() {
        IDisjointSet<Integer> forest = new ArrayDisjointSet<>();
        forest.makeSet(0);

        int numItems = 10000;
        for (int i = 1; i < numItems; i++) {
            forest.makeSet(i);
        }

        int cap = 6000;
        for (int i = 0; i < cap; i++) {
            for (int j = 0; j < numItems; j++) {
                assertEquals(forest.findSet(j), forest.findSet(j));
            }
        }
    }
    
}
