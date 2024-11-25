package com.example.beachplease;

import static org.junit.Assert.assertEquals;
import org.junit.Test;


import java.util.*;

public class TagTiesTest {

    @Test
    public void testTagBreakers() {
        Map<String, Integer> tagsMap = new HashMap<>();
        tagsMap.put("Surfing", 5);
        tagsMap.put("Shaded Areas", 5);
        tagsMap.put("Restrooms Available", 3);
        tagsMap.put("Bonfire-Friendly", 2);

        Set<String> expectedTopTags = new HashSet<>(Arrays.asList("Shaded Areas", "Surfing"));

        List<Map.Entry<String, Integer>> sortedTags = new ArrayList<>(tagsMap.entrySet());
        sortedTags.sort((entry1, entry2) -> {
            int freqComparison = entry2.getValue().compareTo(entry1.getValue());
            return freqComparison != 0 ? freqComparison : entry1.getKey().compareTo(entry2.getKey());
        });

        Set<String> topTags = new HashSet<>();
        for (int i = 0; i < Math.min(2, sortedTags.size()); i++) {
            topTags.add(sortedTags.get(i).getKey());
        }

        assertEquals(expectedTopTags, topTags);
    }
}
