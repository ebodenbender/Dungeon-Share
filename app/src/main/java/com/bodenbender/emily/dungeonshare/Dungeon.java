package com.bodenbender.emily.dungeonshare;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {
    private String DM_name; // fields are case sensitive (should exactly match what's on the database)
    private String dungeon_name;
    private String share_code;

    public Dungeon(String share_code) {
        DM_name = "NO DM NAME";
        dungeon_name = "NO DUNGEON NAME";
        this.share_code = share_code;
    }

    public Dungeon() {}

    public Dungeon(String DM_name, String dungeon_name, String share_code) {
        this.DM_name = DM_name;
        this.dungeon_name = dungeon_name;
        this.share_code = share_code;
    }

    /**
     * Function to set all dungeon values once DM creates dungeon
     * @param DM_name name of Dungeon Master / Host
     * @param dungeon_name name of the active dungeon
     * @param share_code unique share code of the dungeon
     */
    public void setDungeonValues(String DM_name, String dungeon_name, String share_code) {
        this.DM_name = DM_name;
        this.dungeon_name = dungeon_name;
        this.share_code = share_code;
    }

    /**
     * Setter for dungeon_name
     * @param dungeon_name name of the active dungeon
     */
    public void setDungeon_name(String dungeon_name) {
        this.dungeon_name = dungeon_name;
    }

    @Override
    public String toString() {
        return "DM Name: " + this.DM_name + "\nDungeon Name: " + this.dungeon_name + "\nShare Code: " + this.share_code;
    }

    public String getDM_name() {
        return DM_name;
    }

    public String getDungeon_name() {
        return dungeon_name;
    }

    public String getShare_code() {
        return share_code;
    }
}