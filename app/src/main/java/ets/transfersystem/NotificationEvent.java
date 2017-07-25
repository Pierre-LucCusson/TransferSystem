package ets.transfersystem;

/**
 * Created by gab on 23/07/17.
 */

public class NotificationEvent {

    String action;
    String filename;

    public NotificationEvent(String action, String filename)
    {
        this.action = action;
        this.filename = filename;
    }

}
