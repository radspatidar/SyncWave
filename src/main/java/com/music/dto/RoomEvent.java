package com.music.dto;

import com.music.model.User;
import java.util.List;

public class RoomEvent {

    private String type;
    private List<User> users;

    public RoomEvent() {}

    public RoomEvent(String type, List<User> users) {
        this.type = type;
        this.users = users;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}