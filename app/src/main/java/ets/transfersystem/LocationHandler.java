package ets.transfersystem;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by gab on 20/07/17.
 */

public class LocationHandler {

    private Location location;

    public LocationHandler(Activity activity)
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

}
