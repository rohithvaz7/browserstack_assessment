package org.example;

import java.util.*;

public class WordAnalyzer {
    public static void analyzeWordFrequency(List<String> translatedTitles) {
        Map<String, Integer> wordCount = new HashMap<>();

        for (String title : translatedTitles) {
            String[] words = title.toLowerCase().split("\\W+");
            for (String word : words) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        System.out.println("\n🔹 Repeated Words:");
        wordCount.forEach((word, count) -> {
            if (count > 2) {
                System.out.println(word + ": " + count);
            }
        });
    }
}

