package linkedlist;

import java.util.NoSuchElementException;

public class CustomLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public CustomLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Inserts a value at the front of the list.
     *
     * @param x the value to insert
     * @throws IllegalArgumentException if {@code x} is {@code null}
     */
    public void addFirst(T x) {
        validateIfArgumentIsNull(x);
        if (head == null) {
            createFirstNode(x);
        } else {
            head.setPrev(new Node<>(x, null, head));
            head = head.getPrev();
        }
        size++;
    }

    /**
     * Appends a value to the end of the list.
     *
     * @param x the value to append
     * @throws IllegalArgumentException if {@code x} is {@code null}
     */
    public void addLast(T x) {
        validateIfArgumentIsNull(x);
        if (head == null) {
            createFirstNode(x);
        } else {
            tail.setNext(new Node<>(x, tail, null));
            tail = tail.getNext();
        }
        size++;
    }

    /**
     * Returns the first value in the list.
     *
     * @return the first stored value
     * @throws NoSuchElementException if the list is empty
     */
    public T getFirst() {
        validateIfListIsEmpty();
        return head.getValue();
    }

    /**
     * Returns the last value in the list.
     *
     * @return the last stored value
     * @throws NoSuchElementException if the list is empty
     */
    public T getLast() {
        validateIfListIsEmpty();
        return tail.getValue();
    }

    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Throws an exception when the list has no elements.
     *
     * @throws NoSuchElementException if the list is empty
     */
    private void validateIfListIsEmpty() {
        if (head == null) {
            throw new NoSuchElementException();
        }
    }

    /**
     * Throws an exception when a caller supplies a {@code null} value.
     *
     * @param x the value to validate
     * @throws IllegalArgumentException if {@code x} is {@code null}
     */
    private void validateIfArgumentIsNull(T x) {
        if (x == null) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Throws an exception when an index does not identify an existing element.
     *
     * @param index the index to validate
     * @throws IndexOutOfBoundsException if {@code index} is outside the current
     *                                   element range
     */
    private void validateIfIndexExists(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    /**
     * Creates the first node in an empty list.
     *
     * @param x the value to store in the first node
     */
    private void createFirstNode(T x) {
        head = new Node<>(x);
        tail = head;
    }

    public int indexOf(Object o) {
        if (head == null) {
            return -1;
        }

        int index = 0;
        Node<T> node = head;
        do {
            if (node.getValue().equals(o)) {
                return index;
            }
            node = node.getNext();
            index++;
        } while (node != head);

        return -1;
    }

    public int lastIndexOf(Object o) {
        if (tail == null) {
            return -1;
        }

        int index = size - 1;
        Node<T> node = tail;
        do {
            if (node.getValue().equals(o)) {
                return index;
            }
            node = node.getPrev();
            index--;
        } while (node != tail);

        return -1;
    }

    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Returns the list contents in bracketed, comma-separated form.
     *
     * @return a string representation of the list contents
     */
    @Override
    public String toString() {
        if (head == null) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        Node<T> node;
        for (node = head; node.getNext() != head; node = node.getNext()) {
            sb.append(node.getValue().toString()).append(", ");
        }
        sb.append(node.getValue().toString()).append("]");
        return sb.toString();
    }
}
