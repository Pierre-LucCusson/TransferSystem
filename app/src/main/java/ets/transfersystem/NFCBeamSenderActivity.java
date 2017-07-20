package ets.transfersystem;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Created by Pierre-Luc on 2017-07-17.
 * //source: http://www.developer.com/ws/android/nfc-programming-in-android.html
 */

public class NFCBeamSenderActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private NdefMessage message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_sender);
        Log.d("messageNFC", "NFCBeamSenderActivity started");
        
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        String nfcMessageToSend = getIntent().getStringExtra("EXTRA_NFC_MESSAGE_TO_SEND");
        Log.d("messageNFC", nfcMessageToSend);

        message = new NdefMessage(
                new NdefRecord[] {
                        createNewTextRecord(nfcMessageToSend, Locale.ENGLISH, true) });

        returnToMainActivity();


    }

    public static NdefRecord createNewTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char)(utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte)status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (nfcAdapter != null)
            nfcAdapter.enableForegroundNdefPush(this, message);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (nfcAdapter != null)
            nfcAdapter.disableForegroundNdefPush(this);
    }

    private void returnToMainActivity() {
        //TODO
        Log.d("messageNFC", "return to main activity ?");
    }

}
