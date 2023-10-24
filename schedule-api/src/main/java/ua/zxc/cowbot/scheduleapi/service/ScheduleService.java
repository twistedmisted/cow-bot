package ua.zxc.cowbot.scheduleapi.service;

import ua.zxc.cowbot.scheduleapi.exception.LessonFinishedException;
import ua.zxc.cowbot.scheduleapi.exception.LessonNotStartedException;

public interface ScheduleService {

    String getNowLessons(String groupName) throws LessonFinishedException, LessonNotStartedException;

    String getTodayPairs(String groupName);

    String getTomorrowPairs(String groupName);

    String getThisWeekPairs(String groupName);

    String getNextWeekPairs(String groupName);
}
