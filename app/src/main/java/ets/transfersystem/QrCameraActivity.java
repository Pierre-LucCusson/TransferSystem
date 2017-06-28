package ets.transfersystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Pierre-Luc on 2017-06-26.
 */

public class QrCameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_camera);

        //Back button
        final Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                back(view);
            }
        } );
    }

    public void back(View view) {
        Button button = (Button) view;
        Log.d("ButtonClick", String.format("Back button from QR camera was click"));
        startActivity(new Intent(QrCameraActivity.this, MainActivity.class));
    }

}