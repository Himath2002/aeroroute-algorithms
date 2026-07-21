package io.github.himath2002.aeroroute.structure;

import java.util.NoSuchElementException;
import java.util.Objects;

/** A dynamically resized circular queue used by breadth-first route discovery. */
public final class ArrayQueue<T> {
    private static final int DEFAULT_CAPACITY = 16;

    private Object[] elements;
    private int head;
    private int tail;
    private int size;

    public ArrayQueue() {
        elements = new Object[DEFAULT_CAPACITY];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void offer(T value) {
        Objects.requireNonNull(value, "value");
        if (size == elements.length) {
            resize(elements.length * 2);
        }
        elements[tail] = value;
        tail = (tail + 1) % elements.length;
        size++;
    }

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty.");
        }
        return elementAt(head);
    }

    public T poll() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty.");
        }
        T value = elementAt(head);
        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;
        return value;
    }

    @SuppressWarnings("unchecked")
    private T elementAt(int index) {
        return (T) elements[index];
    }

    private void resize(int capacity) {
        Object[] resized = new Object[capacity];
        for (int index = 0; index < size; index++) {
            resized[index] = elements[(head + index) % elements.length];
        }
        elements = resized;
        head = 0;
        tail = size;
    }
}
