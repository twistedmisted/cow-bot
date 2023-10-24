package ua.zxc.cowbot.scheduleapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(value = {"groupCode"})
public class Schedule {

    private Map<String, Week> weeks = new HashMap<>();

    @JsonSetter(value = "scheduleFirstWeek")
    public void setFirstWeek(Week firstWeek) {
        firstWeek.setWeekName("Перший тиждень");
        weeks.put("scheduleFirstWeek", firstWeek);
    }

    @JsonSetter(value = "scheduleSecondWeek")
    public void setSecondWeek(Week secondWeek) {
        secondWeek.setWeekName("Другий тиждень");
        weeks.put("scheduleSecondWeek", secondWeek);
    }

    public Week getWeek(String weekNumber) {
        return weeks.get(weekNumber);
    }
}
