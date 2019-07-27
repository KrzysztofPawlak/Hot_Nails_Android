package com.pawlak.krzysiek.hotnail;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pawlak.krzysiek.hotnail.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.pawlak.krzysiek.hotnail.API_URL.SERVER;

public class Splash extends Activity {

    boolean flaga;
    private RequestQueue requestQueue;
    private static final String URL = SERVER + "user_control.php";
    private StringRequest request;
    private HotNailService hotNailService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        hotNailService = retrofit.create(HotNailService.class);

            SharedPreferences getPrefs = PreferenceManager
            .getDefaultSharedPreferences(this);
    SharedPreferences.Editor checkbox = getPrefs.edit();

        requestQueue = Volley.newRequestQueue(this);

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(3000);

                    synchronized (this) {
                        while (flaga) {
                            wait();
                        }
                    }
                } catch (InterruptedException e){
                    e.printStackTrace();
                } finally{
                    loginAlready();
                    finish();

                }
            }
        };
        timer.start();
    }

    public void loginAlready() { // checking log in/out from pref

        // take email and password from pref
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String email = getPrefs.getString("email", "empty");
        final String password = getPrefs.getString("password", "empty");

        User user = new User(email, password);

        Map<String, String> fields = new HashMap<>();
        fields.put("email", email);
        fields.put("password", password);

        Call<ResponseBody> call = hotNailService.logIn(email, password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.names().get(0).equals("success")) {
                            Intent intent = new Intent(Splash.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            System.out.println("login");
                            Intent intent = new Intent(Splash.this, LoginSignActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor checkbox = sharedPreferences.edit();
        checkbox.putString(key, value);
        checkbox.commit();
    }

    // back button
    int backButtonCount;
    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1) {
            finish();
        } else {
            Toast.makeText(this, "Przyciśnij przycisk jeszcze raz aby wyjść z aplikacji.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
            flaga = true;
        }
    }
}
