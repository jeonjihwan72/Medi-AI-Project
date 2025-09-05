package com.example.medi_ai.model;

import java.util.List;

public class Disease {
    public String name;
    public List<String> symptoms;
    public int priority;

    public Disease(String name, List<String> symptoms, int priority) {
        this.name = name;
        this.symptoms = symptoms;
        this.priority = priority;
    }
}