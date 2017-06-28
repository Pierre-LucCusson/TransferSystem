package ets.transfersystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * Created by Pierre-Luc on 2017-06-26.
 */

public class MyQrActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qr);

        //Back button
        final Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                back(view);
            }
        } );

        //My QR Code Image
        ImageView qrCodeImageView = (ImageView) findViewById(R.id.myQrCode);
        Bitmap qrBitmap = new QrCode().getQrCode();
        qrCodeImageView.setImageBitmap(qrBitmap);

    }

    public void back(View view) {
        Button button = (Button) view;
        Log.d("ButtonClick", String.format("Back button from my QR was click"));
        startActivity(new Intent(MyQrActivity.this, MainActivity.class));
    }


}
