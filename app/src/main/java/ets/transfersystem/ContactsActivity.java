package ets.transfersystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;

/**
 * Created by Pierre-Luc on 2017-06-26.
 */

public class ContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Contacts contacts = new Contacts(getSharedPreferences(Contacts.contactID, 0));
        Log.d("Contacts toJson", String.format(contacts.getAllContactsToJson()));

        OrderContacts orderContacts = new OrderContacts(this, ContactsActivity.this, ContactsActivity.class,contacts.getAllContacts(), getIntent().getStringExtra("EXTRA_ORDER_BY"));
        orderContacts.setOrderButtons();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_list, contacts.getAllContactsToString());

        ListView list = (ListView) findViewById(R.id.contacts_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;
                Contact contact = new Gson().fromJson(textView.getText().toString(), Contact.class);
                String msg = "itemPosition=" + position + " deviceID=" + contact.getId() + " iPaddresse=" + contact.getIp();
                Toast.makeText(ContactsActivity.this, msg, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(ContactsActivity.this, MainActivity.class);
                intent.putExtra("EXTRA_CURRENT_FRIEND", contact.getId() + ":" + contact.getIp());
                intent.putExtra("EXTRA_CURRENT_FRIEND_ID", contact.getId());
                intent.putExtra("EXTRA_CURRENT_FRIEND_IP", contact.getIp());
                startActivity(intent);
            }
        });

    }

}
