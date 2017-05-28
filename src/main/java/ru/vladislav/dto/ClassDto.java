package ru.vladislav.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassDto {

    private Long id;
    private String name;
    private Long userId;

    public boolean isEmpty(){
        if (id == null && name == null && userId == null)
            return true;
        return false;
    }

}
