package ru.vladislav.storage;

import lombok.Getter;
import ru.vladislav.dto.ScoreDto;

import java.util.ArrayList;
import java.util.List;

public class SendingDataHolder {

    public static class DataForUpdatingHolder{
        @Getter
        private static List<ScoreDto> updatingScores = new ArrayList<>();

        public static void addIfNotExist(ScoreDto oldScoreDto, ScoreDto newScoreDto){
            updatingScores.remove(oldScoreDto);
            updatingScores.add(newScoreDto);
        }
        public static void mock(){
            int i = 0;
        }
    }

    public static class DataForCreatingHolder{
        @Getter
        private static List<ScoreDto> newScores = new ArrayList<>();

        public static void addIfNotExist(ScoreDto oldScoreDto, ScoreDto newScoreDto){
            newScores.remove(oldScoreDto);
            newScores.add(newScoreDto);
        }

        public static void mock(){
            int i = 0;
        }

    }

}
