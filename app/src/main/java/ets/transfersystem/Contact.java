package ets.transfersystem;

/**
 * Created by Pierre-Luc on 2017-07-06.
 */

public class Contact  {

    private String ip;
    private String id;
    private boolean online;
    private double distance;
    private long lastLogin;

    public Contact(String information) {
        String[] info = information.split(":");
        this.id = info[0];
        this.ip = info[1];
    }

    public Contact(String id, String ip) {
        this.id = id;
        this.ip = ip;
        online = false;
        distance = 0;
        lastLogin = 0;
    }

    public Contact(String id, String ip, boolean online, double distance, long lastLogin) {
        this.id = id;
        this.ip = ip;
        this.online = false;
        this.distance = distance;
        this.lastLogin = lastLogin;
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

    public Boolean isOnline(){
        return online;
    }

    public void setOnline(boolean online){
        this.online = online;
    }

    public double getDistance(){
        return this.distance;
    }

    public void setDistance(Double distance){
        this.distance = distance;
    }

    public long getLastLogin(){
        return lastLogin;
    }

    public void setLastLogin(Long lastLogin){
        this.lastLogin = lastLogin;
    }
}
