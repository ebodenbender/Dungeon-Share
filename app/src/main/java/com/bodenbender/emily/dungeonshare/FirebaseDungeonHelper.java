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

    List<DungeonKey> availableDungeonKeys;

    public FirebaseDungeonHelper(FirebaseDatabase fdb) {
        this.fdb = fdb;
        dungeonKeyReference = fdb.getReference("/available_dungeon_keys");

        availableDungeonKeys = new ArrayList<>();

        dungeonKeyReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                availableDungeonKeys.add(new DungeonKey(snapshot.getKey(), snapshot.getValue(String.class)));
                Log.d(TAG, "onChildAdded: " + availableDungeonKeys);
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
        });

    }

    /**
     * Gets an available dungeon key from Firebase, removes the key from Firebase and returns it
     * @return available DungeonKey object
     */
    public DungeonKey getAvailableDungeonKey() {
        // get the dungeon key
        if (!availableDungeonKeys.isEmpty()) {
            DungeonKey dk = availableDungeonKeys.remove(0);
            // remove dungeon key from database
            dungeonKeyReference.child(dk.id).removeValue();
            return dk;
        } else {
            return null;
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

    public String addActiveDungeon(DungeonKey dk) {
        return "";
    }

}
