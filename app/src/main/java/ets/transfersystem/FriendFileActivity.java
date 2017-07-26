package ets.transfersystem;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FriendFileActivity extends AppCompatActivity {

    private String friendsDeviceId;
    private String friendsIpAdress;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_files);

        friendsDeviceId = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_ID");
        friendsIpAdress = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_IP");
        filename = getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_FILENAME");

        TextView tv = (TextView) findViewById(R.id.friend_file_name);
        tv.setText(filename);

        Button button = (Button) findViewById(R.id.friend_file_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFriendsFile(filename);
            }
        });
    }

    private String getFriendsFile(String filename) {
        ClientLP client = new ClientLP();
        try {
            InputStream response = client.getFile(friendsIpAdress + ":8080", filename);
            OutputStream out = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),filename));
            int read = 0;
            byte[] bytes = new byte[1024];

            while((read = response.read(bytes)) != -1)
            {
                out.write(bytes,0,read);
            }
            NotifyDownloadEnded(filename);

            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void NotifyDownloadEnded(String filename) throws IOException {
        ClientLP client = new ClientLP();
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        client.confirmReceived(friendsIpAdress + ":8080",deviceId, filename);
        String.format("%s was transfered to %s", filename, deviceId );
    }
}
