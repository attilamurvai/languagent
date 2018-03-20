package hu.athace.business;

import hu.athace.model.Book;
import hu.athace.model.Dictionary;
import hu.athace.model.Sentence;
import hu.athace.model.Word;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TextProcessorImpl implements TextProcessor {
    private static final String DELIMITER = " ";

    public Book parseSubtitle(String subtitlePath) throws IOException {
        Book book = new Book(subtitlePath);

        try (Stream<String> stream = Files.lines(Paths.get(subtitlePath), Charset.forName(Constants.CHARSET))) {
            String text = stream.filter(line ->
                    !(line.matches("")
                            || line.matches("\\d+")
                            || line.matches("\\d{2}:\\d{2}:\\d{2},\\d{3} --> \\d{2}:\\d{2}:\\d{2},\\d{3}")))
                    .collect(Collectors.joining(" "));

            // normalize, mainly to remove space duplicates
            StringUtils.normalizeSpace(text);

            // iterate without streams, because no breakiterator supported in streams
            BreakIterator breakIterator = BreakIterator.getSentenceInstance();
            breakIterator.setText(text);

            int first = breakIterator.first();
            int last = breakIterator.next();
            String sentenceValue;
            while (last != BreakIterator.DONE) {
                sentenceValue = text.substring(first, last);
                book.getSentences().add(new Sentence(sentenceValue));
                first = last;
                last = breakIterator.next();
            }
        }

        // go through the "book"
        book.getSentences()
//                .map(sentence -> sentence.value)
                .forEach(sentence -> {
                    String sentenceValue = sentence.getValue();
                    for (String wordValue : sentenceValue.split(" ")) {

                        // todo revise these replaces
                        wordValue = wordValue.replaceAll("<.+>", "");
                        wordValue = wordValue.replaceAll("<.+", "");
                        wordValue = wordValue.replaceAll(".+>", "");
//            wordValue = wordValue.replaceAll("[^A-za-z']", "");
                        wordValue = wordValue.replaceAll("[^A-za-z]+$", "");
                        if (!wordValue.isEmpty()) {

                            // map the words to lowercase keys to be able to merge words with upper and lower cases
                            String lowerCaseWordValue = wordValue.toLowerCase();
                            Word word = book.getWordMap().get(lowerCaseWordValue);
                            if (word == null) {
                                word = new Word(wordValue);
                                book.getWordMap().put(lowerCaseWordValue, word);
                            } else {
                                // if the word already exists with an uppercase value and we find the same word with lowercase, set it to lowercase
                                // (the existing values are most probably coming from the beginning of sentences)
                                String existingWordValue = word.getValue();
                                if (!existingWordValue.equals(existingWordValue.toLowerCase()) && wordValue.equals(existingWordValue.toLowerCase())) {
                                    word.setValue(wordValue);
                                }
                            }
                            // add the new sentence to the word
                            word.getSentences().add(sentence);
                        }
                    }
                });

        return book;
    }

    public Dictionary readDictionary() throws IOException {
        Dictionary dictionary = new Dictionary();
        //        Path path = Paths.get(filePath);
//        Stream<String> stream = Files.lines(path);

//        Stream<String> stream = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(wordsFileName))).lines();
//        Map<String, String> globalWordCount = stream
//                .filter(s -> s.matches("^\\w+ \\w+"))
//                .collect(Collectors.toMap(k -> k.split(" ")[0], v -> v.split(" ")[1]));

        // init global word count
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(Constants.WORDS_FILE)))) {
            dictionary.globalWordCount = bufferedReader.lines().filter(line -> line.contains(DELIMITER))
                    .collect(Collectors.toMap(k -> k.split(DELIMITER)[0], v -> Long.parseLong(v.split(DELIMITER)[1])));

        }

        // init global work rank from global word count
        List<String> list = dictionary.globalWordCount.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        dictionary.globalWordRank = IntStream.range(0, list.size())
                .boxed()
                .collect(Collectors.toMap(list::get, i -> i));

        return dictionary;
    }
}
