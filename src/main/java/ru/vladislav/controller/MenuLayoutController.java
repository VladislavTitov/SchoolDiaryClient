package ru.vladislav.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import ru.vladislav.app.MyApplication;
import ru.vladislav.dto.ClassDto;
import ru.vladislav.dto.LessonDto;
import ru.vladislav.dto.StudentDto;
import ru.vladislav.dto.SubjectDto;
import ru.vladislav.network.DataRequester;
import ru.vladislav.storage.DataProvider;
import ru.vladislav.storage.SendingDataHolder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public class MenuLayoutController {

    private MyApplication application;
    private DataRequester dataRequester;

    public MenuLayoutController() {
    }

    @FXML
    private void initialize(){

    }

    @FXML
    private void addNewSubject(){
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Новый предмет");
        dialog.setHeaderText(null);
        dialog.setContentText("Название предмета: ");

        Optional<String> subjectName = dialog.showAndWait();

        subjectName.ifPresent(s -> {
            dataRequester.addNewSubject(DataProvider.getInstance().getUserInfoHolder().getToken(),
                    DataProvider.getInstance().getUserInfoHolder().getUserId(),
                    new SubjectDto(null, s, DataProvider.getInstance().getUserInfoHolder().getUserId()));
        });
    }

    @FXML
    private void addNewClass(){
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Новый класс");
        dialog.setHeaderText(null);
        dialog.setContentText("Название класса: ");

        Optional<String> className = dialog.showAndWait();

        className.ifPresent(s -> {
            dataRequester.addNewClass(DataProvider.getInstance().getUserInfoHolder().getToken(),
                    DataProvider.getInstance().getUserInfoHolder().getUserId(),
                    new ClassDto(null, s, DataProvider.getInstance().getUserInfoHolder().getUserId()));
        });
    }

    @FXML
    private void addNewStudent(){
        Dialog<StudentDto> dialog = new Dialog<>();
        dialog.setTitle("Новый ученик/студент");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField surname = new TextField();
        surname.setPromptText("Фамилия");
        TextField name = new TextField();
        name.setPromptText("Имя");
        TextField patronymic = new TextField();
        patronymic.setPromptText("Отчество");
        ChoiceBox<ClassDto> classes = new ChoiceBox<ClassDto>(FXCollections.observableArrayList(DataProvider.getInstance().getClassesHolder().getDtosList()));
        classes.setConverter(new StringConverter<ClassDto>() {
            @Override
            public String toString(ClassDto object) {
                return object.getName();
            }

            @Override
            public ClassDto fromString(String string) {
                return null;
            }
        });

        grid.add(new Label("Фамилия:"), 0, 0);
        grid.add(surname, 1, 0);
        grid.add(new Label("Имя:"), 0, 1);
        grid.add(name, 1, 1);
        grid.add(new Label("Отчество:"), 0, 2);
        grid.add(patronymic, 1, 2);
        grid.add(new Label("Класс: "), 0, 3);
        grid.add(classes, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton.getText().equals("OK")){
                if (!surname.getText().equals("") && !name.getText().equals("") && !patronymic.getText().equals("") &&
                        classes.getSelectionModel().getSelectedItem() != null) {
                    return new StudentDto(null, surname.getText(), name.getText(), patronymic.getText(),
                            classes.getSelectionModel().getSelectedItem().getId());
                }else return null;
            }
            return null;
        });
        Optional<StudentDto> newStudent = dialog.showAndWait();
        newStudent.ifPresent(studentDto -> {
            dataRequester.addNewStudent(DataProvider.getInstance().getUserInfoHolder().getToken(),
                    DataProvider.getInstance().getUserInfoHolder().getUserId(),
                    studentDto.getClassId(),
                    studentDto);
        });
    }

    @FXML
    private void addNewLesson(){
        Dialog<LessonDto> dialog = new Dialog<>();
        dialog.setTitle("Новый урок");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField lessonName = new TextField();
        lessonName.setPromptText("Название урока");

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        datePicker.setShowWeekNumbers(false);
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        datePicker.setConverter(converter);
        datePicker.setPromptText("Дата урока");

        TextField lessonNumber = new TextField();
        lessonNumber.setPromptText("1");
        lessonNumber.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 1, change -> {
            try {
                Integer.parseInt(change.getControlNewText());
                return change;
            }catch (NumberFormatException ex){
                return null;
            }
        }));

        ChoiceBox<ClassDto> classes = new ChoiceBox<ClassDto>(FXCollections.observableArrayList(DataProvider.getInstance().getClassesHolder().getDtosList()));
        classes.setConverter(new StringConverter<ClassDto>() {
            @Override
            public String toString(ClassDto object) {
                return object.getName();
            }

            @Override
            public ClassDto fromString(String string) {
                return null;
            }
        });

        ChoiceBox<SubjectDto> subjects = new ChoiceBox<SubjectDto>(FXCollections.observableArrayList(DataProvider.getInstance().getSubjectsHolder().getDtosList()));
        subjects.setConverter(new StringConverter<SubjectDto>() {
            @Override
            public String toString(SubjectDto object) {
                return object.getName();
            }

            @Override
            public SubjectDto fromString(String string) {
                return null;
            }
        });

        grid.add(new Label("Название урока: "), 0, 0);
        grid.add(lessonName, 1, 0);
        grid.add(new Label("Дата урока: "), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Номер урока: "), 0, 2);
        grid.add(lessonNumber, 1, 2);
        grid.add(new Label("Предмет: "), 0, 3);
        grid.add(subjects, 1, 3);
        grid.add(new Label("Класс: "), 0, 4);
        grid.add(classes, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton.getText().equals("OK")){
                if (!lessonName.getText().equals("") && !lessonNumber.getText().equals("") &&
                        classes.getSelectionModel().getSelectedItem() != null &&
                        subjects.getSelectionModel().getSelectedItem() != null){
                    return new LessonDto(null, lessonName.getText(),
                            datePicker.getValue(),
                            Integer.parseInt(lessonNumber.getText()), classes.getSelectionModel().getSelectedItem().getId(),
                            DataProvider.getInstance().getUserInfoHolder().getUserId(),
                            subjects.getSelectionModel().getSelectedItem().getId());
                }
            }
            return null;
        });
        Optional<LessonDto> resultLesson = dialog.showAndWait();
        resultLesson.ifPresent(lessonDto -> {
            dataRequester.addNewLesson(DataProvider.getInstance().getUserInfoHolder().getToken(),
                    DataProvider.getInstance().getUserInfoHolder().getUserId(),
                    lessonDto);
        });
    }

    @FXML
    private void refresh(){

    }

    @FXML
    private void save(){
        dataRequester.addAllNewScores(DataProvider.getInstance().getUserInfoHolder().getToken(),
                DataProvider.getInstance().getUserInfoHolder().getUserId(),
                SendingDataHolder.DataForCreatingHolder.getNewScores());
    }

    @FXML
    private void exit(){
        System.exit(0);
    }

    public void setApplication(MyApplication application) {
        this.application = application;
    }

    public void initDataRequester(MainLayoutController mainLayoutController) {
         dataRequester = new DataRequester(mainLayoutController);
    }

    public static Node initMenu(MyApplication app){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MenuLayoutController.class.getClassLoader().getResource("views/MenuLayout.fxml"));
            Node node = fxmlLoader.load();

            MenuLayoutController controller = fxmlLoader.getController();
            controller.setApplication(app);

            return node;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
