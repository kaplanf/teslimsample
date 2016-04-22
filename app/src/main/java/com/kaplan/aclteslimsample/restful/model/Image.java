package com.kaplan.aclteslimsample.restful.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Image implements Serializable {

	@SerializedName("id")
	public String id;

	@SerializedName("owner")
	public String owner;

	@SerializedName("secret")
	public String secret;

	@SerializedName("server")
	public String server;

	@SerializedName("farm")
	public int farm;

	@SerializedName("title")
	public String title;

}