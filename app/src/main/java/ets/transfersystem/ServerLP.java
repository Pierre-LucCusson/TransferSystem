package ets.transfersystem;

import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.ClassPath;

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


    private void init()
    {
        blockingQueue = new LinkedBlockingQueue<Event>();
    }

    public ServerLP(int port) {
        super(port);
    }

    public ServerLP(String hostname, int port) {
        super(hostname, port);
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
