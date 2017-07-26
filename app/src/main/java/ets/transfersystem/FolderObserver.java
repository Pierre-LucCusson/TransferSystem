package ets.transfersystem;

import android.content.SharedPreferences;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Base64;
import android.util.Log;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by gab on 23/07/17.
 */

public class FolderObserver extends FileObserver {

    private EventBus eventBus;

    public FolderObserver() {
        super(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getPath());
        eventBus = new EventBus();
    }

    public static String listFiles()
    {
        String[] files = getFilesList();
        String[] base64_files = files.clone();
        for(int i = 0; i < files.length; i++)
        {
            try {
                base64_files[i] = Base64.encodeToString(files[i].getBytes("UTF-8"), Base64.URL_SAFE);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                base64_files[i] = files[i];
            }
        }
        return new Gson().toJson(base64_files);
    }

    public static String[] getFilesList()
    {
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] files = folder.listFiles();

        if (files != null)
        {
            ArrayList<String> files_names = new ArrayList<>();

            for (File file: files)
            {
                files_names.add(file.getName());
            }
            String[] names = files_names.toArray(new String[0]);
            return names;
        }
        else{
            String[] names = {"Empty"};
            return names;
        }


    }

    public static File getFile(String encoded_name)
    {
        try {
            String name = new String(Base64.decode(encoded_name, Base64.URL_SAFE), "UTF-8");
            return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),name);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onEvent(int i, String s) {
        if (i == CREATE)
        {
            eventBus.post(new NotificationEvent("add", s));
        }
        else if (i == DELETE)
        {
            eventBus.post(new NotificationEvent("delete", s));
        }
    }

    public void subscribe(Object object){
        eventBus.register(object);
    }
}
