package ua.zxc.cowbot.scheduleapi.mapper;

import org.springframework.stereotype.Component;
import ua.zxc.cowbot.scheduleapi.model.Lesson;

import java.util.*;

import static ua.zxc.cowbot.scheduleapi.util.ScheduleConstants.NUMBER_OF_PAIR_BY_TIME;

@Component("scheduleLessonMapper")
public class LessonMapper {

    public String dtoToString(Lesson dto) {
        if (dto == null) {
            return "";
        }
        return String.format("<b>%d. %s</b>\n<pre>%s</pre>\n<pre>%s, %s, %s</pre>",
                dto.getNumber(),
                dto.getName(),
                dto.getTeacherName(),
                dto.getType(),
                dto.getPlace().isBlank() ? "-" : dto.getPlace(),
                dto.getStrTime()
        );
    }

    public String dtosToString(List<Lesson> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return "";
        }
        StringBuilder stringDTOS = new StringBuilder();
        dtos.sort(Comparator.comparingInt(Lesson::getNumber));
        for (Lesson dto : dtos) {
            stringDTOS.append(dtoToString(dto))
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
        }
        return stringDTOS.toString();
    }
}
