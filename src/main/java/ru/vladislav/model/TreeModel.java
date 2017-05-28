package ru.vladislav.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.vladislav.dto.ClassDto;
import ru.vladislav.dto.LessonDto;
import ru.vladislav.dto.SubjectDto;

import java.util.*;

@AllArgsConstructor
public class TreeModel {

    @Getter
    private ClassDto classDto;
    @Getter
    private SubjectDto subjectDto;

    public static List<TreeModel> createTreeModelList(List<ClassDto> classes, List<SubjectDto> subjects, List<LessonDto> lessons){
        Set<TreeModel> treeModels = new HashSet<>();
        for (LessonDto lessonDto : lessons) {
            Long subjectId = lessonDto.getSubject_id();
            Long classId = lessonDto.getClassId();

            SubjectDto subjectDto = getSubjectDtoById(subjectId, subjects);
            ClassDto classDto = getClassDtoById(classId, classes);
            if (subjectDto.isEmpty() || classDto.isEmpty()) continue;
            TreeModel treeModel = new TreeModel(classDto, subjectDto);
            treeModels.add(treeModel);

        }
        return new ArrayList<>(treeModels);
    }

    private static ClassDto getClassDtoById(Long id, List<ClassDto> classes){
        for (ClassDto clazz : classes) {
            if (clazz.getId().equals(id)) return clazz;
        }
        return new ClassDto();
    }

    private static SubjectDto getSubjectDtoById(Long id, List<SubjectDto> subjects){
        for (SubjectDto subject : subjects) {
            if (subject.getId().equals(id)) return subject;
        }
        return new SubjectDto();
    }

    @Override
    public String toString() {
        return "TreeModel{" +
                "classDto=" + classDto.toString() +
                ", subjectDto=" + subjectDto.toString() +
                '}';
    }
}
