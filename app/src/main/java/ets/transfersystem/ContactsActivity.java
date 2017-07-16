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
 * Created by Pierre-Luc on 2017-06-26.
 */

public class ContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Contacts contacts = new Contacts(getSharedPreferences("ContactsTest3", 0));
        Log.d("Contacts toJson", String.format(contacts.getAllContactsToJson()));



//        Contacts friendsContacts = new Contacts(contacts.getAllContactsToJson());
//        Log.d("Contacts fromJson", friendsContacts.getAllFriendsContacts()[1].getIp());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_list, contacts.getAllContactsToString());

        ListView list = (ListView) findViewById(R.id.contacts_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;
                QrCode qrCode = new QrCode(textView.getText().toString());
                String msg = "itemPosition=" + position + " deviceID=" + qrCode.getDeviceId() + " iPaddresse=" + qrCode.getIpAddress();
                Toast.makeText(ContactsActivity.this, msg, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(ContactsActivity.this, MainActivity.class);
                intent.putExtra("EXTRA_CURRENT_FRIEND", qrCode.getDeviceId() + ":" + qrCode.getIpAddress());
                intent.putExtra("EXTRA_CURRENT_FRIEND_ID", qrCode.getDeviceId());
                intent.putExtra("EXTRA_CURRENT_FRIEND_IP", qrCode.getIpAddress());
                startActivity(intent);
            }
        });

    }
}
