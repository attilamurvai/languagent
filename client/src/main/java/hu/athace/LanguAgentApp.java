package hu.athace;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class LanguAgentApp extends Application {
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

