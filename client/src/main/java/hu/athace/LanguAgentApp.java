package hu.athace;

import hu.athace.business.TextProcessorImpl;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class LanguAgentApp extends Application {

    private Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (Constants.RUN_WITHOUT_SERVER) {
            TextProcessorImpl textProcessor = new TextProcessorImpl();
            controller = new Controller(primaryStage, textProcessor);
            controller.init();
        } else {
            // todo: implement rest service call
        }
    }


}

