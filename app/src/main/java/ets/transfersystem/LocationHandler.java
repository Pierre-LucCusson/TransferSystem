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

    public static String calculateDistance(Location selfLocation, String latitude, String longitude)
    {
        double lat_f = Double.parseDouble(latitude);
        double lon_f = Double.parseDouble(longitude);
        Location location = new Location("Server");
        location.setLatitude(lat_f);
        location.setLongitude(lon_f);

        return String.valueOf(selfLocation.distanceTo(location));

    }
}
