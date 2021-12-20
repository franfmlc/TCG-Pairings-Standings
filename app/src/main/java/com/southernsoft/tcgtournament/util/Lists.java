package com.southernsoft.tcgtournament.util;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Just a helper class to instantiate List with a single line.
 * Similar to Guava Lists.newArrayList(E... elements).
 */

public class Lists {

    public static <E> ArrayList<E> newArrayList(E... elements) {
        if (elements == null)
            return null;
        ArrayList<E> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }
}