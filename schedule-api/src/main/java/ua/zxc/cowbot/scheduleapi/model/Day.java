package ua.zxc.cowbot.scheduleapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Day {

    private static final Map<String, String> FULL_DAY_NAME = Map.of(
            "Пн", "Понеділок",
            "Вв", "Вівторок",
            "Ср", "Середа",
            "Чт", "Четвер",
            "Пт", "П'ятниця",
            "Сб", "Субота"
    );

    private String dayName;

    @JsonProperty("pairs")
    private List<Lesson> lessons = new ArrayList<>();

    @JsonSetter(value = "day")
    public void setDay(String dayName) {
        this.dayName = FULL_DAY_NAME.get(dayName);
    }

    public List<Lesson> getLessonsByTime(LocalTime timeNow) {
        List<Lesson> lessonsByTime = new ArrayList<>();
        for (Lesson lesson : lessons) {
            LocalTime timeOfStartLesson = lesson.getTime();
            LocalTime timeOfEndLesson = getTimeOfEndLesson(timeOfStartLesson);
            if (isTimeNowBetweenStartAndEndOfPair(timeNow, timeOfStartLesson, timeOfEndLesson)) {
                lessonsByTime.add(lesson);
            }
        }
        return lessonsByTime;
    }

    private LocalTime getTimeOfEndLesson(LocalTime timeOfStartPair) {
        return timeOfStartPair.plusHours(1).plusMinutes(45);
    }

    private boolean isTimeNowBetweenStartAndEndOfPair(LocalTime timeNow,
                                                      LocalTime timeOfStartPair,
                                                      LocalTime timeOfEndPair) {
        return timeNow.isAfter(timeOfStartPair) && timeNow.isBefore(timeOfEndPair);
    }
}
