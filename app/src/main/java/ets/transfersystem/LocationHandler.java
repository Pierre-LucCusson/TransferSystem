package ets.transfersystem;

import android.location.Location;

/**
 * Created by gab on 20/07/17.
 */

public class LocationHandler {

    private Location location;
    private static LocationHandler instance;

    private LocationHandler()
    {
        location = null;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static LocationHandler getInstance()
    {
        return instance == null ? instance = new LocationHandler() : instance;
    }
}
