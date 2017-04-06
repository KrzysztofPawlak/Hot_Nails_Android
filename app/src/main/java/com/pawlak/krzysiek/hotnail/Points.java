package com.pawlak.krzysiek.hotnail;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Points implements Serializable {

    @SerializedName("rate_id")
    public int rate_id;

    @SerializedName("COUNT(rate_id)")
    public float count;

}
