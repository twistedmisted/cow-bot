package ua.zxc.cowbot.scheduleapi.service;

import ua.zxc.cowbot.scheduleapi.model.Group;

public interface GroupService {

    Group getGroupByName(String groupName);
}
