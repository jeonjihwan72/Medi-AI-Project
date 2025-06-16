package com.example.medi_ai.util;

public class PriorityScorer {
    public static double applyPriority(double similarity, int priority) {
        double weight = switch (priority) {
            case 1 -> 1.0;
            case 2 -> 0.9;
            case 3 -> 0.8;
            default -> 0.5;
        };
        return similarity * weight;
    }
}