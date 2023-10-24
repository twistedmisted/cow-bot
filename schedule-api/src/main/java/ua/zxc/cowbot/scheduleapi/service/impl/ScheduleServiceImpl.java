package ua.zxc.cowbot.scheduleapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ua.zxc.cowbot.scheduleapi.ScheduleApi;
import ua.zxc.cowbot.scheduleapi.exception.LessonFinishedException;
import ua.zxc.cowbot.scheduleapi.exception.LessonNotStartedException;
import ua.zxc.cowbot.scheduleapi.mapper.DayMapper;
import ua.zxc.cowbot.scheduleapi.mapper.LessonMapper;
import ua.zxc.cowbot.scheduleapi.mapper.WeekMapper;
import ua.zxc.cowbot.scheduleapi.model.Day;
import ua.zxc.cowbot.scheduleapi.model.Lesson;
import ua.zxc.cowbot.scheduleapi.model.Week;
import ua.zxc.cowbot.scheduleapi.service.ScheduleService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static java.time.temporal.ChronoField.DAY_OF_WEEK;

@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    private static final ZoneId UKRAINIAN_ZONE = ZoneId.of("Europe/Kiev");
    private static final LocalTime START_FIRST_LESSON = LocalTime.of(8, 30);
    private static final LocalTime END_LAST_LESSON = LocalTime.of(20, 15);

    private final ScheduleApi scheduleApi;
    private final LessonMapper lessonMapper;
    private final DayMapper dayMapper;
    private final WeekMapper weekMapper;

    public ScheduleServiceImpl(ScheduleApi scheduleApi, @Qualifier("scheduleLessonMapper") LessonMapper lessonMapper,
                               DayMapper dayMapper, WeekMapper weekMapper) {
        this.scheduleApi = scheduleApi;
        this.lessonMapper = lessonMapper;
        this.dayMapper = dayMapper;
        this.weekMapper = weekMapper;
    }

    @Override
    public String getNowLessons(String groupName) throws LessonFinishedException, LessonNotStartedException {
        LocalTime timeNow = LocalTime.now(UKRAINIAN_ZONE);
        checkIfLessonsStart(timeNow);
        checkIfLessonsNotFinished(timeNow);
        List<Lesson> nowLessons = scheduleApi.getLessonsByTime(groupName, getWeekNow(), getDayIndex(), timeNow);
        return lessonMapper.dtosToString(nowLessons);
    }

    private void checkIfLessonsStart(LocalTime timeNow) throws LessonNotStartedException {
        if (timeNow.isBefore(START_FIRST_LESSON)) {
            throw new LessonNotStartedException();
        }
    }

    private void checkIfLessonsNotFinished(LocalTime timeNow) throws LessonFinishedException {
        if (timeNow.isAfter(END_LAST_LESSON)) {
            throw new LessonFinishedException();
        }
    }

    @Override
    public String getTodayPairs(String groupName) {
        Day scheduleByDay = scheduleApi.getScheduleByDay(groupName, getWeekNow(), getDayIndex());
        return dayMapper.dayToString(scheduleByDay);
    }

    @Override
    public String getTomorrowPairs(String groupName) {
        int dayIndex = getDayIndex();
        String weekNumber;
        if (dayIndex == 5 || dayIndex == 6) {
            dayIndex = 0;
            weekNumber = getNextWeek();
        } else {
            dayIndex++;
            weekNumber = getWeekNow();
        }
        Day scheduleByDay = scheduleApi.getScheduleByDay(groupName, weekNumber, dayIndex);
        return dayMapper.dayToString(scheduleByDay);
    }

    @Override
    public String getThisWeekPairs(String groupName) {
        Week scheduleByWeekNumber = scheduleApi.getScheduleByWeekNumber(groupName, getWeekNow());
        return weekMapper.weekToString(scheduleByWeekNumber);
    }

    @Override
    public String getNextWeekPairs(String groupName) {
        Week scheduleByWeekNumber = scheduleApi.getScheduleByWeekNumber(groupName, getNextWeek());
        return weekMapper.weekToString(scheduleByWeekNumber);
    }

    //    private String getWeekNow() {
    private String getNextWeek() {
        return scheduleApi.getCurrentInfo().getCurrentWeek() == 1 ? "scheduleSecondWeek" : "scheduleFirstWeek";
//        return getNumberOfWeekInYear() % 2 == 0 ? "scheduleSecondWeek" : "scheduleFirstWeek";
    }

    //    private String getNextWeek() {
    private String getWeekNow() {
        return scheduleApi.getCurrentInfo().getCurrentWeek() == 1 ? "scheduleFirstWeek" : "scheduleSecondWeek";
//        return getNumberOfWeekInYear() % 2 == 0 ? "scheduleFirstWeek" : "scheduleSecondWeek";
    }

//    private int getNumberOfWeekInYear() {
//        return LocalDate.now(UKRAINIAN_ZONE).get(ALIGNED_WEEK_OF_YEAR);
//    }

    private int getDayIndex() {
        return getDayOfWeek() - 1;
    }

    private int getDayOfWeek() {
        return LocalDate.now(UKRAINIAN_ZONE).getDayOfWeek().get(DAY_OF_WEEK);
    }
}
