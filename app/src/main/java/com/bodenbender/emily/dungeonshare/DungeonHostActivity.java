package com.bodenbender.emily.dungeonshare;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DungeonHostActivity extends AppCompatActivity {
    // TODO: Listen for players

    public static final String TAG = "HostDungeonActivityTag";

    private String shareCode;
    private String DMName;
    private String dungeonName;

    private FirebaseDatabase db;
    private FirebaseDungeonHelper firebaseDungeonHelper;
    private DatabaseReference dungeonRoomReference;

    private DatabaseReference dungeonPlayerReference;
    private ChildEventListener playerListener;

    private List<Pair<DungeonRoom, String>> dungeonRooms;

    private RecyclerView roomRecyclerView;
    private RoomAdapter adapter;


    ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_dungeon);

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

        TextView shareCodeTextView = findViewById(R.id.shareCodeTextView);
        shareCodeTextView.setText(dungeonName + ": " + shareCode);
        roomRecyclerView = findViewById(R.id.roomRecyclerView);
        Button addRoomButton = findViewById(R.id.addRoomButton);
        addRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DungeonHostActivity.this, RoomDetailsActivity.class);
                launcher.launch(intent);
            }
        });

        dungeonRooms = new ArrayList<>();
        adapter = new RoomAdapter(this, new RoomAdapter.DungeonRoomList() {
            @Override
            public DungeonRoom getDungeonRoomAt(int position) {
                return dungeonRooms.get(position).first;
            }

            @Override
            public int getDungeonSize() {
                return dungeonRooms.size();
            }
        });

        adapter.setClickListener(new RoomAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(DungeonHostActivity.this, RoomDetailsActivity.class);
                intent.putExtra("room", dungeonRooms.get(position).first);
                intent.putExtra("databaseKey", dungeonRooms.get(position).second);
                launcher.launch(intent);
            }
        });
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomRecyclerView.setAdapter(adapter);
        Log.d(TAG, "onCreate: " + shareCode + ", " + DMName + ", " + dungeonName);

        // TODO: Finish launcher callback code
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d(TAG, "onActivityResult: ");
                Intent intent = result.getData();
                if (intent != null) {
                    if (intent.hasExtra("delete") && intent.getBooleanExtra("delete", false)) {
                        firebaseDungeonHelper.deleteRoom(intent.getStringExtra("databaseKey"));
                    } else if (intent.hasExtra("new") && intent.hasExtra("room")) {
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
                Pair<DungeonRoom, String> p = new Pair<>(snapshot.getValue(DungeonRoom.class), snapshot.getKey());
                dungeonRooms.add(p);
                // Notify Adapter
                adapter.notifyItemInserted(dungeonRooms.indexOf(p));
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
                        adapter.notifyItemChanged(dungeonRooms.indexOf(room));
                        return;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String removedKey = snapshot.getKey();
                for (Pair<DungeonRoom, String> room: dungeonRooms) {
                    if (room.second.equals(removedKey)) {
                        int itemIndex = dungeonRooms.indexOf(room);
                        dungeonRooms.remove(room);
                        adapter.notifyItemRemoved(itemIndex);
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

        playerListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String playerName = snapshot.getKey();
                DatabaseReference playerRef = db.getReference("players_in_dungeon").child(shareCode).child(playerName);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DungeonHostActivity.this);
                Log.d(TAG, "onChildAdded: Player Request to join");
                alertDialog.setTitle("Player Request to Join")
                        .setMessage(playerName + " is requesting to join" )
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "onClick: DM allowed entry for " + playerName);
                                playerRef.setValue(true);
                            }
                        })
                        .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "onClick: DM declined entry for " + playerName);
                                playerRef.setValue(null);
                            }
                        });
                alertDialog.show();

            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null && !snapshot.getValue(Boolean.class) ) {
                    String playerName = snapshot.getKey();
                    DatabaseReference playerRef = db.getReference("players_in_dungeon").child(shareCode).child(playerName);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(DungeonHostActivity.this);
                    Log.d(TAG, "onChildChanged: Player Request to join");
                    alertDialog.setTitle("Player Request to Join")
                            .setMessage(playerName + " is requesting to join")
                            .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d(TAG, "onClick: DM allowed entry for " + playerName);
                                    playerRef.setValue(true);
                                }
                            })
                            .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d(TAG, "onClick: DM declined entry for " + playerName);
                                    playerRef.setValue(null);
                                }
                            });
                    alertDialog.show();
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
                Log.d(TAG, "onCancelled: shareCodeListener was cancelled");
            }
        };

        dungeonPlayerReference = db.getReference("players_in_dungeon").child(shareCode);
        dungeonPlayerReference.addChildEventListener(playerListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.dungeon_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.deleteDungeonMenuItem) {
            FirebaseDungeonHelper.destroyDungeon(shareCode);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}