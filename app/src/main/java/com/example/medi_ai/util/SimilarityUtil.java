package com.example.medi_ai.util;

import java.util.*;

public class SimilarityUtil {
    public static double calculateJaccard(List<String> input, List<String> disease) {
        Set<String> set1 = new HashSet<>(input);
        Set<String> set2 = new HashSet<>(disease);

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }
}