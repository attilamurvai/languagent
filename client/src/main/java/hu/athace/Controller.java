package hu.athace;

import hu.athace.model.Dictionary;
import hu.athace.business.TextProcessor;
import hu.athace.model.Book;
import hu.athace.model.Word;
import hu.athace.view.TableColumnResizeHelper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

class Controller {
    private final TextProcessor textProcessor;

    private final Stage stage;
    private TableView<Word> tableView;
    private Dictionary dictionary;

    public Controller(Stage primaryStage, TextProcessor textProcessor) {
        stage = primaryStage;
        this.textProcessor = textProcessor;
    }

    public void init() throws IOException {
        // todo: think about the ideal way and responsibilites of initializing this class
        dictionary = textProcessor.readDictionary();
        initStage(stage);
    }

    private void initStage(Stage primaryStage) {
        primaryStage.setTitle("LanguAgent");

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
                    System.out.println("Processing file at path: " + filePath);
                    try {
                        String ext = filePath.substring(filePath.lastIndexOf('.') + 1);
                        if (ext.toLowerCase().equals("srt")) {
                            Book book = textProcessor.parseSubtitle(filePath);
                            updateView(book);
                        } else {
                            System.err.println("Currently only SRT extension is supported!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateView(Book book) {
        getTable().getItems().clear();

        for (Word word : book.getWordMap().values()) {
            getTable().getItems().add(word);
        }

        TableColumnResizeHelper.autoFitTable(getTable());
    }

    private TableView<Word> getTable() {
        if (tableView == null) {
            tableView = new TableView<>();

            TableColumn<Word, String> column1 = new TableColumn<>("Word");
            column1.setCellValueFactory(
                    new PropertyValueFactory<>("value"));
            tableView.getColumns().add(column1);

            TableColumn<Word, Long> column2 = new TableColumn<>("Global count");
            column2.setCellValueFactory(
                    param -> {
                        Long count = dictionary.globalWordCount.get(param.getValue().getValue().toLowerCase());
                        if (count == null) {
                            count = 0L;
                        }
                        return new SimpleLongProperty(count).asObject();
                    });
            tableView.getColumns().add(column2);

            TableColumn<Word, Integer> column3 = new TableColumn<>("Global rank");
            column3.setCellValueFactory(
                    param -> {
                        Integer rank = dictionary.globalWordRank.get(param.getValue().getValue().toLowerCase());
                        if (rank == null) {
                            rank = 0;
                        }
                        return new SimpleIntegerProperty(rank).asObject();
                    });
            tableView.getColumns().add(column3);

            TableColumn<Word, Integer> column4 = new TableColumn<>("Local count");
            column4.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getSentences().size()).asObject());
            tableView.getColumns().add(column4);

            tableView.setRowFactory(param -> new TableRow<Word>() {
                @Override
                protected void updateItem(Word item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setTooltip(new Tooltip(item.getSentences().get(0).getValue()));
                    }
                }
            });
        }
        return tableView;
    }

}
