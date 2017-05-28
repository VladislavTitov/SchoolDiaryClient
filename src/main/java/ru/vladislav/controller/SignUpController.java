package ru.vladislav.controller;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import ru.vladislav.app.MyApplication;
import ru.vladislav.dto.UserDtoForRegistration;
import ru.vladislav.network.RetrofitFactory;
import ru.vladislav.storage.DataProvider;

import java.io.IOException;
import java.util.prefs.Preferences;

public class SignUpController {

    @FXML
    private TextField loginIn;
    @FXML
    private TextField passwordIn;
    @FXML
    private Button login;
    @FXML
    private TextField loginUp;
    @FXML
    private TextField passwordUp;
    @FXML
    private Button register;
    @FXML
    private ProgressBar progress;


    private MyApplication application;

    public SignUpController() {
    }

    @FXML
    private void initialize(){

        JavaFxObservable.eventsOf(login, ActionEvent.ACTION)
                .doOnNext(actionEvent -> progress.setVisible(true))
                .doOnNext(actionEvent -> progress.setProgress(ProgressBar.INDETERMINATE_PROGRESS))
                .flatMap(actionEvent -> Observable.just(new UserDtoForRegistration(loginIn.getText(), passwordIn.getText())))
                .observeOn(Schedulers.io())
                .flatMap(user -> RetrofitFactory.getInstance().getService().login(user.getName(), user.getPassword()))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(response -> {
                    String token = response.headers().get("Auth-Token");
                    DataProvider.getInstance().getUserInfoHolder().setToken(token);
                    DataProvider.getInstance().getUserInfoHolder().setUserId(response.body().getId());
                    application.goToMain();
                }, Throwable::printStackTrace);

        register.setOnAction(event -> {
            progress.setVisible(true);
            progress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            UserDtoForRegistration user = new UserDtoForRegistration(loginUp.getText(), passwordUp.getText());
            RetrofitFactory.getInstance().getService().register(user)
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe(response -> {
                        String token = response.headers().get("Auth-Token");
                        DataProvider.getInstance().getUserInfoHolder().setToken(token);
                        DataProvider.getInstance().getUserInfoHolder().setUserId(response.body().getId());
                    }, throwable -> {},
                    application::goToMain);
        });
    }

    public void setApplication(MyApplication application) {
        this.application = application;
    }

    public static void signUpInit(MyApplication app){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SignUpController.class.getClassLoader().getResource("views/SignUpLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            app.getPrimaryStage().setScene(scene);

            SignUpController controller = fxmlLoader.getController();
            controller.setApplication(app);

            app.getPrimaryStage().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
