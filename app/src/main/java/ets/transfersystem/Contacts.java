package ets.transfersystem;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

/**
 * Created by Pierre-Luc on 2017-06-29.
 */

public class Contacts {

    private String deviceId;
    private Contact[] contacts;

    private SharedPreferences settings;

    public Contacts(SharedPreferences settings) {
        this.settings = settings;
//        settings = getSharedPreferences("Contacts", 0);
    }

    public Contacts(String json) {
        Gson gson = new Gson();
        contacts = gson.fromJson(json, Contact[].class);
    }

    public void saveContact(String deviceID, String ipAddresse) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(deviceID, deviceID + ":" + ipAddresse);
        editor.commit();
    }

    public String getContact(String deviceID) {
        return settings.getString(deviceID, "");
    }

    public void deleteContact(String deviceID) {
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(deviceID);
        editor.commit();
    }

    public String[] getAllContactsToString() {
        Map<String, ?> mapsContacts = settings.getAll();
        return mapsContacts.values().toArray(new String[0]);
    }

    public Contact[] getAllContacts() {

        Map<String, ?> mapsContacts = settings.getAll();
        String[] stringMapsContacts = mapsContacts.values().toArray(new String[0]);
        contacts = new Contact[stringMapsContacts.length];
        for (int i = 0; i < stringMapsContacts.length; i++) {
            contacts[i] = new Contact(stringMapsContacts[i]);
        }
        return contacts;
    }

    public Contact getContact(int position) {
        Map<String, ?> mapsContacts = settings.getAll();
        String[] stringMapsContacts = mapsContacts.values().toArray(new String[0]);
        return new Contact(stringMapsContacts[position]);
    }

    public String getAllContactsToJson() {
        Gson gson = new Gson();
        return gson.toJson(getAllContacts());
    }

    public Contact[] getAllFriendsContacts() {
        return contacts;
    }

}
