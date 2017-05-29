package ru.vladislav.network;

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import ru.vladislav.controller.MainLayoutController;
import ru.vladislav.dto.*;
import ru.vladislav.storage.DataProvider;

import java.util.List;
import java.util.Optional;

public class DataRequester {

    private MainLayoutController controller;

    public DataRequester(MainLayoutController controller) {
        this.controller = controller;
    }

    public void executeAllRequests(String token, Long userId){
        getAllSubjects(token, userId);
        getAllClasses(token, userId);
        getAllLessons(token, userId);
    }

    public void getAllSubjects(String token, Long userId){
        RetrofitFactory.getInstance().getService().getAllSubjects(token, userId)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(listResponse -> {
                    Optional.ofNullable(listResponse.body())
                            .ifPresent(subjectDtos -> DataProvider.getInstance().getSubjectsHolder().getDtosList().addAll(subjectDtos));
                }, throwable -> {}, this::updateViews);
    }

    public void getAllClasses(String token, Long userId){
        RetrofitFactory.getInstance().getService().getAllClasses(token, userId)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(listResponse -> {
                    Optional.ofNullable(listResponse.body())
                            .ifPresent(classDtos -> DataProvider.getInstance().getClassesHolder().getDtosList().addAll(classDtos));
                }, throwable -> {}, () -> {
                    updateViews();
                    getAllStudents(token, userId, DataProvider.getInstance().getClassesHolder().getDtosList());
                });
    }

    public void getAllLessons(String token, Long userId){
        RetrofitFactory.getInstance().getService().getAllLessons(token, userId)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(listResponse -> {
                    Optional.ofNullable(listResponse.body())
                            .ifPresent(lessonDtos -> DataProvider.getInstance().getLessonsHolder().getDtosList().addAll(lessonDtos));
                }, Throwable::printStackTrace, () -> {
                    updateViews();
                    getAllScores(token, userId, DataProvider.getInstance().getLessonsHolder().getDtosList());
                });
    }

    public void getAllStudents(String token, Long userId, List<ClassDto> classes){
        classes.forEach(classDto -> {
            RetrofitFactory.getInstance().getService().getStudents(token, userId, classDto.getId())
                    .observeOn(Schedulers.newThread())
                    .subscribe(listResponse -> {
                        Optional.ofNullable(listResponse.body())
                                .ifPresent(studentDtos -> DataProvider.getInstance().getStudentHolder().getDtosList().addAll(studentDtos));
                    });
        });
    }

    public void getAllScores(String token, Long userId, List<LessonDto> lessons){
        lessons.forEach(lessonDto -> {
            RetrofitFactory.getInstance().getService().getScore(token, userId, lessonDto.getId())
                    .observeOn(Schedulers.newThread())
                    .subscribe(listResponse -> {
                        Optional.ofNullable(listResponse.body())
                                .ifPresent(scoreDtos -> DataProvider.getInstance().getScoresHolder().getDtosList().addAll(scoreDtos));
                    });
        });
    }

    public void addNewSubject(String token, Long userId, SubjectDto subjectDto){
        RetrofitFactory.getInstance().getService().postNewSubject(token, userId, subjectDto)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(subjectDtoResponse -> {
                    Optional.ofNullable(subjectDtoResponse.body())
                            .ifPresent(returnedSubjectDto ->
                                    DataProvider.getInstance().getSubjectsHolder().getDtosList().add(returnedSubjectDto));
                });
    }

    public void addNewClass(String token, Long userId, ClassDto classDto){
        RetrofitFactory.getInstance().getService().postNewClass(token, userId, classDto)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(classDtoResponse -> {
                    Optional.ofNullable(classDtoResponse.body())
                            .ifPresent(returnedClassDto ->
                                    DataProvider.getInstance().getClassesHolder().getDtosList().add(returnedClassDto));
                });
    }

    public void addNewStudent(String token, Long userId, Long classId, StudentDto studentDto){
        RetrofitFactory.getInstance().getService().postNewStudent(token, userId, classId, studentDto)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(studentDtoResponse -> {
                    Optional.ofNullable(studentDtoResponse.body())
                            .ifPresent(returnedStudentDto ->
                                    DataProvider.getInstance().getStudentHolder().getDtosList().add(returnedStudentDto));
                });
    }

    public void addNewLesson(String token, Long userId, LessonDto lessonDto){
        RetrofitFactory.getInstance().getService().postNewLesson(token, userId, lessonDto)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(lessonDtoResponse -> {
                    Optional.ofNullable(lessonDtoResponse.body())
                            .ifPresent(returnedLessonDto ->
                                    DataProvider.getInstance().getLessonsHolder().getDtosList().add(returnedLessonDto));
                }, throwable -> {}, controller::initTree);
    }

    public void addAllNewScores(String token, Long userId, List<ScoreDto> newScores){
        newScores.forEach(scoreDto -> {
            RetrofitFactory.getInstance().getService().postNewScore(token, userId, scoreDto.getLessonId(), scoreDto)
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe();
        });
    }

    public void updateScores(String token, Long userId, List<ScoreDto> updatingScores){
        updatingScores.forEach(scoreDto -> {
            RetrofitFactory.getInstance().getService().updateScore(token, userId, scoreDto.getLessonId(), scoreDto.getId(), scoreDto)
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe();
        });
    }

    private void updateViews(){
        controller.initTree();
    }

}
