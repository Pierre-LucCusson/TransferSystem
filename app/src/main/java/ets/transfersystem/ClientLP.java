package ets.transfersystem;

import android.location.LocationManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.tls.OkHostnameVerifier;

/**
 * Created by ak90090 on 2017-06-29.
 */

public class ClientLP {

    String url = "http://192.168.2.31:5000";

    public final String WAITING = "/waiting";

    OkHttpClient client;

    public ClientLP()
    {
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS).build();
    }

    public String sendLongPolling() throws IOException {
        Request request = new Request.Builder().url(url+WAITING).build();

        try (Response response = client.newCall(request).execute()){
            if (response.code() == 200)
            {
                return response.body().string();
            }
            else if (response.code() == 408)
            {
                return sendLongPolling();
            }
            return null;
        }
    }
}
