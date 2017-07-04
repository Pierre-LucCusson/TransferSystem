package ets.transfersystem;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Pierre-Luc on 2017-06-29.
 */

public class Contacts {

    private SharedPreferences settings;

    public Contacts(SharedPreferences settings) {
        this.settings = settings;
//        settings = getSharedPreferences("Contacts", 0);
    }

    public void saveContact(String information) {
        String deviceID = information + " oneDeviceID";
        String ipAddresse = information + " oneipAddresse";
        List<String> ets = Arrays.asList(deviceID, ipAddresse);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(deviceID, information);
        editor.commit();
    }

    public void saveContact(String deviceID, String ipAddresse) {
        List<String> ets = Arrays.asList(deviceID, ipAddresse);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(deviceID, deviceID + ":" + ipAddresse);
        editor.commit();
    }

    public String getContact(String deviceID) {
        return settings.getString(deviceID, "");
    }

    public void printAll() {
        Map<String, ?> x = settings.getAll();
        Log.d("Contacts", String.format(x.toString()));
    }

    public String[] getAllContacts() {
        Map<String, ?> mapsContacts = settings.getAll();
        return mapsContacts.values().toArray(new String[0]);
    }

}
