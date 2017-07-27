package ets.transfersystem;

import android.location.LocationManager;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ak90090 on 2017-06-29.
 */

public class ClientLP {

    OkHttpClient client;

    public ClientLP()
    {
            client = new OkHttpClient.Builder()
                    .connectTimeout(100000, TimeUnit.SECONDS)
                    .writeTimeout(100000, TimeUnit.SECONDS)
                    .readTimeout(1000000, TimeUnit.SECONDS).build();
    }

    private String sendLongPolling(String url) throws IOException {
        Request request = new Request.Builder().url("http://" + url ).build();

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
        Request request = new Request.Builder().url("http://" + url).build();
        Log.d("BONJOUR", request.toString());
        try (Response response = client.newCall(request).execute()){
            if (response.code() == 200)
            {
                String bodyRequest = response.body().string();
                Log.d("ClientReceived", bodyRequest);
                return bodyRequest;
            }
            return null;
        }
    }

    private boolean sendFileRequest(String url, String title) throws IOException
    {
        Request request = new Request.Builder().url("http://" + url).build();
        Log.d("BONJOUR", request.toString());
        try (Response response = client.newCall(request).execute()){
            if (response.code() == 200)
            {
                String[] url_split = url.split("/");
                OutputStream out = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), title));
                out.write(response.body().bytes());
                out.close();
                return true;
            }
            return false;
        }
    }

    public String checkForFileChange(String url) throws IOException
    {
        Log.d("", url + HTTPRequests.CHECK_FILE_CHANGE);
        return sendLongPolling(url  + ":8080" + HTTPRequests.CHECK_FILE_CHANGE);
    }
    public String listFriend(String url) throws IOException
    {
        Log.d("listFriend url= ",url);
        return sendRequest(url  + ":8080"+ HTTPRequests.LIST_FRIENDS);
    }

    public String getFriend(String url, String id) throws IOException
    {
        return sendRequest(url + ":8080" + HTTPRequests.GET_FRIEND + id);
    }

    public String listFiles(String url) throws IOException
    {
        return sendRequest(url + ":8080"  +HTTPRequests.LIST_FILES);
    }

    public boolean getFile(String url, String id, String title) throws IOException
    {
        return sendFileRequest(url  + ":8080" + HTTPRequests.GET_FILE + id, title);
    }

    public String confirmReceived(String url,String name, String file) throws IOException
    {
        return sendRequest(url + ":8080" + HTTPRequests.RECEIVE_FILE + file + "/" + name);
    }

    public String getPosition(String url) throws IOException
    {
        return sendRequest(url + ":8080" + HTTPRequests.POSITION);
    }
}
