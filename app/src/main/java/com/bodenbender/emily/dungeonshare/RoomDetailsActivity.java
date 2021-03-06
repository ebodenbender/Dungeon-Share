package com.bodenbender.emily.dungeonshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class RoomDetailsActivity extends AppCompatActivity {
    public static final String TAG = "NewRoomActivityTag";
    EditText nameEditText;
    CheckBox visibleCheckBox;
    EditText descriptionEditText;
    Button doneButton;
    String databaseKey;
    CheckBox deleteCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        nameEditText = findViewById(R.id.editTextRoomName);
        visibleCheckBox = findViewById(R.id.checkBoxVisibleToPlayers);
        descriptionEditText = findViewById(R.id.editTextRoomDescription);
        doneButton = findViewById(R.id.doneButton);
        deleteCheckBox = findViewById(R.id.deleteCheckBox);

        DungeonRoom dungeonRoom = null;

        Intent intent = getIntent();
        if (intent == null) {
            Log.d(TAG, "onCreate: No Intent Found");
        } else {
            if (intent.hasExtra("room")) {
                dungeonRoom = intent.getParcelableExtra("room");
                nameEditText.setText(dungeonRoom.getRoom_name());
                visibleCheckBox.setChecked(dungeonRoom.isVisible_to_players());
                descriptionEditText.setText(dungeonRoom.getRoom_description());

                databaseKey = intent.getStringExtra("databaseKey");
            } else {
                visibleCheckBox.setChecked(false);
            }
        }

        if (dungeonRoom == null) {
            deleteCheckBox.setVisibility(View.GONE);
        }

        DungeonRoom finalDungeonRoom = dungeonRoom;
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (finalDungeonRoom == null) {
                    // mark as a new DungeonRoom
                    intent.putExtra("new", true);
                } else {
                    intent.putExtra("delete", deleteCheckBox.isChecked());
                    intent.putExtra("databaseKey", databaseKey);
                    intent.putExtra("new", false);
                }
                intent.putExtra("room", new DungeonRoom(nameEditText.getText().toString(), descriptionEditText.getText().toString(), visibleCheckBox.isChecked(), -1));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}