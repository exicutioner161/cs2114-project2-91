import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import linkedlist.CustomLinkedList;
import linkedlist.Node;

class LinkedListTest {
    @Test
    void formatsEmptyAndNonemptyLists() {
        CustomLinkedList<String> values = new CustomLinkedList<>();
        assertEquals("[]", values.toString());
        values.add("first");
        values.add("second");
        assertEquals("[first, second]", values.toString());
    }

    @Test
    void defaultNodeStringIsNullSafe() {
        Node<String> node = new Node<>();
        assertNull(node.getValue());
        assertEquals("null", node.toString());
    }
}
