package linkedlist;

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

    public boolean contains(Object o) {
        return indexOf(o) >= 0;
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
