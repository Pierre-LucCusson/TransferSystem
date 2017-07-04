package ets.transfersystem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Pierre-Luc on 2017-06-26.
 */

public class ContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Contacts contacts = new Contacts(getSharedPreferences("ContactsTest2", 0));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_list, contacts.getAllContacts());

        ListView list = (ListView) findViewById(R.id.contacts_list);
        list.setAdapter(adapter);


    }
}
