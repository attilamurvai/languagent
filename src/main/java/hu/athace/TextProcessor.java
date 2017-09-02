package hu.athace;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static hu.athace.SubtitlearnApp.CHARSET;
import static hu.athace.SubtitlearnApp.WORDS_FILE;

public class TextProcessor {
    public static final String DELIMITER = " ";

    public Map<String, Long> globalWordCount;
    public Map<String, Integer> globalWordRank;


    public Map<Word, Integer> parseSubtitle(String subtitlePath) throws IOException {
        Book book = new Book(subtitlePath);

        Map<Word, Integer> localWordCount = new HashMap<>();

        try (Stream<String> stream = Files.lines(Paths.get(subtitlePath), Charset.forName(CHARSET))) {
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
                book.sentences.add(new Sentence(sentenceValue));
                first = last;
                last = breakIterator.next();
            }
        }

        // go through the "book"
        book.sentences.stream()
//                .map(sentence -> sentence.value)
                .forEach(sentence -> {
                    String sentenceValue = sentence.value;
                    for (String wordValue : sentenceValue.split(" ")) {

                        wordValue = wordValue.replaceAll("<.+>", "");
                        wordValue = wordValue.replaceAll("<.+", "");
                        wordValue = wordValue.replaceAll(".+>", "");
//            wordValue = wordValue.replaceAll("[^A-za-z']", "");
                        wordValue = wordValue.replaceAll("[^A-za-z]+$", "");
                        if (!wordValue.isEmpty()) {
//                    String dictForm = wordValue;
//                    if (Character.isUpperCase(wordValue.charAt(0)) && (wordValue.length() == 1 || !Character.isUpperCase(wordValue.charAt(1)))) {
//                        dictForm = wordValue.toLowerCase();
//                    }
                            Word word = new Word(wordValue, sentence);

                            int count = localWordCount.containsKey(word) ? localWordCount.get(word) : 0;
                            localWordCount.put(word, count + 1);

                        }
                    }
                });

        return localWordCount;
    }

    public void initDictionary() {
        //        Path path = Paths.get(filePath);
//        Stream<String> stream = Files.lines(path);

//        Stream<String> stream = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(wordsFileName))).lines();
//        Map<String, String> globalWordCount = stream
//                .filter(s -> s.matches("^\\w+ \\w+"))
//                .collect(Collectors.toMap(k -> k.split(" ")[0], v -> v.split(" ")[1]));

        // init global word count
        globalWordCount = new HashMap<>();
        try (Stream<String> lines = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(WORDS_FILE))).lines()) {
            lines.filter(line -> line.contains(DELIMITER)).forEach(
                    line -> globalWordCount.putIfAbsent(line.split(DELIMITER)[0], Long.parseLong(line.split(DELIMITER)[1]))
            );
        }

        // init global work rank from global word count
        List<String> list = globalWordCount.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        globalWordRank = IntStream.range(0, list.size())
                .boxed()
                .collect(Collectors.toMap(i -> list.get(i), i -> i));

    }
}
