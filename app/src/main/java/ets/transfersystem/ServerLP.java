package ets.transfersystem;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Conjure2 on 29/06/2017.
 */

public class ServerLP extends NanoHTTPD {

    private Activity mainActivity;
    private LinkedBlockingQueue blockingQueue;
    private int timeout = 20;
    private Contacts contacts;
    private FolderObserver fo;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastLocation;
    private String deviceID;

    private void init(Activity mainActivity) {
        this.mainActivity = mainActivity;
        blockingQueue = new LinkedBlockingQueue<>();
        lastLocation = null;
        deviceID = Settings.Secure.getString(mainActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
        lastLocation = null;
    }

    public ServerLP(Contacts contacts, Activity mainActivity, FolderObserver fo) {
        super(8080);
        this.contacts = contacts;
        this.fo = fo;
        fo.subscribe(this);
        init(mainActivity);
    }

    @Subscribe
    public void handleEvent(NotificationEvent event)
    {
        try {
            blockingQueue.put(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {

        Contact contactFromClient = contacts.getContactByIpAddress(session.getHeaders().get("remote-addr"));
        if (contactFromClient != null) {
            contacts.setToOnlineAndSave(contactFromClient);
        }

        if(session.getUri().contains(HTTPRequests.LIST_FRIENDS))
        {
            Log.d("ServerSend", contacts.getAllContactsToJson());
            return new Response(Response.Status.OK, MIME_PLAINTEXT, FolderObserver.listFiles());
        }
        else if(session.getUri().contains(HTTPRequests.GET_FRIEND))
        {
            String[] params = session.getUri().split("/");
            String deviceId = params[params.length-1];
            Log.d("messageNFCserver", deviceId);
            Contact contact = contacts.getContact(deviceId);

            //Start NFCBeamSenderActivity
            Intent nfcSenderIntent = new Intent(mainActivity, NFCBeamSenderActivity.class);
            String nfcMessageToSend = "confirm:" + contact.getId() + ":" + contact.getIp();
            nfcSenderIntent.putExtra("EXTRA_NFC_MESSAGE_TO_SEND", nfcMessageToSend);
            mainActivity.startActivity(nfcSenderIntent);

            return new Response(Response.Status.OK, MIME_PLAINTEXT, contact.getId() + ":" + contact.getIp()) ;

        }else if(session.getUri().contains(HTTPRequests.LIST_FILES))
        {
            //TODO: List files
            return new Response(Response.Status.OK, MIME_PLAINTEXT, fo.listFiles());

        }else if(session.getUri().contains(HTTPRequests.GET_FILE))
        {
            String[] params = session.getUri().split("/");
            //TODO: Get file
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(FolderObserver.getFile(params[params.length-1]));
                return new Response(Response.Status.OK, MIME_PLAINTEXT, fileInputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }else if(session.getUri().contains(HTTPRequests.RECEIVE_FILE))
        {
            String[] params = session.getUri().split("/");
            //TODO: Send notification
            try {
                String filename = new String(Base64.decode(params[params.length-2].getBytes(), Base64.URL_SAFE), "UTF-8");
                Toast.makeText(mainActivity, String.format("%s was transfered to %s", filename, params[params.length-1] ), Toast.LENGTH_LONG).show();

            /*Notification notif = new Notification.Builder(mainActivity.getApplicationContext())
                    .setContentTitle("Transfer System: Transfer Complete")
                    .setContentText(String.format("%s was transfered to %s", filename, params[params.length-1] ))
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                    .build();
            NotificationManager mNotificationManager = (NotificationManager) mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(0, notif);
            */
            return new Response(Response.Status.OK, MIME_PLAINTEXT, "Notified");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }else if(session.getUri().contains(HTTPRequests.POSITION))
        {
            if(lastLocation != null)
            {
                return new Response(Response.Status.OK, MIME_PLAINTEXT, String.format("%f/%f/%s", lastLocation.getLongitude(), lastLocation.getLatitude(), deviceID));
            }
            return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "POSITION NOT FOUND");

        }else if(session.getUri().contains(HTTPRequests.CHECK_FILE_CHANGE))
        {
            NotificationEvent event = null;
            try {
                event = (NotificationEvent) blockingQueue.poll(timeout, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (event != null)
            {
                return new Response(Response.Status.OK, MIME_PLAINTEXT, new Gson().toJson(event));
            }
            else
            {
                return new Response(REQUEST_TIMEOUT, MIME_PLAINTEXT, "Request Timeout");
            }
        }

        return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "NOT FOUND");

    }

    public void setLastLocation(Location location)
    {
        lastLocation = location;
    }

    public static final Response.IStatus REQUEST_TIMEOUT = new Response.IStatus() {
        @Override
        public int getRequestStatus() {
            return 408;
        }

        @Override
        public String getDescription() {
            return "Request Timeout";
        }
    };
}
