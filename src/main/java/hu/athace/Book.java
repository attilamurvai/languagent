package hu.athace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Book {
    private final String name;
    private final List<Sentence> sentences = new ArrayList<>();
    private final Map<String, Word> wordMap = new HashMap<>();

    public Book(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public Map<String, Word> getWordMap() {
        return wordMap;
    }
}
