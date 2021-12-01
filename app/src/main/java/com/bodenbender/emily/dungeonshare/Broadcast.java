package com.bodenbender.emily.dungeonshare;

import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

public class Broadcast extends AppCompatActivity
{
    private String otherDevice;
    ConnectionsClient connectionsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        connectionsClient = Nearby.getConnectionsClient(this);
        startAdvertising();

        Button broadcastButton = findViewById(R.id.button);
        EditText editText = findViewById(R.id.textInputEditText);
        broadcastButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String message = editText.getText().toString();
                connectionsClient.sendPayload(otherDevice, Payload.fromBytes(message.getBytes(UTF_8)));
            }
        });
    }

    private void startAdvertising()
    {
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        connectionsClient.startAdvertising("device name", getPackageName(), connectionLifecycleCallback, advertisingOptions);
    }

    private final PayloadCallback payloadCallback = new PayloadCallback()
    {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload)
        {
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate payloadTransferUpdate)
        {

        }
    };

    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback()
    {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo)
        {
            connectionsClient.requestConnection("device name", endpointId, connectionLifecycleCallback);
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId)
        {

        }
    };

    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback()
    {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo)
        {
            connectionsClient.acceptConnection(endpointId, payloadCallback);
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution)
        {
            connectionsClient.stopAdvertising();
            connectionsClient.stopDiscovery();

            otherDevice = endpointId;
        }

        @Override
        public void onDisconnected(@NonNull String endpointId)
        {

        }
    };
}