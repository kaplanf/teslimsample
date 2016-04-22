package com.kaplan.aclteslimsample.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import com.kaplan.aclteslimsample.R;
import com.kaplan.aclteslimsample.location.LocationManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.List;

/**
 * Created by kaplanfatt on 03/03/16.
 */
@EFragment(R.layout.fragment_acl_map)
public class MapACLFragment extends BaseFragment implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    @ViewById(R.id.acl_map_text)
    TextView acl_map_text;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;

    @AfterViews
    protected void AfterViews() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getActivity().getApplicationContext(), getActivity())) {
            initGoogleMap();
        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getActivity().getApplicationContext(), getActivity());
        }
//        initGoogleMap();
    }

    private void initGoogleMap() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (status == ConnectionResult.SUCCESS) {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.acl_map_frame, mapFragment, MapFragment.class.getSimpleName());
            fragmentTransaction.commit();
            mapFragment.getMapAsync(this);
        } else GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (LocationManager.isLocationEnabled(getActivity())) {
            googleMap.setMyLocationEnabled(true);
            LatLng myPosition = LocationManager.getInstance().getMyPosition();
            if (myPosition == null) {
//                myPosition = LocationManager.getInstance().getMockLocation();
            }
//            Crashlytics.log(Log.ASSERT, "gps location", myPosition);
            if (myPosition != null) {
                myPosition = new LatLng(LocationManager.getInstance().getMyPosition().latitude, LocationManager.getInstance().getMyPosition().longitude);
            }
//            Crashlytics.log(Log.ASSERT, "gps location2", myPosition.toString());
            if (myPosition != null) {
                LatLngBounds bounds = toBounds(myPosition, 360);

                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View itemLayout = layoutInflater.inflate(R.layout.extend_map_item_marker, null, false);


                IconGenerator itemIconGenerator = new IconGenerator(getActivity());
                itemIconGenerator.setContentView(itemLayout);
                itemIconGenerator.setBackground(getResources().getDrawable(android.R.color.transparent));
                Bitmap bitmap = itemIconGenerator.makeIcon();
                final MarkerOptions markerOptions = new MarkerOptions();
                final Marker marker;
                markerOptions.draggable(true);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                markerOptions.position(myPosition);
                marker = googleMap.addMarker(markerOptions);
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        animateMarker(marker, latLng, false);
                        getAdressText(latLng);
                    }
                });

                moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
            }
        }
    }

    @UiThread
    protected void moveCamera(CameraUpdate cameraUpdate) {
        googleMap.animateCamera(cameraUpdate);
    }


    @UiThread
    protected void initMapText(String address) {
        acl_map_text.setText(address);
    }

    @Background
    protected void getAdressText(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getActivity());
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList.size() > 0) {
                initMapText(addressList.get(0).getAddressLine(1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    @UiThread
    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    public void requestPermission(String strPermission, int perCode, Context _c, Activity _a) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a, strPermission)) {
            Toast.makeText(getActivity().getApplicationContext(), "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {

            ActivityCompat.requestPermissions(_a, new String[]{strPermission}, perCode);
        }
    }

    public static boolean checkPermission(String strPermission, Context _c, Activity _a) {
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    initGoogleMap();

                } else {

                    Toast.makeText(getActivity().getApplicationContext(), "Permission Denied, You cannot access location data.", Toast.LENGTH_LONG).show();

                }
                break;

        }
    }

}
