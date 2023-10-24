package ua.zxc.cowbot.scheduleapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static ua.zxc.cowbot.scheduleapi.util.ScheduleConstants.NUMBER_OF_PAIR_BY_TIME;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(value = {"lecturerId"})
public class Lesson {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H.mm");

    private String teacherName;

    private String type;

    private String time;

    private String name;

    private String place;

    private String tag;

    public int getNumber() {
        return NUMBER_OF_PAIR_BY_TIME.get(getStrTime());
    }

    public LocalTime getTime() {
        return LocalTime.parse(time, TIME_FORMAT);
    }

    public String getStrTime() {
        return this.time;
    }
}
