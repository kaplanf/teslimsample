package com.kaplan.aclteslimsample.location;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.kaplan.aclteslimsample.R;

public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final float INVALID_DISTANCE = -1f;

    private static LocationManager instance;
    private static GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location lastLocation;
    private Context context;

    private LocationManager() {
    }

    public static synchronized LocationManager getInstance() {
        return instance;
    }

    public static synchronized void initializeManager(Context context) {
        if (instance == null) instance = new LocationManager();
        instance.context = context;
        mGoogleApiClient =
                new GoogleApiClient.Builder(context).addConnectionCallbacks(instance).addOnConnectionFailedListener(instance).addApi(LocationServices.API)
                        .build();
        mGoogleApiClient.connect();
    }

    public double computeDistanceBetween(LatLng from, LatLng to) {
        return SphericalUtil.computeDistanceBetween(from, to);
    }

    public double computeDistanceFromMe(LatLng to) {
        LatLng currentPosition = getMyPosition();
        if (currentPosition != null)
            return SphericalUtil.computeDistanceBetween(currentPosition, to);
        else return 0;
    }

    public String getDistanceFromMe(LatLng to) {
        LatLng currentPosition = getMyPosition();
        if (currentPosition != null)
            return formatMile(SphericalUtil.computeDistanceBetween(currentPosition, to));
        else return null;
    }

    private String formatMile(double distance) {
        return String.format("%4.1f%s", distance / 1609.344d, "ml");
    }

    public LatLng getMockLocation() {
        Location location = new Location("MockedLocation");
        LocationServices.FusedLocationApi.setMockMode(mGoogleApiClient,true);
        // Time is needed to create a valid Location
        long currentTime = System.currentTimeMillis();
        location.setTime(currentTime);
        location.setLatitude(41.015137);
        location.setLongitude(28.979530);
        LocationServices.FusedLocationApi.setMockLocation(mGoogleApiClient, location);
        return new LatLng(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLatitude(),
                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLatitude());
    }

    public LatLng getMyPosition() {
        if (lastLocation != null)
            return new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        else return null;
    }

    public Location getLastLocation() {
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mGoogleApiClient.disconnect();
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest request = new LocationRequest();
        request.setInterval(5 * 60 * 1000); // 5 minutes
        request.setFastestInterval(5 * 60 * 1000);
        request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        return request;
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch (Exception e) {
            e.printStackTrace();
            lastLocation = null;
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//            mActivity.onGooglePlayServicesError(this, connectionResult);
    }

    protected void startLocationUpdates() {
        if (mLocationRequest == null) {
            mLocationRequest = createLocationRequest();
        }
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            lastLocation = location;
        }
    }

    public float getDistanceToPoint(double latitude, double longitude) {
        if (lastLocation == null) {
            return INVALID_DISTANCE;
        }
        Location loc = new Location("");
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        return lastLocation.distanceTo(loc);
    }

    public static boolean isLocationEnabled(final Context context) {
        Crashlytics.log(Log.ASSERT, "GPS", "GPS not enabled");
        android.location.LocationManager lm = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
        return gps_enabled;
    }

}
