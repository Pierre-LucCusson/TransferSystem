package ets.transfersystem;

/**
 * Created by Pierre-Luc on 2017-07-06.
 */

public class Contact {

    private String ip;
    private String id;

    public Contact(String id, String ip) {
        this.id = id;
        this.ip = ip;
    }

    public Contact(String information) {
        String[] info = information.split(":");
        this.id = info[0];
        this.ip = info[1];
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
