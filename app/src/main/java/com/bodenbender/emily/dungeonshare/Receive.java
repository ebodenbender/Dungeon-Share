package com.bodenbender.emily.dungeonshare;

import androidx.appcompat.app.AppCompatActivity;
import static java.nio.charset.StandardCharsets.UTF_8;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

public class Receive extends AppCompatActivity {
    public static final String TAG = "ReceiveActivityTag";
    private static final Strategy STRATEGY = Strategy.P2P_STAR;
    // Our handle to Nearby Connections
    private ConnectionsClient connectionsClient;
    private final String deviceName = "receiver";
    private String otherDeviceName;
    private String otherDeviceEndpoint;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        textView = findViewById(R.id.textView);

        connectionsClient = Nearby.getConnectionsClient(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        startDiscovery();
    }

    /** Starts looking for other players using Nearby Connections. */
    private void startDiscovery() {
        // Note: Discovery may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.startDiscovery(
                getPackageName(), endpointDiscoveryCallback,
                new DiscoveryOptions.Builder().setStrategy(STRATEGY).build());
    }

    // Callbacks for receiving payloads
    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    textView.setText(new String(payload.asBytes(), UTF_8));
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {

                    }
                }
            };


    // Callbacks for finding other devices
    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    Log.i(TAG, "onEndpointFound: endpoint found, connecting");
                    connectionsClient.requestConnection(deviceName, endpointId, connectionLifecycleCallback);
                }

                @Override
                public void onEndpointLost(String endpointId) {}
            };

    // Callbacks for connections to other devices
    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(TAG, "onConnectionInitiated: accepting connection");
                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                    otherDeviceName = connectionInfo.getEndpointName();
                    Toast.makeText(Receive.this, "Connection Initiated", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Log.i(TAG, "onConnectionResult: connection successful");

                        connectionsClient.stopDiscovery();
                        connectionsClient.stopAdvertising();

                        otherDeviceEndpoint = endpointId;
                        Toast.makeText(Receive.this, "Connected to " + otherDeviceEndpoint, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "onConnectionResult: connection failed");
                        Toast.makeText(Receive.this, "Connection to " + otherDeviceEndpoint + " failed.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.i(TAG, "onDisconnected: disconnected from the opponent");
                    Toast.makeText(Receive.this, "You Were Disconnected", Toast.LENGTH_SHORT).show();
                }
            };
}