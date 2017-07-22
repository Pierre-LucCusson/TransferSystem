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

import com.google.gson.Gson;

import java.io.IOException;

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
        Log.d("friendsContactsInJson", friendsContactsInJson);

        if (friendsContactsInJson != null) {
            friendsContacts.deleteAllContacts();
            friendsContacts.saveContactsWithJson(friendsContactsInJson);
        }

        OrderContacts orderContacts = new OrderContacts(this, FriendsContactsActivity.this, FriendsContactsActivity.class, friendsContacts.getAllContacts(), getIntent().getStringExtra("EXTRA_ORDER_BY"));
        orderContacts.setOrderButtons();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_list, friendsContacts.getAllContactsToString());

        ListView list = (ListView) findViewById(R.id.contacts_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;
                Contact contact = new Gson().fromJson(textView.getText().toString(), Contact.class);
                String msg = "itemPosition=" + position + " deviceID=" + contact.getId() + " iPaddresse=" + contact.getIp();
                Toast.makeText(FriendsContactsActivity.this, msg, Toast.LENGTH_LONG).show();

                ClientLP client = new ClientLP();
                try {
                    Log.d("messageNFC clientID", contact.getId());
                    String response = client.getFriend(friendsIpAdress + ":8080", contact.getId());
                    Intent nfcReceiverIntent = new Intent(FriendsContactsActivity.this, NFCBeamReceiverActivity.class);
                    startActivity(nfcReceiverIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    private String getFriendsContactsInJson() {
        ClientLP client = new ClientLP();
        try {
            String response = client.listFriend(friendsIpAdress + ":8080");
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        //return "[{\"id\":\"friend3\",\"ip\":\"123456\"},{\"id\":\"friend2\",\"ip\":\"123456\"},{\"id\":\"friend1\",\"ip\":\"123456\"}]";
    }
}
