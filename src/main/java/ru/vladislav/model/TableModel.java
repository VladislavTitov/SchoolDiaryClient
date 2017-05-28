package ru.vladislav.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.vladislav.dto.LessonDto;
import ru.vladislav.dto.ScoreDto;
import ru.vladislav.dto.StudentDto;
import ru.vladislav.storage.DataProvider;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TableModel {

    @Getter
    private StudentDto student;
    @Getter
    private Map<LessonDto, ScoreDto> dateScoreMap;

    public static List<TableModel> getTableModelList(Long classId){
        List<StudentDto> students = getStudentList(classId);
        return students.stream().map(studentDto -> {
            Map<LessonDto, ScoreDto> dateScoreMap = new HashMap<>();
            getScoreByStudentId(studentDto.getId()).forEach(scoreDto -> {
                LessonDto lesson = findLessonById(scoreDto.getLessonId());
                dateScoreMap.put(lesson, scoreDto);
            });
            DataProvider.getInstance().getLessonsHolder().getDtosList().forEach(lessonDto -> {
                if (!dateScoreMap.containsKey(lessonDto)){
                    dateScoreMap.put(lessonDto, new ScoreDto());
                }
            });
            return new TableModel(studentDto, dateScoreMap);
        }).collect(Collectors.toList());
    }

    private static List<StudentDto> getStudentList(Long classId){
        return DataProvider.getInstance().getStudentHolder().getDtosList().stream()
                .filter(studentDto -> studentDto.getClassId().equals(classId))
                .collect(Collectors.toList());
    }

    private static List<ScoreDto> getScoreByStudentId(Long studentId){
        return DataProvider.getInstance().getScoresHolder().getDtosList().stream()
                .filter(scoreDto -> scoreDto.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    private static LessonDto findLessonById(Long lessonId){
        return DataProvider.getInstance().getLessonsHolder().getDtosList().stream()
                .filter(lessonDto -> lessonDto.getId().equals(lessonId)).findFirst().orElse(new LessonDto());
    }



}
