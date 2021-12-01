package com.bodenbender.emily.dungeonshare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.DiscoveryOptions;

public class Receive extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
    }
}