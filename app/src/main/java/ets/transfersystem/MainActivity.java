package ets.transfersystem;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.FileObserver;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import static ets.transfersystem.LocationHandler.calculateDistance;

public class MainActivity extends AppCompatActivity implements LocationListener {


    ServerLP serverLP;
    FolderObserver fo;
    LocationManager lo;
    String provider;
    LoopingThread longrunning;

    private void initFiles() {
        fo = new FolderObserver();
        serverLP = new ServerLP(new Contacts(getSharedPreferences(Contacts.contactID, 0)), this, fo);

        fo.startWatching();

        try {
            serverLP.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLocation() {
        lo = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = lo
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Missing permissions for location", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "Got permissions for location", Toast.LENGTH_LONG).show();
        }

        Location location = lo.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lo.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        onLocationChanged(location);
        serverLP.setLastLocation(location);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        initFiles();
        initLocation();

        //Show My QR button
        final Button myQrButton = (Button) findViewById(R.id.myQrButton);
        myQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyQr(view);
            }
        });

        //Show My QR Camera button
        final Button cameraQrButton = (Button) findViewById(R.id.cameraQrButton);
        cameraQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openQrCamera(view);
            }
        });

        //Show My Contacts button
        final Button contactsButton = (Button) findViewById(R.id.contactsButton);
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContacts(view);
            }
        });

        QrCode qrCode = new QrCode(this);

        //My deviceID
        final TextView myDeviceIdText = (TextView) findViewById(R.id.myDeviceIdText);
        myDeviceIdText.setText("My Device ID: " + qrCode.getDeviceId());

        //My IP address
        final TextView myIpAddressText = (TextView) findViewById(R.id.myIpAddressText);
        myIpAddressText.setText("My IP Address: " + qrCode.getIpAddress());

        String myCurrentFriendInfo = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND");

        //My Current Friend
        final TextView myCurrentFriend = (TextView) findViewById(R.id.myCurrentFriendText);

        //Show files
        final Button filesButton = (Button) findViewById(R.id.fileButton);
        filesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyFiles(view);
            }
        });

        //Show Friends contacts
        final Button friendContactsButton = (Button) findViewById(R.id.myCurrentFriendContactsButton);
        friendContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFriendsContacts(view);
            }
        });

        //Show Friends contacts
        final Button nfcButton = (Button) findViewById(R.id.nfcButton);
        nfcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNfc(view);
            }
        });

        //Delete this friend
        final Button deleteFriendButton = (Button) findViewById(R.id.deleteFriendButton);
        deleteFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteThisFriend(view);
            }
        });
        //Friend files
        final Button FriendFilesButton = (Button) findViewById(R.id.friendfilesButton);
        FriendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFriendFiles(view);
            }
        });


        //Change current UI
        if (myCurrentFriendInfo == null) {
            myCurrentFriend.setText("Please select a contact.");
            friendContactsButton.setVisibility(View.INVISIBLE);
            nfcButton.setVisibility(View.INVISIBLE);
            deleteFriendButton.setVisibility(View.INVISIBLE);
            FriendFilesButton.setVisibility(View.INVISIBLE);
        } else {
            myCurrentFriend.setText("My Current Friend: " + myCurrentFriendInfo);
            friendContactsButton.setVisibility(View.VISIBLE);
            nfcButton.setVisibility(View.VISIBLE);
            deleteFriendButton.setVisibility(View.VISIBLE);
            FriendFilesButton.setVisibility(View.VISIBLE);
        }


    }

    public void startLongRunning()
    {
        String myCurrentFriendInfo = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND");
        if (myCurrentFriendInfo != null)
        {
            if (longrunning != null)
            {
                longrunning.cancel(true);
            }
            longrunning = new LoopingThread(this);
            longrunning.execute(myCurrentFriendInfo.split(":")[1]);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            lo.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
            startLongRunning();

        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        lo.removeUpdates(this);
        if(longrunning != null)
        {
            longrunning.cancel(true);
        }
    }

    public void showMyQr(View view) {
        Button button = (Button) view;
        Log.d("ButtonClick", String.format("Show My QR button was click"));
        startActivity(new Intent(MainActivity.this, MyQrActivity.class));
    }

    public void openQrCamera(View view) {
        Button button = (Button) view;
        Log.d("ButtonClick", String.format("Open QR Camera button was click"));
        startActivity(new Intent(MainActivity.this, QrCameraActivity.class));
    }

    public void showContacts(View view) {
        Button button = (Button) view;
        Log.d("ButtonClick", String.format("Show My Contacts button was click"));
        startActivity(new Intent(MainActivity.this, ContactsActivity.class));
    }

    public void showMyFiles(View view) {
        Button button = (Button) view;
        Log.d("ButtonClick", String.format("Show My Files button was click"));
        startActivity(new Intent(MainActivity.this, FileActivity.class));
    }

    public void showFriendsContacts(View view) {
        Button button = (Button) view;
        Log.d("ButtonClick", String.format("Show My Friends Contacts button was click"));

        Intent intent = new Intent(MainActivity.this, FriendsContactsActivity.class);
        intent.putExtra("EXTRA_CURRENT_FRIEND", getIntent().getStringExtra("EXTRA_CURRENT_FRIEND"));
        intent.putExtra("EXTRA_CURRENT_FRIEND_ID", getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_ID"));
        intent.putExtra("EXTRA_CURRENT_FRIEND_IP", getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_IP"));
        startActivity(intent);
    }

    public void openNfc(View view) {
        Button button = (Button) view;
        Log.d("ButtonClick", String.format("Open NFC button was click"));

        //TODO move to folowing code the proper place when the server receive GET 00.00.00.00:8080/getfriend/2
        int position = 0; //TODO position = getfriend/position
        Contacts contacts = new Contacts(getSharedPreferences(Contacts.contactID, 0));
        String[] myContacts = contacts.getAllContactsToString();

        Intent nfcSenderIntent = new Intent(MainActivity.this, NFCBeamSenderActivity.class);
        String nfcMessageToSend = "confirm:" + myContacts[position];
        nfcSenderIntent.putExtra("EXTRA_NFC_MESSAGE_TO_SEND", nfcMessageToSend);
        startActivity(nfcSenderIntent);
    }

    public void deleteThisFriend(View view) {
        Button button = (Button) view;
        Log.d("ButtonClick", String.format("Delete This Friend button was click"));

        Contacts contacts = new Contacts(getSharedPreferences(Contacts.contactID, 0));
        contacts.deleteContact(getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_ID"));

        getIntent().removeExtra("EXTRA_CURRENT_FRIEND");
        getIntent().removeExtra("EXTRA_CURRENT_FRIEND_ID");
        getIntent().removeExtra("EXTRA_CURRENT_FRIEND_IP");
        startActivity(getIntent());
    }

    public void showFriendFiles(View view)
    {
        Button button = (Button) view;
        Log.d("ButtonClick", String.format("Show My Friends Contacts button was click"));

        Intent intent = new Intent(MainActivity.this, FriendFilesActivity.class);
        intent.putExtra("EXTRA_CURRENT_FRIEND", getIntent().getStringExtra("EXTRA_CURRENT_FRIEND"));
        intent.putExtra("EXTRA_CURRENT_FRIEND_ID", getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_ID"));
        intent.putExtra("EXTRA_CURRENT_FRIEND_IP", getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_IP"));
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("WWWWWWWWWWWWWWWWWWWWWWWWW", "onLocationChanged: WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
        serverLP.setLastLocation(location);
        String[] position = getFriendPosition();

        TextView LatitudeView = (TextView) findViewById(R.id.PositionText);
        LatitudeView.setText(position == null ? location.toString() : String.valueOf(calculateDistance(location, position[1], position[0])));
    }


    private String[] getFriendPosition()
    {
        ClientLP client = new ClientLP();
        String myCurrentFriendInfo = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND");
        if (myCurrentFriendInfo != null)
        {
            try {
                return client.getPosition(myCurrentFriendInfo.split(":")[1]).split("/");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverLP = null;
    }

    private class LoopingThread extends AsyncTask<String, Void, Integer>
    {
        ClientLP client;
        Context context;

        public LoopingThread(Context context)
        {
            client = new ClientLP();
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
                try {
                    String response = "";
                    do{
                        response = client.checkForFileChange(params[0]);
                        NotificationEvent notificationEvent = new Gson().fromJson(response, NotificationEvent.class);
                        Toast.makeText(context, String.format("%s : %s", notificationEvent.action.toUpperCase(), notificationEvent.filename),
                                Toast.LENGTH_LONG).show();
                    }while(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return null;
        }
    }
}
