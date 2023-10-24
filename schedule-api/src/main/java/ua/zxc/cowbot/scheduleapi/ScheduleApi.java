package ua.zxc.cowbot.scheduleapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.zxc.cowbot.scheduleapi.model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

import static ua.zxc.cowbot.scheduleapi.util.ScheduleConstants.*;

@Component
@RequiredArgsConstructor
public class ScheduleApi {

    private static final Function<String, String> getResponse = response -> response;

    private final ObjectMapper objectMapper;

    public Schedule getFullSchedule(String groupName) {
        String url = getLessonsUrl(groupName);
        String source = sendRequest(url);
        try {
            JsonNode rootNode = objectMapper.readTree(source);
            JsonNode dataNode = rootNode.path("data");
            return objectMapper.treeToValue(dataNode, Schedule.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't get full schedule by groupName = [" + groupName + "]", e);
        }
    }

    public Week getScheduleByWeekNumber(String groupName, String weekNumber) {
        Schedule fullSchedule = getFullSchedule(groupName);
        return fullSchedule.getWeek(weekNumber);
    }

    public Day getScheduleByDay(String groupName, String weekNumber, int dayIndex) {
        Week scheduleByWeekNumber = getScheduleByWeekNumber(groupName, weekNumber);
        return scheduleByWeekNumber.getDayByIndex(dayIndex);
    }

    public List<Lesson> getLessonsByTime(String groupName, String weekNumber, int dayIndex, LocalTime time) {
        Day scheduleByDay = getScheduleByDay(groupName, weekNumber, dayIndex);
        return scheduleByDay.getLessonsByTime(time);
    }

    public Group getAllGroups(String groupName) {
        try {
            String body = sendRequest(getGroupsUrl());
            JsonNode rootNode = objectMapper.readTree(body);
            JsonNode dataNode = rootNode.path("data");
            List<Group> groups = objectMapper.treeToValue(dataNode, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, Group.class));
            return groups.stream()
                    .filter(g -> g.getName().equalsIgnoreCase(groupName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Can't find group by name = [" + groupName + "]"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't get full list of groups", e);
        }
    }

    public CurrentInfo getCurrentInfo() {
        try {
            String body = sendRequest(GET_CURRENT_INFO);
            JsonNode rootNode = objectMapper.readTree(body);
            JsonNode dataNode = rootNode.path("data");
            return objectMapper.treeToValue(dataNode, CurrentInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't get current time", e);
        }
    }

    private String sendRequest(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(getResponse)
                .join();
    }

    private String getLessonsUrl(String groupName) {
        return String.format(GET_LESSONS, groupName);
    }

    private String getGroupsUrl() {
        return GET_GROUPS;
    }
}
