package linkedlist;

public class Node<T> {
    private T value;
    private Node<T> prev;
    private Node<T> next;

    /**
     * Creates an empty node with no links.
     */
    public Node() {
        this.value = null;
        this.prev = null;
        this.next = null;
    }

    /**
     * Creates a node containing a value with no links.
     *
     * @param value the value to store
     */
    public Node(T value) {
        this.value = value;
        this.prev = null;
        this.next = null;
    }

    /**
     * Creates a node containing a value and a next-node link.
     *
     * @param value the value to store
     * @param next  the next node in the list
     */
    public Node(T value, Node<T> next) {
        this.value = value;
        this.prev = null;
        this.next = prev;
    }

    /**
     * Creates a node containing a value and links to adjacent nodes.
     *
     * @param value the value to store
     * @param prev  the previous node in the list
     * @param next  the next node in the list
     */
    public Node(T value, Node<T> prev, Node<T> next) {
        this.value = value;
        this.prev = prev;
        this.next = next;
    }

    /**
     * Returns the value stored in this node.
     *
     * @return the stored value
     */
    public T getValue() {
        return value;
    }

    /**
     * Replaces the value stored in this node.
     *
     * @param value the new value
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Returns the previous node link.
     *
     * @return the previous node, or {@code null} if none is linked
     */
    public Node<T> getPrev() {
        return prev;
    }

    /**
     * Returns the next node link.
     *
     * @return the next node, or {@code null} if none is linked
     */
    public Node<T> getNext() {
        return next;
    }

    /**
     * Replaces the previous node link.
     *
     * @param prev the new previous node
     */
    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    /**
     * Replaces the next node link.
     *
     * @param next the new next node
     */
    public void setNext(Node<T> next) {
        this.next = next;
    }

    /**
     * Returns the string representation of the stored value.
     *
     * @return the stored value converted to a string
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
