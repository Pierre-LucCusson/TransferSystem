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

    OkHttpClient client;

    public ClientLP()
    {
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS).build();
    }

    private String sendLongPolling(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()){
            if (response.code() == 200)
            {
                return response.body().string();
            }
            else if (response.code() == 408)
            {
                return sendLongPolling(url);
            }
            return null;
        }
    }


    private String sendRequest(String url) throws IOException
    {
        Request request = new Request.Builder().url("http://" + url+HTTPRequests.CHECK_FILE_CHANGE).build();

        try (Response response = client.newCall(request).execute()){
            if (response.code() == 200)
            {
                return response.body().string();
            }
            return null;
        }
    }

    public String checkForFileChange(String url) throws IOException
    {
        return sendLongPolling(url + HTTPRequests.CHECK_FILE_CHANGE);
    }
    public String listFriend(String url) throws IOException
    {
        return sendRequest(url + HTTPRequests.LIST_FRIENDS);
    }

    public String getFriend(String url, String id) throws IOException
    {
        return sendRequest(url + HTTPRequests.GET_FRIEND + id);
    }

    public String listFiles(String url) throws IOException
    {
        return sendRequest(url  +HTTPRequests.LIST_FILES);
    }

    public String getFile(String url, String id) throws IOException
    {
        return sendRequest(url + HTTPRequests.GET_FILE+ id);
    }

    public String confirmReceived(String url,String name, String file) throws IOException
    {
        return sendRequest(url + HTTPRequests.RECEIVE_FILE + file + "/" + name);
    }

    public String getPosition(String url) throws IOException
    {
        return sendRequest(url + HTTPRequests.POSITION);
    }
}
