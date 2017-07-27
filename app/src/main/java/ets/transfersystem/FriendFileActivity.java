package ets.transfersystem;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FriendFileActivity extends AppCompatActivity {

    private String friendsDeviceId;
    private String friendsIpAdress;
    private String filename;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_files);

        friendsDeviceId = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_ID");
        friendsIpAdress = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_IP");
        filename = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_FILENAME");
        id = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_FILE_ID");

        TextView tv = (TextView) findViewById(R.id.friend_file_name);
        tv.setText(filename);

        Button button = (Button) findViewById(R.id.friend_file_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFriendsFile(id, filename);
            }
        });
    }

    private String getFriendsFile(String id, String title) {
        ClientLP client = new ClientLP();
        try {
            if(client.getFile(friendsIpAdress, id, title))
            {
                NotifyDownloadEnded(title);
            }

            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void NotifyDownloadEnded(String filename) throws IOException {
        ClientLP client = new ClientLP();
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        client.confirmReceived(friendsIpAdress,deviceId, filename);
        String msg = String.format("%s was transfered to %s", filename, deviceId );
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
