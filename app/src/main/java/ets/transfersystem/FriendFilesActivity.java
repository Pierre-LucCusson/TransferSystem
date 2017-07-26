package ets.transfersystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

public class FriendFilesActivity extends AppCompatActivity {

    private String friendsDeviceId;
    private String friendsIpAdress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        friendsDeviceId = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_ID");
        friendsIpAdress = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_IP");

        String[] friendsContacts = new Gson().fromJson(getFriendsFilesInJson(), new String[0].getClass());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_list, friendsContacts);

        ListView list = (ListView) findViewById(R.id.file_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;
                Intent intent = new Intent(FriendFilesActivity.this, FriendFileActivity.class);
                intent.putExtra("EXTRA_CURRENT_FRIEND_ID", friendsDeviceId);
                intent.putExtra("EXTRA_CURRENT_FRIEND_IP", friendsIpAdress);
                intent.putExtra("EXTRA_CURRENT_FRIEND_FILENAME", textView.getText());
                startActivity(intent);
            }
        });
    }

    private String getFriendsFilesInJson() {
        ClientLP client = new ClientLP();
        try {
            String response = client.listFiles(friendsIpAdress + ":8080");
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
