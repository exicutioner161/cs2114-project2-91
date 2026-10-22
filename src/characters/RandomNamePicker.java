package characters;

import java.util.ArrayList;
import java.util.Random;

/** Supplies randomly selected names from the opponent name pool. */
public class RandomNamePicker {
    private static final Random random = new Random();

    private ArrayList<String> names;

    /** Creates an empty picker whose names are loaded on first use. */
    public RandomNamePicker() {
        names = new ArrayList<>();
    }

    /** Loads the fixed pool of opponent names. */
    private void fillNameList() {
        names.add("Ash");
        names.add("Blaze");
        names.add("Cinder");
        names.add("Dusk");
        names.add("Ember");
        names.add("Frost");
        names.add("Iris");
        names.add("Jade");
        names.add("Kestrel");
        names.add("Nova");
        names.add("Onyx");
        names.add("Quill");
        names.add("Rook");
        names.add("Sable");
        names.add("Vale");
    }

    /** @return a randomly selected opponent name */
    public String pickName() {
        if (names.isEmpty()) {
            fillNameList();
        }
        return names.get(random.nextInt(names.size()));
    }
}
