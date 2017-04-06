package com.pawlak.krzysiek.hotnail;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Serializable {

    @SerializedName("pid")
    public int pid;

    @SerializedName("image_name")
    public String image_name;

    @SerializedName("image_id")
    public String image_id;

    @SerializedName("url")
    public String url;

    @SerializedName("data_add")
    public String data_add;

    @SerializedName("avg")
    public String avg;

    @SerializedName("vote")
    public float vote;

    @SerializedName("rate_id")
    public int rate_id;
}
