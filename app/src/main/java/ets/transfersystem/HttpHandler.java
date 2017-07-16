package ets.transfersystem;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by gab on 13/07/17.
 */

public class HttpHandler extends Service {

    IBinder binder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String sendLongPolling()
    {
        ClientLP client = new ClientLP();
        try {
            return client.checkForFileChange("127.0.0.1:8080");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

