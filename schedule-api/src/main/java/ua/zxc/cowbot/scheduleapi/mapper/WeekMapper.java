package ua.zxc.cowbot.scheduleapi.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.zxc.cowbot.scheduleapi.model.Week;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class WeekMapper {

    private final DayMapper dayMapper;

    public String weekToString(Week week) {
        if (isNull(week)) {
            return "";
        }
        String days = dayMapper.daysToString(week.getDays());
        if (days.isBlank()) {
            return "";
        }
        return "<b>" + week.getWeekName() + "</b>\n\n" + days;
    }
}
