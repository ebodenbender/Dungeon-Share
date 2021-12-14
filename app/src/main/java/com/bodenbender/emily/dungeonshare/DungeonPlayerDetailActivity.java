package com.bodenbender.emily.dungeonshare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class DungeonPlayerDetailActivity extends AppCompatActivity
{
    static final String TAG = "PlayerDetailActivityTag";
    // TODO delete LookForDungeonActivity once this is starting to look right
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeon_player_detail);
    }
}