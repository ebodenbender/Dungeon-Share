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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DungeonPlayerActivity extends AppCompatActivity
{
    static final String TAG = "PlayerActivityTag";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference activeDungeonsReference;
    private List<Pair<Dungeon, String>> activeDungeons;

    private RecyclerView activeDungeonsRecyclerView;
    private DungeonAdapter adapter; // adapter for recycler view

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeon_player);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        activeDungeonsReference = mFirebaseDatabase.getReference("/active_dungeons");

        activeDungeons = new ArrayList<>();

        // set adapter for recycler view
        activeDungeonsRecyclerView = findViewById(R.id.availableDungeonsRecyclerView);
        adapter = new DungeonAdapter(this, new DungeonAdapter.DungeonList()
        {
            @Override
            public Dungeon getDungeonAt(int position)
            {
                return activeDungeons.get(position).first;
            }

            @Override
            public int getDungeonSize()
            {
                return activeDungeons.size();
            }
        });

        adapter.setClickListener(new DungeonAdapter.ItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                // launch activity to request player details (to send to DM for approval)
                Intent intent = new Intent(DungeonPlayerActivity.this, DungeonPlayerRequestActivity.class);
                intent.putExtra("shareCode", activeDungeons.get(position).first.getShare_code());
                startActivity(intent);
            }
        });

        activeDungeonsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activeDungeonsRecyclerView.setAdapter(adapter);

        ChildEventListener activeDungeonsCEL = new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                Pair<Dungeon, String> dungeon = new Pair<>(snapshot.getValue(Dungeon.class), snapshot.getKey());
                activeDungeons.add(dungeon);
                adapter.notifyItemInserted(activeDungeons.indexOf(dungeon));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                Pair<Dungeon, String> updatedDungeon = new Pair<>(snapshot.getValue(Dungeon.class), snapshot.getKey());
                String key = updatedDungeon.second;
                int i = 0;
                for (Pair<Dungeon, String> dungeon : activeDungeons)
                {
                    if (key.equals(dungeon.second)) // using key for indexing the list since share code will never change
                    {
                        activeDungeons.set(i, updatedDungeon);
                        adapter.notifyItemChanged(activeDungeons.indexOf(updatedDungeon));
                    }
                    i++;
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // TODO implement this
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

    // using this method for debugging
    public List<Pair<Dungeon, String>> printActiveDungeonsList()
    {
        for (int i = 0; i < activeDungeons.size(); i++)
        {
            Log.d(TAG, "printActiveDungeonsList: " + activeDungeons.get(i).first.toString());
        }
        return activeDungeons;
    }
}