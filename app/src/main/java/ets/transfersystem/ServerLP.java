package ets.transfersystem;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.eventbus.Subscribe;

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
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastLocation;
    private String deviceID;

    private void init(Activity mainActivity) {
        this.mainActivity = mainActivity;
        blockingQueue = new LinkedBlockingQueue<Event>();
        lastLocation = null;
        deviceID = Settings.Secure.getString(mainActivity.getContentResolver(), Settings.Secure.ANDROID_ID);

//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainActivity);
//        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(mainActivity, new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    if (location != null) {
//                        lastLocation = location;
//                    }
//                }
//            });
//            return;
//        }
    }

    public ServerLP(Contacts contacts, Activity mainActivity) {
        super(8080);
        this.contacts = contacts;
        init(mainActivity);
    }

    //TODO: eventbus.register(server); somewhere, maybe MainActivity
    @Subscribe
    public void handleEvent(Event event)
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
            return new Response(Response.Status.OK, MIME_PLAINTEXT, contacts.getAllContactsToJson());
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
            return new Response(Response.Status.OK, MIME_PLAINTEXT, contacts.getAllContactsToJson());

        }else if(session.getUri().contains(HTTPRequests.GET_FILE))
        {
            String[] params = session.getUri().split("/");
            //TODO: Get file
            return new Response(Response.Status.OK, MIME_PLAINTEXT, contacts.getContactInJson(params[params.length-1]));

        }else if(session.getUri().contains(HTTPRequests.RECEIVE_FILE))
        {
            String[] params = session.getUri().split("/");
            //TODO: Send notification
            return new Response(Response.Status.OK, MIME_PLAINTEXT, contacts.getContactInJson(params[params.length-1]));

        }else if(session.getUri().contains(HTTPRequests.POSITION))
        {
            if(lastLocation != null)
            {
                return new Response(Response.Status.OK, MIME_PLAINTEXT, String.format("%f/%f/%s", lastLocation.getLongitude(), lastLocation.getLatitude(), deviceID));
            }
            return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "POSITION NOT FOUND");

        }else if(session.getUri().contains(HTTPRequests.CHECK_FILE_CHANGE))
        {
            Event event = null;
            try {
                event = (Event) blockingQueue.poll(timeout, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (event != null)
            {
                return new Response(Response.Status.OK, MIME_PLAINTEXT, event.transferString);
            }
            else
            {
                return new Response(REQUEST_TIMEOUT, MIME_PLAINTEXT, "Request Timeout");
            }
        }

        return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "NOT FOUND");

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
