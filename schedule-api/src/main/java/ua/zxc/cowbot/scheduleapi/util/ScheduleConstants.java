package ua.zxc.cowbot.scheduleapi.util;

import java.util.Map;

public class ScheduleConstants {

    // LINKS
    public static final String GET_LESSONS = "https://api.campus.kpi.ua/schedule/lessons?groupId=%s";
    public static final String GET_GROUPS = "https://api.campus.kpi.ua/schedule/groups";
    public static final String GET_CURRENT_INFO = "https://api.campus.kpi.ua/time/current";

    public static final Map<String, Integer> NUMBER_OF_PAIR_BY_TIME = Map.of(
            "8.30", 1,
            "10.25", 2,
            "12.20", 3,
            "14.15", 4,
            "16.10", 5,
            "18.30", 6
    );
}
