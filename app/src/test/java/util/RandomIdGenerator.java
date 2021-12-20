package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomIdGenerator {
    private final int MAX_IDS = 20;
    private List<Integer> randomIds;

    public RandomIdGenerator() {
        randomIds = new ArrayList<>();

        for (int i = 2; i <= MAX_IDS; i++)
            randomIds.add(i);
    }

    public int getRandomId() {
        Collections.shuffle(randomIds);
        return randomIds.remove(0);
    }
}
