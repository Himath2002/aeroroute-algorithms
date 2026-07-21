package io.github.himath2002.aeroroute.structure;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;

/** A comparator-driven binary min-heap with logarithmic insertion and removal. */
public final class BinaryHeap<T> {
    private static final int DEFAULT_CAPACITY = 16;

    private final Comparator<? super T> comparator;
    private Object[] elements = new Object[DEFAULT_CAPACITY];
    private int size;

    /** Creates an empty min-heap ordered by the supplied comparator. */
    public BinaryHeap(Comparator<? super T> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator");
    }

    /** Returns the number of values in the heap. */
    public int size() {
        return size;
    }

    /** Returns whether the heap has no values. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Adds a non-null value while preserving heap order. */
    public void offer(T value) {
        Objects.requireNonNull(value, "value");
        ensureCapacity();
        elements[size] = value;
        siftUp(size);
        size++;
    }

    /** Returns the minimum value without removing it. */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty.");
        }
        return elementAt(0);
    }

    /** Removes and returns the minimum value. */
    public T poll() {
        T root = peek();
        size--;
        elements[0] = elements[size];
        elements[size] = null;
        if (size > 0) {
            siftDown(0);
        }
        return root;
    }

    private void siftUp(int index) {
        int current = index;
        while (current > 0) {
            int parent = (current - 1) / 2;
            if (comparator.compare(elementAt(current), elementAt(parent)) >= 0) {
                return;
            }
            swap(current, parent);
            current = parent;
        }
    }

    private void siftDown(int index) {
        int current = index;
        while (true) {
            int left = current * 2 + 1;
            if (left >= size) {
                return;
            }
            int right = left + 1;
            int smallest = right < size
                    && comparator.compare(elementAt(right), elementAt(left)) < 0 ? right : left;
            if (comparator.compare(elementAt(current), elementAt(smallest)) <= 0) {
                return;
            }
            swap(current, smallest);
            current = smallest;
        }
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, elements.length * 2);
        }
    }

    private void swap(int first, int second) {
        Object value = elements[first];
        elements[first] = elements[second];
        elements[second] = value;
    }

    @SuppressWarnings("unchecked")
    private T elementAt(int index) {
        return (T) elements[index];
    }
}
