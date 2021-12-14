/**
 * Activity for player to input name and share code. Issues a request to the DM to approve or deny
 * their entry to the dungeon.
 */

package com.bodenbender.emily.dungeonshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DungeonPlayerRequestActivity extends AppCompatActivity
{
    static final String TAG = "PlayerRequestActivity";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference playersInDungeonRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeon_player_request);

        Intent intent = getIntent();
        String shareCode = intent.getStringExtra("shareCode");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        playersInDungeonRef = mFirebaseDatabase.getReference("/players_in_dungeon");
        playersInDungeonRef = playersInDungeonRef.child(shareCode);

        EditText playerNameEditText = findViewById(R.id.editTextPlayerName);
        EditText shareCodeEditText = findViewById(R.id.editTextShareCode);

        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (playerNameEditText.getText().toString().isEmpty() ||
                        shareCodeEditText.getText().toString().isEmpty())
                {
                    Toast.makeText(DungeonPlayerRequestActivity.this,
                            "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String playerName = playerNameEditText.getText().toString();
                    String inputShareCode = shareCodeEditText.getText().toString();
                    if (inputShareCode.equals(shareCode))
                    {
                        playersInDungeonRef.child(playerName).setValue(false);
                        playersInDungeonRef.child(playerName).addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {

                                Boolean requestAccepted = snapshot.getValue(Boolean.class);
                                if (requestAccepted != null)
                                {
                                    if (requestAccepted)
                                    {
                                        Intent intent = new Intent(DungeonPlayerRequestActivity.this, DungeonPlayerDetailActivity.class);
                                        startActivity(intent);
                                    }
                                }
                                else
                                {
                                    Toast.makeText(DungeonPlayerRequestActivity.this,
                                            "Failed to join dungeon", Toast.LENGTH_SHORT).show();
                                    DungeonPlayerRequestActivity.this.finish();
                                    // TODO if player is removed from a dungeon after joining, they stay in the dungeon activity anyway
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(DungeonPlayerRequestActivity.this,
                                "Incorrect share code", Toast.LENGTH_SHORT).show();
                        shareCodeEditText.setText("");
                    }
                }
            }
        });
    }
}