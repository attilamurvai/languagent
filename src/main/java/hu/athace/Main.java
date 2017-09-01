package hu.athace;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main extends Application {
    public static final String WORDS_FILE = "google-books-common-words.txt";
    public static final String DELIMITER = " ";
    //    public static final String CHARSET = "UTF-8";
    public static final String CHARSET = "ISO-8859-2";

    private TableView tableView;
    private Map<String, Long> globalWordCount;
    private Map<String, Integer> globalWordRank;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
        initDictionary();
    }

    private void initStage(Stage primaryStage) {
        primaryStage.setTitle("Subtitlearn");

        StackPane root = new StackPane();

        root.getChildren().add(getTable());

        Scene scene = new Scene(root, 600, 600);

        scene.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });

        scene.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                String filePath;
                for (File file : db.getFiles()) {
                    filePath = file.getAbsolutePath();
                    try {
                        String ext = filePath.substring(filePath.lastIndexOf('.') + 1);
                        if (ext.toLowerCase().equals("srt")) {
                            Map<Word, Integer> wordIntegerMap = parseSubtitle(filePath);
                            updateView(wordIntegerMap);
                        } else {
                            System.err.println("Currently only SRT extension is supported!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(filePath);
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initDictionary() {
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

    private Map<Word, Integer> parseSubtitle(String subtitlePath) throws IOException {
        Map<Word, Integer> localWordCount = new HashMap<>();

        try (Stream<String> stream = Files.lines(Paths.get(subtitlePath), Charset.forName(CHARSET))) {
            stream.forEach(line -> {
                for (String wordValue : line.split(" ")) {

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

                        Word word = new Word(wordValue);
                        int count = localWordCount.containsKey(word) ? localWordCount.get(word) : 0;
                        localWordCount.put(word, count + 1);

                    }
                }
            });
        }

        return localWordCount;
    }

    private void updateView(Map<Word, Integer> localWordCount) {
        getTable().getItems().clear();

        for (Word word : localWordCount.keySet()) {
            String wordValue = word.value;
            System.out.println(wordValue);
            getTable().getItems().add(new RowItem(wordValue, globalWordCount.get(wordValue.toLowerCase()), globalWordRank.get(wordValue.toLowerCase()), localWordCount.get(word)));
        }

        TableColumnResizeHelper.autoFitTable(getTable());
    }


    public TableView getTable() {
        if (tableView == null) {
            tableView = new TableView();

            TableColumn column1 = new TableColumn("Word");
            column1.setCellValueFactory(
                    new PropertyValueFactory<RowItem, String>("word"));
            tableView.getColumns().add(column1);

            TableColumn column2 = new TableColumn("Global count");
            column2.setCellValueFactory(
                    new PropertyValueFactory<RowItem, Long>("globalCount"));
            tableView.getColumns().add(column2);

            TableColumn column3 = new TableColumn("Global rank");
            column3.setCellValueFactory(
                    new PropertyValueFactory<RowItem, Long>("globalRank"));
            tableView.getColumns().add(column3);

            TableColumn column4 = new TableColumn("Local count");
            column4.setCellValueFactory(
                    new PropertyValueFactory<RowItem, Integer>("localCount"));
            tableView.getColumns().add(column4);
        }
        return tableView;
    }

}

