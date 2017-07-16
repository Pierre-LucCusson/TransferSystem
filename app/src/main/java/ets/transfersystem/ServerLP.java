package ets.transfersystem;

import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.ClassPath;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Conjure2 on 29/06/2017.
 */

public class ServerLP extends NanoHTTPD {

    LinkedBlockingQueue blockingQueue;
    int timeout = 20;
    Contacts contacts;

    private void init()
    {
        blockingQueue = new LinkedBlockingQueue<Event>();
    }

    public ServerLP(QrCode qrcode, Contacts contacts) {
        super(qrcode.getIpAddress(), 8080);
        this.contacts = contacts;
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

        if(session.getUri().contains(HTTPRequests.LIST_FRIENDS))
        {
            return new Response(Response.Status.OK, MIME_PLAINTEXT, contacts.getAllContactsToJson());
        }
        else if(session.getUri().contains(HTTPRequests.GET_FRIEND))
        {
            String[] params = session.getUri().split("/");
            return new Response(Response.Status.OK, MIME_PLAINTEXT, contacts.getContact(params[params.length-1])) ;

        }else if(session.getUri().contains(HTTPRequests.LIST_FILES))
        {
            //TODO: List files
            return new Response(Response.Status.OK, MIME_PLAINTEXT, contacts.getAllContactsToJson());

        }else if(session.getUri().contains(HTTPRequests.GET_FILE))
        {
            String[] params = session.getUri().split("/");
            //TODO: Get file
            return new Response(Response.Status.OK, MIME_PLAINTEXT, contacts.getContact(params[params.length-1]));

        }else if(session.getUri().contains(HTTPRequests.RECEIVE_FILE))
        {
            String[] params = session.getUri().split("/");
            //TODO: Send notification
            return new Response(Response.Status.OK, MIME_PLAINTEXT, contacts.getContact(params[params.length-1]));

        }else if(session.getUri().contains(HTTPRequests.POSITION))
        {
            String[] params = session.getUri().split("/");
            //TODO: get Position
            return new Response(Response.Status.OK, MIME_PLAINTEXT, contacts.getContact(params[params.length-1]));

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
