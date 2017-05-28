package ru.vladislav.storage;

import lombok.Getter;
import lombok.Setter;
import ru.vladislav.app.MyApplication;
import ru.vladislav.dto.*;
import ru.vladislav.model.TableModel;
import ru.vladislav.model.TreeModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class DataProvider {
    private static DataProvider ourInstance = new DataProvider();
    public static DataProvider getInstance() {
        return ourInstance;
    }

    @Getter
    private UserInfoHolder userInfoHolder;
    @Getter
    private ModelsHolder<ClassDto> classesHolder;
    @Getter
    private ModelsHolder<SubjectDto> subjectsHolder;
    @Getter
    private ModelsHolder<LessonDto> lessonsHolder;
    @Getter
    private ModelsHolder<StudentDto> studentHolder;
    @Getter
    private ModelsHolder<ScoreDto> scoresHolder;

    private List<TreeModel> treeModelList;

    private DataProvider() {
        userInfoHolder = new UserInfoHolder();
        classesHolder = new ModelsHolder<>();
        subjectsHolder = new ModelsHolder<>();
        lessonsHolder = new ModelsHolder<>();
        studentHolder = new ModelsHolder<>();
        scoresHolder = new ModelsHolder<>();

//        classesHolder.dtosList.add(new ClassDto(1L, "1a", 1L));
//        classesHolder.dtosList.add(new ClassDto(2L, "2a", 1L));
//        classesHolder.dtosList.add(new ClassDto(3L, "3a", 1L));
//        classesHolder.dtosList.add(new ClassDto(4L, "4a", 1L));
//        classesHolder.dtosList.add(new ClassDto(5L, "5a", 1L));
//
//        subjectsHolder.dtosList.add(new SubjectDto(1L, "math", 1L));
//        subjectsHolder.dtosList.add(new SubjectDto(2L, "cs", 1L));
//
//        lessonsHolder.dtosList.add(new LessonDto(1L, "multiplying", new Date(System.currentTimeMillis()+1000000), 2, 1L, 1L, 1L));
//        lessonsHolder.dtosList.add(new LessonDto(2L, "dividing", new Date(System.currentTimeMillis()+2000000), 2, 2L, 1L, 1L));
//        lessonsHolder.dtosList.add(new LessonDto(3L, "process tasks", new Date(System.currentTimeMillis()+3000000), 2, 3L, 1L, 1L));
//        lessonsHolder.dtosList.add(new LessonDto(4L, "some more", new Date(System.currentTimeMillis()+4000000), 2, 4L, 1L, 1L));
//        lessonsHolder.dtosList.add(new LessonDto(5L, "negative numbers", new Date(System.currentTimeMillis()+5000000), 2, 5L, 1L, 1L));
//        lessonsHolder.dtosList.add(new LessonDto(6L, "computers", new Date(System.currentTimeMillis()+6000000), 2, 4L, 1L, 2L));
//        lessonsHolder.dtosList.add(new LessonDto(7L, "information", new Date(System.currentTimeMillis()+7000000), 2, 5L, 1L, 2L));
//
//        studentHolder.dtosList.add(new StudentDto(1L, "titov", "vlad", null, 1L));
//        studentHolder.dtosList.add(new StudentDto(2L, "pestov", "vlad", null, 1L));
//        studentHolder.dtosList.add(new StudentDto(3L, "titov", "vlad", null, 2L));
//        studentHolder.dtosList.add(new StudentDto(4L, "titov", "vlad", null, 3L));
//        scoresHolder.dtosList.add(new ScoreDto(1L, 5, 4L, 1L));
//        scoresHolder.dtosList.add(new ScoreDto(2L, 4, 7L, 1L));
    }

    public List<TreeModel> getTreeModelList(){
        return TreeModel.createTreeModelList(classesHolder.getDtosList(), subjectsHolder.getDtosList(), lessonsHolder.getDtosList());
    }

    public List<TableModel> getTableModelList(Long classId){
        return TableModel.getTableModelList(classId);
    }

    public List<LessonDto> getLessonDtoList(Long subjectId, Long classId){
        return getLessonsHolder().getDtosList().stream()
                .filter(lessonDto -> lessonDto.getSubject_id().equals(subjectId) && lessonDto.getClassId().equals(classId))
                .sorted(Comparator.comparing(LessonDto::getDate))
                .collect(Collectors.toList());
    }

    public SubjectDto getSubjectByName(String subjectName){
        return getSubjectsHolder().getDtosList().stream()
                .filter(subjectDto -> subjectDto.getName().equals(subjectName))
                .findFirst().orElse(new SubjectDto());
    }

    public ClassDto getClassByName(String className){
        return getClassesHolder().getDtosList().stream()
                .filter(classDto -> classDto.getName().equals(className))
                .findFirst().orElse(new ClassDto());
    }

    public class UserInfoHolder{
        @Getter
        private String token;
        @Getter
        private Long userId = -1L; // TODO: 25.05.2017 add getting id

        private UserInfoHolder() {
            this.token = Preferences.userNodeForPackage(MyApplication.class).get("token", null);
            this.userId = Preferences.userNodeForPackage(MyApplication.class).getLong("userId", -1);
        }

        public void setToken(String token){
            this.token = token;
            Preferences.userNodeForPackage(MyApplication.class).put("token", token);
        }

        public void setUserId(Long userId){
            this.userId = userId;
            Preferences.userNodeForPackage(MyApplication.class).putLong("userId", userId);
        }
    }

    public class ModelsHolder<D>{
        @Getter
        private List<D> dtosList;

        private ModelsHolder() {
            this.dtosList = new ArrayList<>();
        }

        private ModelsHolder(List<D> dtos) {
            this.dtosList = new ArrayList<>(dtos);
        }
    }

}
