package linkedlist;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

public class CustomLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    /**
     * Creates an empty linked list.
     */
    public CustomLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Creates a linked list containing the values from a collection in iteration
     * order.
     *
     * @param c the collection whose values should be copied into the list
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public CustomLinkedList(Collection<? extends T> c) {
        head = null;
        tail = null;
        size = 0;
        addAll(c);
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
     * Returns whether the list contains a value equal to the specified object.
     *
     * @param o the object to search for
     * @return {@code true} if the object is present; otherwise {@code false}
     */
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Creates the first node in an empty list and links it to itself.
     *
     * @param x the value to store in the first node
     */
    private void createFirstNode(T x) {
        head = new Node<>(x);
        tail = head;
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

    /**
     * Removes and returns the first value in the list.
     *
     * @return the removed first value
     * @throws NoSuchElementException if the list is empty
     */
    public T removeFirst() {
        validateIfListIsEmpty();
        return unlinkNode(head);
    }

    /**
     * Removes and returns the last value in the list.
     *
     * @return the removed last value
     * @throws NoSuchElementException if the list is empty
     */
    public T removeLast() {
        validateIfListIsEmpty();
        return unlinkNode(tail);
    }

    /**
     * Returns the number of values in the list.
     *
     * @return the list size
     */
    public int size() {
        return size;
    }

    /**
     * Returns the value at a specified index.
     *
     * @param index the index of the value to return
     * @return the value at {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is outside the list
     */
    public T get(int index) {
        validateIfIndexExists(index);
        return getNode(index).getValue();
    }

    /**
     * Replaces the first value in the list.
     *
     * @param x the replacement value
     * @return the value formerly stored at the front
     * @throws IllegalArgumentException if {@code x} is {@code null}
     * @throws NoSuchElementException   if the list is empty
     */
    public T setFirst(T x) {
        validateIfArgumentIsNull(x);
        validateIfListIsEmpty();
        T oldValue = head.getValue();
        head.setValue(x);
        return oldValue;
    }

    /**
     * Replaces the last value in the list.
     *
     * @param x the replacement value
     * @return the value formerly stored at the end
     * @throws IllegalArgumentException if {@code x} is {@code null}
     * @throws NoSuchElementException   if the list is empty
     */
    public T setLast(T x) {
        validateIfArgumentIsNull(x);
        validateIfListIsEmpty();
        T oldValue = tail.getValue();
        tail.setValue(x);
        return oldValue;
    }

    /**
     * Replaces the value at a specified index.
     *
     * @param index the index of the value to replace
     * @param x     the replacement value
     * @return the value formerly stored at {@code index}
     * @throws IllegalArgumentException  if {@code x} is {@code null}
     * @throws IndexOutOfBoundsException if {@code index} is outside the list
     */
    public T set(int index, T x) {
        validateIfArgumentIsNull(x);
        validateIfIndexExists(index);
        Node<T> node = getNode(index);
        T oldValue = node.getValue();
        node.setValue(x);
        return oldValue;
    }

    /**
     * Appends a value to the end of the list.
     *
     * @param x the value to append
     * @return {@code true} after the value is inserted
     * @throws IllegalArgumentException if {@code x} is {@code null}
     */
    public boolean add(T x) {
        addLast(x);
        return true;
    }

    /**
     * Inserts a value at a specified index.
     *
     * @param index the insertion index
     * @param x     the value to insert
     * @throws IllegalArgumentException  if {@code x} is {@code null}
     * @throws IndexOutOfBoundsException if {@code index} is outside the range
     *                                   from {@code 0} through the list size
     */
    public void add(int index, T x) {
        validateIfArgumentIsNull(x);
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (index == 0) {
            addFirst(x);
            return;
        }
        if (index == size) {
            addLast(x);
            return;
        }
        Node<T> node = getNode(index);
        Node<T> prevNode = node.getPrev();
        Node<T> newNode = new Node<>(x, prevNode, node);
        prevNode.setNext(newNode);
        node.setPrev(newNode);
        size++;
    }

    /**
     * Removes and returns the value at a specified index.
     *
     * @param index the index of the value to remove
     * @return the removed value
     * @throws IndexOutOfBoundsException if {@code index} is outside the list
     */
    public T remove(int index) {
        validateIfIndexExists(index);
        return unlinkNode(getNode(index));
    }

    /**
     * Appends all values from a collection to the list.
     *
     * @param c the collection whose values should be appended
     * @return {@code true} if the list changed; otherwise {@code false}
     */
    public boolean addAll(Collection<? extends T> c) {
        return addAll(size, c);
    }

    /**
     * Inserts all values from a collection at a specified index.
     *
     * @param index the insertion index
     * @param c     the collection whose values should be inserted
     * @return {@code true} if the list changed; otherwise {@code false}
     * @throws IndexOutOfBoundsException if {@code index} is outside the range
     *                                   from {@code 0} through the list size
     */
    @SuppressWarnings("unchecked")

    public boolean addAll(int index, Collection<? extends T> c) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        Object[] arr = c.toArray();
        if (arr.length == 0) {
            return false;
        }
        if (index == size) {
            for (Object obj : arr) {
                addLast((T) obj);
            }
        } else {
            int counter = index;
            for (Object obj : arr) {
                add(counter, (T) obj);
                counter++;
            }
        }
        return true;
    }

    /**
     * Removes all values from the list.
     */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Removes a specific node from the list and returns its value.
     *
     * @param node the node to unlink
     * @return the value stored in the removed node
     */
    private T unlinkNode(Node<T> node) {
        T oldValue = node.getValue();
        if (size == 1) {
            clear();
            return oldValue;
        }

        Node<T> prevNode = node.getPrev();
        Node<T> nextNode = node.getNext();
        if (node == head) {
            head = nextNode;
            node.setNext(null);
            head.setPrev(null);
        } else if (node == tail) {
            tail = prevNode;
            node.setPrev(null);
            tail.setNext(null);
        } else {
            prevNode.setNext(nextNode);
            nextNode.setPrev(prevNode);
        }
        size--;
        return oldValue;
    }

    /**
     * Returns the node stored at a valid index.
     *
     * @param index the index to resolve
     * @return the node at {@code index}
     */
    private Node<T> getNode(int index) {
        int distanceFromTail = size - index - 1;
        Node<T> node;
        int i;
        if (distanceFromTail < index) {
            node = tail;
            for (i = 0; i < distanceFromTail; i++) {
                node = node.getPrev();
            }
        } else {
            node = head;
            for (i = 0; i < index; i++) {
                node = node.getNext();
            }
        }
        return node;
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
     * Returns the index of the first matching value.
     *
     * @param o the object to search for
     * @return the first matching index, or {@code -1} if no match exists
     */
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

    /**
     * Returns the index of the last matching value.
     *
     * @param o the object to search for
     * @return the last matching index, or {@code -1} if no match exists
     */
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

    /**
     * Returns whether the list contains no values.
     *
     * @return {@code true} if the list is empty; otherwise {@code false}
     */
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Inserts a value at the front of the list.
     *
     * @param x the value to insert
     * @return {@code true} after the value is inserted
     * @throws IllegalArgumentException if {@code x} is {@code null}
     */
    public boolean offerFirst(T x) {
        addFirst(x);
        return true;
    }

    /**
     * Appends a value to the end of the list.
     *
     * @param x the value to append
     * @return {@code true} after the value is inserted
     * @throws IllegalArgumentException if {@code x} is {@code null}
     */
    public boolean offerLast(T x) {
        return add(x);
    }

    /**
     * Removes and returns the first value, or returns {@code null} when the list
     * is empty.
     *
     * @return the removed first value, or {@code null} if the list is empty
     */
    public T pollFirst() {
        return head == null ? null : removeFirst();
    }

    /**
     * Removes and returns the last value, or returns {@code null} when the list
     * is empty.
     *
     * @return the removed last value, or {@code null} if the list is empty
     */
    public T pollLast() {
        return tail == null ? null : removeLast();
    }

    /**
     * Returns the first value without removing it, or {@code null} when the list
     * is empty.
     *
     * @return the first value, or {@code null} if the list is empty
     */
    public T peekFirst() {
        return head == null ? null : getFirst();
    }

    /**
     * Returns the last value without removing it, or {@code null} when the list
     * is empty.
     *
     * @return the last value, or {@code null} if the list is empty
     */
    public T peekLast() {
        return tail == null ? null : getLast();
    }

    /**
     * Removes the first occurrence of a matching value.
     *
     * @param x the value to remove
     * @return {@code true} if a matching value was removed; otherwise
     *         {@code false}
     */
    public boolean removeFirstOccurrence(T x) {
        return remove(x);
    }

    /**
     * Removes the last occurrence of a matching value.
     *
     * @param x the value to remove
     * @return {@code true} if a matching value was removed; otherwise
     *         {@code false}
     */
    public boolean removeLastOccurrence(T x) {
        if (tail == null) {
            return false;
        }

        Node<T> node = tail;
        do {
            if (node.getValue().equals(x)) {
                unlinkNode(node);
                return true;
            }
            node = node.getPrev();
        } while (node != tail);

        return false;
    }

    /**
     * Appends a value to the end of the list.
     *
     * @param x the value to append
     * @return {@code true} after the value is inserted
     * @throws IllegalArgumentException if {@code x} is {@code null}
     */
    public boolean offer(T x) {
        return add(x);
    }

    /**
     * Removes and returns the first value in the list.
     *
     * @return the removed first value
     * @throws NoSuchElementException if the list is empty
     */
    public T remove() {
        return removeFirst();
    }

    /**
     * Removes and returns the first value, or returns {@code null} when the list
     * is empty.
     *
     * @return the removed first value, or {@code null} if the list is empty
     */
    public T poll() {
        return head == null ? null : removeFirst();
    }

    /**
     * Returns the first value without removing it.
     *
     * @return the first value
     * @throws NoSuchElementException if the list is empty
     */
    public T element() {
        return getFirst();
    }

    /**
     * Returns the first value without removing it, or {@code null} when the list
     * is empty.
     *
     * @return the first value, or {@code null} if the list is empty
     */
    public T peek() {
        return head == null ? null : getFirst();
    }

    /**
     * Pushes a value onto the front of the list for stack-style use.
     *
     * @param x the value to push
     * @throws IllegalArgumentException if {@code x} is {@code null}
     */
    public void push(T x) {
        addFirst(x);
    }

    /**
     * Pops and returns the first value in the list.
     *
     * @return the removed first value
     * @throws NoSuchElementException if the list is empty
     */
    public T pop() {
        return removeFirst();
    }

    /**
     * Removes the first occurrence of a matching value.
     *
     * @param o the object to remove
     * @return {@code true} if a matching value was removed; otherwise {@code false}
     */
    public boolean remove(Object o) {
        if (head == null) {
            return false;
        }

        Node<T> node = head;
        do {
            if (node.getValue().equals(o)) {
                unlinkNode(node);
                return true;
            }
            node = node.getNext();
        } while (node != head);

        return false;
    }

    /**
     * Returns the list contents as an array.
     *
     * @return an array containing the list values in order
     */
    public Object[] toArray() {
        Object[] arr = new Object[size];
        if (head == null) {
            return arr;
        }

        int index = 0;
        Node<T> node = head;
        do {
            arr[index++] = node.getValue();
            node = node.getNext();
        } while (node != head);

        return arr;
    }

    /**
     * Returns the list contents in an array of the requested type.
     *
     * @param a   the array into which values should be stored
     * @param <E> the array element type
     * @return an array containing the list values in order
     */
    @SuppressWarnings("unchecked")

    public <E> E[] toArray(E[] a) {
        Object[] arr = toArray();
        if (a.length < size) {
            return (E[]) Arrays.copyOf(arr, size, a.getClass());
        }
        System.arraycopy(arr, 0, a, 0, size);
        if (a.length > size()) {
            a[size] = null;
        }
        return a;
    }

    /**
     * Returns whether the list contains every value in a collection.
     *
     * @param c the collection of values to find
     * @return {@code true} if every value is present; otherwise {@code false}
     */
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes every value that is also contained in a collection.
     *
     * @param c the collection of values to remove
     * @return {@code true} if the list changed; otherwise {@code false}
     */
    public boolean removeAll(Collection<?> c) {
        if (head == null || c.isEmpty()) {
            return false;
        }

        boolean modified = false;
        int originalSize = size;
        Node<T> node = head;
        for (int i = 0; i < originalSize && size > 0; i++) {
            Node<T> nextNode = node.getNext();
            if (c.contains(node.getValue())) {
                unlinkNode(node);
                modified = true;
            }
            node = nextNode;
        }
        return modified;
    }

    /**
     * Removes every value that is not contained in a collection.
     *
     * @param c the collection of values to retain
     * @return {@code true} if the list changed; otherwise {@code false}
     */
    public boolean retainAll(Collection<?> c) {
        if (head == null) {
            return false;
        }

        boolean modified = false;
        int originalSize = size;
        Node<T> node = head;
        for (int i = 0; i < originalSize && size > 0; i++) {
            Node<T> nextNode = node.getNext();
            if (!c.contains(node.getValue())) {
                unlinkNode(node);
                modified = true;
            }
            node = nextNode;
        }
        return modified;
    }

    /**
     * Compares this list with another object for equal size and element order.
     *
     * @param o the object to compare with this list
     * @return {@code true} if the objects contain equal values in the same order;
     *         otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        var other = (CustomLinkedList<?>) o;
        if (size != other.size()) {
            return false;
        }
        return Arrays.equals(this.toArray(), other.toArray());
    }

    /**
     * Returns a hash code based on the values in this list and their order.
     *
     * @return the hash code for this list
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(toArray());
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