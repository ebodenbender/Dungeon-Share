package com.bodenbender.emily.dungeonshare;

import com.google.firebase.database.DatabaseReference;

public class DungeonRoom {
    private String room_name;
    private int room_number;
    private String room_description;
    private boolean visible_to_players;

    public DungeonRoom(String room_name, int room_number, String room_description, boolean visible_to_players) {
        this.room_name = room_name;
        this.room_number = room_number;
        this.room_description = room_description;
        this.visible_to_players = visible_to_players;
    }
}
