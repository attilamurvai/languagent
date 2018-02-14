package hu.athace.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity class for the words. Current implementation is case insensitive.
 */
public class Word {
    private String value;
    private final List<Sentence> sentences = new ArrayList<>();

    public Word(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // self check
        if (this == obj)
            return true;
        // null check
        if (obj == null)
            return false;
        // type check and cast
        if (getClass() != obj.getClass())
            return false;
        Word word = (Word) obj;
        // field comparison
        return Objects.equals(value, word.value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

}
