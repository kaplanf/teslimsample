package com.kaplan.aclteslimsample;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.kaplan.aclteslimsample.location.LocationManager;
import com.kaplan.aclteslimsample.restful.model.Image;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EApplication;

import io.fabric.sdk.android.Fabric;

/**
 * Created by kaplanfatt on 01/03/16.
 */
@EApplication
public class ACLTeslimApplication extends Application {

    private static ACLTeslimApplication singleton;
    private Picasso p;


    public String FLICKR_API_KEY = "e2b28abe57f4953a4e0aa7e69fba766b";
    public String FLICKR_API_SUFFIX = "json";

    public static ACLTeslimApplication getInstance() {
        if (singleton == null)
            singleton = new ACLTeslimApplication();
        return singleton;
    }

    public void onCreate() {
        super.onCreate();
        singleton = this;
        Fabric.with(this, new Crashlytics());
        p = getPicassoInstance();
        LocationManager.initializeManager(this);
    }

    public String createFlickrImageURL(Image image) {
        String url = "";
        url = "https://farm" + image.farm + "." + "staticflickr.com/" + image.server + "/" + image.id + "_" + image.secret + ".jpg";
        return url;
    }


    public Picasso getPicassoInstance() {
        if (p == null) {
            final int memClass = ((ActivityManager) getApplicationContext().getSystemService(
                    Context.ACTIVITY_SERVICE)).getMemoryClass();

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = 1024 * 1024 * memClass / 8;
            p = new Picasso.Builder(getApplicationContext())
                    .memoryCache(new LruCache(cacheSize))
                    .build();

        }
        return p;
    }

}
