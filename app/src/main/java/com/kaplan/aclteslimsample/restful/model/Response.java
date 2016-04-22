package com.kaplan.aclteslimsample.restful.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by kaplanfatt on 01/03/16.
 */
public class Response {

    @SerializedName("photos")
    public Photos photos;

    @SerializedName("stat")
    public String stat;


    public class Photos
    {
        @SerializedName("page")
        private int page;

        @SerializedName("photo")
        public ArrayList<Image> imageList;
    }
}
