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
    private FirebaseDatabase fdb;
    // just references messages portion of the database
    private DatabaseReference dungeonKeyReference;
    private List<DungeonKey> availableDungeonKeys;
    private ChildEventListener keyListener;

    private KeyRequester keyRequester;

    private DatabaseReference activeDungeonRef;
    private DatabaseReference playersInDungeonRef;
    private DatabaseReference dungeonRoomsRef;

    class DungeonKey {
        private String id;
        private String key;

        DungeonKey(String id, String key) {
            this.id = id;
            this.key = key;
        }

        @NonNull
        @Override
        public String toString() {
            return "(" + id + "," + key + ")";
        }

        public String getId() {
            return id;
        }

        public String getKey() {
            return key;
        }
    }



    public FirebaseDungeonHelper(FirebaseDatabase fdb) {
        this.fdb = fdb;
        dungeonKeyReference = this.fdb.getReference("/available_dungeon_keys");

        availableDungeonKeys = new ArrayList<>();
        addKeyListener();
    }

    public void removeKeyListener() {
        if (keyListener != null) {
            dungeonKeyReference.removeEventListener(keyListener);
        }
    }

    public void addKeyListener() {
        keyListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                availableDungeonKeys.add(new DungeonKey(snapshot.getKey(), snapshot.getValue(String.class)));
                Log.d(TAG, "onChildAdded: " + availableDungeonKeys);
                notifyKeyAvailable();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (DungeonKey dk: availableDungeonKeys) {
                    if (dk.id.equals(snapshot.getKey())) {
                        availableDungeonKeys.remove(dk);
                    }
                }
                Log.d(TAG, "onChildAdded: " + availableDungeonKeys);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dungeonKeyReference.addChildEventListener(keyListener);
    }

    /**
     * Gets an available dungeon key from Firebase, removes the key from Firebase and returns it
     * @return available DungeonKey object
     */
    public void requestAvailableDungeonKey(KeyRequester keyRequester) {
        if (!availableDungeonKeys.isEmpty()) {
            notifyKeyAvailable();
        }
        this.keyRequester = keyRequester;
    }

    public void removeKeyRequester() {
        this.keyRequester = null;
    }

    private void notifyKeyAvailable() {
        if (keyRequester != null) {
            // get the dungeon key
            DungeonKey dk = null;
            if (!availableDungeonKeys.isEmpty()) {
                dk = availableDungeonKeys.remove(0);
                Log.d(TAG, "notifyKeyAvailable: Sending key " + dk.key);
                // remove dungeon key from database

                /****************************
                 // NOT REMOVING KEYS DURING TESTING

                 // dungeonKeyReference.child(dk.id).removeValue();
                 */

            }
            keyRequester.onKeyAvailable(dk);
        }
    }

    /**
     * Returns a dungeon key to the database when party is done using it
     * NOTE: Should set key reference to null after calling this function
     *
     * @param dk String share_key for the finished dungeon
     */
    public void returnDungeonKey(DungeonKey dk) {
        if (dk != null) {
            dungeonKeyReference.push().setValue(dk.key);
        }
    }

    /**
     * Set up database record for new dungeon with empty data to be filled in later. Called after
     * selecting to make a new Dungeon-Share
     *
     * @param dk Dungeon Key for room identification
     */
    public void initDatabaseReferences(DungeonKey dk) {
        // Set up active_dungeon
        activeDungeonRef = fdb.getReference("/active_dungeons").child(dk.key);
        // Set up players_in_dungeon
        playersInDungeonRef = fdb.getReference("/players_in_dungeon").child(dk.key);
        // Set up dungeon_rooms
        dungeonRoomsRef = fdb.getReference("/dungeon_rooms").child(dk.key);
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
}
