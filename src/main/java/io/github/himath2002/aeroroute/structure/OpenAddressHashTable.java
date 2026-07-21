package io.github.himath2002.aeroroute.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;

/** A string-keyed, linear-probing hash table with tombstones and automatic resizing. */
public final class OpenAddressHashTable<V> {
    private static final int MINIMUM_CAPACITY = 11;
    private static final double MAXIMUM_LOAD = 0.65;

    private Slot<V>[] slots;
    private int size;

    /** Creates an empty table with the minimum prime capacity. */
    public OpenAddressHashTable() {
        this(MINIMUM_CAPACITY);
    }

    /** Creates an empty table using at least the requested capacity. */
    @SuppressWarnings("unchecked")
    public OpenAddressHashTable(int requestedCapacity) {
        slots = (Slot<V>[]) new Slot<?>[nextPrime(Math.max(MINIMUM_CAPACITY, requestedCapacity))];
    }

    /** Returns the number of active entries. */
    public int size() {
        return size;
    }

    /** Returns whether the table has no active entries. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns active entries divided by slot capacity. */
    public double loadFactor() {
        return (double) size / slots.length;
    }

    /** Returns whether a normalized key exists. */
    public boolean containsKey(String key) {
        return findIndex(normalizeKey(key)) >= 0;
    }

    /** Returns the value for a key or fails when the key is absent. */
    public V get(String key) {
        int index = findIndex(normalizeKey(key));
        if (index < 0) {
            throw new NoSuchElementException("No value exists for key " + key + ".");
        }
        return slots[index].value;
    }

    /** Returns the value for a key, or {@code null} when absent. */
    public V getOrNull(String key) {
        int index = findIndex(normalizeKey(key));
        return index < 0 ? null : slots[index].value;
    }

    /** Inserts or replaces a non-null value under a normalized key. */
    public void put(String key, V value) {
        String normalizedKey = normalizeKey(key);
        Objects.requireNonNull(value, "value");

        if ((double) (size + 1) / slots.length > MAXIMUM_LOAD) {
            resize(nextPrime(slots.length * 2));
        }

        insert(normalizedKey, value);
    }

    /** Tombstones a key and returns its former value. */
    public V remove(String key) {
        int index = findIndex(normalizeKey(key));
        if (index < 0) {
            throw new NoSuchElementException("No value exists for key " + key + ".");
        }

        Slot<V> slot = slots[index];
        slot.deleted = true;
        size--;
        return slot.value;
    }

    /** Returns an immutable snapshot of active entries. */
    public List<Entry<V>> entries() {
        List<Entry<V>> entries = new ArrayList<>(size);
        for (Slot<V> slot : slots) {
            if (slot != null && !slot.deleted) {
                entries.add(new Entry<>(slot.key, slot.value));
            }
        }
        return List.copyOf(entries);
    }

    private void insert(String key, V value) {
        int start = indexFor(key, slots.length);
        int firstDeleted = -1;

        for (int offset = 0; offset < slots.length; offset++) {
            int index = (start + offset) % slots.length;
            Slot<V> slot = slots[index];

            if (slot == null) {
                int destination = firstDeleted >= 0 ? firstDeleted : index;
                slots[destination] = new Slot<>(key, value);
                size++;
                return;
            }
            if (slot.deleted) {
                if (firstDeleted < 0) {
                    firstDeleted = index;
                }
            } else if (slot.key.equals(key)) {
                slot.value = value;
                return;
            }
        }

        if (firstDeleted >= 0) {
            slots[firstDeleted] = new Slot<>(key, value);
            size++;
            return;
        }
        throw new IllegalStateException("Hash table has no available slot.");
    }

    private int findIndex(String key) {
        int start = indexFor(key, slots.length);
        for (int offset = 0; offset < slots.length; offset++) {
            int index = (start + offset) % slots.length;
            Slot<V> slot = slots[index];
            if (slot == null) {
                return -1;
            }
            if (!slot.deleted && slot.key.equals(key)) {
                return index;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private void resize(int capacity) {
        Slot<V>[] previous = slots;
        slots = (Slot<V>[]) new Slot<?>[capacity];
        size = 0;
        for (Slot<V> slot : previous) {
            if (slot != null && !slot.deleted) {
                insert(slot.key, slot.value);
            }
        }
    }

    private static String normalizeKey(String key) {
        Objects.requireNonNull(key, "key");
        String normalized = key.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be blank.");
        }
        return normalized;
    }

    private static int indexFor(String key, int capacity) {
        int hash = 0;
        for (int index = 0; index < key.length(); index++) {
            hash = 31 * hash + key.charAt(index);
        }
        return Math.floorMod(hash, capacity);
    }

    private static int nextPrime(int value) {
        int candidate = Math.max(2, value);
        while (!isPrime(candidate)) {
            candidate++;
        }
        return candidate;
    }

    private static boolean isPrime(int value) {
        if (value < 2) {
            return false;
        }
        for (int divisor = 2; divisor * divisor <= value; divisor++) {
            if (value % divisor == 0) {
                return false;
            }
        }
        return true;
    }

    /** Immutable key-value pair exposed by snapshot operations. */
    public record Entry<V>(String key, V value) {
    }

    private static final class Slot<V> {
        private final String key;
        private V value;
        private boolean deleted;

        private Slot(String key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
