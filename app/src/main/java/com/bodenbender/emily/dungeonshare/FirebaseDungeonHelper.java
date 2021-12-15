package com.bodenbender.emily.dungeonshare;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FirebaseDungeonHelper {
    public static final String TAG = "FBDHelperTag";
    String shareCode;
    FirebaseDatabase db;
    // just references messages portion of the database

    private DatabaseReference activeDungeonRef;
    private DatabaseReference playersInDungeonRef;
    private DatabaseReference dungeonRoomsRef;


    public FirebaseDungeonHelper(String shareCode, Dungeon dungeon) {
        this.shareCode = shareCode;
        db = FirebaseDatabase.getInstance();
        initDatabaseReferences(shareCode, dungeon);
    }

    /**
     * Returns a dungeon key to the database when party is done using it
     * NOTE: Should set key reference to null after calling this function
     *
     * @param shareCode String share_key for the finished dungeon
     */
    public static void returnDungeonKey(String shareCode) {
        if (shareCode != null) {
            FirebaseDatabase.getInstance().getReference("available_dungeon_keys").push().setValue(shareCode);
        }
    }


    /**
     * Sets all specific dungeon attributes to null
     * @param shareCode
     */
    public static void destroyDungeon(String shareCode) {
        if (shareCode != null) {
            returnDungeonKey(shareCode);
            FirebaseDatabase.getInstance().getReference("active_dungeons").child(shareCode).setValue(null);
            Log.d(TAG, "destroyDungeon: " + shareCode);
            FirebaseDatabase.getInstance().getReference("players_in_dungeon").child(shareCode).setValue(null);
            Log.d(TAG, "destroyDungeon: " + shareCode);
            FirebaseDatabase.getInstance().getReference("dungeon_rooms").child(shareCode).setValue(null);
            Log.d(TAG, "destroyDungeon: " + shareCode);
        }
    }

    /**
     * Set up database record for new dungeon with empty data to be filled in later. Called after
     * selecting to make a new Dungeon-Share
     *
     * @param shareCode Dungeon Key for room identification
     */
    public void initDatabaseReferences(String shareCode, Dungeon dungeon) {
        // Set up active_dungeon
        activeDungeonRef = db.getReference("/active_dungeons").child(shareCode);
        activeDungeonRef.setValue(dungeon);
        // Set up players_in_dungeon
        playersInDungeonRef = db.getReference("/players_in_dungeon").child(shareCode);
        // Set up dungeon_rooms
        dungeonRoomsRef = db.getReference("/dungeon_rooms").child(shareCode);
    }

    public DatabaseReference getActiveDungeonRef() {
        return activeDungeonRef;
    }

    public DatabaseReference getPlayersInDungeonRef() {
        return playersInDungeonRef;
    }

    public DatabaseReference getDungeonRoomsRef() {
        return dungeonRoomsRef;
    }

    public void addNewRoom(DungeonRoom room) {
        dungeonRoomsRef.push().setValue(room);
    }

    public void updateRoom(DungeonRoom room, String roomKey) {
        dungeonRoomsRef.child(roomKey).setValue(room);
    }

    public void deleteRoom(String databaseKey) {
        dungeonRoomsRef.child(databaseKey).setValue(null);
    }
}
