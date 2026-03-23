package dev.mzhao.connect4;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

class TestSets {

    static final Map<String, String> TEST_SETS;

    static {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("End-Easy", "Test_L3_R1");
        map.put("Middle-Easy", "Test_L2_R1");
        map.put("Middle-Medium", "Test_L2_R2");
        map.put("Begin-Easy", "Test_L1_R1");
        map.put("Begin-Medium", "Test_L1_R2");
        map.put("Begin-Hard", "Test_L1_R3");
        TEST_SETS = Collections.unmodifiableMap(map);
    }
}
