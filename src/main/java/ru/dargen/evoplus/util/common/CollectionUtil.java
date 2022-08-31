package ru.dargen.evoplus.util.common;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.*;

@UtilityClass
public class CollectionUtil {

    public List<Integer> intRange(int min, int max, int step) {
        val list = new LinkedList<Integer>();
        for (int i = min; i <= max; i += step) list.add(i);
        return list;
    }

}
