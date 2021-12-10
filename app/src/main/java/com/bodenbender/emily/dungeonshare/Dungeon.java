package com.bodenbender.emily.dungeonshare;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Dungeon
{
    private String DM_name; // fields are case sensitive (should exactly match what's on the database)
    private String dungeon_name;
    private String share_code;

    public Dungeon() {}

    public Dungeon(String DM_name, String dungeon_name, String share_code)
    {
        this.DM_name = DM_name;
        this.dungeon_name = dungeon_name;
        this.share_code = share_code;
    }

    public void setDM_name(String DM_name) {
        this.DM_name = DM_name;
    }

    public void setDungeon_name(String dungeon_name) {
        this.dungeon_name = dungeon_name;
    }

    public void setShare_code(String share_code) {
        this.share_code = share_code;
    }

    // TODO "No setter/field for DM_name found on class com.bodenbender.emily.dungeonshare.Dungeon"

    public String getDungeonName()
    {
        return this.dungeon_name;
    }

    public String getDmName()
    {
        return this.DM_name;
    }

    public String getShareCode()
    {
        return this.share_code;
    }

    @NonNull
    @Override
    public String toString()
    {
        return "DM Name: " + this.DM_name + "\nDungeon Name: " + this.dungeon_name + "\nShare Code: " + this.share_code;
    }
}
