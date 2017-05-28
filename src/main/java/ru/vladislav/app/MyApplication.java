package ru.vladislav.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.vladislav.controller.MainLayoutController;
import ru.vladislav.controller.MenuLayoutController;
import ru.vladislav.controller.SignUpController;
import ru.vladislav.storage.DataProvider;

import java.io.IOException;
import java.util.prefs.Preferences;

public class MyApplication extends Application{

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("School Diary");
        this.primaryStage.getIcons().add(new Image("images/open-book.png"));

        String token = DataProvider.getInstance().getUserInfoHolder().getToken();
        if (token == null){
            SignUpController.signUpInit(this);
        }else {
            goToMain();
        }

    }

    /*private void initFirstLayout(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/EnteringLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            primaryStage.setScene(scene);

            EnteringLayoutController enteringLayoutController = fxmlLoader.getController();
            enteringLayoutController.setStage(this.primaryStage);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void goToMain(){
        BorderPane menuLayout = ((BorderPane) MenuLayoutController.initMenu(this));
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MenuLayoutController.class.getClassLoader().getResource("views/MenuLayout.fxml"));
            menuLayout = fxmlLoader.load();

            MenuLayoutController menuLayoutController = fxmlLoader.getController();
            menuLayoutController.setApplication(this);

            FXMLLoader fxmlLoader1 = new FXMLLoader(MainLayoutController.class.getClassLoader().getResource("views/MainLayout.fxml"));
            menuLayout.setCenter(fxmlLoader1.load());

            MainLayoutController mainController = fxmlLoader1.getController();
            mainController.setApp(this);
            menuLayoutController.initDataRequester(mainController);
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setScene(new Scene(menuLayout));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        /*Preferences prefs = Preferences.userNodeForPackage(MyApplication.class);
        prefs.remove("token");*/
        launch(args);
    }
}
