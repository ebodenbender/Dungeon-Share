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
import com.google.firebase.database.ValueEventListener;

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
            {   // TODO if snapshot.isVisible_to_players() != visibleRooms.get(i).isVisible_to_players, then move between lists
                Pair<DungeonRoom, String> updatedRoom = new Pair<>(snapshot.getValue(DungeonRoom.class), snapshot.getKey());
                for (Pair<DungeonRoom, String> room : visibleRooms)
                {
                    if (updatedRoom.second.equals(room.second))
                    {
                        if (updatedRoom.first.isVisible_to_players() != room.first.isVisible_to_players())
                        {
                            // visibility changed
                            int roomIndex = visibleRooms.indexOf(room);
                            visibleRooms.remove(roomIndex);
                            hiddenRooms.add(updatedRoom);
                            // TODO notify adapter item removed
                            adapter.notifyItemRemoved(roomIndex);
                            Log.d(TAG, "onChildChanged: visible -> hidden"+ updatedRoom.second);
                            return;
                        }
                        else
                        {
                            visibleRooms.set(visibleRooms.indexOf(room), updatedRoom);
                            // TODO sort rooms list again here
                            adapter.notifyItemChanged(visibleRooms.indexOf(updatedRoom));
                            Log.d(TAG, "onChildChanged: visibility same both visible" + updatedRoom.second);
                            return;
                        }
                    }
                }
                for (Pair<DungeonRoom, String> room : hiddenRooms)
                {
                    if (updatedRoom.second.equals(room.second))
                    {
                        if (updatedRoom.first.isVisible_to_players() != room.first.isVisible_to_players())
                        {
                            // visibility changed
                            hiddenRooms.remove(hiddenRooms.indexOf(room));
                            visibleRooms.add(updatedRoom);
                            // TODO sort rooms again
                            // TODO notify adapter that item was added
                            adapter.notifyItemInserted(visibleRooms.size() - 1);
                            Log.d(TAG, "onChildChanged: hidden -> visible"+ updatedRoom.second);
                            return;
                        }
                        else
                        {
                            hiddenRooms.set(hiddenRooms.indexOf(room), updatedRoom);
                            Log.d(TAG, "onChildChanged: visibility same invisible"+ updatedRoom.second);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {
                // TODO implement this (walk through both lists and if the key is in there remove it)
                // TODO if it's in the visibleRooms lists update adapter
                String key = snapshot.getKey(); // TODO make sure this gets the key correctly
                for (Pair<DungeonRoom, String> room : visibleRooms)
                {
                    if (room.second.equals(key))
                    {

                    }
                }
                for (Pair<DungeonRoom, String> room : hiddenRooms)
                {
                    if (room.second.equals(key))
                    {

                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dungeonRoomsReference.addChildEventListener(dungeonRoomsCEL);
    }
}