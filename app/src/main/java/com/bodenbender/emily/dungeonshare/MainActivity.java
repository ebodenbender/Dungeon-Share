package com.bodenbender.emily.dungeonshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements KeyRequester {
    public final String TAG = "MainActivityTag";
    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private FirebaseDatabase mFirebaseDatabase;
    FirebaseDungeonHelper firebaseDungeonHelper;

    FirebaseDungeonHelper.DungeonKey dungeonKey;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDungeonHelper = new FirebaseDungeonHelper(mFirebaseDatabase);

        Button hostDungeonButton = findViewById(R.id.hostDungeonButton);
        Button lookForDungeonButton = findViewById(R.id.lookForDungeonButton);

        firebaseDungeonHelper.requestAvailableDungeonKey(this);

        hostDungeonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dungeonKey == null) {
                    Toast.makeText(MainActivity.this, "No Dungeon Keys Available", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, DungeonHostDetailActivity.class);
                    intent.putExtra("shareKey", dungeonKey.getKey());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseDungeonHelper.removeKeyListener();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (dungeonKey == null) {
            firebaseDungeonHelper.addKeyListener();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // TODO define a main_menu.xml file; inflate the menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /** Returns true if the app was granted all the permissions. Otherwise, returns false. */
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /** Handles user acceptance (or denial) of our permission request. */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return;
        }

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permissions Not Granted", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        recreate();
    }

    @Override
    public void onKeyAvailable(FirebaseDungeonHelper.DungeonKey dungeonKey) {
        this.dungeonKey = dungeonKey;
        firebaseDungeonHelper.removeKeyRequester();
        firebaseDungeonHelper.removeKeyListener();
    }
}