package com.bestlis.playeriotestclient;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Debug;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bestlis.playeriotestclient.databinding.ActivityMainBinding;
import com.playerio.Callback;
import com.playerio.Client;
import com.playerio.Connection;
import com.playerio.PlayerIO;
import com.playerio.PlayerIOError;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private Button btn;
    private TextView tvStatus;

    private Client cl;
    private Connection con;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.btnConnectServer);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToServer();
            }
        });
        tvStatus = findViewById(R.id.tvStatus);
    }

    boolean isConnecting = false;
    void connectToServer()
    {
        if(isConnecting)
            return;
        isConnecting = true;

        Log.v(TAG, "connecting to server...");
        tvStatus.append("Connecting to server...\n");
        HashMap<String, String> authenticationArguments = new HashMap<String, String>();
        authenticationArguments.put("userId", "start_user");

        PlayerIO.setUseSecureApiRequests(true);
        PlayerIO.authenticate(
                this,                                  //Your main Activity
                "testipv6project-ps3f0ciutewzfi42eodywa",           //Your game id
                "public",                              //Your connection id
                authenticationArguments,               //Authentication arguments
                null,                                  //PlayerInsight segments
                new Callback<Client>() {
                    public void onSuccess(Client client) {
                        Log.v(TAG, "server connected!");
                        tvStatus.append("Server connected!\n");

                        cl = client;

                        createRoom((client));
                    }
                    public void onError(PlayerIOError error) {
                        Log.v(TAG, "Connection failed! " + error.toString());
                        tvStatus.append("Server connection failed: " + error.toString() + "\n");
                        isConnecting = false;
                    }
                }
        );
    }

    void  createRoom(Client client)
    {
        Log.v(TAG, "connecting to room...");
        tvStatus.append("Connecting to room...\n");
        //Join a multiplayer room
        client.multiplayer.createJoinRoom(
                "START_ROOM",
                "StartRoom",
                true,
                null,
                null,
                new Callback<Connection>() {
                    public void onSuccess(Connection connection) {
                        //Success!
                        Log.v(TAG, "room connected: " + client.getConnectUserId());
                        tvStatus.append("Room connected!\n");
                        isConnecting = false;
                        con = connection;
                    }
                    public void onError(PlayerIOError error) {
                        //Error connecting
                        Log.v(TAG, "Room connection failed! " + error.toString());
                        tvStatus.append("Room connection failed: " + error.toString() + "\n");
                        isConnecting = false;
                    }
                }
        );
    }



    @Override
    protected void onDestroy() {
        if(con != null)
            con.disconnect();
        Log.v(TAG, "Disconnected");
        super.onDestroy();

    }
}