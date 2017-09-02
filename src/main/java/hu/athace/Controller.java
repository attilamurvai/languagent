package hu.athace;

import hu.athace.view.RowItem;
import hu.athace.view.TableColumnResizeHelper;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Controller {
    private final TextProcessor processor;

    private Stage stage;
    private TableView tableView;

    public Controller(Stage primaryStage) {
        stage = primaryStage;
        processor = new TextProcessor();

    }

    public void init() {
        processor.initDictionary();
        initStage(stage);
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
                            Map<Word, Integer> wordIntegerMap = processor.parseSubtitle(filePath);
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

    private void updateView(Map<Word, Integer> localWordCount) {
        getTable().getItems().clear();

        for (Word word : localWordCount.keySet()) {
            String wordValue = word.value;
            System.out.println(wordValue);
            getTable().getItems().add(new RowItem(wordValue, processor.globalWordCount.get(wordValue.toLowerCase()), processor.globalWordRank.get(wordValue.toLowerCase()), localWordCount.get(word)));
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
