package ets.transfersystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Pierre-Luc on 2017-07-06.
 */

public class FriendsContactsActivity extends AppCompatActivity {

    private Contacts friendsContacts;
    private String friendsDeviceId;
    private String friendsIpAdress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        friendsDeviceId = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_ID");
        friendsIpAdress = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_IP");

        friendsContacts = new Contacts(getSharedPreferences(friendsDeviceId, 0));

        String friendsContactsInJson = getFriendsContactsInJson();

        if (friendsContactsInJson != null) {
            friendsContacts.deleteAllContacts();
            friendsContacts.saveContactsWithJson(friendsContactsInJson);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_list, friendsContacts.getAllContactsToString());

        ListView list = (ListView) findViewById(R.id.contacts_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;
                QrCode qrCode = new QrCode(textView.getText().toString());
                String msg = "itemPosition=" + position + " deviceID=" + qrCode.getDeviceId() + " iPaddresse=" + qrCode.getIpAddress();
                Toast.makeText(FriendsContactsActivity.this, msg, Toast.LENGTH_LONG).show();

                //TODO start NFC activity

//                Intent intent = new Intent(FriendsContactsActivity.this, MainActivity.class);
//                intent.putExtra("EXTRA_CURRENT_FRIEND", qrCode.getDeviceId() + ":" + qrCode.getIpAddress());
//                intent.putExtra("EXTRA_CURRENT_FRIEND_ID", qrCode.getDeviceId());
//                intent.putExtra("EXTRA_CURRENT_FRIEND_IP", qrCode.getIpAddress());
//                startActivity(intent);

            }
        });

    }

    private String getFriendsContactsInJson() {
        //TODO GET contacts with server, return null if no response
        return "[{\"id\":\"friend3\",\"ip\":\"123456\"},{\"id\":\"friend2\",\"ip\":\"123456\"},{\"id\":\"friend1\",\"ip\":\"123456\"}]";
    }
}
