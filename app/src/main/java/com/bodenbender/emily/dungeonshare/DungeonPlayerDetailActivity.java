package com.bodenbender.emily.dungeonshare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DungeonPlayerDetailActivity extends AppCompatActivity
{
    static final String TAG = "PlayerDetailActivityTag";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference dungeonRoomsReference;
    private List<Dungeon> dungeonRoomsList;
    // TODO delete LookForDungeonActivity once this is starting to look right
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeon_player_detail);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        dungeonRoomsReference = mFirebaseDatabase.getReference("/dungeon_rooms");
    }
}