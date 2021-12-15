package com.bodenbender.emily.dungeonshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DungeonHostDetailActivity extends AppCompatActivity {
    public static final String TAG = "DungeonHostDetailTag";
    private FirebaseDatabase mFirebaseDatabase;
    private String shareCode;
    private ChildEventListener shareCodeListener;
    private DatabaseReference dungeonKeyReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeon_host_detail);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        EditText DMNameEditText = findViewById(R.id.editTextDMName);
        EditText DungeonNameEditText = findViewById(R.id.editTextDungeonName);
        Button continueButton = findViewById(R.id.continueButton);

        // Attach listener to button to start DungeonHostActivity

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shareCode == null) {
                    Toast.makeText(DungeonHostDetailActivity.this, "Share Code Available", Toast.LENGTH_SHORT).show();
                } else {
                    String finalShareCode = shareCode;
                    // Ensure both EditTexts are filled
                    if (DMNameEditText.getText().toString().isEmpty() || DungeonNameEditText.getText().toString().isEmpty()) {
                        Toast.makeText(DungeonHostDetailActivity.this, "Please Fill All Names", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(DungeonHostDetailActivity.this, DungeonHostActivity.class);
                        intent.putExtra("shareCode", shareCode);
                        intent.putExtra("DMName", DMNameEditText.getText().toString());
                        intent.putExtra("DungeonName", DungeonNameEditText.getText().toString());
                        shareCode = null;
                        startActivity(intent);
                    }
                }
            }
        });

        shareCodeListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (shareCode == null && snapshot.getKey() != null) {
                    shareCode = snapshot.getValue(String.class);
                    mFirebaseDatabase.getReference().child("available_dungeon_keys").child(snapshot.getKey()).setValue(null);
                }
                dungeonKeyReference.removeEventListener(shareCodeListener);
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
                Log.d(TAG, "onCancelled: shareCodeListener was cancelled");
            }
        };

        dungeonKeyReference = mFirebaseDatabase.getReference("available_dungeon_keys");
        dungeonKeyReference.addChildEventListener(shareCodeListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.dungeon_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.deleteDungeonMenuItem) {
            FirebaseDungeonHelper.destroyDungeon(shareCode);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dungeonKeyReference.removeEventListener(shareCodeListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (shareCodeListener != null && shareCode == null) {
            dungeonKeyReference.addChildEventListener(shareCodeListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}