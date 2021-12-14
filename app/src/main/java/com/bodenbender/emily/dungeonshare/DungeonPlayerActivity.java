package com.bodenbender.emily.dungeonshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DungeonPlayerActivity extends AppCompatActivity
{   // TODO delete LookForDungeonActivity once this is starting to look right
    static final String TAG = "PlayerActivityTag";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference activeDungeonsReference;
    private List<Dungeon> activeDungeonsList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeon_player);
        // TODO get list of available dungeons
        // TODO start new activity to request to connect to dungeon (on dungeon click in the recycler view)
        // TODO put list of available dungeons in a recycler view

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        activeDungeonsReference = mFirebaseDatabase.getReference("/active_dungeons"); // TODO remove "RHE202" from path later once I can at least grab this one

        activeDungeonsList = new ArrayList<>();

        ChildEventListener activeDungeonsCEL = new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                Dungeon dungeon = snapshot.getValue(Dungeon.class);
                activeDungeonsList.add(dungeon);
                Log.d(TAG, "onChildAdded: " + dungeon.toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
        activeDungeonsReference.addChildEventListener(activeDungeonsCEL);
        // TODO we can detach this later
        for (int i = 0; i < activeDungeonsList.size(); i++) // TODO seems to grab the data in onChildAdded just fine, yet this for loop doesn't execute?
        {
            Log.d(TAG, "onCreate: " + activeDungeonsList.get(i).toString());
        }
    }
}