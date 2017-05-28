package ru.vladislav.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonDto {

    private Long id;
    private String name;
    private LocalDate date;
    private Integer number;
    private Long classId;
    private Long user_id;
    private Long subject_id;

}
