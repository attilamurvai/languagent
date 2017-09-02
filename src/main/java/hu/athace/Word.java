package hu.athace;

import java.util.Objects;

/**
 * Entity class for the words. Current implementation is case insensitive.
 */
public class Word {
    final String value;
    Sentence sentence;
    String fileName;

    public Word(String value) {
        this.value = value;
    }

    public Word(String value, Sentence sentence) {
        this(value);
        this.sentence = sentence;
    }

    @Override
    public int hashCode() {
        return value.toLowerCase().hashCode();
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
        return Objects.equals(value.toLowerCase(), word.value.toLowerCase());
    }
}
