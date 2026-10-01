package linkedlist;

public class Node<T> {
    private T value;
    private Node<T> prev;
    private Node<T> next;

    public Node() {
        this.value = null;
        this.prev = null;
        this.next = null;
    }

    public Node(T value) {
        this.value = value;
        this.prev = null;
        this.next = null;
    }

    public Node(T value, Node<T> next) {
        this.value = value;
        this.prev = null;
        this.next = prev;
    }

    public Node(T value, Node<T> prev, Node<T> next) {
        this.value = value;
        this.prev = prev;
        this.next = next;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public void set(boolean setNext, Node<T> node) {
        if (setNext) {
            next = node;
        } else {
            prev = node;
        }
    }
}
