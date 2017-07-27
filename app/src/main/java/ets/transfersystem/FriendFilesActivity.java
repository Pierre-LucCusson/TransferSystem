package ets.transfersystem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static ets.transfersystem.FolderObserver.getFilesList;

public class FriendFilesActivity extends AppCompatActivity {

    private String friendsDeviceId;
    private String friendsIpAdress;
    FileClass[] friendsfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        friendsDeviceId = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_ID");
        friendsIpAdress = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_IP");

        friendsfiles = new Gson().fromJson(getFriendsFilesInJson(), new FileClass[0].getClass());

        final ArrayList<FileClass> files = new ArrayList();
        for(FileClass file : friendsfiles)
        {
            files.add(file.id, file);
        }
        final ArrayAdapter<FileClass> adapter = new ArrayAdapter<FileClass>(this, android.R.layout.simple_list_item_2, android.R.id.text1, files)
        {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setText(String.valueOf(files.get(position).id));

                TextView text2 =(TextView) view.findViewById(android.R.id.text2);
                text2.setText(files.get(position).title);
                return view;

            }
        };


        ListView list = (ListView) findViewById(R.id.file_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewClicked, int position, long id) {
                Intent intent = new Intent(FriendFilesActivity.this, FriendFileActivity.class);
                intent.putExtra("EXTRA_CURRENT_FRIEND_ID", friendsDeviceId);
                intent.putExtra("EXTRA_CURRENT_FRIEND_IP", friendsIpAdress);
                intent.putExtra("EXTRA_CURRENT_FRIEND_FILE_ID", String.valueOf(files.get(position).id));
                intent.putExtra("EXTRA_CURRENT_FRIEND_FILENAME", files.get(position).title);
                startActivity(intent);
            }
        });
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
