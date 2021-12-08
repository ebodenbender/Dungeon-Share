package com.bodenbender.emily.dungeonshare;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDungeonHelper {
    private FirebaseDatabase mFirebaseDatabase;
    // just references messages portion of the database
    private DatabaseReference mDatabaseReference;

    public FirebaseDungeonHelper(FirebaseDatabase fdb) {
        mFirebaseDatabase = fdb;
        mDatabaseReference = mFirebaseDatabase.getReference();
    }

    public String getAvailableRoomCode() {

    }
}
