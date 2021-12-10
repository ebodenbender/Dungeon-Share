/**
 * Player view for dungeon share. After clicking "Look for Dungeon" button they'll be given a list
 * of dungeon/DM names, and prompted to enter a share code on clicking one.
 * Updates the display as visibility of rooms in the dungeon database changes.
 */

package com.bodenbender.emily.dungeonshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class LookForDungeonActivity extends AppCompatActivity
{
    static final String TAG = "LookForDungeonTag";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference activeDungeonsReference;
    private DatabaseReference dungeonRoomsReference;

    private List<Dungeon> activeDungeonsList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_for_dungeon);

        activeDungeonsList = new ArrayList<>();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        activeDungeonsReference = mFirebaseDatabase.getReference("/active_dungeons");
        dungeonRoomsReference = mFirebaseDatabase.getReference("/dungeon_rooms");

        // TODO set up recycler view to populate with rooms in dungeon

        ChildEventListener activeDungeonsCEL = new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                //activeDungeonsList.add(snapshot.getValue(Dungeon.class)); // TODO "No setter/field for DM_name found on class com.bodenbender.emily.dungeonshare.Dungeon"
                for (DataSnapshot child : snapshot.getChildren()) // cycles through each unique key to get data for all active dungeons
                {
                    activeDungeonsList.add(child.getValue(Dungeon.class));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        };
        activeDungeonsReference.addChildEventListener(activeDungeonsCEL);
        // TODO once we connect we can detatch this listener

        for (Dungeon dungeon : activeDungeonsList)
        {
            Log.d(TAG, "instance initializer: " + dungeon.toString());
        }
    }

    // TODO set child event listener for rooms as well
}