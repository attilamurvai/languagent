package hu.athace;

import java.util.ArrayList;
import java.util.List;

public class Book {
    String name;
    List<Sentence> sentences = new ArrayList<>();

    public Book(String name) {
        this.name = name;
    }
}
