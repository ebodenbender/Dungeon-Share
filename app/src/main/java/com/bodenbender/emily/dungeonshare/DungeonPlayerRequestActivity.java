/**
 * Activity for player to input name and share code. Issues a request to the DM to approve or deny
 * their entry to the dungeon.
 */

package com.bodenbender.emily.dungeonshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DungeonPlayerRequestActivity extends AppCompatActivity
{
    static final String TAG = "PlayerRequestActivityTag";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference playersInDungeonRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeon_player_request);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        playersInDungeonRef = mFirebaseDatabase.getReference("/players_in_dungeon");

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
                    Intent intent = getIntent();
                    String shareCode = intent.getStringExtra("shareCode");
                    String playerName = playerNameEditText.getText().toString();
                    String inputShareCode = shareCodeEditText.getText().toString();
                    if (inputShareCode.equals(shareCode))
                    {
                        playersInDungeonRef.child(inputShareCode).child(playerName).setValue(false);
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