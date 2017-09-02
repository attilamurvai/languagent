package hu.athace;

import java.util.ArrayList;
import java.util.List;

public class Sentence {
    String value;
    List<Word> words = new ArrayList<>();

    public Sentence(String value) {
        this.value = value;
    }
}
