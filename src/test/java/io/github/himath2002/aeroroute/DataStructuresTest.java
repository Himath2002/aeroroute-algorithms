package io.github.himath2002.aeroroute;

import io.github.himath2002.aeroroute.structure.ArrayQueue;
import io.github.himath2002.aeroroute.structure.BinaryHeap;
import io.github.himath2002.aeroroute.structure.DoublyLinkedList;
import io.github.himath2002.aeroroute.structure.OpenAddressHashTable;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataStructuresTest {
    @Test
    void linkedListMaintainsBothEndsAcrossRemoval() {
        DoublyLinkedList<String> values = new DoublyLinkedList<>();
        values.addFirst("B");
        values.addFirst("A");
        values.addLast("C");

        assertEquals("A", values.removeFirst());
        assertTrue(values.removeIf("B"::equals));
        assertEquals("C", values.first());
        assertEquals("C", values.last());
        assertEquals(1, values.size());
    }

    @Test
    void circularQueuePreservesOrderWhenItResizesAndWraps() {
        ArrayQueue<Integer> queue = new ArrayQueue<>();
        for (int value = 0; value < 40; value++) {
            queue.offer(value);
        }
        for (int expected = 0; expected < 20; expected++) {
            assertEquals(expected, queue.poll());
        }
        for (int value = 40; value < 80; value++) {
            queue.offer(value);
        }
        for (int expected = 20; expected < 80; expected++) {
            assertEquals(expected, queue.poll());
        }
        assertTrue(queue.isEmpty());
    }

    @Test
    void hashTableReusesTombstonesAndSurvivesResizing() {
        OpenAddressHashTable<Integer> table = new OpenAddressHashTable<>(3);
        for (int value = 0; value < 200; value++) {
            table.put("KEY" + value, value);
        }
        for (int value = 0; value < 100; value += 2) {
            assertEquals(value, table.remove("KEY" + value));
        }
        for (int value = 200; value < 260; value++) {
            table.put("KEY" + value, value);
        }

        assertEquals(210, table.size());
        assertEquals(259, table.get("key259"));
        assertFalse(table.containsKey("KEY20"));
        assertThrows(NoSuchElementException.class, () -> table.get("KEY20"));
    }

    @Test
    void binaryHeapReturnsValuesByComparatorPriority() {
        BinaryHeap<Integer> heap = new BinaryHeap<>(Comparator.naturalOrder());
        heap.offer(7);
        heap.offer(2);
        heap.offer(9);
        heap.offer(1);

        assertEquals(1, heap.poll());
        assertEquals(2, heap.poll());
        assertEquals(7, heap.poll());
        assertEquals(9, heap.poll());
        assertThrows(NoSuchElementException.class, heap::poll);
    }
}
