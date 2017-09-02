package hu.athace;

import hu.athace.view.RowItem;
import hu.athace.view.TableColumnResizeHelper;
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

public class SubtitlearnApp extends Application {
    // TODO extract to config file
    public static final String WORDS_FILE = "google-books-common-words.txt";
    //    public static final String CHARSET = "UTF-8";
    public static final String CHARSET = "ISO-8859-2";

    private Controller controller;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        controller = new Controller(primaryStage);
        controller.init();
    }


}

