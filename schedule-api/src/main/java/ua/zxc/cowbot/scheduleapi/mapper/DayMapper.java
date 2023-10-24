package ua.zxc.cowbot.scheduleapi.mapper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ua.zxc.cowbot.scheduleapi.model.Day;

import java.util.List;

import static java.util.Objects.isNull;

@Component
public class DayMapper {

    private final LessonMapper lessonMapper;

    public DayMapper(@Qualifier("scheduleLessonMapper") LessonMapper lessonMapper) {
        this.lessonMapper = lessonMapper;
    }

    public String dayToString(Day day) {
        if (isNull(day)) {
            return "";
        }
        String lessonsStr = lessonMapper.dtosToString(day.getLessons());
        if (lessonsStr.isBlank()) {
            lessonsStr = "<i>Заняття відсутні</i>";
        }
        return "<b>" + day.getDayName() + "</b>\n\n" + lessonsStr;
    }

    public String daysToString(List<Day> days) {
        if (isNull(days) || days.isEmpty()) {
            return "";
        }
        StringBuilder lessons = new StringBuilder();
        for (Day day : days) {
            lessons.append(dayToString(day))
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
        }
        return lessons.toString();
    }
}
