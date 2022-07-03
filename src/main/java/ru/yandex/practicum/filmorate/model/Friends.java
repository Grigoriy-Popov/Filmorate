package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friends {
    private int user1_id;
    private int user2_id;
    private boolean isFriendshipAccepted;

    public Friends(int user1_id, int user2_id, boolean isFriendshipAccepted) {
        this.user1_id = user1_id;
        this.user2_id = user2_id;
        this.isFriendshipAccepted = isFriendshipAccepted;
    }
}
