package com.bodenbender.emily.dungeonshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class NewRoomActivity extends AppCompatActivity {
    public static final String TAG = "NewRoomActivityTag";
    EditText nameEditText;
    CheckBox visibleCheckBox;
    EditText descriptionEditText;
    Button doneButton;
    String databaseKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);

        nameEditText = findViewById(R.id.editTextRoomName);
        visibleCheckBox = findViewById(R.id.checkBoxVisibleToPlayers);
        descriptionEditText = findViewById(R.id.editTextRoomDescription);
        doneButton = findViewById(R.id.doneButton);

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

        DungeonRoom finalDungeonRoom = dungeonRoom;
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (finalDungeonRoom == null) {
                    // mark as a new DungeonRoom
                    intent.putExtra("new", true);
                } else {
                    intent.putExtra("databaseKey", databaseKey);
                    intent.putExtra("new", false);
                }
                intent.putExtra("room", new DungeonRoom(nameEditText.getText().toString(), descriptionEditText.getText().toString(), visibleCheckBox.isChecked()));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}