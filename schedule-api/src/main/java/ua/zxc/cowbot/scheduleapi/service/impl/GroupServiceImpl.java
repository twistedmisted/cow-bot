package ua.zxc.cowbot.scheduleapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.zxc.cowbot.scheduleapi.ScheduleApi;
import ua.zxc.cowbot.scheduleapi.model.Group;
import ua.zxc.cowbot.scheduleapi.service.GroupService;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final ScheduleApi scheduleApi;

    @Override
    public Group getGroupByName(String groupName) {
        return scheduleApi.getAllGroups(groupName);
    }
}
