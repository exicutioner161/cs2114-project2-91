import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import characters.RandomNamePicker;

class RandomNamePickerTest {
    @Test
    void picksPopulatedOpponentNames() {
        RandomNamePicker picker = new RandomNamePicker();
        String name = picker.pickName();
        assertNotNull(name);
        assertFalse(name.isBlank());
    }
}
