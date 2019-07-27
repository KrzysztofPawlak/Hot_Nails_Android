package com.pawlak.krzysiek.hotnail;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface HotNailService {

    @FormUrlEncoded
    @POST("user_control.php")
    Call<ResponseBody> logIn(@Field("email") String email, @Field("password") String password);
}
