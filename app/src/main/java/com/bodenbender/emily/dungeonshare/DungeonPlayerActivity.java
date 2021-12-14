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
        activeDungeonsReference = mFirebaseDatabase.getReference("/active_dungeons");

        activeDungeonsList = new ArrayList<>();

        ChildEventListener activeDungeonsCEL = new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                Dungeon dungeon = snapshot.getValue(Dungeon.class);
                activeDungeonsList.add(dungeon);
                printActiveDungeonsList(); // TODO remove this when no longer needed for debugging
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                Dungeon updatedDungeon = snapshot.getValue(Dungeon.class);
                String shareCode = updatedDungeon.getShare_code();
                int i = 0;
                for (Dungeon dungeon : activeDungeonsList)
                {
                    if (shareCode.equals(dungeon.getShare_code())) // using share code for indexing the list since share code will never change
                    {
                        activeDungeonsList.set(i, updatedDungeon);
                    }
                    i++;
                }
                printActiveDungeonsList(); // TODO remove this when no longer needed for debugging
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
    }

    public List<Dungeon> printActiveDungeonsList() // TODO remove this later; just using it to debug
    {
        for (int i = 0; i < activeDungeonsList.size(); i++)
        {
            Log.d(TAG, "printActiveDungeonsList: " + activeDungeonsList.get(i).toString());
        }
        return activeDungeonsList;
    }
}