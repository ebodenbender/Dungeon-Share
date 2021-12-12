package com.bodenbender.emily.dungeonshare;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DungeonRoom implements Parcelable {
    private String room_name;
    private String room_description;
    private boolean visible_to_players;
    private int room_number;

    public DungeonRoom(String room_name,  String room_description, boolean visible_to_players, int room_number) {
        this.room_name = room_name;
        this.room_description = room_description;
        this.visible_to_players = visible_to_players;
        this.room_number = room_number;
    }

    public DungeonRoom() {}

    public DungeonRoom(Parcel in) {
        String [] data = new String[4];
        in.readStringArray(data);

        this.room_name = data[0];
        this.room_description = data[1];
        this.visible_to_players = Boolean.parseBoolean(data[2]);
        this.room_number = Integer.parseInt(data[3]);
    }

    public void updateValues(DungeonRoom other) {
        this.room_name = other.room_name;
        this.room_description = other.room_description;
        this.visible_to_players = other.visible_to_players;
        this.room_number = other.room_number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{room_name, room_description, String.valueOf(visible_to_players), String.valueOf(room_number)});
    }

    public static final Parcelable.Creator<DungeonRoom> CREATOR = new Parcelable.Creator<DungeonRoom>() {
        @Override
        public DungeonRoom createFromParcel(Parcel source) {
            return new DungeonRoom(source);
        }

        @Override
        public DungeonRoom[] newArray(int size) {
            return new DungeonRoom[size];
        }
    };

    public String getRoom_name() {
        return room_name;
    }

    public String getRoom_description() {
        return room_description;
    }

    public int getRoom_number() {
        return room_number;
    }

    public boolean isVisible_to_players() {
        return visible_to_players;
    }

    @NonNull
    @Override
    public String toString() {
        return room_name;
    }
}
