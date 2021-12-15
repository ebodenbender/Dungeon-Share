package com.bodenbender.emily.dungeonshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DungeonPlayerDetailActivity extends AppCompatActivity
{
    static final String TAG = "PlayerDetailActivityTag";

    private FirebaseDatabase db;
    private DatabaseReference dungeonRoomsReference;
    
    private List<Pair<DungeonRoom, String>> visibleRooms;
    private List<Pair<DungeonRoom, String>> hiddenRooms;

    private RecyclerView roomRecyclerView;
    private RoomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeon_player_detail);

        Intent intent = getIntent();
        String shareCode = intent.getStringExtra("shareCode");

        db = FirebaseDatabase.getInstance();
        dungeonRoomsReference = db.getReference("/dungeon_rooms").child(shareCode);

        visibleRooms = new ArrayList<>();
        hiddenRooms = new ArrayList<>();

        roomRecyclerView = findViewById(R.id.dungeonRoomsRecyclerView);
        adapter = new RoomAdapter(this, new RoomAdapter.DungeonRoomList()
        {
            @Override
            public DungeonRoom getDungeonRoomAt(int position)
            {
                return visibleRooms.get(position).first;
            }

            @Override
            public int getDungeonSize()
            {
                return visibleRooms.size();
            }
        });

        adapter.setClickListener(new RoomAdapter.ItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                // TODO launch player version of room detail activity (essentially what the DM has but not editable)
                // TODO perhaps add a notes feature as well, if time
            }
        });

        roomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomRecyclerView.setAdapter(adapter);

        ChildEventListener dungeonRoomsCEL = new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) 
            {
                Pair<DungeonRoom, String> room = new Pair<>(snapshot.getValue(DungeonRoom.class), snapshot.getKey());
                if (room.first.isVisible_to_players())
                {
                    visibleRooms.add(room); // TODO make a method to sort the list by room number and call it after adding room (to display in order)
                    adapter.notifyItemInserted(visibleRooms.indexOf(room));
                    Log.d(TAG, "onChildAdded: room is visible to players");
                }
                else
                {
                    hiddenRooms.add(room);
                    Log.d(TAG, "onChildAdded: room is hidden from players");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                Pair<DungeonRoom, String> updatedRoom = new Pair<>(snapshot.getValue(DungeonRoom.class), snapshot.getKey());
                String key = updatedRoom.second;
                int i = 0;
                for (Pair<DungeonRoom, String> room : visibleRooms)
                {
                    if (key.equals(room.second)) // using key for indexing the list since key will never change
                    {
                        visibleRooms.set(i, updatedRoom);
                        // TODO sort rooms list again here
                        adapter.notifyItemChanged(visibleRooms.indexOf(updatedRoom));
                    }
                    i++;
                }
                int j = 0;
                for (Pair<DungeonRoom, String> room : hiddenRooms)
                {
                    if (key.equals(room.second))
                    {
                        hiddenRooms.set(j, updatedRoom);
                    }
                    j++;
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dungeonRoomsReference.addChildEventListener(dungeonRoomsCEL);

        // TODO set up a ValueEventListener specifically for the visible_to_players field?
    }
}