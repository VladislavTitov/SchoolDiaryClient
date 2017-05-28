package ru.vladislav.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkDto {
    private Long id;
    private String title;
    private String description;
    private Long lessonId;
}
