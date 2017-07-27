package ets.transfersystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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
import java.io.UnsupportedEncodingException;

public class FriendFilesActivity extends AppCompatActivity {

    private String friendsDeviceId;
    private String friendsIpAdress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        friendsDeviceId = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_ID");
        friendsIpAdress = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_IP");

        String[] friendsfiles= new Gson().fromJson(getFriendsFilesInJson(), new String[0].getClass());
        String[] files = friendsfiles.clone();
        for(int i = 0; i < friendsfiles.length; i++)
        {
            try {
                files[i] = new String(Base64.decode(friendsfiles[i].getBytes(), Base64.URL_SAFE), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                files[i] = friendsfiles[i];
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_list, files);

        ListView list = (ListView) findViewById(R.id.file_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;
                Intent intent = new Intent(FriendFilesActivity.this, FriendFileActivity.class);
                intent.putExtra("EXTRA_CURRENT_FRIEND_ID", friendsDeviceId);
                intent.putExtra("EXTRA_CURRENT_FRIEND_IP", friendsIpAdress);
                intent.putExtra("EXTRA_CURRENT_FRIEND_FILENAME", encodeB64(textView.getText().toString()));
                startActivity(intent);
            }
        });
    }

    private String encodeB64(String toEncode)
    {
        try {
            return new String(Base64.encode(toEncode.getBytes("UTF-8"), Base64.URL_SAFE));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    private String getFriendsFilesInJson() {
        ClientLP client = new ClientLP();
        try {
            String response = client.listFiles(friendsIpAdress);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
