package ru.vladislav.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {

    private Long id;
    private String lastName;
    private String firstName;
    private String patronymic;
    private Long classId;

}
