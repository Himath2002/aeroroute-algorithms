package io.github.himath2002.aeroroute.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

/** A compact, generic doubly linked list used by the graph adjacency model. */
public final class DoublyLinkedList<T> implements Iterable<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    /** Returns whether the list has no values. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns the number of values in the list. */
    public int size() {
        return size;
    }

    /** Inserts a non-null value at the head. */
    public void addFirst(T value) {
        Node<T> node = new Node<>(Objects.requireNonNull(value, "value"));
        node.next = head;
        if (head == null) {
            tail = node;
        } else {
            head.previous = node;
        }
        head = node;
        size++;
    }

    /** Appends a non-null value at the tail. */
    public void addLast(T value) {
        Node<T> node = new Node<>(Objects.requireNonNull(value, "value"));
        node.previous = tail;
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        size++;
    }

    /** Returns the head value without removing it. */
    public T first() {
        if (head == null) {
            throw new NoSuchElementException("List is empty.");
        }
        return head.value;
    }

    /** Returns the tail value without removing it. */
    public T last() {
        if (tail == null) {
            throw new NoSuchElementException("List is empty.");
        }
        return tail.value;
    }

    /** Removes and returns the head value. */
    public T removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("List is empty.");
        }
        return unlink(head);
    }

    /** Removes and returns the tail value. */
    public T removeLast() {
        if (tail == null) {
            throw new NoSuchElementException("List is empty.");
        }
        return unlink(tail);
    }

    /** Removes the first value equal to the requested value. */
    public boolean remove(T value) {
        for (Node<T> current = head; current != null; current = current.next) {
            if (Objects.equals(current.value, value)) {
                unlink(current);
                return true;
            }
        }
        return false;
    }

    /** Removes the first value accepted by the predicate. */
    public boolean removeIf(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        for (Node<T> current = head; current != null; current = current.next) {
            if (predicate.test(current.value)) {
                unlink(current);
                return true;
            }
        }
        return false;
    }

    /** Returns whether an equal value exists in the list. */
    public boolean contains(T value) {
        for (T item : this) {
            if (Objects.equals(item, value)) {
                return true;
            }
        }
        return false;
    }

    /** Returns an immutable snapshot in linked-list order. */
    public List<T> toList() {
        List<T> values = new ArrayList<>(size);
        for (T item : this) {
            values.add(item);
        }
        return List.copyOf(values);
    }

    private T unlink(Node<T> node) {
        if (node.previous == null) {
            head = node.next;
        } else {
            node.previous.next = node.next;
        }

        if (node.next == null) {
            tail = node.previous;
        } else {
            node.next.previous = node.previous;
        }

        size--;
        return node.value;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node<T> next = head;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public T next() {
                if (next == null) {
                    throw new NoSuchElementException();
                }
                T value = next.value;
                next = next.next;
                return value;
            }
        };
    }

    private static final class Node<T> {
        private final T value;
        private Node<T> next;
        private Node<T> previous;

        private Node(T value) {
            this.value = value;
        }
    }
}
