package com.bodenbender.emily.dungeonshare;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HostDungeonActivity extends AppCompatActivity {

    public static final String TAG = "HostDungeonActivityTag";

    private String shareCode;
    private String DMName;
    private String dungeonName;

    private FirebaseDatabase db;
    private FirebaseDungeonHelper firebaseDungeonHelper;
    private DatabaseReference dungeonRoomReference;

    private List<Pair<DungeonRoom, String>> dungeonRooms;

    private RecyclerView roomRecyclerView;

    ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_dungeon);

        dungeonRooms = new ArrayList<>();

        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, "ERROR: No Intent", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (intent.hasExtra("shareCode")) {
                shareCode = intent.getStringExtra("shareCode");
            }
            if (intent.hasExtra("DMName")) {
                DMName = intent.getStringExtra("DMName");
            }
            if (intent.hasExtra("DungeonName")) {
                dungeonName = intent.getStringExtra("DungeonName");
            }
        }

        db = FirebaseDatabase.getInstance();
        firebaseDungeonHelper = new FirebaseDungeonHelper(shareCode, new Dungeon(DMName, dungeonName, shareCode));

        roomRecyclerView = findViewById(R.id.roomRecyclerView);
        Button addRoomButton = findViewById(R.id.addRoomButton);
        addRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HostDungeonActivity.this, NewRoomActivity.class);
                launcher.launch(intent);
            }
        });

        Log.d(TAG, "onCreate: " + shareCode + ", " + DMName + ", " + dungeonName);

        // TODO: Finish launcher callback code
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d(TAG, "onActivityResult: ");
                Intent intent = result.getData();
                if (intent != null) {
                    if (intent.hasExtra("new") && intent.hasExtra("room")) {
                        DungeonRoom room = intent.getParcelableExtra("room");
                        if (intent.getBooleanExtra("new", false)) {
                            // is a new room
                            firebaseDungeonHelper.addNewRoom(room);
                        } else {
                            // updating a room
                            firebaseDungeonHelper.updateRoom(room, intent.getStringExtra("databaseKey"));
                        }
                    }
                }
            }
        });

        dungeonRoomReference = firebaseDungeonHelper.getDungeonRoomsRef();
        dungeonRoomReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                dungeonRooms.add(new Pair<>(snapshot.getValue(DungeonRoom.class), snapshot.getKey()));
                // Notify Adapter
                Log.d(TAG, "onChildAdded: " + dungeonRooms.toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String updatedKey = snapshot.getKey();
                for (Pair<DungeonRoom, String> room: dungeonRooms) {
                    if (room.second.equals(updatedKey)) {
                        DungeonRoom updatedRoom = snapshot.getValue(DungeonRoom.class);
                        assert updatedRoom != null;
                        room.first.updateValues(updatedRoom);
                        // notify adapter
                        return;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String removedKey = snapshot.getKey();
                for (Pair<DungeonRoom, String> room: dungeonRooms) {
                    if (room.second.equals(removedKey)) {
                        dungeonRooms.remove(room);
                        // notify adapter
                        return;
                    }
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}