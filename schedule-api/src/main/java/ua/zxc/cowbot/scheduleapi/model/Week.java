package ua.zxc.cowbot.scheduleapi.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.zxc.cowbot.scheduleapi.model.deserializer.WeekDeserializer;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = WeekDeserializer.class)
public class Week {

    private String weekName;

    private List<Day> days = new ArrayList<>();

    public Week(List<Day> days) {
        this.days = days;
    }

    public Day getDayByIndex(int index) {
        return days.get(index);
    }
}
