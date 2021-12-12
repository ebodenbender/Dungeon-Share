package com.bodenbender.emily.dungeonshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DungeonHostDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeon_host_detail);
        String shareKey = null;

        // Get intent from MainActivity
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("shareKey")) {
                shareKey = intent.getStringExtra("shareKey");
            } else {
                Toast.makeText(this, "ERROR: No Share Key Available", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "ERROR: No Intent Found", Toast.LENGTH_SHORT).show();
            finish();
        }

        EditText DMNameEditText = findViewById(R.id.editTextDMName);
        EditText DungeonNameEditText = findViewById(R.id.editTextDungeonName);
        Button continueButton = findViewById(R.id.continueButton);

        // Attach listener to button to start DungeonHostActivity
        String finalShareKey = shareKey;
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass data from fields to DungeonHostActivity with Share Code

                // Ensure both EditTexts are filled
                if (DMNameEditText.getText().toString().isEmpty() || DungeonNameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(DungeonHostDetailActivity.this, "Please Fill All Names", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(DungeonHostDetailActivity.this, HostDungeonActivity.class);
                    intent.putExtra("shareKey", finalShareKey);
                    intent.putExtra("DMName", DMNameEditText.getText().toString());
                    intent.putExtra("DungeonName", DungeonNameEditText.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
}