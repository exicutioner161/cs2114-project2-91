
import characters;
package characters;

import junit.framework.TestCase;

public class ItemTest extends TestCase {

    private Item item;

    public void setUp() {
        item = new Item("Sword", 10.0, 20.0, 30.0, 100);
    }

    public void testConstructor() {
        assertEquals("Sword", item.getName());
        assertEquals(10.0, item.getAggro(), 0.001);
        assertEquals(20.0, item.getControl(), 0.001);
        assertEquals(30.0, item.getMidrange(), 0.001);
        assertEquals(100, item.getPrice());
    }

    public void testGetName() {
        assertEquals("Sword", item.getName());
    }

    public void testSetName() {
        item.setName("Shield");
        assertEquals("Shield", item.getName());
    }

    public void testGetAggro() {
        assertEquals(10.0, item.getAggro(), 0.001);
    }

    public void testSetAggro() {
        item.setAggro(15.0);
        assertEquals(15.0, item.getAggro(), 0.001);
    }

    public void testGetControl() {
        assertEquals(20.0, item.getControl(), 0.001);
    }

    public void testSetControl() {
        item.setControl(25.0);
        assertEquals(25.0, item.getControl(), 0.001);
    }

    public void testGetMidrange() {
        assertEquals(30.0, item.getMidrange(), 0.001);
    }

    public void testSetMidrange() {
        item.setMidrange(35.0);
        assertEquals(35.0, item.getMidrange(), 0.001);
    }

    public void testGetPrice() {
        assertEquals(100, item.getPrice());
    }

    public void testSetPrice() {
        item.setPrice(200);
        assertEquals(200, item.getPrice());
    }
}
