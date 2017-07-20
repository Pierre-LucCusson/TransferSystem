package ets.transfersystem;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    ServerLP serverLP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        serverLP = new ServerLP(new Contacts(getSharedPreferences(Contacts.contactID, 0)), this);

        try {
            serverLP.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Show My QR button
        final Button myQrButton = (Button) findViewById(R.id.myQrButton);
        myQrButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                showMyQr(view);
            }
        } );

        //Show My QR Camera button
        final Button cameraQrButton = (Button) findViewById(R.id.cameraQrButton);
        cameraQrButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                openQrCamera(view);
            }
        } );

        //Show My Contacts button
        final Button contactsButton = (Button) findViewById(R.id.contactsButton);
        contactsButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                showContacts(view);
            }
        } );

        String myCurrentFriendInfo = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND");

        //My Current Friend
        final TextView myCurrentFriend = (TextView) findViewById(R.id.myCurrentFriendText);

        //Show Friends contacts
        final Button friendContactsButton = (Button) findViewById(R.id.myCurrentFriendContactsButton);
        friendContactsButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                showFriendsContacts(view);
            }
        } );

        //Show Friends contacts
        final Button nfcButton = (Button) findViewById(R.id.nfcButton);
        nfcButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                openNfc(view);
            }
        } );

        //Delete this friend
        final Button deleteFriendButton = (Button) findViewById(R.id.deleteFriendButton);
        deleteFriendButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                deleteThisFriend(view);
            }
        } );

        //Change current UI
        if(myCurrentFriendInfo == null) {
            myCurrentFriend.setText("Please select a contact.");
            friendContactsButton.setVisibility(View.INVISIBLE);
            nfcButton.setVisibility(View.INVISIBLE);
            deleteFriendButton.setVisibility(View.INVISIBLE);
        }
        else {
            myCurrentFriend.setText(myCurrentFriendInfo);
            friendContactsButton.setVisibility(View.VISIBLE);
            nfcButton.setVisibility(View.VISIBLE);
            deleteFriendButton.setVisibility(View.VISIBLE);
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
}
