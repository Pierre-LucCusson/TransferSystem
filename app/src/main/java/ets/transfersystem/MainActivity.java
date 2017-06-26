package ets.transfersystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Show My QR button
        final Button myQrButton = (Button) findViewById(R.id.myQrButton);
        myQrButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                showMyQr(view);
            }
        } );

        //Show My QR button
        final Button cameraQrButton = (Button) findViewById(R.id.cameraQrButton);
        cameraQrButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                openQrCamera(view);
            }
        } );

        //Show My QR button
        final Button contactsButton = (Button) findViewById(R.id.contactsButton);
        contactsButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                showContacts(view);
            }
        } );
    }

    public void showMyQr(View view) {
        Button button = (Button) view;
        Log.d("ButtonClick", String.format("Show My QR button was click"));
        startActivity(new Intent(MainActivity.this, MyQrActivity.class));
    }

    public void openQrCamera(View view) {
        Button button = (Button) view;
        Log.d("ButtonClick", String.format("Open QR Camera button was click"));
        startActivity(new Intent(MainActivity.this, QrCameraActivity.class));
    }

    public void showContacts(View view) {
        Button button = (Button) view;
        Log.d("ButtonClick", String.format("Show My Contacts button was click"));
        startActivity(new Intent(MainActivity.this, ContactsActivity.class));
    }
}
