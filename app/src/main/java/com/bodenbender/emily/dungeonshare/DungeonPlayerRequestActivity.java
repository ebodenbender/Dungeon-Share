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

public class DungeonPlayerRequestActivity extends AppCompatActivity
{
    static final String TAG = "PlayerRequestActivityTag";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeon_player_request);

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
                            "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // TODO check share code, if incorrect make a toast and clear the edit text,
                    // TODO if share code is correct then send a request to DM
                }
            }
        });
    }
}