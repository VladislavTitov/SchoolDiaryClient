package ru.vladislav.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import ru.vladislav.app.MyApplication;
import ru.vladislav.dto.*;
import ru.vladislav.model.TableModel;
import ru.vladislav.model.TreeModel;
import ru.vladislav.network.DataRequester;
import ru.vladislav.storage.DataProvider;
import ru.vladislav.storage.SendingDataHolder;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainLayoutController {

    @FXML
    private TreeView<String> treeView;

    @FXML
    private TableView<TableModel> tableView;

    @Getter @Setter
    private MyApplication app;

    public MainLayoutController() {
    }

    @FXML
    private void initialize(){
        initTree();
        tableView.setEditable(true);
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isLeaf()){
                return;
            }
            SubjectDto subjectDto = DataProvider.getInstance().getSubjectByName(newValue.getParent().getValue());
            ClassDto classDto = DataProvider.getInstance().getClassByName(newValue.getValue());
            if (subjectDto.isEmpty() || classDto.isEmpty()){
                return;
            }
            initTable(subjectDto.getId(), classDto.getId());
            tableView.setItems(FXCollections.observableArrayList(DataProvider.getInstance().getTableModelList(classDto.getId())));
        });
        DataRequester dataRequester = new DataRequester(this);
        dataRequester.executeAllRequests(DataProvider.getInstance().getUserInfoHolder().getToken(), DataProvider.getInstance().getUserInfoHolder().getUserId());
    }

    public void initTable(Long subjectId, Long classId){
        tableView.getColumns().clear();
        TableColumn<TableModel, String> studentNameColumn = new TableColumn<>("Ученик/студент");
        studentNameColumn.setMinWidth(200);
        studentNameColumn.setCellValueFactory(param -> {
            TableModel model = param.getValue();
            return new SimpleObjectProperty<>(model.getStudent().getLastName() + " " + model.getStudent().getFirstName());
        });
        tableView.getColumns().add(studentNameColumn);

        DataProvider.getInstance().getLessonsHolder().getDtosList().stream()
                .filter(lessonDto -> lessonDto.getClassId().equals(classId) && lessonDto.getSubject_id().equals(subjectId))
                .sorted(Comparator.comparing(LessonDto::getDate))
                .forEachOrdered(lessonDto -> {
                    TableColumn<TableModel, String> column = new TableColumn<>(lessonDto.getDate().getDayOfMonth() + " " +
                            lessonDto.getDate().getMonth().name() + "\n" + lessonDto.getName());
                    column.setCellValueFactory(param -> {
                        TableModel model = param.getValue();
                        int score = model.getDateScoreMap().get(lessonDto).getScore();
                        if (score >= 1 && score <= 5) {
                            return new SimpleObjectProperty<>(score + "");
                        }else {
                            return new SimpleObjectProperty<>("");
                        }
                    });
                    column.setCellFactory(TextFieldTableCell.forTableColumn());
                    column.setOnEditCommit(event -> {
                        TablePosition<TableModel, String> pos = event.getTablePosition();
                        TableModel model = pos.getTableView().getItems().get(pos.getRow());
                        LessonDto lesson = lessonDto;/*DataProvider.getInstance().getLessonsHolder().getDtosList().stream()
                                .sorted(Comparator.comparing(LessonDto::getDate))
                                .collect(Collectors.toList())
                                .get(pos.getColumn() - 1);*/
                        try {
                            ScoreDto scoreDto = model.getDateScoreMap().get(lesson);
                            int oldScore = Integer.parseInt(event.getOldValue().equals("") ? "-1" : event.getOldValue());
                            int newScore = Integer.parseInt(event.getNewValue());
                            if (newScore < 1 || newScore > 5) throw new NumberFormatException();
                            scoreDto.setScore(newScore);
                            if (scoreDto.getId() == null){ // TODO: 24.05.2017 add updating
                                StudentDto studentDto = event.getRowValue().getStudent();
                                ScoreDto score = new ScoreDto(null, newScore,
                                        lesson.getId(), studentDto.getId());
                                DataProvider.getInstance().getScoresHolder().getDtosList().add(score);
                                SendingDataHolder.DataForCreatingHolder.addIfNotExist(new ScoreDto(null, oldScore, lesson.getId(), studentDto.getId()),
                                        score);
                                SendingDataHolder.DataForCreatingHolder.mock();
                            }else {
                                StudentDto studentDto = event.getRowValue().getStudent();
                                ScoreDto score = new ScoreDto(scoreDto.getId(), newScore,
                                        lesson.getId(), studentDto.getId());
                                DataProvider.getInstance().getScoresHolder().getDtosList().stream()
                                        .filter(scoreDto1 -> scoreDto1.getId().equals(scoreDto.getId()))
                                        .findFirst().ifPresent(scoreDto1 -> scoreDto1.setScore(newScore));
                                SendingDataHolder.DataForUpdatingHolder.addIfNotExist(scoreDto, score);
                                SendingDataHolder.DataForUpdatingHolder.mock();
                            }
                        }catch (NumberFormatException ex){
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Ошибка!");
                            alert.setHeaderText("Вы ввели данные неверного формата!");
                            alert.setContentText("Введите число от 1 до 5 заместо " + event.getNewValue());
                            alert.showAndWait();
                        }
                    });
                    tableView.getColumns().add(column);
                });

    }

    public void initTree(){
        TreeItem<String> rootNode = new TreeItem<>("Root");
        List<TreeModel> treeModels = DataProvider.getInstance().getTreeModelList();
        for (TreeModel treeModel: treeModels) {
            TreeItem<String> classNode = new TreeItem<>(treeModel.getClassDto().getName());
            boolean found = false;
            for (TreeItem<String> subjectNode : rootNode.getChildren()){
                if (subjectNode.getValue().contentEquals(treeModel.getSubjectDto().getName())){
                    Optional<TreeItem<String>> duplicate = subjectNode.getChildren().stream()
                            .filter(stringTreeItem -> stringTreeItem.getValue().equals(treeModel.getClassDto().getName()))
                            .findFirst();
                    if (!duplicate.isPresent()) {
                        subjectNode.getChildren().add(classNode);
                    }
                    found = true;
                    break;
                }
            }
            if (!found){
                TreeItem<String> newSubjectNode = new TreeItem<>(treeModel.getSubjectDto().getName());
                newSubjectNode.getChildren().add(classNode);
                rootNode.getChildren().add(newSubjectNode);
            }
        }
        treeView.setRoot(rootNode);
        treeView.setShowRoot(false);
    }

    public static Node initMain(MyApplication app){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainLayoutController.class.getClassLoader().getResource("views/MainLayout.fxml"));
            Node node = fxmlLoader.load();

            MainLayoutController controller = fxmlLoader.getController();
            controller.setApp(app);
            return node;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
