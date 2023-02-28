package com.wangjg.guavacase.map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Integer> nameToId = Maps.newHashMap();
        Map<Integer, String> idToName = Maps.newHashMap();

        nameToId.put("Bob", 42);
        idToName.put(42, "Bob");
        // what happens if "Bob" or 42 are already present?
        // weird bugs can arise if we forget to keep these in sync...



    }
}
