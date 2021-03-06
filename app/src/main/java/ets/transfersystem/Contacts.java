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
    private Gson gson = new Gson();

    public static final String contactID = "ContactsTest3";
    private SharedPreferences settings;

    public Contacts(SharedPreferences settings) {
        this.settings = settings;
//        settings = getSharedPreferences("Contacts", 0);
    }

    public Contacts(String json) {
        contacts = gson.fromJson(json, Contact[].class);
    }

    public void saveContact(String deviceID, String ipAddresse) {
        Contact contact = new Contact(deviceID, ipAddresse);
        saveContact(contact);
//        old code
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(deviceID, deviceID + ":" + ipAddresse);
//        editor.commit();
    }

    public void saveContact(Contact contact) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(contact.getId(), gson.toJson(contact));
        editor.commit();
    }

    public void saveContact(Contact[] contacts) {
        for (Contact contact : contacts) {
            saveContact(contact);
        }
    }

    public Contact getContact(String deviceID) {
        return new Gson().fromJson(getContactInJson(deviceID), Contact.class);
    }
    public String getContactInJson(String deviceID) {
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
            contacts[i] = new Gson().fromJson(stringMapsContacts[i], Contact.class);
        }
        return contacts;
    }

    public Contact getContact(int position) {
        Map<String, ?> mapsContacts = settings.getAll();
        String[] stringMapsContacts = mapsContacts.values().toArray(new String[0]);
        return new Contact(stringMapsContacts[position]);
    }

    public String getAllContactsToJson() {
        return gson.toJson(getAllContacts());
    }

    public Contact[] getAllFriendsContacts() {
        return contacts;
    }

    public void saveContactsWithJson(String contactsInJson) {
        SharedPreferences.Editor editor = settings.edit();
        contacts = gson.fromJson(contactsInJson, Contact[].class);
        saveContact(contacts);
    }

    public void deleteAllContacts() {
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public void setToOnlineAndSave(String deviceId) {
        Contact contact = getContact(deviceId);
        setToOnlineAndSave(contact);
    }

    public void setToOnlineAndSave(Contact contact) {
        contact.setOnline(true);
        contact.setLastLogin(System.currentTimeMillis());
        saveContact(contact);
    }

    public void setToOfflineAndSave(String deviceId) {
        Contact contact = getContact(deviceId);
        setToOfflineAndSave(contact);
    }

    public void setToOfflineAndSave(Contact contact) {
        contact.setOnline(false);
        saveContact(contact);
    }

    public Contact getContactByIpAddress(String ipAddress) {
        Contact[] myContacts = getAllContacts();
        for (Contact contact: myContacts) {
            if (contact.getIp().equals(ipAddress)) {
                return  contact;
            }
        }
        return null;
    }
}
